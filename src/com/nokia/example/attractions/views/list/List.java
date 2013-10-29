/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.views.list;

import com.nokia.example.attractions.Main;
import com.nokia.example.attractions.views.ViewMaster;
import com.nokia.example.attractions.Visual;
import com.nokia.example.attractions.utils.UIUtils;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/**
 * Custom UI control for list view.
 * <P>
 * Provides a cursor for focus and a scrollbar for visualization.
 * Uses a ListDrawer based class for drawing each line of the list.
 */
public class List {

    private final static int SCROLLBAR_WIDTH = 4;
    private int focusedRowIndex = -1;
    private Listener listener;
    private Drawer drawer;
    private int x;  // top left x coordinage
    private int y;  // top left y coordinate
    private int width;  // width for the list
    private int height;  // height for the list
    Vector data = null;
    private int translateY = 0;  // y-coordinate of the top of visible area
    private volatile boolean refreshListHeight = false;
    private int listHeight = 0;
    private int bottomPadding = 0;
    private final ViewMaster view;

    protected List() {
        view = ViewMaster.getInstance();
    }

    /**
     * Creates a list object. If Gesture API is supported returns a list 
     * which uses gestures.  
     * 
     * @param view ViewMaster object
     * @param drawer Drawer for the list items
     * @param listener Listener which is notified when an item is selected
     * @return the list object
     */
    public static List getList(Drawer drawer,
        Listener listener) {
        List list = null;
        try {
            Class.forName("com.nokia.mid.ui.frameanimator.FrameAnimator");
            Class.forName("com.nokia.mid.ui.gestures."
                + "GestureRegistrationManager");
            Class c = Class.forName("com.nokia.example.attractions.views.list."
                + "GestureList");
            list = (List) c.newInstance();
        }
        catch (Exception e) {
            list = Main.isS60Phone() ? new CompatibleList() : new List();
        }
        list.drawer = drawer;
        list.listener = listener;
        return list;
    }

    /**
     * Stops all animations the list might be showing
     */
    public void disable() {
        stopScrollAnimation();
    }

    /**
     * Enables list to show animations
     */
    public void enable() {
    }

    /**
     * This methods is called when the position of the list on the screen 
     * changes.
     * 
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public void resize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * @return width of the area for drawing content
     */
    public final int getContentWidth() {
        return width - SCROLLBAR_WIDTH;
    }

    /**
     * Sets the data to be displayed.
     * @param data
     */
    public final void setData(Vector data) {
        if (this.data != null && data != null && this.data == data
            && this.data.size() == data.size()) {
            return;
        }

        reset();
        this.data = data;
        contentHeightChanged();
    }

    public final void reset() {
        translateY = 0;
        resetFocusedRowIndex();
    }

    /**
     * This method is called when the height of the content changes and 
     * the layout of the list should be updated.
     */
    public final void contentHeightChanged() {
        refreshListHeight = true;
    }

    /**
     * Returns the data for accessing.
     * @return
     */
    public final Vector getData() {
        return data;
    }

    /**
     * @return true if data is set
     */
    public final boolean hasData() {
        return data != null;
    }

    /**
     * Select the currently focused row.
     */
    public final void select() {
        if (isFocusedRowIndex() && listener != null) {
            listener.select(focusedRowIndex);
        }
    }

    /**
     * Paints the list.
     * @param g
     */
    public final void draw(Graphics g) {

        if (drawer == null) {
            return;
        }

        int yOffset = translateY;

        if (refreshListHeight || isFocusedRowIndex()) {
            refreshListHeight = false;
            bottomPadding = UIUtils.bottomPadding();
            listHeight = bottomPadding;
            if (data != null) {
                int heightSoFar = 0;
                int heightNext = 0;
                for (int i = 0, size = data.size(); i < size; i++) {
                    final int itemHeight = drawer.itemHeight();
                    heightSoFar = heightNext;
                    heightNext += itemHeight;
                    if (i == focusedRowIndex && itemHeight < height) {
                        if (heightSoFar + yOffset < 0) {
                            // Focused item is above the view -> move view up.
                            yOffset = -heightSoFar;
                        }
                        else if (heightNext + yOffset > height) {
                            // Focused item is below the view -> move view down.
                            yOffset = height - heightNext;
                        }
                    }
                }
                listHeight += heightNext;
            }
        }

        if (yOffset > 0) {
            // Trying to scroll beyond list start.
            yOffset = 0;
            stopScrollAnimation();
        }
        else if ((yOffset + listHeight) < height) {
            if (listHeight < height) {
                yOffset = 0;
            }
            else {
                // Trying to scroll beyond list end.
                yOffset = -listHeight + height;
            }
            stopScrollAnimation();
        }

        translateY = yOffset;

        int prevClipX = g.getClipX();
        int prevClipY = g.getClipY();
        int prevClipWidth = g.getClipWidth();
        int prevClipHeight = g.getClipHeight();

        // Ensure that text does not overlap outside our allocated area.
        g.setClip(x, y, width, height);

        // Background
        ViewMaster.drawBackground(g, x, y, width, height, false);

        // Row drawing loop
        if (data != null) {
            int heightSoFar = 0;
            int heightNext = 0;
            for (int i = 0, size = data.size(); i < size; i++) {
                heightSoFar = heightNext;
                heightNext += drawer.itemHeight();

                if (heightNext + yOffset < 0) {
                    // Item is not visible -> skip drawing it.
                    continue;

                }
                else if (heightSoFar + yOffset > height) {
                    // Item would be drawn "under" the visible area 
                    // -> stop drawing.
                    break;
                }
                drawer.drawItem(data, g, i,
                    this.x, this.y + heightSoFar + yOffset,
                    width - SCROLLBAR_WIDTH, height, i == focusedRowIndex);
            }
        }

        // Detemine the need for scrollbar, and the height for it.
        if (listHeight > height) {
            int scrollBarHeight = (height - bottomPadding) * height / listHeight
                + 1;
            int yPos = (height - bottomPadding) * (-yOffset) / listHeight;
            g.setColor(Visual.LIST_SCROLLBAR_COLOR);
            g.fillRect(width - SCROLLBAR_WIDTH, yPos + this.y,
                SCROLLBAR_WIDTH, scrollBarHeight);
        }

        g.setClip(prevClipX, prevClipY, prevClipWidth, prevClipHeight);
    }

    protected final void addTranslateY(int delta) {
        translateY += delta;
    }

    protected final int getTranslateY() {
        return translateY;
    }

    protected final int itemHeight() {
        return drawer.itemHeight();
    }

    protected final void resetFocusedRowIndex() {
        focusedRowIndex = -1;
        view.draw();
    }

    protected final void setFocusedRowIndex(int index) {
        focusedRowIndex = index;
    }

    protected final boolean isFocusedRowIndex() {
        return focusedRowIndex > -1;
    }

    /**
     * Move cursor up one row.
     */
    public final void focusUp() {
        focusedRowIndex--;
        if (data == null || data.isEmpty()) {
            resetFocusedRowIndex();
        }
        else if (focusedRowIndex < 0) {
            focusedRowIndex = 0;
        }
    }

    /**
     * Move cursor down one row.
     */
    public final void focusDown() {
        focusedRowIndex++;
        if (data == null || data.isEmpty()) {
            resetFocusedRowIndex();
        }
        else if (focusedRowIndex >= data.size()) {
            focusedRowIndex = data.size() - 1;
        }
    }

    /**
     * Scroll up one fourth of the view height
     */
    public final void scrollUp() {
        translateY += height / 4;
    }

    /**
     * Scroll down one fourth of the view height
     */
    public final void scrollDown() {
        translateY -= height / 4;
    }

    /**
     * Stops scrolling animation.
     */
    protected void stopScrollAnimation() {
    }

    /**
     * Interface for notifying that a row is selected.
     */
    public static interface Listener {

        /**
         * A row was selected.
         * @param focusedRowIndex 
         */
        void select(int focusedRowIndex);
    }

    /**
     * An interface for rendering the rows in a list.
     */
    public static interface Drawer {

        /**
         * Returns the height of one list item (ie. row) in pixels.
         * @return
         */
        int itemHeight();

        /**
         * Draws one item.
         * @param data Data vector
         * @param g Graphics context to use for drawing
         * @param itemIndex Item to draw from the data vector
         * @param x Top left x coordinate of the drawing area
         * @param y Top left y coordinate of the drawing area
         * @param width Width of the drawing area
         * @param height Height of the drawing area
         * @param focused true if this item is currently focused 
         * (ie. a highlight is required)
         */
        void drawItem(Vector data, Graphics g, int itemIndex, int x, int y,
            int width, int height, boolean focused);
    }
}
