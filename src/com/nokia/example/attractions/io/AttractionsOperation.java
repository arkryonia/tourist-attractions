/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.io;

import com.nokia.example.attractions.models.Guide;
import com.nokia.example.attractions.utils.UrlEncoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Operation for retrieving guide attractions.
 */
public final class AttractionsOperation {

    /**
     * Listener interface
     */
    public interface Listener {

        /**
         * Returns the retrieved attractions or null in case of an error.
         * @param attractions
         */
        public void attractionsReceived(Vector attractions);
    }
    private Guide guide;
    private Listener listener;

    /**
     * Cosntructor
     * @param listener
     * @param guide Guide from which the attarctions are being downloaded
     */
    public AttractionsOperation(Listener listener, Guide guide) {
        this.listener = listener;
        this.guide = guide;
    }

    /**
     * Starts the operation.
     */
    public final void start() {
        new Thread() {

            public void run() {
                try {
                    // Try to read guide locally
                    startLocal();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }.start();
    }

    private void startLocal()
        throws IOException {
        InputStream is = getClass().getResourceAsStream(guide.getUrl());
        if (is == null) {
            throw new IOException();
        }
        StringBuffer sb = new StringBuffer();
        int chars = 0;
        while ((chars = is.read()) != -1) {
            sb.append((char) chars);
        }
        // Parse local xml
        parseAttractions(sb.toString());
    }

    /**
     * Parses the server response and calls the listener.     
     * @param response
     */
    public final void networkHttpPostResponse(String response) {
        parseAttractions(response);
    }

    private void parseAttractions(String xml) {
        try {
            AttractionsParser handler = new AttractionsParser(guide);
            parse(xml, handler);

            listener.attractionsReceived(handler.getAttractions());
        }
        catch (ParseException e) {
            listener.attractionsReceived(null);
        }
    }
    
    protected void parse(String response, DefaultHandler handler)
        throws ParseException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            InputSource source =
                new InputSource(
                new ByteArrayInputStream(response.getBytes()));
            parser.parse(source, handler);
        }
        catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }
}
