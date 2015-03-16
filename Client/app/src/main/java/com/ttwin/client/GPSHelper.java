package com.ttwin.client;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Marc on 2015-03-11.
 */
public class GPSHelper {

    Context context;
    LocationManager lManager;
    //LocationProvider lProvider;
    LocationListener lListener;
    private final int GPS_INTERVAL_TIME_MS = 10000;
    private final int GPS_DISTANCE_DELTA_M = 10;

    private GPSHelper()
    {

    }

    public GPSHelper( Context c)
    {
        context = c;
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

        if ( lManager.isProviderEnabled(LocationManager.GPS_PROVIDER ) )
        {
            lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    GPS_INTERVAL_TIME_MS,
                    GPS_DISTANCE_DELTA_M,
                    lListener);
        }
        else if ( lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) )
        {
            lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    GPS_INTERVAL_TIME_MS,
                    GPS_DISTANCE_DELTA_M,
                    lListener);
        }
    }

    private class GPSListener implements LocationListener {

        private String BROADCAST_ACTION = "com.ttwin.client.GPSHELPER";

        /**
         * When location is updated, create a new Intent to handle it.
         *
         * @author Marc R
         * @author Marc V
         * @designer Marc R
         * @designer Marc V
         * @date March 14th 2015
         */
        public void onLocationChanged(Location l)
        {
            Intent broadcast = new Intent();
            broadcast.setAction(BROADCAST_ACTION);
            context.sendBroadcast(broadcast);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String s) {}
        public void onProviderDisabled(String s) {}
    }




}
