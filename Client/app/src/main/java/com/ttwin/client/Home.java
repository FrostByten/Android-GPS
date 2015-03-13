package com.ttwin.client;

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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;


public class Home extends Activity {
    SharedPreferences pref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        ((EditText)findViewById(R.id.IP)).setText(pref.getString("ip", "00.00.00.00 or milliways.ca"));
        ((EditText)findViewById(R.id.Port)).setText(pref.getString("port", "7000"));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    private boolean valid(String ip, String port) {
        int portsize = port.length();
        //InetAddress address;
        //String[] rawipstring;
        //byte[] rawip = new byte[4];

        Boolean validAddress = false;

        try
        {
            validAddress = new AddressValidation().execute(ip).get();
            if(!validAddress)
            {
                Toast.makeText(getApplicationContext(), "Invalid ip address", Toast.LENGTH_SHORT).show();
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

        for (int i = 0; i < portsize; i++)
        {
            if (!(Character.isDigit(port.charAt(i))))
            {
                Toast.makeText(getApplicationContext(), "Invalid port", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        Toast.makeText(getApplicationContext(), "connection successful", Toast.LENGTH_SHORT).show();

        return true;
    }

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
        finish();
    }

    private class AddressValidation extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... ip) {

            InetAddress address;
            String[] rawipstring;
            byte[] rawip = new byte[4];

            // Check if host name or ip address
            if (ip[0].charAt(0) > 57 || ip[0].charAt(0) < 48)
            {
                try{
                    address = InetAddress.getByName(ip[0]);
                } catch (UnknownHostException e) {
                    return false;
                }
            } else
            {
                try{
                    rawipstring = ip[0].split(".");
                    for (int i = 0; i < rawipstring.length; i++)
                    {
                        rawip[i] = Byte.parseByte(rawipstring[i]);
                    }
                    address = InetAddress.getByAddress(rawip);
                } catch (UnknownHostException e) {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //do nothing
        }
    }
}
