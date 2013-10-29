/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.views;

import javax.microedition.lcdui.Graphics;

/**
 * View to display the map
 */
public final class MapView
    extends BaseView {

    MapView() {
    }

    public final void draw(final Graphics g) {
        drawLoading(g);
    }

    public final void activate() {
        super.activate();
        viewMaster.setTitle("Map");
        viewMaster.addCommand(backCmd);

        new Thread() {

            public void run() {
                openMap();
            }
        }.start();
    }

    private synchronized void openMap() {
        try {
            midlet.initMap();
            if (isActive()) {
                midlet.initMapMarkers();
            }
            if (isActive()) {
                midlet.refreshCurrentPositionOnMap();
            }
            if (isActive()) {
                midlet.openMap(data.getCurrentAttraction());
            }
        }
        catch (SecurityException e) {
            showAlert(e.toString(), "Map failure",
                "No permissions to use map.");
            if (isActive()) {
                viewMaster.setPreviousView();
            }
        }
        catch (Exception e) {
            showAlert(e.toString(), "Map failure", e.getMessage());
            if (isActive()) {
                viewMaster.setPreviousView();
            }
        }
    }
}
