/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.map;

import com.nokia.example.attractions.Main;
import com.nokia.example.attractions.utils.KeyCodes;
import com.nokia.example.attractions.Visual;
import com.nokia.example.attractions.utils.ImageLoader;
import com.nokia.maps.map.EventListener;
import com.nokia.maps.map.MapComponent;
import com.nokia.maps.map.MapDisplay;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Draws the back button on top of the map and listens for
 * when the user presses said button.
 */
public final class TouristMapOverlays
    implements MapComponent, EventListener {

    private MapDisplay mapDisplay;
    private int height, width;
    private Image back;
    private Image backFocus;
    private volatile boolean backHasFocus = false;

    /**
     * @see MapComponent#getId() 
     */
    public final String getId() {
        return "overlays";
    }

    /**
     * @see MapComponent#getVersion() 
     */
    public final String getVersion() {
        return "0.1";
    }

    /**
     * @see MapComponent#attach(com.nokia.maps.map.MapDisplay) 
     */
    public final void attach(MapDisplay md) {
        mapDisplay = md;
        height = mapDisplay.getHeight();
        width = mapDisplay.getWidth();
        try {
            back = ImageLoader.getInstance().loadImage("/icons/back.png", null);
            backFocus = ImageLoader.getInstance().loadImage("/icons/back_focus.png", null);
        }
        catch (IOException e) {
        }
    }

    /**
     * @see MapComponent#detach(com.nokia.maps.map.MapDisplay) 
     */
    public final void detach(MapDisplay md) {
        mapDisplay = null;
        back = null;
    }

    /**
     * @see MapComponent#paint(javax.microedition.lcdui.Graphics) 
     */
    public final void paint(Graphics g) {
        if (back != null && backFocus != null) {
            g.drawImage(backHasFocus ? backFocus : back, 
                width - Visual.SOFTKEY_MARGIN, height - Visual.SOFTKEY_MARGIN, 
                Graphics.RIGHT | Graphics.BOTTOM);
        }
    }

    /**
     * @see MapComponent#mapUpdated(boolean) 
     */
    public final void mapUpdated(boolean bln) {
    }

    /**
     * @see MapComponent#getEventListener() 
     */
    public final EventListener getEventListener() {
        return this;
    }

    /**
     * @see EventListener#keyPressed(int, int) 
     */
    public final boolean keyPressed(int keyCode, int i) {
        if (keyCode == KeyCodes.RIGHT_SOFTKEY) {
            backHasFocus = true;
            return true;
        }
        return false;
    }

    /**
     * @see EventListener#keyReleased(int, int) 
     */
    public final boolean keyReleased(int keyCode, int i) {
        backHasFocus = false;
        if (keyCode == KeyCodes.RIGHT_SOFTKEY) {
            Main.getInstance().closeMap();
            return true;
        }
        return false;
    }

    /**
     * @see EventListener#keyRepeated(int, int, int) 
     */
    public final boolean keyRepeated(int i, int i1, int i2) {
        return false;
    }

    /**
     * @see EventListener#pointerDragged(int, int) 
     */
    public final boolean pointerDragged(int x, int y) {
        if (backHasFocus) {
            backHasFocus = hitsBackButton(x, y);
            return true;
        }
        return false;
    }

    /**
     * @see EventListener#pointerPressed(int, int) 
     */
    public final boolean pointerPressed(int x, int y) {
        if (hitsBackButton(x, y)) {
            backHasFocus = true;
            return true;
        }
        return false;
    }

    /**
     * @see EventListener#pointerReleased(int, int) 
     */
    public final boolean pointerReleased(int x, int y) {
        if (backHasFocus && hitsBackButton(x, y)) {
            backHasFocus = false;
            Main.getInstance().closeMap();
            return true;
        }
        return false;
    }
    
    private boolean hitsBackButton(int x, int y) {
        //Check the coordinates to see if the back button was hit
        final int buttonSize = Math.max(width, height) / 8;
        return x > (width - buttonSize) && y > (height - buttonSize);
    }
}
