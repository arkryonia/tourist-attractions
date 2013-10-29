/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions;

import com.nokia.example.attractions.location.LocationFinder;
import com.nokia.example.attractions.models.Attraction;
import com.nokia.example.attractions.map.TouristMap;
import com.nokia.example.attractions.models.Guide;
import com.nokia.example.attractions.utils.UIUtils;
import com.nokia.example.attractions.views.ViewMaster;
import com.nokia.maps.common.ApplicationContext;
import com.nokia.maps.common.GeoCoordinate;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

/**
 * Main MIDlet
 */
public final class Main
    extends MIDlet {

    private static Main instance;
    private Display display = null;
    private SplashCanvas splashCanvas;
    private ViewMaster viewMaster;
    private TouristMap touristMap;
    private final Object mapLock = new Object();
    private LocationFinder locationFinder;
    private static final String APP_ID = "rdDjsQ0xruTZCiwWjo01"; // needed to use the API
    private static final String APP_TOKEN = "EdMChGcbrECxiMbgCxS_sw"; // needed to use the API
    
    /**
     * @see MIDlet#startApp() 
     */
    public final void startApp() {
        if (display == null) {
            instance = this;
            ApplicationContext.getInstance().setAppID(APP_ID);
            ApplicationContext.getInstance().setToken(APP_TOKEN);
            display = Display.getDisplay(this);

            // Set up the first view of the application.
            splashCanvas = SplashCanvas.getInstance();
            display.setCurrent(splashCanvas);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                public void run() {
                    initGuides();
                    initViewMaster();
                    initLocationFinder();
                }
            }, 100);
            timer.schedule(new TimerTask() {

                public void run() {
                    closeSplash();
                }
            }, 1000); // minimum time the splash is shown
        }
    }

    /**
     * Loads the guides
     */
    private void initGuides() {
        DataModel data = DataModel.getInstance();
        data.loadGuides();
        if (data.getGuides().isEmpty()) {
            Guide helsinki = new Guide();
            helsinki.setId("helsinki");
            helsinki.setUrl("/guides/helsinki/guide.xml");
            helsinki.setImageUrl("/guides/helsinki/guide.png");
            helsinki.setCity("Helsinki Highlights");
            data.getGuides().addElement(helsinki);
            data.setCurrentGuideIndex(0);
        }
    }

    private synchronized void initLocationFinder() {
        if (locationFinder == null) {
            locationFinder =
                LocationFinder.getFinder(new LocationFinder.Listener() {

                public void newLocation(double lat, double lon, int accuracy) {
                    DataModel.getInstance().setCurrentPosition(new GeoCoordinate(
                        lat, lon, 0), accuracy);
                    refreshCurrentPositionOnMap();
                    if (viewMaster != null) {
                        viewMaster.draw();
                    }
                }
            });
        }
        refreshLocationFinder();
    }

    private void initViewMaster() {
        UIUtils.init();
        viewMaster = ViewMaster.getInstance();
    }

    public void callSerially(Runnable r) {
        display.callSerially(r);
    }

    private void closeSplash() {
        viewMaster.showAttractionsView();
        display.setCurrent(viewMaster);
    }

    private synchronized void startLocationFinder() {
        if (locationFinder != null) {
            locationFinder.start();
        }
    }

    private synchronized void stopLocationFinder() {
        if (locationFinder != null) {
            locationFinder.quit();
        }
    }

    /**
     * @see MIDlet#pauseApp() 
     */
    public final void pauseApp() {
    }

    /**
     * @see MIDlet#destroyApp(boolean) 
     */
    public final void destroyApp(boolean unconditional) {
        display = null;
    }

    /**
     * @return MIDlet object
     */
    public static Main getInstance() {
        return instance;
    }

    /**
     * Close the MIDlet.
     */
    public final void commandExit() {
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Refresh the location finder.
     */
    public final void refreshLocationFinder() {
        if (!splashCanvas.isHidden()
            || !(viewMaster == null || viewMaster.isHidden())
            || !(touristMap == null || touristMap.isHidden())) {
            startLocationFinder();
        }
        else {
            stopLocationFinder();
        }
    }

    /**
     * Initializes map canvas.
     */
    public final void initMap() {
        synchronized (mapLock) {
            if (touristMap == null) {
                // Get your own app_id and token by registering at
                // https://api.developer.nokia.com/ovi-api/ui/registration
                //ApplicationContext.getInstance().setAppID(...);
                //ApplicationContext.getInstance().setToken(...);
                touristMap = new TouristMap(display);
            }
        }
    }

    /**
     * Initializes attractions on the map.
     */
    public final void initMapMarkers() {
        synchronized (mapLock) {
            touristMap.addMarkers();
        }
    }

    /**
     * Refresh the user's current position on the map.
     */
    public final void refreshCurrentPositionOnMap() {
        synchronized (mapLock) {
            if (touristMap == null || touristMap.isHidden()) {
                return;
            }
            touristMap.refreshCurrentPositionCircle();
        }
    }

    /**
     * Pans the map to the position of the attraction and opens the map.
     * If attraction is null the map pans to the users position.
     * @param attr 
     */
    public final void openMap(Attraction attr) {
        synchronized (mapLock) {
            touristMap.refreshCurrentPositionCircle();
            if (attr == null) {
                touristMap.goToMe();
            }
            else {
                touristMap.goToAttraction(attr);
            }
            display.setCurrent(touristMap);
        }
    }

    /**
     * Close the map.
     */
    public final void closeMap() {
        display.setCurrent(viewMaster);
        viewMaster.setPreviousView();
    }

    /**
     * Show alert message on the screen.
     * @param title
     * @param alertText
     * @param type 
     */
    public final void showAlertMessage(String title, String alertText,
        AlertType type) {
        Alert alert = new Alert(title, alertText, null, type);
        display.setCurrent(alert, display.getCurrent());
    }

    /**
     * @return name of the MIDlet.
     */
    public final String getName() {
        return getAppProperty("MIDlet-Name");
    }

    /**
     * @return vendor of the MIDlet.
     */
    public final String getVendor() {
        return getAppProperty("MIDlet-Vendor");
    }

    /**
     * @return version of the MIDlet.
     */
    public final String getVersion() {
        return getAppProperty("MIDlet-Version");
    }

    public static boolean isS60Phone() {
        String platform = System.getProperty("microedition.platform");
        if (platform == null) {
            platform = "";
        }
        if (platform.indexOf("sw_platform=S60") > 0) {
            return true;
        }
        if (platform.indexOf("/S60_") > 0) {
            return true;
        }
        try {
            Class.forName("com.symbian.gcf.NativeInputStream");
            return true;
        }
        catch (ClassNotFoundException e) {
        }

        return false;
    }
}
