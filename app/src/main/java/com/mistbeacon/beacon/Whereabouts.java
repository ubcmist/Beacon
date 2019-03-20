package com.mistbeacon.beacon;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.util.ArrayList;

/**
 * Uses Google Play API for obtaining device locations
 * Created by alejandro.tkachuk
 * alejandro@calculistik.com
 * www.calculistik.com Mobile Development
 */

class Wherebouts implements android.location.LocationListener{

    protected double latitude = 0;
    protected double longitude = 0;
    protected Location lastLoc;
    protected ArrayList<Integer> DailyMovement = new ArrayList<Integer>();
    protected double travelled = 0;

    @Override
    public void onLocationChanged(Location location) {
        lastLoc = location;
        double dist = distance(lastLoc.getLatitude(), lastLoc.getLongitude(), latitude, longitude);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.d("LOCATION", longitude + " " + latitude + " --diff: " + dist);

        if(dist > 0.1 && dist < 5){
            DailyMovement.add(0,1);
            travelled += dist;
        }else if(dist < 0.1){
            DailyMovement.add(0,0);
            travelled += dist;
        }
    }

    /** calculates the distance between two locations in MILES */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    protected ArrayList<Integer> getDailyMovement(){
        return this.DailyMovement;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}