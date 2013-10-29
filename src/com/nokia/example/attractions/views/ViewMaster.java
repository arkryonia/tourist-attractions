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
import com.nokia.example.attractions.Main;
import com.nokia.example.attractions.utils.ImageLoader;
import com.nokia.example.attractions.utils.Sleeper;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.Sprite;

/**
 * Class for handling application views. Handles view switching and drawing.
 * Displaying is done using GameCanvas.
 */
public final class ViewMaster
    extends GameCanvas
    implements CommandListener {

    private static ViewMaster instance = null;
    private BaseView detailsView;
    private BaseView attractionsView;
    private BaseView guidesView;
    private BaseView buyGuidesView;
    private BaseView mapView;
    private BaseView aboutView;
    private BaseView helpView;
    private Image defaultThumbnailIcon;
    private Sprite defaultLoaderSprite;
    private BaseView activeView = null;
    private final Vector viewStack = new Vector();
    private final Sleeper drawLock = new Sleeper();
    private volatile boolean hidden = true;
    private volatile boolean canPaint = false;
    private volatile boolean refreshScreen = false;
    private PointerEventListener pointerEventListener = null;

    private ViewMaster() {
        super(false);
        this.setFullScreenMode(false);
        setTitle("");
    }

    /**
     * @return ViewMaster singleton
     */
    public static ViewMaster getInstance() {
        if (instance == null) {
            instance = new ViewMaster();
            instance.initialize();
        }
        return instance;
    }

    /**
     * Initializes the singleton.
     */
    private void initialize() {
        this.setCommandListener(this);
        try {
            defaultThumbnailIcon = ImageLoader.getInstance().loadImage(
                "/thumbnail.png", null);
        }
        catch (IOException e) {
        }
        try {
            Image i = ImageLoader.getInstance().loadImage("/loader_content.png",
                null);
            defaultLoaderSprite =
                new Sprite(i, i.getWidth() / 24, i.getHeight());
        }
        catch (IOException e) {
        }
        attractionsView = new AttractionsView();
        detailsView = new DetailsView();
        mapView = new MapView();
        guidesView = new GuidesView();
        aboutView = new AboutView();
        helpView = new HelpView();
    }

    public final boolean isHidden() {
        return hidden;
    }

    public final boolean hasBuyGuidesView() {
        return buyGuidesView != null;
    }

    public final void showAboutView() {
        setView(aboutView);
    }

    public final void showAttractionsView() {
        setView(attractionsView);
    }

    public final void showBuyGuidesView() {
        setView(buyGuidesView);
    }

    public final void showDetailsView() {
        setView(detailsView);
    }

    public final void showGuidesView() {
        setView(guidesView);
    }

    public final void showHelpView() {
        setView(helpView);
    }

    public final void showMapView() {
        setView(mapView);
    }

    public final Sprite getDefaultLoaderSprite() {
        return defaultLoaderSprite;
    }

    public final Image getDefaultThumbnailIcon() {
        return defaultThumbnailIcon;
    }

    /**
     * @see GameCanvas#showNotify() 
     */
    protected final void showNotify() {
        hidden = false;
        Main.getInstance().refreshLocationFinder();

        // S60 doesn't call sizeChanged on start-up so it has to be called here.
        sizeChanged(getWidth(), getHeight());

        draw();
        final Graphics g = getGraphics();
        new Thread() {

            public void run() {
                while (!hidden) {
                    if (refreshScreen && canPaint) {
                        refreshScreen = false;
                        if (activeView != null) {
                            activeView.draw(g);
                            flushGraphics();
                        }
                    }
                    drawLock.sleep(50);
                }
            }
        }.start();
        new Thread() {

            public void run() {
                try {
                    sleep(500);
                    draw();
                }
                catch (InterruptedException e) {
                }
            }
        }.start();
    }

    /**
     * @see GameCanvas#paint(javax.microedition.lcdui.Graphics) 
     */
    public final void paint(Graphics g) {
        canPaint = true;
        forceDraw();
    }

    /**
     * @see GameCanvas#hideNotify() 
     */
    protected final void hideNotify() {
        hidden = true;
        canPaint = false;
        Main.getInstance().refreshLocationFinder();
    }

    /**
     * @see GameCanvas#sizeChanged(int, int) 
     */
    protected final void sizeChanged(int width, int height) {
        attractionsView.resize(0, 0, width, height);
        guidesView.resize(0, 0, width, height);
        if (buyGuidesView != null) {
            buyGuidesView.resize(0, 0, width, height);
        }
        detailsView.resize(0, 0, width, height);
        mapView.resize(0, 0, width, height);
        aboutView.resize(0, 0, width, height);
        helpView.resize(0, 0, width, height);
        draw();
    }

    /**
     * Sets a view to be displayed. Transitions the previous view away.
     * @param view
     */
    private void setView(final BaseView view) {
        Main.getInstance().callSerially(new Runnable() {

            public void run() {
                if (activeView != null) {
                    if (activeView == view) {
                        // Trying to activate a view that's already active.
                        return;
                    }
                    else {
                        activeView.deactivate();
                    }
                }

                activeView = view;
                synchronized (viewStack) {
                    // Remove the view from viewstack.
                    viewStack.removeElement(activeView);
                    // Push the view into viewstack.
                    viewStack.addElement(activeView);
                }
                activeView.activate();
                forceDraw();
            }
        });
    }

    /**
     * Activate the previous view from the stack.
     * At least two views need to be there.
     */
    public final void setPreviousView() {
        BaseView view = null;
        synchronized (viewStack) {
            if (viewStack.size() >= 2) {
                // First remove and forget the current view.
                viewStack.removeElement(viewStack.lastElement());
                // Get the 2nd to last, which we want to activate.
                view = (BaseView) viewStack.lastElement();
            }
        }
        // Activate the view.
        if (view != null) {
            setView(view);
        }
    }

    /**
     * Draws the active view immediately
     */
    public final void forceDraw() {
        draw();
        drawLock.wakeup();
    }

    /**
     * Draws the active view.
     */
    public final void draw() {
        refreshScreen = true;
    }

    /**
     * Key pressed event handler.
     * @param keyCode
     */
    protected final void keyPressed(int keyCode) {
        if (activeView == null) {
            return;
        }
        activeView.keyPressed(keyCode);
    }

    /**
     * Key repeated event handler.
     * @param keyCode
     */
    protected final void keyRepeated(int keyCode) {
        if (activeView == null) {
            return;
        }
        activeView.keyRepeated(keyCode);
    }

    /**
     * Key released event handler.
     * @param keyCode
     */
    protected final void keyReleased(int keyCode) {
        if (activeView == null) {
            return;
        }
        activeView.keyReleased(keyCode);
    }

    /**
     * Utility to draw the background image. 
     * This is used to draw the background and list highlights.
     * @param g
     * @param x
     * @param y
     * @param width
     * @param height
     * @param highlight Set to true to have a highlighted effect.
     */
    public static void drawBackground(Graphics g, int x, int y, int width,
        int height, boolean highlight) {
        g.setColor(Visual.BACKGROUND_COLOR);
        g.fillRect(x, y, width, height);

        if (highlight) {
            drawDimmedBackground(g, x, y, width, height);
        }
    }

    /**
     * Render a part of the background dimmed. 
     * Used for drawing list focus cursor.
     * @param g
     * @param x
     * @param y
     * @param width
     * @param height
     */
    private static void drawDimmedBackground(Graphics g, int x, int y,
        int width, int height) {
        // This would be more optimized if the alpha mask were pregenerated.
        int[] pixels = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            //pixels[i] = 0xaa000000;
            pixels[i] = 0x55000000;
        }

        g.drawRGB(pixels, 0, width, x, y, width, height, true);
        pixels = null;
    }

    /**
     * @see GameCanvas#pointerPressed(int, int) 
     */
    protected final void pointerPressed(int x, int y) {
        if (pointerEventListener != null) {
            pointerEventListener.pointerPressed(x, y);
        }
    }

    /**
     * @see GameCanvas#pointerDragged(int, int) 
     */
    protected final void pointerDragged(int x, int y) {
        if (pointerEventListener != null) {
            pointerEventListener.pointerDragged(x, y);
        }
    }

    /**
     * @see GameCanvas#pointerReleased(int, int) 
     */
    protected final void pointerReleased(int x, int y) {
        if (pointerEventListener != null) {
            pointerEventListener.pointerReleased(x, y);
        }
    }

    /**
     * @see CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable) 
     */
    public final void commandAction(Command cmd, Displayable dsplbl) {
        if (activeView != null) {
            activeView.commandAction(cmd);
        }
    }

    /**
     * Set pointer event listener. Can be set to null.
     * @param listener pointer event listener
     */
    public final void setPointerEventListener(PointerEventListener listener) {
        pointerEventListener = listener;
    }

    /**
     * Interface for pointer event notifications
     */
    public interface PointerEventListener {

        void pointerPressed(int x, int y);

        void pointerDragged(int x, int y);

        void pointerReleased(int x, int y);
    }
}
