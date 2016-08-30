package com.arpaul.gpslibrary.fetchAddressGeoCode;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.arpaul.gpslibrary.R;

import java.util.List;

/**
 * Created by Aritra on 21-06-2016.
 */
public class FetchDistanceLoader extends AsyncTaskLoader {

    private String TAG = "TAG";

    private Context context;
    private Location currentLocation;
    private List<AddressDO> listAddressDO;

    public FetchDistanceLoader(Context context){
        super(context);
    }

    public FetchDistanceLoader(Context context, Location currentLocation, List<AddressDO> listAddressDO){
        super(context);
        this.context = context;
        this.currentLocation = currentLocation;
        this.listAddressDO = listAddressDO;

    }

    @Override
    public List<AddressDO> loadInBackground() {

        for(int i = 0; i < listAddressDO.size(); i++){

            AddressDO addressDO = listAddressDO.get(i);
            try {

                addressDO.currentLocation = currentLocation;
                addressDO.distance = addressDO.currentLocation.distanceTo(addressDO.location);

            } catch (IllegalArgumentException illegalArgumentException) {
                addressDO.code = AddressConstants.FAILURE_RESULT;
                addressDO.message = context.getString(R.string.invalid_address);
            } catch (Exception e) {
                Log.e(TAG, "Unable to connect to Geocoder", e);
                addressDO.code = AddressConstants.FAILURE_RESULT;
                addressDO.message = context.getString(R.string.service_not_available);
            } finally {
                if(addressDO.code != AddressConstants.FAILURE_RESULT)
                    addressDO.code = AddressConstants.SUCCESS_RESULT;
            }
        }
        return listAddressDO;
    }
}
