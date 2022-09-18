package com.carwasher.qiblafinder;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class QiblaActivity extends AppCompatActivity {
ImageView dialImg,indicatorImg;
TextView degressTxt;
View view;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qibla);

        dialImg=findViewById(R.id.dial);
        indicatorImg=findViewById(R.id.qibla_indicator);
        degressTxt=findViewById(R.id.degress_txt);
        view=findViewById(R.id.view);
      //  view=findViewById(R.id.view);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        getLocation();

    }
    public void getLocation()
    {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                QiblaDirectionCompass qiblaDirectionCompass=new QiblaDirectionCompass(QiblaActivity.this,
                        indicatorImg,dialImg, location.getLongitude(),location.getLatitude(), location.getAltitude(),degressTxt,view);
            }
        });
    }
}