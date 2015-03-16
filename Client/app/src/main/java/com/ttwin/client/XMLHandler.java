package com.ttwin.client;


import android.accounts.AccountManager;
import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @File XMLHandler.java
 *
 * @class XMLHandler
 *
 * @date 2015-03-11
 *
 * @designer Marc Vouve
 *
 * @programmer Marc Vouve
 *
 * @method XMLHanlder(Context, GPSHelper) String
 * @method getStringFromDocument() void
 * @method updateGPS() void
 * @method updateTime() void
 * @method updateIdent() void
 * @method updateInfo() void
 * @method clearNode() void
 * @method getTimeFormatted() void
 */
public class XMLHandler {

    Document Doc;
    DocumentBuilderFactory DocFactory;
    DocumentBuilder  DocBuilder;

    final private static String TIME_FORMAT = "yyyy-MM-dd HH:mm";

    Element Entry;
    Element TimeNode;
    Element GPSNode;
    Element IdentNode;
    Element InfoNode;
    GPSHelper gpsHelp;

    private Context AppContext;

    /**
     * This is the main constructor for the XMLHandler class.
     *
     * @author Marc Vouve
     *
     * @designer Marc Vouve
     *
     * @date March 12, 2015
     *
     * @signature XMLHandler(Context c, GPSHelper help)
     *
     * @param c Context the current device context.
     * @param help A GPS Helper to get GPS fixes.
     *
     */
    public XMLHandler(Context c, GPSHelper help)
    {
        gpsHelp = help;
        AppContext = c;
        DocFactory = DocumentBuilderFactory.newInstance();
        try
        {
            DocBuilder = DocFactory.newDocumentBuilder();
        }
        catch( ParserConfigurationException e )
        {
            // nothing
        }
        Doc = DocBuilder.newDocument();


        Entry = Doc.createElement("ENTRY");   // I don't know if the server's expecting from this level or DATA.
        Doc.appendChild(Entry);

        TimeNode = Doc.createElement("TIME");
        Entry.appendChild(TimeNode);

        GPSNode = Doc.createElement("GPS");
        Entry.appendChild(GPSNode);

        IdentNode = Doc.createElement("IDENT");
        Entry.appendChild(IdentNode);

        InfoNode = Doc.createElement("INFO");
        Entry.appendChild(InfoNode);

        updateTime();
        updateGPS();
        updateIdent();
        updateInfo();



    }

    /**
     * @date March 12, 2015
     *
     * @designer Nambari
     *
     * @designer Marc Vouve
     *
     * @author Nambari - VIA stack overflow
     *
     * @author Marc Vouve
     *
     * @signature String getStringFromDocument
     *
     * @return String the formatted XML document as a string.
     */
    public String getStringFromDocument()
    {
        updateTime();
        updateGPS();
        try
        {
            DOMSource domSource = new DOMSource(Doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, result);
            String str = writer.toString();

            Log.d("[XML]", str.substring(str.indexOf("<ENTRY>")));
            return str.substring(str.indexOf("<ENTRY>"));
        }
        catch(TransformerException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * UpdateGPS - Updates the GPS tag with the users location info.
     *
     * @method updateGPS
     *
     * @date Thursday March 12, 2015
     *
     * @designer Marc Vouve
     *
     * @author Marc Vouve
     *
     * @signature void updateGPS()
     *
     */
    public void updateGPS()
    {

        Map<String, Node> nodeMap = new HashMap<String, Node>();
        Location loc = gpsHelp.getLocation();



        clearNode(GPSNode);

        nodeMap.put("LATITUDE", Doc.createElement("LATITUDE"));
        nodeMap.put("LONGITUDE", Doc.createElement("LONGITUDE"));
        nodeMap.put("ALTITUDE", Doc.createElement("ALTITUDE"));
        nodeMap.put("SPEED", Doc.createElement("SPEED"));
        nodeMap.put("HEADING", Doc.createElement("HEADING"));

        if(loc == null)
        {
            return;
        }

        nodeMap.get("LATITUDE").appendChild(Doc.createTextNode("" + loc.getLatitude()));
        nodeMap.get("LONGITUDE").appendChild(Doc.createTextNode("" + loc.getLongitude()));
        nodeMap.get("ALTITUDE").appendChild(Doc.createTextNode(("" + loc.getAltitude())));
        nodeMap.get("SPEED").appendChild(Doc.createTextNode("" + loc.getSpeed()));
        try
        {
            nodeMap.get("HEADING").appendChild(Doc.createTextNode("" + loc.getBearing()));
        }
        catch(Exception e)
        {
            nodeMap.get("HEADING").appendChild(Doc.createTextNode("0.0"));
        }


        clearNode(GPSNode);

        for(Node n : nodeMap.values())
        {
            GPSNode.appendChild(n);
        }
    }

    /**
     * UpdateTime Updates the time tag with the current time
     *
     * @date Thursday March 12, 2015
     * @designer Marc Vouve
     * @author Marc Vouve
     *
     */
    public void updateTime()
    {
        clearNode(TimeNode);
        TimeNode.appendChild(Doc.createTextNode(getTimeFormatted()));
    }

    /**
     * UpdateIdent - Updates the IDENT tag with connection info: IP, HOSTNAME, MAC
     *
     * @date Thursday March 12, 2015
     * @designer Marc Vouve
     * @author Marc Vouve
     *
     */
    public void updateIdent()
    {
        Map<String, Node> nodeMap = new HashMap<String, Node>();

        clearNode(IdentNode);
        //WifiManager wm = (WifiManager) AppContext.getSystemService(Context.WIFI_SERVICE);

        nodeMap.put("IP", Doc.createElement("IP"));
        nodeMap.put("HOSTNAME", Doc.createElement("HOSTNAME"));
        nodeMap.put("MAC", Doc.createElement("MAC"));

        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        nodeMap.get("IP").appendChild(Doc.createTextNode(inetAddress.getHostAddress()));
                        if(inetAddress.getHostName() != null)
                        {
                            nodeMap.get("HOSTNAME").appendChild(Doc.createTextNode(inetAddress.getHostName()));
                        }
                        nodeMap.get("MAC").appendChild(Doc.createTextNode(intf.getHardwareAddress().toString()));
                    }
                }
            }

        }catch(Exception e)
        {
            e.getStackTrace();
            WifiManager wm = (WifiManager) AppContext.getSystemService(Context.WIFI_SERVICE);

            nodeMap.get("IP").appendChild(Doc.createTextNode(Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress())));
            nodeMap.get("HOSTNAME").appendChild(Doc.createTextNode(""));
            try{
                nodeMap.get("MAC").appendChild(Doc.createTextNode(wm.getConnectionInfo().getMacAddress()));
            }
            catch( NullPointerException n)
            {
                // do nothing
            }

        }






        for(Node n : nodeMap.values())
        {
            IdentNode.appendChild(n);
        }


    }

    /**
     * UpdateInfo - Updates the INFO tag with Personal Info, Phone Numer IP Google Account ETC.
     *
     * @method updateInfo(
     *
     * @date Thursday March 12, 2015
     *
     * @designer Marc Vouve
     *
     * @author Marc Vouve
     *
     * @signature updateInfo()
     *
     */
    public void updateInfo() {
        TelephonyManager tm = (TelephonyManager) AppContext.getSystemService(Context.TELEPHONY_SERVICE);
        AccountManager am = (AccountManager) AppContext.getSystemService(Context.ACCOUNT_SERVICE);

        Map<String, Node> nodeMap = new HashMap<String, Node>();

        clearNode(InfoNode);

        nodeMap.put("IMEI", Doc.createElement("IMEI"));
        nodeMap.put("DEVID", Doc.createElement("DEVID"));
        nodeMap.put("PHONE", Doc.createElement("PHONE"));
        nodeMap.put("GOOGLE", Doc.createElement("GOOGLE"));
        nodeMap.put("ICON", Doc.createElement("ICON"));

        nodeMap.get("IMEI").appendChild(Doc.createTextNode(tm.getDeviceId()));
        nodeMap.get("DEVID").appendChild(Doc.createTextNode(tm.getDeviceId()));
        nodeMap.get("PHONE").appendChild(Doc.createTextNode(tm.getLine1Number()));
        try
        {
            nodeMap.get("GOOGLE").appendChild(Doc.createTextNode(am.getAccounts()[0].name));
        }
        catch(IndexOutOfBoundsException e)
        {
            nodeMap.get("GOOGLE").appendChild(Doc.createTextNode("example@example.ca"));
        }
        nodeMap.get("ICON").appendChild(Doc.createTextNode(""));

        for (Node n : nodeMap.values()) {
            InfoNode.appendChild(n);
        }
    }

    /**
     * This method clears the nodes
     *
     * @method clearNode
     *
     * @date March 10, 2015
     *
     * @author Marc Vouve
     *
     * @designer Marc Vouve
     *
     * @signature void clearNode(Node node)
     *
     * @param node node to clear
     */
    public void clearNode(Node node)
    {
        Node child;

        while(node.hasChildNodes())
        {
            while(node.getFirstChild().hasChildNodes())
            {
                clearNode(node.getFirstChild());
            }
            node.removeChild(node.getFirstChild());
        }

    }

    /**
     * Returns a formatted string of the current time and date.
     *
     * @method getTimeFormatted
     *
     * @date March 10, 2015
     *
     * @author Marc Vouve
     *
     * @designer Marc Vouve
     *
     * @signature String getTimeFormatted
     *
     * @return string in TIME_FORMAT
     */
    private String getTimeFormatted()
    {

        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.CANADA);
        return sdf.format(new Date(System.currentTimeMillis()));
    }
}
