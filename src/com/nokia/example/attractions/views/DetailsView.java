/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.views;

import com.nokia.example.attractions.Visual;
import com.nokia.example.attractions.utils.ImageLoader;
import com.nokia.example.attractions.utils.TextWrapper;
import com.nokia.example.attractions.models.Attraction;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * View to display a single attraction.
 * Used when the user selects an attraction from the attraction view
 */
public final class DetailsView
    extends AbstractScrollableView {

    private Image image;
    private String imageUrl;
    private Attraction attraction;
    private Vector lines;
    private final Hashtable cache = new Hashtable();

    DetailsView() {
        super();
    }

    public final void activate() {
        if (attraction != data.getCurrentAttraction()) {
            resetScrollBar();
            attraction = data.getCurrentAttraction();
        }
        loadImage();
        super.activate();
        viewMaster.addCommand(backCmd);
        viewMaster.addCommand(mapCmd);
    }

    public final void deactivate() {
        super.deactivate();
        image = null;
        cache.clear();
    }

    protected final String getTitle() {
        return data.getCurrentAttraction().getName();
    }

    /**
     * @see BaseView#getScreenName() 
     */
    protected String getScreenName() {
        return super.getScreenName() + " - " + data.getCurrentAttraction().
            getName();
    }

    private void loadImage() {
        imageUrl = attraction.getImageUrl();
        final String url = imageUrl;
        if (!url.equals("")) {
            try {
                image = ImageLoader.getInstance().loadImage(url, cache);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    protected final int getContentHeight() {
        lines = TextWrapper.wrapTextToWidth(attraction.getDescription(),
            getContentWidth(), Visual.SMALL_FONT);
        int h = 0;
        if (image != null) {
            h += image.getHeight() + 4;
        }
        h += Visual.SMALL_BOLD_FONT.getHeight() + 4;
        h += lines.size() * Visual.SMALL_FONT.getHeight() + 4;
        return h;
    }

    protected int getBufferHeight() {
        return super.getBufferHeight()
            - (image == null ? 0 : image.getHeight() + 4);
    }

    protected void drawContent(Graphics g, int x, int y, int width, int height) {
        int x0 = x;
        int y0 = y;
        if (image != null) {
            g.drawImage(image, x0 + width / 2, y0, Graphics.TOP
                | Graphics.HCENTER);
            y0 += image.getHeight() + 4;
        }
        super.drawContent(g, x0, y0, width, height);
    }

    protected final void drawBuffer(Graphics g) {
        int w = getContentWidth();
        int h = getContentHeight();
        int x0 = 0;
        int y0 = 0;
        
        ViewMaster.drawBackground(g, x0, y0, w, h, false);

        g.setFont(Visual.SMALL_BOLD_FONT);
        g.setColor(Visual.LIST_SECONDARY_COLOR);
        g.drawString(attraction.getStreet(), x0, y0, Graphics.TOP
            | Graphics.LEFT);

        if (attraction.getDistance() != null) {
            g.drawString(attraction.getDistance(), w, y0, Graphics.TOP
                | Graphics.RIGHT);
        }

        y0 += Visual.SMALL_BOLD_FONT.getHeight() + 4;

        g.setFont(Visual.SMALL_FONT);
        g.setColor(Visual.LIST_PRIMARY_COLOR);

        for (int i = 0; i < lines.size(); i++) {
            g.drawString((String) lines.elementAt(i), x0, y0, Graphics.TOP
                | Graphics.LEFT);
            y0 += Visual.SMALL_FONT.getHeight();
        }
    }
}
