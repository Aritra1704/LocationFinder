package com.arpaul.gpslibrary.fetchAddressGeoCode;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.arpaul.gpslibrary.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Aritra on 21-06-2016.
 */
public class FetchGeoCodeLoader extends AsyncTaskLoader {

    private String TAG = "TAG";
    private String address;

    private Context context;
    private AddressDO addressDO;
    public FetchGeoCodeLoader(Context context){
        super(context);
    }

    public FetchGeoCodeLoader(Context context, String address){
        super(context);
        this.context = context;

        this.address = address;

        addressDO = new AddressDO();
    }

    @Override
    public AddressDO loadInBackground() {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        List<Address> listAddress;

        try {
            listAddress = geocoder.getFromLocationName(address,5);
            if (listAddress==null || listAddress.size() <= 0) {
                return null;
            }
            Address location=listAddress.get(0);
            addressDO.location = new Location(AddressConstants.FETCH_LOCATION);
            addressDO.location.setLatitude(location.getLatitude());
            addressDO.location.setLongitude(location.getLongitude());


//            Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
//            float distance = locationA.distanceTo(locationB);

//            p1 = new Barcode.GeoPoint((int) (location.getLatitude() * 1E6), (int) (location.getLongitude() * 1E6));

        } catch (IOException e) {
            Log.e(TAG, "Unable to connect to Geocoder", e);
            addressDO.code = AddressConstants.FAILURE_RESULT;
            addressDO.message = context.getString(R.string.service_not_available);
        } catch (IllegalArgumentException illegalArgumentException) {
            addressDO.code = AddressConstants.FAILURE_RESULT;
            addressDO.message = context.getString(R.string.invalid_address);
        } finally {
            if(addressDO.code != AddressConstants.FAILURE_RESULT)
                addressDO.code = AddressConstants.SUCCESS_RESULT;
        }
        return addressDO;
    }
}
