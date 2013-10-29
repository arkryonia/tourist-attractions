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
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * View to display version and vendor of the application
 */
public final class AboutView
    extends BaseView {

    private final Font nameFont = Visual.SMALL_BOLD_FONT;
    private final Font vendorFont = Visual.SMALL_FONT;
    private final Font versionFont = Visual.SMALL_FONT;

    AboutView() {
    }

    public final void activate() {
        viewMaster.setTitle("About");
        super.activate();
        viewMaster.addCommand(backCmd);
    }

    public final void draw(final Graphics g) {
        if (!isActive()) {
            return;
        }
        drawText(g);
    }

    public final void drawText(final Graphics g) {
        ViewMaster.drawBackground(g, getX(), getY(), getWidth(), getHeight(),
            false);

        int x0 = getX() + getWidth() / 2;
        int y0 = getY() + getHeight() / 2 - vendorFont.getHeight() / 2;

        g.setColor(Visual.LIST_PRIMARY_COLOR);
        g.setFont(nameFont);
        g.drawString("" + midlet.getName(), x0, y0,
            Graphics.BOTTOM | Graphics.HCENTER);
        g.setFont(vendorFont);
        g.drawString("by " + midlet.getVendor(), x0, y0,
            Graphics.TOP | Graphics.HCENTER);
        y0 += vendorFont.getHeight();
        g.setFont(versionFont);
        g.drawString("Version " + midlet.getVersion(), x0, y0,
            Graphics.TOP | Graphics.HCENTER);
    }
}
