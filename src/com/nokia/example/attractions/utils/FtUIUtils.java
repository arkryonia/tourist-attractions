/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.utils;

import com.nokia.mid.ui.CategoryBar;
import com.nokia.mid.ui.IconCommand;
import com.nokia.mid.ui.VirtualKeyboard;
import java.io.IOException;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;

/**
 * UI utilities for Full Touch devices.
 */
public class FtUIUtils
    extends UIUtils {

    FtUIUtils() {
    }

    /**
     * @see UIUtils#initialize() 
     */
    protected void initialize() {
        super.initialize();
        VirtualKeyboard.hideOpenKeypadCommand(true);
    }

    /**
     * @see UIUtils#newCommand(int) 
     */
    protected Command newCommand(int command) {
        Command result;
        switch (command) {
            case EXIT:
                result = new IconCommand("Exit", Command.EXIT, 1,
                    IconCommand.ICON_BACK);
                break;
            case BACK:
                result = new IconCommand("Back", Command.BACK, 1,
                    IconCommand.ICON_BACK);
                break;
            case MAP:
                result = newCommand("Map", "/icons/map.png",
                    Command.SCREEN, 1);
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
                result = new IconCommand("Settings", Command.HELP, 3,
                    IconCommand.ICON_OPTIONS);
                break;
            case OPEN:
                result = new Command("Open", Command.OK, 1);
                break;
            case ACCEPT:
                result = newCommand("Accept", "/icons/ok.png",
                    Command.OK, 1);
                break;
            case CANCEL:
                result = newCommand("Cancel", "/icons/cancel.png",
                    Command.BACK, 1);
                break;
            case CHANGE:
                result = new Command("Change", Command.OK, 1);
                break;
            case SAVE:
                result = newCommand("Save", "/icons/save.png", Command.OK, 2);
                break;
            case LOGIN:
                result = newCommand("Login", "/icons/login.png", Command.OK, 2);
                break;
            default:
                result = null;
                break;
        }
        return result;
    }

    /**
     * @see UIUtils#getBottomPadding() 
     */
    protected int getBottomPadding() {
        return CategoryBar.getBestImageHeight(CategoryBar.IMAGE_TYPE_BACKGROUND)
            - 4;
    }

    private Command newCommand(String label, String iconPath, int commandType,
        int priority) {
        try {
            Image icon = ImageLoader.getInstance().loadImage(iconPath, null);
            return new IconCommand(label, icon, icon, commandType, priority);
        }
        catch (IOException e) {
            return new Command(label, commandType, priority);
        }
    }
}
