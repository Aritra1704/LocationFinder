package com.arpaul.gpslibrary.fetchLocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ARPaul on 05-09-2016.
 */
public class GPSFusedLocationApi implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Context context;
    private final String LOG_TAG ="GPSFusedLocationApi";
    private GPSCallback gpsCallback;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    public GPSFusedLocationApi(Context context, GPSCallback gpsCallback){
        this.context = context;
        this.gpsCallback = gpsCallback;

        bindControls();
    }

    private void bindControls(){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1 * 1000); // Update location every second

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } else
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        LogUtils.infoLog(LOG_TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtils.infoLog(LOG_TAG, "GoogleApiClient connection has failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        LogUtils.infoLog(LOG_TAG, location.toString());

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(gpsCallback != null){
            if(currentLatLng.latitude == 0.0 && currentLatLng.longitude == 0.0){
                gpsCallback.gotGpsValidationResponse(currentLatLng, GPSErrorCode.EC_UNABLE_TO_FIND_LOCATION);
                GPSLogutils.createLogDataForLib("getCurrentLatLng", "lattitude : "+currentLatLng.latitude+", "+currentLatLng.longitude, "EC_UNABLE_TO_FIND_LOCATION");
            } else {
                gpsCallback.gotGpsValidationResponse(currentLatLng, GPSErrorCode.EC_LOCATION_FOUND);
                GPSLogutils.createLogDataForLib("getCurrentLatLng", "lattitude : "+currentLatLng.latitude+", "+currentLatLng.longitude, "EC_LOCATION_FOUND");

                mGoogleApiClient.disconnect();
            }
        }
    }

    public void connectApiClient() {
        if(mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            bindControls();
        }
    }

    public void disconnectApiClient() {
        if(mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
