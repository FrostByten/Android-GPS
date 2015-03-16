package com.ttwin.client;

/**
 * Client home activity to initialize the connection to the server
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
 * @programmer  Marc Rafanan
 *
 * @note
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * Home class - home activity to initialize the connection to the server
 * @class       Home
 *
 * @sourceFile	Home.java
 *
 * @program		Client
 *
 * @method	protected void onCreate(Bundle savedInstanceState)
 * @method	public boolean onCreateOptionsMenu(Menu menu)
 * @method	public boolean onOptionsItemSelected(MenuItem item)
 * @method	private boolean valid(String ip, String port)
 * @method	public void go(View view)
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
public class Home extends Activity {
    SharedPreferences pref;

    /**
     * Timeout value when checking connection
     */
    private static final int TIMEOUT = 3000;

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
        setContentView(R.layout.activity_home);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        ((EditText)findViewById(R.id.IP)).setText(pref.getString("ip", "00.00.00.00 or milliways.ca"));
        ((EditText)findViewById(R.id.Port)).setText(pref.getString("port", "7000"));

    }

    /**
     * Default Activity onCreateOptionsMenu method
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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
     * Method for validating host and port inputs
     *
     * @method	    valid
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
     * @signature	private boolean valid(String ip, String port)
     *
     * @param ip    - string ip either host name or actual ip of the server
     * @param port  - port number of the server
     * @return      boolean - valid or invalid inputs
     */
    private boolean valid(String ip, String port) {

        Boolean validAddress = false;

        try
        {
            validAddress = new AddressValidation().execute(ip, port).get();
            if(!validAddress)
            {
                Toast.makeText(getApplicationContext(), "Invalid host/port", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch(InterruptedException e)
        {
            Toast.makeText(getApplicationContext(), "Network Interrupted", Toast.LENGTH_SHORT).show();
            return false;
        }
        catch(ExecutionException e)
        {
            Toast.makeText(getApplicationContext(), "Execution Error", Toast.LENGTH_SHORT).show();
            return false;
        }

        Toast.makeText(getApplicationContext(), "Connection Successful", Toast.LENGTH_SHORT).show();

        return true;
    }

    /**
     * On click method for GO button in home activity
     *
     * @method	    go
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
     * @notes       The method will pass the ip and port number to the Sending activity
     *              to start sending gps locations.
     *
     * @signature   public void go(View view)
     *
     * @param view  - view from which the onClick event came from
     *
     * @return  void
     */
    public void go(View view) {
        SharedPreferences.Editor editor = pref.edit();
        String ip = ((EditText)findViewById(R.id.IP)).getText().toString();
        String port = ((EditText)findViewById(R.id.Port)).getText().toString();

        if (!valid(ip, port))
            return;

        editor.putString("ip", ip);
        editor.putString("port", port);
        editor.apply();

        Bundle b = new Bundle();
        b.putString("ip", ip);
        b.putString("port", port);
        Intent intent = new Intent(this, Sending.class);
        intent.putExtras(b);
        startActivity(intent);
        //finish(); // commented out to let the sending activity go back to home
    }

    /**
     * AddressValidation inner class
     * @class       AddressValidation
     *
     * @method	protected void onPostExecute(Boolean result)
     * @method	protected Boolean doInBackground(String... ip)
     *
     * @date		2015-03-09
     *
     * @revision	none
     *
     * @designer	Marc Rafanan
     * @designer    Thomas Tallentire
     *
     * @programmer	Marc Rafanan
     * @programmer	Thomas Tallentire
     *
     * @note        An inner class that extends AsyncTask to do the actual server validation. This
     *              is used because Android will complain if there are network processes (or any
     *              long running process) running in the main UI thread.
     *
     */
    private class AddressValidation extends AsyncTask<String, Void, Boolean> {

        /**
         * Default doInBackground method for the AsyncTask
         *
         * @method	    doInBackground
         *
         * @date		2015-03-09
         *
         * @revisions	none
         *
         * @designer	Marc Rafanan
         * @designer    Thomas Tallentire
         *
         * @programmer	Marc Rafanan
         * @programmer	Thomas Tallentire
         *
         * @notes       The method will validate if the hostname or ip is valid
         *
         * @signature   protected Boolean doInBackground(String... ip)
         *
         * @param       param
         * @return Boolean - valid or invalid ip
         */
        @Override
        protected Boolean doInBackground(String... param) {

            try {
                // Use 10.0.2.2 for localhost testing in android studio emulator
                // Connect and disconnect to test the server
                Socket client = new Socket();
                client.connect(new InetSocketAddress(param[0], Integer.valueOf(param[1])), TIMEOUT);
                client.close();
            } catch (UnknownHostException e) {
                return false;
            }
            catch (IOException e) {
                return false;
            }

            return true;
        }
    }
}
