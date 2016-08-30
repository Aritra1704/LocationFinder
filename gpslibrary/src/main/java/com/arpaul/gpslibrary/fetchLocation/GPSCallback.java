package com.arpaul.gpslibrary.fetchLocation;



public interface GPSCallback 
{
    public abstract void gotGpsValidationResponse(Object response, GPSErrorCode code);
}
