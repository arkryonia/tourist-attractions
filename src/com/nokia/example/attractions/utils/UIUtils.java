/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.utils;

import javax.microedition.lcdui.Command;

/**
 * UI utilities.
 */
public class UIUtils {

    public static final int EXIT = 0;
    public static final int BACK = 1;
    public static final int MAP = 2;
    public static final int GUIDES = 3;
    public static final int BUY_GUIDES = 4;
    public static final int ABOUT = 5;
    public static final int HELP = 6;
    public static final int POLICY = 7;
    public static final int SETTINGS = 8;
    public static final int OPEN = 9;
    public static final int BUY = 10;
    public static final int ACCEPT = 11;
    public static final int CANCEL = 12;
    public static final int CHANGE = 13;
    public static final int SAVE = 14;
    public static final int LOGIN = 15;    
    private static UIUtils instance;

    private static UIUtils getInstance() {
        if (instance == null) {
            try {
                Class.forName("com.nokia.mid.ui.IconCommand");
                Class.forName("com.nokia.mid.ui.CategoryBar");
                Class.forName("com.nokia.mid.ui.VirtualKeyboard");
                Class clazz = Class.forName("com.nokia.example.attractions."
                    + "utils.FtUIUtils");
                instance = (UIUtils) clazz.newInstance();
            }
            catch (Exception e) {
                instance = new UIUtils();
            }
        }
        return instance;
    }

    protected UIUtils() {
    }

    /**
     * Initializes user interface.
     */
    public static void init() {
        getInstance().initialize();
    }
    
    /**
     * Creates commands.
     * @param command
     * @return 
     */
    public static Command createCommand(int command) {
        return getInstance().newCommand(command);
    }

    /**
     * @return padding that should be added to all scrollable views
     */
    public static int bottomPadding() {
        return getInstance().getBottomPadding();
    }

    /**
     * Subclasses should override this.
     */
    protected void initialize() {
    }

    /**
     * Creates commands.
     * Subclasses should override this.
     * @param command
     * @return 
     */
    protected Command newCommand(int command) {
        Command result;
        switch (command) {
            case EXIT:
                result = new Command("Exit", Command.EXIT, 1);
                break;
            case BACK:
                result = new Command("Back", Command.BACK, 1);
                break;
            case MAP:
                result = new Command("Map", Command.SCREEN, 1);
                break;
            case GUIDES:
                result = new Command("Guides", Command.SCREEN, 2);
                break;
            case ABOUT:
                result = new Command("About", Command.HELP, 3);
                break;
            case HELP:
                result = new Command("Help", Command.HELP, 3);
                break;
            case POLICY:
                result = new Command("Policy", Command.HELP, 3);
                break;
            case SETTINGS:
                result = new Command("Settings", Command.HELP, 3);
                break;
            case OPEN:
                result = new Command("Open", Command.OK, 1);
                break;
            case ACCEPT:
                result = new Command("Accept", Command.OK, 1);
                break;
            case CANCEL:
                result = new Command("Cancel", Command.CANCEL, 1);
                break;
            case CHANGE:
                result = new Command("Change", Command.OK, 1);
                break;
            case SAVE:
                result = new Command("Save", Command.OK, 2);
                break;
            case LOGIN:
                result = new Command("Login", Command.OK, 1);
                break;
            default:
                result = null;
                break;
        }
        return result;
    }

    /**
     * Subclasses should override this.
     * @return padding that should be added to all scrollable views
     */
    protected int getBottomPadding() {
        return 0;
    }
}
