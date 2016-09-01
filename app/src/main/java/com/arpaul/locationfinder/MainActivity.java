package com.arpaul.locationfinder;

import android.Manifest;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.arpaul.customalertlibrary.popups.statingDialog.CustomPopup;
import com.arpaul.customalertlibrary.popups.statingDialog.CustomPopupType;
import com.arpaul.customalertlibrary.popups.statingDialog.PopupListener;
import com.arpaul.gpslibrary.fetchLocation.GPSCallback;
import com.arpaul.gpslibrary.fetchLocation.GPSErrorCode;
import com.arpaul.gpslibrary.fetchLocation.GPSUtills;
import com.arpaul.utilitieslib.LogUtils;
import com.arpaul.utilitieslib.NetworkUtility;
import com.arpaul.utilitieslib.PermissionUtils;
import com.arpaul.utilitieslib.StringUtils;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements GPSCallback, PopupListener {

    private GPSUtills gpsUtills;
    private CustomPopup cPopup;
    private boolean ispermissionGranted = false;
    private boolean isGpsEnabled;
    private LatLng currentLatLng = null;

    private TextView tvLatitude, tvLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude = (TextView) findViewById(R.id.tvLongitude);

        gpsUtills = GPSUtills.getInstance(MainActivity.this);
        gpsUtills.setLogEnable(true);
        gpsUtills.setPackegeName(getPackageName());
        gpsUtills.setListner(MainActivity.this);

        if(Build.VERSION.SDK_INT >= 23 && new PermissionUtils().checkPermission(MainActivity.this) != 0){
            new PermissionUtils().verifyLocation(MainActivity.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
        }
        else{
            createGPSUtils();
        }
    }

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
            LogUtils.debug("GPSTrack", "Currrent latLng :"+currentLatLng.latitude+" \n"+currentLatLng.longitude);

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
    }

    @Override
    public void onStop() {
        super.onStop();
        if(gpsUtills != null)
            gpsUtills.disConnectGoogleApiClient();
    }

    private void createGPSUtils(){
        gpsUtills.isGpsProviderEnabled();
    }

    /**
     * Shows Dialog with user defined buttons.
     * @param title
     * @param message
     * @param okButton
     * @param noButton
     * @param from
     * @param isCancelable
     */
    public void showCustomDialog(final String title, final String message, final String okButton, final String noButton, final String from, boolean isCancelable){
        runOnUiThread(new RunShowDialog(title,message,okButton,noButton,from, isCancelable));
    }

    public void showCustomDialog(final String title, final String message, final String okButton, final String noButton, final String from, CustomPopupType dislogType, boolean isCancelable){
        runOnUiThread(new RunShowDialog(title,message,okButton,noButton,from, dislogType, isCancelable));
    }

    public void hideCustomDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cPopup != null && cPopup.isShowing())
                    cPopup.dismiss();
            }
        });
    }

    class RunShowDialog implements Runnable {
        private String strTitle;// FarmName of the materialDialog
        private String strMessage;// Message to be shown in materialDialog
        private String firstBtnName;
        private String secondBtnName;
        private String from;
        private String params;
        private boolean isCancelable=false;
        private CustomPopupType dislogType = CustomPopupType.DIALOG_NORMAL;
        private int isNormal = 0;
        public RunShowDialog(String strTitle,String strMessage, String firstBtnName, String secondBtnName,	String from, boolean isCancelable)
        {
            this.strTitle 		= strTitle;
            this.strMessage 	= strMessage;
            this.firstBtnName 	= firstBtnName;
            this.secondBtnName	= secondBtnName;
            this.isCancelable 	= isCancelable;
            if (from != null)
                this.from = from;
            else
                this.from = "";

            isNormal = 0;
        }

        public RunShowDialog(String strTitle,String strMessage, String firstBtnName, String secondBtnName,	String from, CustomPopupType dislogType, boolean isCancelable)
        {
            this.strTitle 		= strTitle;
            this.strMessage 	= strMessage;
            this.firstBtnName 	= firstBtnName;
            this.secondBtnName	= secondBtnName;
            this.dislogType     = dislogType;
            this.isCancelable 	= isCancelable;
            if (from != null)
                this.from = from;
            else
                this.from = "";

            isNormal = 1;
        }

        @Override
        public void run() {
            if(isNormal > 0)
                showNotNormal();
            else
                showNormal();
        }

        private void showNotNormal(){
            try{
                if (cPopup != null && cPopup.isShowing())
                    cPopup.dismiss();

                cPopup = new CustomPopup(MainActivity.this, MainActivity.this,strTitle,strMessage,
                        firstBtnName, secondBtnName, from, dislogType);

//                cPopup.setTypeface(tfAdventProMedium,tfAdventProRegular);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        cPopup.show();
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        private void showNormal(){
            try{
                if (cPopup != null && cPopup.isShowing())
                    cPopup.dismiss();

                cPopup = new CustomPopup(MainActivity.this, MainActivity.this,strTitle,strMessage,
                        firstBtnName, secondBtnName, from, CustomPopupType.DIALOG_NORMAL);

//                cPopup.setTypeface(tfAdventProMedium,tfAdventProRegular);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        cPopup.show();
                    }
                });
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void OnButtonYesClick(String from) {
        dialogYesClick(from);
    }

    @Override
    public void OnButtonNoClick(String from) {
        dialogNoClick(from);
    }

    public void dialogYesClick(String from) {

    }

    public void dialogNoClick(String from) {
        if(from.equalsIgnoreCase("")){

        }
    }
}
