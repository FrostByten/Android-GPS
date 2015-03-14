package com.ttwin.client;

/**
 * Sending activity - activity that sends gets location changes and sends updates to the server
 *
 * @sourceFile	Home.java
 *
 * @program		Client
 *
 * @date		2015-03-09
 *
 * @revision	none
 *
 * @designer	Thomas Tallentire
 *
 * @programmer	Thomas Tallentire
 * @programmer	Marc Rafanan
 *
 * @note        This activity will send gps updates to the server either my manually sending it
 *              through a send button or by automatic updates when location changes
 *
 */

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

/**
 * Sending activity - activity that sends gets location changes and sends updates to the server
 * @class       Sending
 *
 * @sourceFile	Sending.java
 *
 * @program		Client
 *
 * @variable    private String ip
 * @variable    private String port
 * @variable    private boolean host
 * @variable    private LocationManager locationManager
 *
 * @method	    protected void onCreate(Bundle savedInstanceState)
 * @method	    public boolean onCreateOptionsMenu(Menu menu)
 * @method	    public boolean onOptionsItemSelected(MenuItem item)
 * @method	    private boolean valid(String ip, String port)
 * @method	    public void go(View view)
 *
 * @date		2015-03-09
 *
 * @revision	none
 *
 * @designer	Thomas Tallentire
 *
 * @programmer	Thomas Tallentire
 * @programmer	Marc Rafanan
 *
 * @note
 */
public class Sending extends Activity implements LocationListener {

    /**
     * server ip
     */
    private String ip;

    /**
     * server port
     */
    private String port;

    /**
     * host flag
     */
    private boolean host = false;

    /**
     * locationManager object
     */
    private LocationManager locationManager;

    /**
     * Main method at activity startup
     *
     * @method      onCreate
     *
     * @date		2015-03-09
     *
     * @revisions	none
     *
     * @designer	Thomas Tallentire
     *
     * @programmer	Thomas Tallentire
     * @programmer	Marc Rafanan
     *
     * @notes
     *
     * @signature	protected void onCreate(Bundle savedInstanceState)
     *
     * @param		savedInstanceState - Bundle for the activity
     *
     * @return       void
     */
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


    /**
     * Default Activity onCreateOptionsMenu method
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sending, menu);
        return true;
    }

    /**
     * Default onOptionsItemSelected method
     * @param item
     * @return
     */
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

    /**
     * Method called by LocationListener when gps location changes
     *
     * @method	    onLocationChanged(Location location)
     *
     * @date		2015-03-13
     *
     * @revisions	none
     *
     * @designer	Marc Rafanan
     *
     * @programmer	Marc Rafanan
     *
     * @notes       This method will be called when the location changes and fulfills parameters
     *              set in requestLocationUpdates.
     *
     * @signature	private boolean valid(String ip, String port)
     *
     * @param location
     *
     * @return  void
     */
    @Override
    public void onLocationChanged(Location location)
    {
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Log.d("onLocationChanged", String.valueOf(lat) + " " + String.valueOf(lng));

        AsyncSendLocation sendlocation = new AsyncSendLocation();
        sendlocation.execute(String.valueOf(lat), String.valueOf(lng));
    }

    /**
     * Default onProviderDisabled method
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider)
    {
        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
    }

    /**
     * Default onProviderEnabled method
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider)
    {
        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
    }

    /**
     * Default onStatusChanged method
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // TODO Auto-generated method stub
    }

    /**
     * onClick method for manually sending gps updates to the server
     *
     * @method	    sendLocation
     *
     * @date		2015-03-13
     *
     * @revisions	none
     *
     * @designer	Marc Rafanan
     *
     * @programmer	Marc Rafanan
     *
     * @notes
     *
     * @signature	public void sendLocation(View view)
     *
     * @param view
     */
    public void sendLocation(View view)
    {
        GPSHelper gpsHelper = new GPSHelper(this);

        Location location = gpsHelper.getLocation();

        if(location == null)
        {
            // do not send anything if there is no fix
            return;
        }

        double lat = (double)(location.getLatitude());
        double lng = (double)(location.getLongitude());

        AsyncSendLocation sendlocation = new AsyncSendLocation();
        sendlocation.execute(String.valueOf(lat), String.valueOf(lng));
    }

    /**
     * onClick method for going back to home activity
     *
     * @method	    sendLocation
     *
     * @date		2015-03-13
     *
     * @revisions	none
     *
     * @designer	Marc Rafanan
     *
     * @programmer	Marc Rafanan
     *
     * @param view
     */
    public void goHome(View view)
    {
        finish();
    }

    /**
     * AsyncSendLocation inner class
     * @class       AsyncSendLocation
     *
     * @method	    protected Void doInBackground(String... param)
     *
     * @date		2015-03-13
     *
     * @revision	none
     *
     * @designer	Marc Rafanan
     *
     * @programmer	Marc Rafanan
     *
     * @note        An inner class that extends AsyncTask to send gps location to the server. This
     *              is used because Android will complain if there are network processes (or any
     *              long running process) running in the main UI thread.
     *
     */
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
