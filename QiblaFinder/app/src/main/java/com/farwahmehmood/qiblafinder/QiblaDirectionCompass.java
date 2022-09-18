package com.carwasher.qiblafinder;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class QiblaDirectionCompass extends Service implements SensorEventListener {

    public static ImageView image;

    // record the compass picture angle turned
    private float currentDegree = 0f;
    private float currentDegreeNeedle = 0f;
    private float currentDegreeView = 0f;
    Context context;
    Location userLoc=new Location("service Provider");
    // device sensor manager
    private static SensorManager mSensorManager ;
    private Sensor sensor;
    public static TextView tvHeading;
    View view;
    ImageView dial;


    public QiblaDirectionCompass(Context context, ImageView indicator, ImageView dial, double longi, double lati, double alti, TextView heading, View view) {
        this.context = context;
        this.dial=dial;
        this.view=view;
        image=indicator;
        tvHeading = heading;
        userLoc.setLongitude(longi);
        userLoc.setLatitude(lati);
        userLoc.setAltitude(alti);
        mSensorManager =  (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if(sensor!=null) {
            // for the system's orientation sensor registered listeners
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH);//SensorManager.SENSOR_DELAY_Fastest
        }else{
            Toast.makeText(context,"Not Supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float degree = Math.round(sensorEvent.values[0]);
        float degree2 = Math.round(sensorEvent.values[1]);

        float degree1 = Math.round(sensorEvent.values[2]);


        float head = Math.round(sensorEvent.values[0]);
        float bearTo;
        Location destinationLoc = new Location("service Provider");

        destinationLoc.setLatitude(21.422487); //kaaba latitude setting
        destinationLoc.setLongitude(39.826206); //kaaba longitude setting
        bearTo=userLoc.bearingTo(destinationLoc);

        //bearTo = The angle from true north to the destination location from the point we're your currently standing.(asal image k N se destination taak angle )

        //head = The angle that you've rotated your phone from true north. (jaise image lagi hai wo true north per hai ab phone jitne rotate yani jitna image ka n change hai us ka angle hai ye)



        GeomagneticField geoField = new GeomagneticField( Double.valueOf( userLoc.getLatitude() ).floatValue(), Double
                .valueOf( userLoc.getLongitude() ).floatValue(),
                Double.valueOf( userLoc.getAltitude() ).floatValue(),
                System.currentTimeMillis() );
        head -= geoField.getDeclination(); // converts magnetic north into true north
        float z =geoField.getZ();
        if (bearTo < 0) {
            bearTo = bearTo + 360;
            //bearTo = -100 + 360  = 260;
        }

//This is where we choose to point it
        float direction = bearTo - head;

// If the direction is smaller than 0, add 360 to get the rotation clockwise.
        if (direction < 0) {
            direction = direction + 360;
        }
        tvHeading.setText("X: " + degree + " - Y : "+degree2+ " - Z:"+degree1);

        RotateAnimation raQibla = new RotateAnimation(currentDegreeNeedle, direction, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        raQibla.setDuration(210);
        raQibla.setFillAfter(true);
        view.startAnimation(raQibla);
        image.startAnimation(raQibla);

        currentDegreeNeedle = direction;

 //create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
// how long the animation will take place
        ra.setDuration(210);
// set the animation after the end of the reservation status
        ra.setFillAfter(true);
// Start the animation
        dial.startAnimation(ra);

        currentDegree = -degree;


//        RotateAnimation ra1 = new RotateAnimation(currentDegreeView, degree2, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -degree2);
//
//        // how long the animation will take place
//        ra1.setDuration(210);
//// set the animation after the end of the reservation status
//        ra1.setFillAfter(true);
//// Start the animation
//        view.startAnimation(ra1);
//        currentDegreeView = -degree2;
      // ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotationY", 0.0f, 360f);
       //        animation.setDuration(3600);
//        animation.setRepeatCount(ObjectAnimator.INFINITE);
//        animation.setInterpolator(new AccelerateDecelerateInterpolator());
//        animation.start();

        ValueAnimator lockAnimator = ValueAnimator.ofFloat(0, 1);     // value from 0 to 1
        lockAnimator.setDuration(210);
        lockAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator pAnimation) {
                float value = (Float) (pAnimation.getAnimatedValue());
                view.setRotationY(degree2);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onCreate() {
        Toast.makeText(context, "Started", Toast.LENGTH_SHORT).show();
        mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_STATUS_ACCURACY_HIGH); //SensorManager.SENSOR_DELAY_Fastest
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        Toast.makeText(context, "Destroy", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
