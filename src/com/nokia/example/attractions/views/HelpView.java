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
import com.nokia.example.attractions.utils.TextWrapper;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * View to display instructions for using this application
 */
public final class HelpView
    extends AbstractScrollableView {

    private static final String TEXT = "The main view of the application shows "
        + "a list of attractions in the selected guide. "
        + "You can view details of an attraction by tapping it.\n\n"
        + "From the attractions list you can open the map to show your "
        + "current position and if your position is not available the "
        + "center of the guide. By opening the map from the details of an "
        + "attraction, the location of the attraction is shown.\n\n"
        + "From the guides list you can open a different guide or buy "
        + "new guides.";
    private final Font font = Visual.SMALL_FONT;
    private Vector lines;

    HelpView() {
        super();
    }

    public final void activate() {
        super.activate();
        viewMaster.addCommand(backCmd);
    }

    protected final int getContentHeight() {
        lines = TextWrapper.wrapTextToWidth(TEXT, getContentWidth(), font);
        return lines.size() * font.getHeight() + 4;
    }

    protected final String getTitle() {
        return "Help";
    }

    protected final void drawBuffer(Graphics g) {
        int x0 = 0;
        int y0 = 0;
        g.setFont(font);
        g.setColor(Visual.LIST_PRIMARY_COLOR);

        for (int i = 0; i < lines.size(); i++) {
            g.drawString((String) lines.elementAt(i), x0, y0,
                Graphics.TOP | Graphics.LEFT);
            y0 += font.getHeight();
        }
    }
}
