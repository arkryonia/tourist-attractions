/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.views;

import com.nokia.example.attractions.utils.UIUtils;
import com.nokia.example.attractions.views.list.GuideDrawer;
import com.nokia.example.attractions.views.list.List;
import java.util.Hashtable;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;

/**
 * View to display bought guides
 */
public final class GuidesView
    extends BaseView {

    private final List guidesList;
    private final Command openCmd = UIUtils.createCommand(UIUtils.OPEN);
    private final Hashtable cache = new Hashtable();

    GuidesView() {
        guidesList = List.getList(new GuideDrawer(viewMaster, viewMaster.
            getDefaultThumbnailIcon(),
            cache),
            new List.Listener() {

                public void select(int focusedRowIndex) {
                    selectGuide(focusedRowIndex);
                }

            });
    }

    private void selectGuide(int index) {
        data.setCurrentGuideIndex(index);
        viewMaster.showAttractionsView();
    }

    public final void resize(int x, int y, int width, int height) {
        super.resize(x, y, width, height);
        guidesList.resize(x, y, width, height);
    }

    public final void activate() {
        viewMaster.setTitle("Select guide");
        guidesList.setData(data.getGuides());
        guidesList.enable();
        super.activate();
        viewMaster.addCommand(backCmd);
        if (!viewMaster.hasPointerEvents()) {
            viewMaster.addCommand(openCmd);
        }
    }

    public final void deactivate() {
        viewMaster.removeCommand(openCmd);
        super.deactivate();
        guidesList.disable();
        cache.clear();
    }

    public final void draw(final Graphics g) {
        if (!isActive()) {
            return;
        }
        guidesList.draw(g);
    }

    public final void keyPressed(int keyCode) {
        handleKey(keyCode);
    }

    public final void keyRepeated(int keyCode) {
        handleKey(keyCode);
    }

    private void handleKey(int keyCode) {
        if (!isActive()) {
            return;
        }
        switch (viewMaster.getGameAction(keyCode)) {
            case ViewMaster.UP:
                guidesList.focusUp();
                viewMaster.forceDraw();
                break;
            case ViewMaster.DOWN:
                guidesList.focusDown();
                viewMaster.forceDraw();
                break;
            case ViewMaster.FIRE:
                guidesList.select();
                break;
            default:
                break;
        }
    }

    public final void commandAction(Command cmd) {
        if (cmd == openCmd) {
            guidesList.select();
        }
        else {
            super.commandAction(cmd);
        }
    }
}
