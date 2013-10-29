/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.location;

import com.nokia.example.attractions.Main;

/**
 * Abstract location finder.
 * <P>
 * Extended by the class AbstractLocationFinder.
 * @see com.nokia.example.attractions.location.AbstractLocationFinder
 */
public abstract class LocationFinder {

    private static final String[] S40_GPS_DEVICES = {"2710", "6350", "6750",
        "3710", "6700", "6260"};

    /**
     * Initialize the location finder.
     */
    protected void init(Listener listener)
        throws InitializationException, SecurityException {
        this.listener = listener;
    }

    /**
     * Start finding location.
     */
    public abstract void start();

    /**
     * Stop finding location.
     */
    public abstract void quit();
    protected Listener listener;

    /**
     * Interface for notifying when the location has changed.
     */
    public interface Listener {

        void newLocation(double lat, double lon, int accuracy);
    }

    /**
     * Creates a location finder based on the capabilities the device.
     * @param listener
     * @return 
     */
    public static LocationFinder getFinder(Listener listener) {
        if (listener == null) {
            throw new NullPointerException("listener not defined");
        }

        LocationFinder finder = null;
        try {
            // this will throw an exception if JSR-179 is missing
            Class.forName("javax.microedition.location.Location");

            if (finder == null && supportsGPS()) {
                Class c =
                    Class.forName(
                    "com.nokia.example.attractions.location.GpsLocationFinder");
                finder = (LocationFinder) (c.newInstance());
                try {
                    finder.init(listener);
                }
                catch (InitializationException e) {
                    finder = null;
                }
            }
            if (finder == null && supportsCellId()) {
                Class c =
                    Class.forName(
                    "com.nokia.example.attractions.location.CellIdLocationFinder");
                finder = (LocationFinder) (c.newInstance());
                try {
                    finder.init(listener);
                }
                catch (InitializationException e) {
                    finder = null;
                }
            }
        }
        catch (Exception e) {
            finder = null;
        }
        return finder;
    }

    private static boolean supportsGPS() {
        String platform = System.getProperty("microedition.platform");
        if (platform != null) {
            for (int i = 0; i < S40_GPS_DEVICES.length; i++) {
                if (platform.indexOf(S40_GPS_DEVICES[i]) > -1) {
                    return true;
                }
            }
        }
        return Main.isS60Phone();
    }

    private static boolean supportsCellId() {
        try {
            Class.forName("com.nokia.mid.location.LocationUtil");
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    protected static class InitializationException
        extends Exception {

        public InitializationException() {
        }

        public InitializationException(String s) {
            super(s);
        }
    }
}
