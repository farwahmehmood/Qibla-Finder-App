package com.carwasher.qiblafinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements LocationListener, SensorEventListener {
    SensorManager sensorManager;
    Sensor accelerometerSensor, magnetmeterSensor;
    ImageView compass_img;
    TextView degrees_txt;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    boolean isLastAccelerometerArrayCopied = false;
    boolean isLastMagnetometerCopied = false;

    long lastUpdatedTime = 0;
    float currentDegree = 0f;

    double longi;
    double lati;
    double alti;
FusedLocationProviderClient fusedLocationProviderClient;
Button findQibla;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        degrees_txt = findViewById(R.id.degress_txt);
        compass_img = findViewById(R.id.arrow_img);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetmeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        findQibla=findViewById(R.id.button);
        findQibla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, com.carwasher.qiblafinder.QiblaActivity.class));
            }
        });

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        //getLocation();

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }


//         LocationListener mMyLocationListener = new MyLocationListener() {
//             @Override
//             public void onLocationChanged(final Location location) {
//                 //your code here
//                 longi=location.getLongitude();
//                 lati=location.getLatitude();
//                 alti=location.getAltitude();
//                 QiblaDirectionCompass qiblaDirectionCompass=new QiblaDirectionCompass(MainActivity.this,
//                         compass_img, longi,lati, alti,degrees_txt);
//
//             }
//         };
//         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,
//                 10, mLocationListener);
//         QiblaDirectionCompass qiblaDirectionCompass=new QiblaDirectionCompass(MainActivity.this,
//                 compass_img, longi,lati, alti,degrees_txt);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
//    public void getLocation()
//    {
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                QiblaDirectionCompass qiblaDirectionCompass=new QiblaDirectionCompass(MainActivity.this,
//                        compass_img, location.getLongitude(),location.getLatitude(), location.getAltitude(),degrees_txt);
//            }
//        });
//    }
//
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor==accelerometerSensor)
        {
            System.arraycopy(sensorEvent.values,0,lastAccelerometer,0,sensorEvent.values.length);
            isLastAccelerometerArrayCopied=true;
        }
        else if (sensorEvent.sensor==magnetmeterSensor)
        {
            System.arraycopy(sensorEvent.values,0,lastMagnetometer,0,sensorEvent.values.length);
            isLastMagnetometerCopied=true;
        }
        if (isLastAccelerometerArrayCopied&&isLastMagnetometerCopied&&System.currentTimeMillis()-lastUpdatedTime>250)
        {


            float degree = Math.round(sensorEvent.values[0]);
            float head = Math.round(sensorEvent.values[0]);
            float bearTo;
            Location userLoc=new Location("service Provider");
            //get longitudeM Latitude and altitude of current location with gps class and  set in userLoc
            userLoc.setLongitude(longi);
            userLoc.setLatitude(lati);
            userLoc.setAltitude(alti);

            Location destinationLoc = new Location("service Provider");
            destinationLoc.setLatitude(21.422487); //kaaba latitude setting
            destinationLoc.setLongitude(39.826206);
           //kaaba longitude setting
            float bearTo1=userLoc.bearingTo(destinationLoc);


            GeomagneticField geoField = new GeomagneticField( Double.valueOf( userLoc.getLatitude() ).floatValue(), Double
                    .valueOf( userLoc.getLongitude() ).floatValue(),
                    Double.valueOf( userLoc.getAltitude() ).floatValue(),
                    System.currentTimeMillis() );
            head -= geoField.getDeclination(); // converts magnetic north into true north

            if (bearTo1 < 0) {
                bearTo1 = bearTo1 + 360;
                //bearTo = -100 + 360  = 260;
            }

//This is where we choose to point it
            float direction = bearTo1 - head;

// If the direction is smaller than 0, add 360 to get the rotation clockwise.
            if (direction < 0) {
                direction = direction + 360;
            }

            SensorManager.getRotationMatrix(rotationMatrix,null,lastAccelerometer,lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix,orientation);



            float azimuthInRadians=orientation[0];
            float azimuthInDegree= (float) Math.toDegrees(azimuthInRadians);

            float azimuthInRadians1=orientation[2];
            float azimuthInDegree1= (float) Math.toDegrees(azimuthInRadians1);

//            RotateAnimation rotateAnimation=
//                    new RotateAnimation(currentDegree,-azimuthInDegree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//            rotateAnimation.setDuration(250);
//            rotateAnimation.setFillAfter(true);

//            RotateAnimation rotateAnimation1=
//                    new RotateAnimation(currentDegree,-azimuthInDegree1, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//            rotateAnimation1.setDuration(250);
//            rotateAnimation1.setFillAfter(true);
//
//            compass_img.startAnimation(rotateAnimation);

            RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

// how long the animation will take place
            ra.setDuration(210);


// set the animation after the end of the reservation status
            ra.setFillAfter(true);

// Start the animation
            compass_img.startAnimation(ra);

            currentDegree=-azimuthInDegree;
            lastUpdatedTime=System.currentTimeMillis();

            int z=(int) azimuthInDegree1;
            int x= (int) azimuthInDegree;
            degrees_txt.setText(x+" degrees       "+"z-axis"+z);

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this,magnetmeterSensor,SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this,accelerometerSensor);
        sensorManager.unregisterListener(this,magnetmeterSensor);
    }

}