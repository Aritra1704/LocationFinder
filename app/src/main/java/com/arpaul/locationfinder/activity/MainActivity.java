package com.arpaul.locationfinder.activity;

import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arpaul.customalertlibrary.popups.statingDialog.CustomPopupType;
import com.arpaul.gpslibrary.fetchLocation.GPSCallback;
import com.arpaul.gpslibrary.fetchLocation.GPSErrorCode;
import com.arpaul.gpslibrary.fetchLocation.GPSUtills;
import com.arpaul.locationfinder.R;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.PermissionUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends BaseActivity implements GPSCallback/*,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener*/ {

    private View llSunShineActivity;
    private TextView tvLatitude, tvLongitude;
    private final String LOG_TAG ="LocationFinderSample";

    private GPSUtills gpsUtills;
    private boolean ispermissionGranted = false;
    private boolean isGpsEnabled;
    private LatLng currentLatLng = null;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public void initialize(Bundle savedInstanceState) {
        llSunShineActivity = baseInflater.inflate(R.layout.activity_main,null);
        llBody.addView(llSunShineActivity, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        initialiseControls();

        bindControls();
    }

    private void bindControls(){
        gpsUtills = GPSUtills.getInstance(this);
        gpsUtills.setLogEnable(true);
        gpsUtills.setPackegeName(getPackageName());
        gpsUtills.setListner(this);

        if(new PermissionUtils().checkPermission(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}) != 0){
            new PermissionUtils().verifyLocation(this,new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION});
        }
        else{
            createGPSUtils();
        }

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();*/
    }

    /*@Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1 * 1000); // Update location every second

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
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
        //txtOutput.setText(location.toString());

//        tvLatitude.setText("" + location.getLatitude());
//        tvLongitude.setText("" + location.getLongitude());

    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            ispermissionGranted = true;
            gpsUtills.connectGoogleApiClient();
            createGPSUtils();

            getCurrentLocation();
        }
    }

    @Override
    public void gotGpsValidationResponse(Object response, GPSErrorCode code)
    {
        if(code == GPSErrorCode.EC_GPS_PROVIDER_NOT_ENABLED) {
            isGpsEnabled = false;
            showCustomDialog(getString(R.string.gpssettings),getString(R.string.gps_not_enabled),getString(R.string.settings),getString(R.string.cancel),getString(R.string.settings), CustomPopupType.DIALOG_ALERT,false);
        }
        else if(code == GPSErrorCode.EC_GPS_PROVIDER_ENABLED) {
            isGpsEnabled = true;
            gpsUtills.getCurrentLatLng();
        }
        else if(code == GPSErrorCode.EC_UNABLE_TO_FIND_LOCATION) {
            currentLatLng = (LatLng) response;

            showCustomDialog(getString(R.string.alert),getString(R.string.unable_to_fetch_your_current_location),getString(R.string.ok),null,getString(R.string.unable_to_fetch_your_current_location), CustomPopupType.DIALOG_ALERT,false);
        }
        else if(code == GPSErrorCode.EC_LOCATION_FOUND) {
            currentLatLng = (LatLng) response;
            LogUtils.debugLog("GPSTrack", "Currrent latLng :"+currentLatLng.latitude+" \n"+currentLatLng.longitude);

            tvLatitude.setText("" + currentLatLng.latitude);
            tvLongitude.setText("" + currentLatLng.longitude);

            gpsUtills.stopLocationUpdates();
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_LOCATION_IS_VALID) {
        }
        else if(code == GPSErrorCode.EC_CUSTOMER_lOCATION_IS_INVAILD) {
        }
        else if(code == GPSErrorCode.EC_DEVICE_CONFIGURED_PROPERLY) {
        }
    }

    private void getCurrentLocation(){
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                gpsUtills.getCurrentLatLng();
            }
        }, 2 * 1000);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(gpsUtills != null && ispermissionGranted){
            gpsUtills.connectGoogleApiClient();

            getCurrentLocation();
        }

        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();

    }

    @Override
    public void onStop() {

        if(gpsUtills != null)
            gpsUtills.disConnectGoogleApiClient();

        if(mGoogleApiClient != null)
            mGoogleApiClient.disconnect();

        super.onStop();
    }

    private void createGPSUtils(){
        gpsUtills.isGpsProviderEnabled();
    }

    private void initialiseControls(){
        tvLatitude = (TextView) llSunShineActivity.findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) llSunShineActivity.findViewById(R.id.tvLongitude);
    }
}
