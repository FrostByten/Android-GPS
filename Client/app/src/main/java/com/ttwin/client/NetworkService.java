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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * NetworkService - service that handles sending TCP datagram to server
 *
 * @class       NetworkService
 *
 * @sourceFile	Sending.java
 *
 * @program		Client
 *
 * @method	    public IBinder onBind(Intent intent)
 * @method      public int onStartCommand(Intent intent, int flags, int startId)
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
public class NetworkService extends Service {

    /**
     * Overrides Service method.
     *
     * @method      onStartCommand
     *
     * @date		2015-03-09
     *
     * @revisions	none
     *
     * @designer	Marc Rafanan
     *
     * @programmer	Marc Rafanan
     *
     * @notes       This method will create a thread to send data to server
     *
     * @signature	protected void onCreate(Bundle savedInstanceState)
     *
     * @param		intent - intent containing extras for the method
     *
     * @return       void
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String server = intent.getStringExtra("SERVER");
        final String port = intent.getStringExtra("PORT");
        final String data = intent.getStringExtra("DATA");

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // Use 10.0.2.2 for localhost testing in android studio emulator
                    Socket client = new Socket(server, Integer.valueOf(port));
                    OutputStream outToServer = client.getOutputStream();

                    // Send data (Should be in xml form)
                    outToServer.write(data.getBytes());

                    client.close();
                } catch (UnknownHostException e) {
                    // this should not happen because the ip is already validated
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        try
        {
            thread.join(5000);
            stopSelf();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Service.START_NOT_STICKY;
    }

    /**
     * Default onBind Method
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        // DO nothing
        return null;
    }
}
