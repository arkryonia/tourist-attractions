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
import com.nokia.example.attractions.DataModel;
import com.nokia.example.attractions.utils.UIUtils;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

/**
 * Base class for views.
 */
public abstract class BaseView {

    private int x;  // position of the view on screen, x-coordinate
    private int y;  // position of the view on screen, y-coordinate
    private int width;  // available width on screen to be used
    private int height;  // available height on screen to be used
    protected final Main midlet = Main.getInstance();
    protected final ViewMaster viewMaster = ViewMaster.getInstance();
    protected final DataModel data = DataModel.getInstance();
    private boolean active = false;
    // common commands
    protected final Command exitCmd = UIUtils.createCommand(UIUtils.EXIT);
    protected final Command backCmd = UIUtils.createCommand(UIUtils.BACK);
    protected final Command mapCmd = UIUtils.createCommand(UIUtils.MAP);
    protected final Command guidesCmd = UIUtils.createCommand(UIUtils.GUIDES);
    protected final Command buyGuidesCmd = UIUtils.createCommand(
        UIUtils.BUY_GUIDES);
    protected final Command aboutCmd = UIUtils.createCommand(UIUtils.ABOUT);
    protected final Command helpCmd = UIUtils.createCommand(UIUtils.HELP);
    protected final Command loginCmd = UIUtils.createCommand(UIUtils.LOGIN);

    public boolean isActive() {
        return active;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Resize the view.
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
     * After this call the view will be painted on screen by calling draw().
     */
    public void activate() {
        active = true;
        logEvent("activated");
    }

    /**
     *  After this call the view should not do anything.
     */
    public void deactivate() {
        viewMaster.removeCommand(exitCmd);
        viewMaster.removeCommand(backCmd);
        viewMaster.removeCommand(mapCmd);
        viewMaster.removeCommand(guidesCmd);
        viewMaster.removeCommand(buyGuidesCmd);
        viewMaster.removeCommand(aboutCmd);
        viewMaster.removeCommand(helpCmd);
        viewMaster.removeCommand(loginCmd);
        active = false;
    }

    /**     
     * Draw view.
     */
    public void draw(final Graphics g) {
    }

    /**
     * Draw loading indicator.
     * @param g 
     */
    protected final void drawLoading(final Graphics g) {
        ViewMaster.drawBackground(g, x, y, width, height, false);
        g.setColor(Visual.LIST_PRIMARY_COLOR);
        g.setFont(Visual.MEDIUM_FONT);
        int x0 = x + width / 2;
        int y0 = y + height / 2 - g.getFont().getHeight() / 2;
        Sprite loader = viewMaster.getDefaultLoaderSprite();
        if (loader != null) {
            y0 += loader.getHeight() / 2;
            loader.nextFrame();
            loader.setPosition(x0 - loader.getWidth() / 2, y0
                - loader.getHeight());
            loader.paint(g);
            viewMaster.draw();
        }
        g.drawString("loading...", x0, y0, Graphics.TOP | Graphics.HCENTER);
    }

    /**
     * Empty implementation
     * Key press event.
     */
    public void keyPressed(int keyCode) {
    }

    /**
     * Empty implementation
     * Key repeated event.
     */
    public void keyRepeated(int keyCode) {
    }

    /**
     * Empty implementation.
     * Key released event.
     */
    public void keyReleased(int keyCode) {
    }

    /**
     * Common command actions
     * @param cmd 
     */
    public void commandAction(Command cmd) {
        if (cmd == exitCmd) {
            midlet.commandExit();
        }
        else if (cmd == backCmd) {
            viewMaster.setPreviousView();
        }
        else if (cmd == guidesCmd) {
            viewMaster.showGuidesView();
        }
        else if (cmd == mapCmd) {
            viewMaster.showMapView();
        }
        else if (cmd == aboutCmd) {
            viewMaster.showAboutView();
        }
        else if (cmd == helpCmd) {
            viewMaster.showHelpView();
        }
    }

    /**
     * Log an event
     * @param event 
     */
    protected void logEvent(final String event) {
    }

    /**
     * Log an error event
     * @param event 
     */
    protected void logErrorEvent(final String event) {
    }

    /**
     * @return name of the view for logging
     */
    protected String getScreenName() {
        final String className = this.getClass().getName();
        final int index = className.lastIndexOf('.');
        return index > -1 && index < className.length() ? className.substring(index
            + 1) : className;
    }

    /**
     * Show alert dialog and log an error event
     * @param event
     * @param title
     * @param message 
     */
    protected void showAlert(final String event, final String title,
        final String message) {
        logErrorEvent(event);
        midlet.showAlertMessage(title, message, AlertType.ERROR);
    }
}
