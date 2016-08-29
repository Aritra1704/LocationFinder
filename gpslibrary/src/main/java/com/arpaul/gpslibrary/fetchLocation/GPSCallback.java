package com.param.gpsutilities.fetchLocation;



public interface GPSCallback 
{
    public abstract void gotGpsValidationResponse(Object response, GPSErrorCode code);
}
