package com.ttwin.client;

/**
 * Sending activity - activity that gets location changes and sends updates to the server
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
 * @designer    Marc Rafanan
 *
 * @programmer	Thomas Tallentire
 * @programmer	Marc Rafanan
 *
 * @note        This activity will send gps updates to the server either my manually sending it
 *              through a send button or by automatic updates when location changes. Sending data
 *              is done by calling NetworkService
 *
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
 * @variable    private BroadcastReceiver receiver
 *
 * @method	    protected void onCreate(Bundle savedInstanceState)
 * @method      protected void onResume()
 * @method      protected void onPause()
 * @method	    public boolean onCreateOptionsMenu(Menu menu)
 * @method	    public boolean onOptionsItemSelected(MenuItem item)
 * @method	    public void sendLocation(View view)
 * @method	    public void goHome(View view)
 *
 * @date		2015-03-09
 *
 * @revision	none
 *
 * @designer	Thomas Tallentire
 * @designer    Marc Rafanan
 *
 * @programmer	Thomas Tallentire
 * @programmer	Marc Rafanan
 *
 * @note
 */
public class Sending extends Activity {

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
     * GPSHelper object
     */
    private GPSHelper gpsHelper;

    /**
     * XMLHandler object
     */
    private XMLHandler xmlHandler;

    /**
     * BroadcastReceiver that will get notification if gps location changed
     * and then calls NetworkService to send xml data.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent i) {

            Intent intent = new Intent(Sending.this, NetworkService.class);

            intent.putExtra("SERVER", ip);
            intent.putExtra("PORT", port);
            intent.putExtra("DATA", xmlHandler.getStringFromDocument());
            startService(intent);
        }
    };

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

        gpsHelper = new GPSHelper(this);

        xmlHandler = new XMLHandler(this, gpsHelper);

    }

    /**
     * Method that will be called when application resumes
     *
     * @method      onResume
     *
     * @date		2015-03-14
     *
     * @revisions	none
     *
     * @designer	Marc Rafanan
     *
     * @programmer	Marc Rafanan
     *
     * @notes       Registers the GPSHelper receiver dynamically
     *
     * @signature	protected void onResume()
     *
     * @param
     *
     * @return
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter("com.ttwin.client.GPSHELPER"));
    }

    /**
     * Method that will be called when application pauses
     *
     * @method      onPause
     *
     * @date		2015-03-14
     *
     * @revisions	none
     *
     * @designer	Marc Rafanan
     *
     * @programmer	Marc Rafanan
     *
     * @notes       Unregisters the GPSHelper receiver dynamically
     *
     * @signature	protected void onPause()
     *
     * @param
     *
     * @return
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
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
        Intent intent = new Intent(Sending.this, NetworkService.class);

        intent.putExtra("SERVER", ip);
        intent.putExtra("PORT", port);
        intent.putExtra("DATA", xmlHandler.getStringFromDocument());
        startService(intent);
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
}
