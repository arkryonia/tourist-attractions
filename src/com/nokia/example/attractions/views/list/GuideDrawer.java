/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.views.list;

import com.nokia.example.attractions.models.Guide;
import com.nokia.example.attractions.views.ViewMaster;
import com.nokia.example.attractions.Visual;
import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * A drawer class for rendering the rows in the list of bought guides.
 */
public class GuideDrawer
    extends AbstractDrawer {

    public GuideDrawer(ViewMaster view, Image defaultThumbnailIcon,
        Hashtable cache) {
        super(view, defaultThumbnailIcon, cache);
    }

    protected final String getThumbnailUrl(Object item) {
        return ((Guide) item).getImageUrl();
    }

    protected void drawText(Graphics g, Object item, int x, int y,
        int width, int height) {
        Guide guide = (Guide) item;

        y += itemHeight() / 2; // center
        g.setColor(Visual.LIST_PRIMARY_COLOR);
        g.setFont(Visual.MEDIUM_BOLD_FONT);
        y += g.getFont().getHeight() / 2;
        g.drawString(guide.getCity(), x, y, Graphics.BOTTOM | Graphics.LEFT);
    }
}
