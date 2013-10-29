/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.views;

import com.nokia.example.attractions.views.list.AttractionDrawer;
import com.nokia.example.attractions.views.list.List;
import com.nokia.example.attractions.io.AttractionsOperation;
import com.nokia.example.attractions.utils.UIUtils;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Graphics;

/**
 * View to display a guide as a list of attractions.
 * The main view in the application.
 */
public final class AttractionsView
    extends BaseView {

    private List attractionsList;
    private volatile boolean loading = true;
    private final Command openCmd = UIUtils.createCommand(UIUtils.OPEN);
    private final Hashtable cache = new Hashtable();

    AttractionsView() {
        attractionsList = List.getList(new AttractionDrawer(viewMaster,
            viewMaster.getDefaultThumbnailIcon(), cache),
            new List.Listener() {

                public void select(int focusedRowIndex) {
                    data.setCurrentAttractionIndex(focusedRowIndex);
                    viewMaster.showDetailsView();
                }
            });
    }

    public final void resize(int x, int y, int width, int height) {
        super.resize(x, y, width, height);
        attractionsList.resize(x, y, width, height);
    }

    public final void activate() {
        viewMaster.setTitle(data.getCurrentGuide().getCity());
        data.setCurrentAttractionIndex(-1);
        loading = true;
        super.activate();
        loadAttractions();
        viewMaster.addCommand(exitCmd);
        viewMaster.addCommand(mapCmd);
        viewMaster.addCommand(aboutCmd);
        viewMaster.addCommand(helpCmd);
        if (!viewMaster.hasPointerEvents()) {
            viewMaster.addCommand(openCmd);
        }
    }

    /**
     * @see BaseView#getScreenName() 
     */
    protected String getScreenName() {
        return super.getScreenName() + " - " + data.getCurrentGuide().getCity();
    }

    /**
     * Loads the attractions.
     */
    private void loadAttractions() {
        if (attractionsList.hasData() && attractionsList.getData() == data.
            getAttractions()) {
            attractionsList.enable();
            loading = false;
        }
        else {
            loading = true;
            new AttractionsOperation(new AttractionsOperation.Listener() {

                public void attractionsReceived(Vector attractions) {
                    if (!isActive()) {
                        return;
                    }
                    if (attractions == null) {
                        showAlert("Network failure", "Network failure",
                            "Loading attractions failed.");
                        viewMaster.showGuidesView();
                        return;
                    }
                    data.setAttractions(attractions);
                    attractionsList.setData(attractions);
                    attractionsList.enable();
                    loading = false;
                }
            }, data.getCurrentGuide()).start();
        }
    }

    public final void deactivate() {
        viewMaster.removeCommand(openCmd);
        super.deactivate();
        attractionsList.disable();
        cache.clear();
    }

    public final void draw(final Graphics g) {
        if (!isActive()) {
            return;
        }
        if (loading) {
            drawLoading(g);
        }
        else {
            attractionsList.draw(g);
        }
    }

    public final void keyPressed(int keyCode) {
        handleKey(keyCode);
    }

    public final void keyRepeated(int keyCode) {
        handleKey(keyCode);
    }

    private void handleKey(int keyCode) {
        if (!isActive() || loading) {
            return;
        }
        switch (viewMaster.getGameAction(keyCode)) {
            case ViewMaster.UP:
                attractionsList.focusUp();
                viewMaster.forceDraw();
                break;
            case ViewMaster.DOWN:
                attractionsList.focusDown();
                viewMaster.forceDraw();
                break;
            case ViewMaster.FIRE:
                attractionsList.select();
                break;
            default:
                break;
        }
    }

    public final void commandAction(Command cmd) {
        if (cmd == openCmd) {
            if (!loading) {
                attractionsList.select();
            }
        }
        else {
            super.commandAction(cmd);
        }
    }
}
