/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.views.list;

import com.nokia.example.attractions.utils.ImageLoader;
import com.nokia.example.attractions.models.Attraction;
import com.nokia.example.attractions.views.ViewMaster;
import com.nokia.example.attractions.Visual;
import java.io.IOException;
import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * A drawer class for rendering the rows in the list of attractions.
 */
public final class AttractionDrawer
    extends AbstractDrawer {

    public AttractionDrawer(ViewMaster view, Image defaultThumbnailIcon,
        Hashtable cache) {
        super(view, defaultThumbnailIcon, cache);
    }

    protected final String getThumbnailUrl(Object item) {
        return ((Attraction) item).getThumbnailUrl();
    }

    protected final void drawText(Graphics g, Object item, int x, int y,
        int width, int height) {
        Attraction attraction = (Attraction) item;

        y += itemHeight() / 2; // center
        g.setColor(Visual.LIST_PRIMARY_COLOR);
        g.setFont(Visual.SMALL_BOLD_FONT);

        if (attraction.getDistance() == null) {
            g.drawString(attraction.getName(), x, y + g.getFont().getHeight()
                / 2,
                Graphics.BOTTOM | Graphics.LEFT);
        }
        else {
            g.drawString(attraction.getName(), x, y,
                Graphics.BOTTOM | Graphics.LEFT);
            g.setFont(Visual.SMALL_FONT);
            g.drawString(attraction.getDistance(), x, y,
                Graphics.TOP | Graphics.LEFT);
        }

        try {
            Image mapMarker = ImageLoader.getInstance().loadMapMarker(
                attraction.getId(), "/location.png", cache);
            x += width - 2;
            g.drawImage(mapMarker, x, y, Graphics.VCENTER | Graphics.RIGHT);
        }
        catch (IOException e) {
        }
    }
}
