/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.views;

import com.nokia.example.attractions.views.list.List;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Abstract view for scrollable content views.
 */
public abstract class AbstractScrollableView
    extends BaseView {

    private static final int MARGIN = 4;
    private List list;
    private int contentHeight;
    private Image buffer = null;
    private final Object bufferLock = new Object();

    /**
     * Constructor
     * @param viewId 
     */
    public AbstractScrollableView() {
        list = List.getList(new List.Drawer() {

            public int itemHeight() {
                return contentHeight;
            }

            public void drawItem(Vector data, Graphics g, int itemIndex,
                int x, int y, int width, int height, boolean focused) {
                drawContent(g, x, y, width, height);
            }
        }, null);
        Vector contentDummy = new Vector();
        contentDummy.addElement(new Object());
        list.setData(contentDummy);
    }

    /**
     * @see BaseView#activate() 
     */
    public void activate() {
        viewMaster.setTitle(getTitle());
        list.enable();

        refreshContentSize();
        super.activate();
    }

    protected final void resetScrollBar() {
        list.reset();
    }

    /**
     * Title of the view
     * @return title
     */
    protected abstract String getTitle();

    /**
     * Refresh content size and redraw
     */
    protected final void refreshView() {
        if (isActive()) {
            refreshContentSize();
            viewMaster.draw();
        }
    }

    /**
     * @see BaseView#resize(int, int, int, int)   
     */
    public void resize(int x, int y, int width, int height) {
        super.resize(x, y, width, height);
        list.resize(x, y, width, height);
        refreshView();
    }

    /**
     * @see BaseView#deactivate()  
     */
    public void deactivate() {
        super.deactivate();
        list.disable();
    }

    /**
     * @see BaseView#draw(javax.microedition.lcdui.Graphics) 
     */
    public final void draw(final Graphics g) {
        if (!isActive()) {
            return;
        }
        list.draw(g);
    }

    private void refreshContentSize() {
        contentHeight = getContentHeight();
        synchronized (bufferLock) {
            buffer = null;
        }
        list.contentHeightChanged();
    }

    /**
     * @return width of the content area
     */
    protected final int getContentWidth() {
        return list.getContentWidth() - 2 * MARGIN;
    }

    /**
     * Subclasses need to provide height for the content.
     * @return content height
     */
    protected abstract int getContentHeight();

    protected void drawContent(final Graphics g, int x, int y, int width,
        int height) {
        ViewMaster.drawBackground(g, x, y, width, height, false);

        synchronized (bufferLock) {
            if (buffer == null) {
                refreshBuffer();
            }
            g.drawImage(buffer, x + MARGIN, y, Graphics.TOP | Graphics.LEFT);
        }
    }

    private void refreshBuffer() {
        int w = getContentWidth();
        int h = getBufferHeight();
        buffer = Image.createImage(w, h);
        Graphics cg = buffer.getGraphics();
        drawBuffer(cg);
    }

    protected int getBufferHeight() {
        return contentHeight;
    }

    protected abstract void drawBuffer(Graphics g);

    public void keyPressed(int keyCode) {
        handleKey(keyCode);
    }

    public void keyRepeated(int keyCode) {
        handleKey(keyCode);
    }

    private void handleKey(int keyCode) {
        if (!isActive()) {
            return;
        }
        switch (viewMaster.getGameAction(keyCode)) {
            case ViewMaster.UP:
                list.scrollUp();
                viewMaster.forceDraw();
                break;
            case ViewMaster.DOWN:
                list.scrollDown();
                viewMaster.forceDraw();
                break;
            default:
                break;
        }
    }
}
