package com.carwasher.qiblafinder;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class MyLocationListener implements android.location.LocationListener {
public static double lat,lon,alt;
    @Override
    public void onLocationChanged(@NonNull Location location) {
        lat=location.getLatitude();
        lon=location.getLongitude();
         alt=location.getAltitude();
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
}
