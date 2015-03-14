package com.ttwin.client;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Sending extends Activity implements LocationListener {

    private String ip;
    private String port;
    private boolean host = false;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sending);
        Bundle b = getIntent().getExtras();
        ip = b.getString("ip");
        port = b.getString("port");
        if (ip.charAt(0) > 57 || ip.charAt(0) < 48)
        {
            host = true;
        } else
        {
            host = false;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        int INTERVAL = 1000;

        //requestLocationUpdates(provider, minTime, minDistance, listener)
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, INTERVAL, 10, this);
        locationManager.requestLocationUpdates( LocationManager.PASSIVE_PROVIDER, INTERVAL, 10, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sending, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Log.d("onLocationChanged", String.valueOf(lat) + " " + String.valueOf(lng));

        AsyncSendLocation sendlocation = new AsyncSendLocation();
        sendlocation.execute(String.valueOf(lat), String.valueOf(lng));
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // TODO Auto-generated method stub
    }

    public void sendLocation(View view)
    {
        GPSHelper gpsHelper = new GPSHelper(this);

        Location location = gpsHelper.getLocation();

        if(location == null)
        {

        }

        double lat = (double)(location.getLatitude());
        double lng = (double)(location.getLongitude());

        AsyncSendLocation sendlocation = new AsyncSendLocation();
        sendlocation.execute(String.valueOf(lat), String.valueOf(lng));
    }

    public void goHome(View view)
    {
        finish();
    }

    private class AsyncSendLocation extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... param) {

            String lat = param[0];
            String lng = param[1];

            try
            {
                // Use 10.0.2.2 for localhost testing in android studio emulator
                Socket client = new Socket(ip, Integer.valueOf(port));
                OutputStream outToServer = client.getOutputStream();
                DataOutputStream out = new DataOutputStream(outToServer);

                // Send data (Should be in xml form)
                out.writeUTF("Latitude: " + lat + " Longitude: " + lng);

                client.close();
            }
            catch(UnknownHostException e)
            {
                // this should not happen because the ip is already validated
                e.printStackTrace();
            }
            catch(IOException e)
            {

                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(Sending.this, "Connection Error", Toast.LENGTH_SHORT).show();
                    }
                });
                finish();
            }

            return null;
        }
    }
}
