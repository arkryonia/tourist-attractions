/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.map;

import com.nokia.example.attractions.DataModel;
import com.nokia.example.attractions.Main;
import com.nokia.example.attractions.models.Attraction;
import com.nokia.example.attractions.utils.ImageLoader;
import com.nokia.example.attractions.utils.UIUtils;
import com.nokia.maps.common.GeoCoordinate;
import com.nokia.maps.map.MapCanvas;
import com.nokia.maps.map.MapCircle;
import com.nokia.maps.map.MapMarker;
import com.nokia.maps.map.Point;
import com.nokia.mid.ui.gestures.GestureEvent;
import com.nokia.mid.ui.gestures.GestureInteractiveZone;
import com.nokia.mid.ui.gestures.GestureListener;
import com.nokia.mid.ui.gestures.GestureRegistrationManager;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;

/**
 * This class represents the map component of the application.
 */
public final class TouristMap
    extends MapCanvas
    implements CommandListener {

    private final static int CIRCLE_COLOR = 0x88CD0000;
    private final static double DEFAULT_ZOOM_LEVEL = 16.0;
    private DataModel dataModel = DataModel.getInstance();
    private MapCircle currentPositionCircle;
    private GeoCoordinate center;
    private boolean pinching;
    private Vector attractions = null;
    private Object gestureListener;
    private volatile boolean hidden = true;
    
    public TouristMap(Display d) {
        super(d);
        setFullScreenMode(true);

        map.setZoomLevel(DEFAULT_ZOOM_LEVEL, 0, 0);
        
        String keyboardType = System.getProperty("com.nokia.keyboard.type");
        if (keyboardType.equals("OnekeyBack")) {
            addCommand(UIUtils.createCommand(UIUtils.BACK));
            setCommandListener(this);
        }
        else {
            map.addMapComponent(new TouristMapOverlays());
        }
        
        try {
            Class c = Class.forName("com.nokia.mid.ui.gestures.GestureInteractiveZone");
            gestureListener = new GestureListener () {

                public void gestureAction(Object container, GestureInteractiveZone gestureInteractiveZone, GestureEvent ge) {
                    int eventType = ge.getType();
                    switch (eventType) {
                        case GestureInteractiveZone.GESTURE_RECOGNITION_START:
                            pinching = false;
                            center = map.getCenter();
                            break;
                        case GestureInteractiveZone.GESTURE_PINCH:
                            pinching = true;
                            int curPinchDistance = ge.getPinchDistanceCurrent();
                            int startingPinchDistance = ge
                                    .getPinchDistanceStarting();
                            int zoomNew = map.getZoomLevel()
                                    + (int) ((curPinchDistance - startingPinchDistance) / 100);
                            if (zoomNew > map.getMaxZoomLevel()) {
                                zoomNew = (int) map.getMaxZoomLevel();
                            }
                            if (zoomNew < map.getMinZoomLevel()) {
                                zoomNew = (int) map.getMinZoomLevel();
                            }
                            if (zoomNew != map.getZoomLevel()) {
                                map.setZoomLevel(zoomNew, 0, 0);
                                map.setCenter(center);
                            }                            
                            break;
                    }
                }
            };
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void pointerDragged(int x, int y) {
        if (!pinching) {
            super.pointerDragged(x, y);
        }
    }

    public void pointerReleased(int x, int y) {
        if (!pinching) {
            super.pointerReleased(x, y);
        }
    }
    
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @see MapCanvas#showNotify() 
     */
    protected final void showNotify() {
        super.showNotify();
        hidden = false;
        Main.getInstance().refreshLocationFinder();
        if (gestureListener != null) {
            try {
                GestureRegistrationManager.setListener(this, (GestureListener) gestureListener);
                GestureInteractiveZone gestureZone = new GestureInteractiveZone(
                        GestureInteractiveZone.GESTURE_RECOGNITION_START
                        | GestureInteractiveZone.GESTURE_PINCH);
                GestureRegistrationManager.register(this, gestureZone);
            } catch (IllegalArgumentException e) {
                // Gestures are not supported
                e.printStackTrace();
            }
        }
    }

    /**
     * @see MapCanvas#hideNotify() 
     */
    protected final void hideNotify() {
        hidden = true;
        Main.getInstance().refreshLocationFinder();
        super.hideNotify();
        if (gestureListener != null) {
            GestureRegistrationManager.unregisterAll(this);
        }
    }

    /**
     * Add the markers to the map.
     */
    public final void addMarkers() {
        Vector attrs = dataModel.getAttractions();
        if (attrs == attractions || attrs == null) {
            return;
        }
        attractions = attrs;
        //Remove all markers
        map.removeAllMapObjects();
        if (currentPositionCircle != null) {
            map.addMapObject(currentPositionCircle);
        }

        //Add a marker for each attraction
        for (int i = 0, size = attractions.size(); i < size; i++) {
            Attraction attr = (Attraction) attractions.elementAt(i);
            try {
                Image image = ImageLoader.getInstance().loadMapMarker(
                    attr.getId(), "/location.png", null);
                GeoCoordinate c = attr.getLocation();
                MapMarker marker = mapFactory.createMapMarker(c, image);
                marker.setAnchor(new Point(image.getWidth() / 2, image.
                    getHeight()));
                map.addMapObject(marker);
            }
            catch (IOException e) {
            }
        }
    }

    /**
     * Sets the center of the map to the center of the guide.
     */
    private void moveCenterToGuide() {
        if (dataModel.getCurrentGuide() != null) {
            map.setCenter(dataModel.getCurrentGuide().getCenter());
        }
    }

    /**
     * Center the map on a certain attraction
     * @param attr 
     */
    public final void goToAttraction(Attraction attr) {
        goToPosition(attr.getLocation());
    }

    /**
     * Center of the map to current position
     */
    public final void goToMe() {
        goToPosition(dataModel.getCurrentPosition());
    }

    /**
     * Center of the map to specified coordinates
     * @param pos 
     */
    private void goToPosition(GeoCoordinate pos) {
        if (pos != null) {
            map.setCenter(pos);
        }
        else {
            moveCenterToGuide();
        }
    }

    /**
     * Refresh the circle showing the user's current position.
     */
    public final void refreshCurrentPositionCircle() {
        GeoCoordinate currentPosition = dataModel.getCurrentPosition();
        if (currentPosition == null) {
            return;
        }
        int accuracy = dataModel.getAccuracy();
        if (currentPositionCircle == null) {
            currentPositionCircle = mapFactory.createMapCircle(accuracy,
                currentPosition);
            currentPositionCircle.setColor(CIRCLE_COLOR);
            map.addMapObject(currentPositionCircle);
        }
        else {
            currentPositionCircle.setCenter(currentPosition);
            currentPositionCircle.setRadius(accuracy);
        }
    }

    public void onMapUpdateError(String string, Throwable thrwbl, boolean bln) {
        Main.getInstance().closeMap();
        Main.getInstance().showAlertMessage("Map failure", string,
            AlertType.ERROR);
    }

    public synchronized void onMapContentComplete() {
    }
    
    /**
     * Common command actions
     * @param cmd 
     */
    public void commandAction(Command cmd, Displayable d) {
        if (cmd.getCommandType() == Command.BACK) {
            Main.getInstance().closeMap();
        }
    }    
}
