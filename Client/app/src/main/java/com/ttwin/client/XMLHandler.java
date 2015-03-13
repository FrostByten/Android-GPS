package com.ttwin.client;


import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This class is to format and produce an XML document for the APP.
 *
 * Created by Marc on 2015-03-11.
 */
public class XMLHandler {

    Document doc;
    DocumentBuilderFactory DocFactory;
    DocumentBuilder  DocBuilder;

    final private static String TIME_FORMAT = "yyyy-MM-dd HH:mm";

    Element Entry;
    Element TimeElement;
    Element GPS;

    public XMLHandler(Context c)
    {
        DocFactory = DocumentBuilderFactory.newInstance();
        try
        {
            DocBuilder = DocFactory.newDocumentBuilder();
        }
        catch( ParserConfigurationException e )
        {
            // nothing
        }

    }

    /**
     * This function forces the default XML file to regenerate.
     *
     * @author Marc Vouve
     * @designer Marc Vouve
     *
     * @return void
     */
    public void WriteXMLFile()
    {
        doc = DocBuilder.newDocument();

        Entry = doc.createElement("ENTRY");   // I don't know if the server's expecting from this level or DATA.
        doc.appendChild(Entry);

        updateTime();

    }

    public void updateTime()
    {
        TimeElement = doc.createElement("TIME");
        TimeElement.appendChild(doc.createTextNode());
        doc.appendChild(TimeElement);
    }

    /**
     * Returns a formatted string of the current time and date.
     *
     * @author Marc Vouve
     * @designer Marc Vouve
     *
     * @return string in TIME_FORMAT
     */
    private String getTimeFormatted()
    {
        final int time =  Calendar.getInstance().get(Calendar.SECOND);

        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT, Locale.CANADA);

        return sdf.format(new Date(time));
    }
}
