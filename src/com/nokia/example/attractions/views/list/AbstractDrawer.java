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
import com.nokia.example.attractions.views.ViewMaster;
import com.nokia.example.attractions.Visual;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * An abstract drawer class for rendering the rows in a list.
 */
public abstract class AbstractDrawer
    implements List.Drawer {

    // 42 is the thumbnail size, minimum height is 48
    private final int ROW_HEIGHT = Math.max(2 * Visual.SMALL_FONT.getHeight()
        + 2, 48);
    private final Image defaultThumbnail;
    private final ViewMaster view;
    protected final Hashtable cache;

    public AbstractDrawer(ViewMaster view, Image defaultThumbnailIcon,
        Hashtable cache) {
        this.view = view;
        this.defaultThumbnail = defaultThumbnailIcon;
        this.cache = cache;
    }

    public final int itemHeight() {
        return ROW_HEIGHT;
    }

    /**
     * Returns a thumbnail for a list item. Loads from network if not available,
     * after loading the main view is refreshed.
     * @param attraction
     * @return
     */
    private Image getThumbnail(Object item) {

        final String url = getThumbnailUrl(item);
        if (url.equals("")) {
            return null;
        }
        try {
            return ImageLoader.getInstance().loadImage(url, cache);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected abstract String getThumbnailUrl(Object item);

    public final void drawItem(Vector data, Graphics g, int itemIndex,
        final int x, final int y,
        final int width, final int height, boolean focused) {

        ViewMaster.drawBackground(g, x, y, width, ROW_HEIGHT, focused);

        Object item = data.elementAt(itemIndex);

        int x0 = x + 2; // margins
        int w0 = width;

        Image thumbnail = getThumbnail(item);
        if (thumbnail != null) {
            g.drawImage(thumbnail, x0, y + ROW_HEIGHT / 2,
                Graphics.VCENTER | Graphics.LEFT);
            x0 += thumbnail.getWidth() + 2; // margins
            w0 = width - thumbnail.getWidth() - 2; // margins
        }

        drawText(g, item, x0, y, w0, height);
    }

    protected abstract void drawText(Graphics g, Object item, int x, int y,
        int width, int height);
}
