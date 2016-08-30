package com.arpaul.gpslibrary.fetchAddressGeoCode;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import com.arpaul.gpslibrary.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Aritra on 21-06-2016.
 */
public class FetchAddressLoader extends AsyncTaskLoader {

    private String TAG = "TAG";
    private Location location;

    private Context context;
    private AddressDO addressDO;
    public FetchAddressLoader(Context context){
        super(context);
    }

    public FetchAddressLoader(Context context, LatLng latlang){
        super(context);
        this.context = context;

        this.location = new Location(AddressConstants.FETCH_ADDRESS);
        location.setLatitude(latlang.latitude);
        location.setLongitude(latlang.longitude);

        addressDO = new AddressDO();
    }

    @Override
    public AddressDO loadInBackground() {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        String errorMessage = "";

        // Get the location passed to this service through an extra.
//        Location location = intent.getParcelableExtra(AppConstants.LOCATION_DATA_EXTRA);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = context.getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = context.getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = context.getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            addressDO.code = AddressConstants.FAILURE_RESULT;
            addressDO.message = errorMessage;
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            LogUtils.infoLog(TAG, context.getString(R.string.address_found));

            addressDO.code = AddressConstants.SUCCESS_RESULT;
            addressDO.message = TextUtils.join(System.getProperty("line.separator"), addressFragments);
        }
        return addressDO;
    }
}
