package com.ttwin.client;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Marc on 2015-03-11.
 */
public class GPSHelper {
    LocationManager lManager;
    //LocationProvider lProvider;
    //LocationListener lListener;

    private GPSHelper()
    {

    }

    public GPSHelper( Context c)
    {
        lManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        //lListener = new GPSListener();
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


}
