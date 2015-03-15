package com.ttwin.client;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Marc on 2015-03-11.
 */
public class GPSHelper {
    LocationManager lManager;
    //LocationProvider lProvider;
    LocationListener lListener;
    private final int GPS_INTERVAL_TIME_MS = 5000;
    private final int GPS_DISTANCE_DELTA_M = 10;

    private GPSHelper()
    {

    }

    public GPSHelper( Context c)
    {
        lManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        lListener = new GPSListener();
        updateLocation();
    }


    public Location getLocation()
    {
        if(lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null)
        {
            return lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        else if(lManager.getLastKnownLocation( LocationManager.GPS_PROVIDER ) != null)
        {
            return lManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );
        }
        else if(lManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)!= null)
        {
            return lManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        return null;
    }

    private void updateLocation()
    {
        if ( lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )
        {
            lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    GPS_INTERVAL_TIME_MS,
                    GPS_DISTANCE_DELTA_M,
                    lListener);
        }
        else if ( lManager.isProviderEnabled(LocationManager.GPS_PROVIDER ) )
        {
            lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    GPS_INTERVAL_TIME_MS,
                    GPS_DISTANCE_DELTA_M,
                    lListener);
        }
    }

    private class GPSListener implements LocationListener {
        /**
         * When a new location is requested, update update the onscreen textview.
         *
         * @param l
         */
        public void onLocationChanged(Location l)
        {
            //TODO what should happen when the location changes?
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String s) {}
        public void onProviderDisabled(String s) {}
    }




}
