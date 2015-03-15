package com.ttwin.client;

/**
 * NetworkService - handles sending TCP datagram to server
 *
 * @sourceFile	NetworkService.java
 *
 * @program		Client
 *
 * @date		2015-03-14
 *
 * @revision	none
 *
 * @designer    Marc Rafanan
 *
 * @programmer	Marc Rafanan
 *
 * @note
 *
 */

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * NetworkService - Intent service that handles sending TCP datagram to server
 *
 * @class       NetworkService
 *
 * @sourceFile	Sending.java
 *
 * @program		Client
 *
 * @method	    NetworkService()
 * @method      protected void onHandleIntent(Intent intent)
 *
 * @date		2015-03-14
 *
 * @revision	none
 *
 * @designer    Marc Rafanan
 *
 * @programmer	Marc Rafanan
 *
 * @note
 */
public class NetworkService extends IntentService {

    public NetworkService()
    {
        super("NetworkService");
    }

    /**
     * Overrides IntentService method.
     *
     * @method      onHandleIntent
     *
     * @date		2015-03-09
     *
     * @revisions	none
     *
     * @designer	Marc Rafanan
     *
     * @programmer	Marc Rafanan
     *
     * @notes       This method will run asynchronously
     *
     * @signature	protected void onCreate(Bundle savedInstanceState)
     *
     * @param		intent - intent containing extras for the method
     *
     * @return       void
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        String server = intent.getStringExtra("SERVER");
        String port = intent.getStringExtra("PORT");
        String data = intent.getStringExtra("DATA");

        try {
            // Use 10.0.2.2 for localhost testing in android studio emulator
            Socket client = new Socket(server, Integer.valueOf(port));
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            // Send data (Should be in xml form)
            out.writeUTF(data);

            client.close();
        } catch (UnknownHostException e) {
            // this should not happen because the ip is already validated
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_SHORT).show();
        }
    }
}
