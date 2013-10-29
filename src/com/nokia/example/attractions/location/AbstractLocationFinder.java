/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.location;

import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

/**
 * Abstract location finder.
 * <P>
 * Extended by the classes CellIdLocationFinder and GpsLocationFinder.
 * 
 * @see com.nokia.example.attractions.location.CellIdLocationFinder
 * @see com.nokia.example.attractions.location.GpsLocationFinder
 */
public abstract class AbstractLocationFinder
    extends LocationFinder {

    protected int interval = 60000;
    private LocationProvider lp;
    private long lastRequestTime = -1;
    private static boolean supportsFindingLocation = true;
    private final Object updateLock = new Object();
    private UpdateThread updateThread;

    /**
     * Get a location provider. 
     * @return location provider
     * @throws LocationException
     * @throws SecurityException
     */
    protected abstract LocationProvider getLocationProvider()
        throws LocationException, SecurityException;

    /**
     * @see LocationFinder#init(com.nokia.example.attractions.location.LocationFinder.Listener) 
     */
    protected final void init(Listener listener)
        throws InitializationException, SecurityException {
        super.init(listener);
        LocationProvider lp = null;
        try {
            lp = getLocationProvider();
        }
        catch (LocationException se) {
            throw new InitializationException(se.getMessage());
        }
        if (lp == null) {
            throw new InitializationException("Provider not found");
        }
        this.lp = lp;
    }

    /**
     * @see LocationFinder#start(int) 
     */
    public synchronized final void start() {
        if (supportsFindingLocation && updateThread == null) {
            updateThread = new UpdateThread();
            updateThread.start();
        }
    }

    /**
     * @see LocationFinder#quit() 
     */
    public synchronized final void quit() {
        if (updateThread != null) {
            updateThread.cancel();
            updateThread = null;
        }
    }

    private void requestLocation() {
        synchronized (updateLock) {
            if (System.currentTimeMillis() >= lastRequestTime + interval) {
                lastRequestTime = System.currentTimeMillis();
                try {
                    QualifiedCoordinates coordinates =
                        lp.getLocation(-1).getQualifiedCoordinates();
                    if (coordinates != null) {
                        double lat = coordinates.getLatitude();
                        double lon = coordinates.getLongitude();
                        int accuracy = (int) coordinates.getHorizontalAccuracy();
                        accuracy = Math.max(accuracy, 10);
                        listener.newLocation(lat, lon, accuracy);
                    }
                }
                catch (LocationException e) {
                    if(shouldQuit(e)) {
                        supportsFindingLocation = false;
                        quit();
                    }
                }
                catch (InterruptedException e) {
                }
                catch (SecurityException e) {
                    supportsFindingLocation = false;
                    quit();
                }
            }
        }
    }
    
    /**
     * @param e
     * @return true if should quit because location provider doesn't work 
     */
    protected abstract boolean shouldQuit(LocationException e);

    private class UpdateThread
        extends Thread {

        private static final long MIN_DELAY = 5000;
        private volatile boolean running = true;

        public void run() {
            while (running) {
                requestLocation();
                try {
                    sleep(Math.max(interval + lastRequestTime
                        - System.currentTimeMillis(), MIN_DELAY));
                }
                catch (InterruptedException e) {
                }
            }
        }

        public void cancel() {
            running = false;
        }
    }
}
