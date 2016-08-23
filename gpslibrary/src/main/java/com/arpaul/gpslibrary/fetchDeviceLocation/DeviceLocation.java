package com.arpaul.gpslibrary.fetchDeviceLocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by ARPaul on 23-08-2016.
 */
public class DeviceLocation {

    public String getCountry(Context context){
        String locale = context.getResources().getConfiguration().locale.getCountry();

        Locale.getDefault().getCountry();
        return locale;
    }

    public String getCountryTelephony(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();

        return countryCodeValue;
    }

    private void getCountryCode(final Context mContext, final Location location, final ResponseListener listener) {

        AsyncTask<Void, Void, String> countryCodeTask = new AsyncTask<Void, Void, String>() {

            final float latitude = (float) location.getLatitude();
            final float longitude = (float) location.getLongitude();
            List<Address> addresses = null;
            Geocoder geocoder = new Geocoder(mContext);
            String code = null;

            @Override
            protected String doInBackground(Void... params) {
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    code = addresses.get(0).getCountryCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return code;
            }

            @Override
            protected void onPostExecute(String code) {
                if(listener != null)
                    listener.getResponse(code);
            }

        };
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            countryCodeTask.execute();
        } else {
            countryCodeTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
