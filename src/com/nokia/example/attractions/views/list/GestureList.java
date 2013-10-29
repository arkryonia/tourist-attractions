/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.views.list;

import com.nokia.example.attractions.views.ViewMaster;
import com.nokia.mid.ui.frameanimator.FrameAnimator;
import com.nokia.mid.ui.frameanimator.FrameAnimatorListener;
import com.nokia.mid.ui.gestures.GestureEvent;
import com.nokia.mid.ui.gestures.GestureInteractiveZone;
import com.nokia.mid.ui.gestures.GestureListener;
import com.nokia.mid.ui.gestures.GestureRegistrationManager;

/**
 * List view custom UI control with gesture controls.
 */
final class GestureList
    extends List
    implements GestureListener, FrameAnimatorListener {

    private final static int GESTURES = GestureInteractiveZone.GESTURE_DRAG
        | GestureInteractiveZone.GESTURE_TAP
        | GestureInteractiveZone.GESTURE_FLICK;
    private final FrameAnimator animator;
    private boolean scrollingActive;
    private int pendingGestureEvent;
    private Thread delayThread;
    private boolean enabled = false;
    private final GestureInteractiveZone zone;
    private final ViewMaster view;

    GestureList() {
        zone = new GestureInteractiveZone(GESTURES);
        animator = new FrameAnimator();
        view = ViewMaster.getInstance();
    }

    public final void disable() {
        if (!enabled) {
            return;
        }
        enabled = false;
        GestureRegistrationManager.unregister(view, zone);
        super.disable();
        animator.unregister();
    }

    public final void enable() {
        if (enabled) {
            return;
        }
        enabled = true;
        super.enable();
        // Use default values for maxFps an maxPps 
        // (zero param means that default is used).
        final short maxFps = 0;
        final short maxPps = 0;
        if (animator.register((short) 0, (short) 0, maxFps, maxPps, this)
            != true) {
            throw new RuntimeException("FrameAnimator.register() failed!");
        }
        if (GestureRegistrationManager.register(view, zone) != true) {
            throw new RuntimeException(
                "GestureRegistrationManager.register() failed!");
        }
        GestureRegistrationManager.setListener(view, this);
    }

    public final void resize(int x, int y, int width, int height) {
        super.resize(x, y, width, height);
        zone.setRectangle(x, y, width, height);
    }

    /**
     * @see com.nokia.mid.ui.frameanimator.FrameAnimatorListener#animate(
     * com.nokia.mid.ui.frameanimator.FrameAnimator, int, int, short, short, 
     * short, boolean)
     */
    public final void animate(FrameAnimator animator, int x, int y, short delta,
        short deltaX, short deltaY, boolean lastFrame) {
        addTranslateY(deltaY);
        // Scrolling is no longer active if this is the last frame
        scrollingActive = !lastFrame;
        // Request repaint from ViewMaster
        view.forceDraw();
    }

    /**
     * Stops scrolling.
     */
    private void stopScrolling() {
        scrollingActive = false;
        animator.stop();
    }

    /**
     * @see com.nokia.mid.ui.gestures.GestureListener#gestureAction(
     * java.lang.Object, com.nokia.mid.ui.gestures.GestureInteractiveZone, 
     * com.nokia.mid.ui.gestures.GestureEvent)
     */
    public final void gestureAction(Object container,
        GestureInteractiveZone zone, GestureEvent event) {

        // Block gesture handling if already handling one,
        // this may happen due to delayed handling of events 
        // (delay is needed for showing focus on selected item)
        if (pendingGestureEvent > 0) {
            return;
        }

        resetFocusedRowIndex();
        switch (event.getType()) {
            case GestureInteractiveZone.GESTURE_DRAG: {
                if (scrollingActive) {
                    stopScrolling();
                }
                else {
                    addTranslateY(event.getDragDistanceY());
                    view.forceDraw();
                }
                break;
            }
            case GestureInteractiveZone.GESTURE_TAP: {
                if (scrollingActive) {
                    stopScrolling();
                }
                else {
                    handleSelectEvent(event);
                }
                break;
            }
            case GestureInteractiveZone.GESTURE_FLICK: {
                // Start vertical flick only if the gesture is 
                // more vertical than horizontal.
                float angle = Math.abs(event.getFlickDirection());
                if ((angle > (Math.PI / 4) && angle < (3 * Math.PI / 4))) {
                    int startSpeed = event.getFlickSpeedY();
                    int direction = FrameAnimator.FRAME_ANIMATOR_VERTICAL;
                    int friction = FrameAnimator.FRAME_ANIMATOR_FRICTION_LOW;
                    scrollingActive = true;
                    animator.kineticScroll(startSpeed, direction, friction, 0);
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * Common handler function for selection events.
     * 
     * @param event Gesture event.
     */
    private void handleSelectEvent(GestureEvent event) {
        setFocusedRowIndex(getRowAt(event.getStartX(), event.getStartY()));
        if (isFocusedRowIndex()) {
            view.draw();
            // Delay the event handling, 
            // so that user gets a chance to see the focus.
            // Gesture is stored so that we can handle it later on.
            pendingGestureEvent = event.getType();
            delayThread = new Thread() {

                public void run() {
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                    if (pendingGestureEvent > 0) {
                        switch (pendingGestureEvent) {
                            case GestureInteractiveZone.GESTURE_TAP:
                                select();
                                break;
                            default:
                                break;
                        }
                        resetFocusedRowIndex();
                        pendingGestureEvent = 0;
                        delayThread = null;
                    }
                }
            };
            delayThread.start();
        }
    }

    /**
     * Finds the row with the given coordinates.
     * 
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return Row at the given coordinates or -1 if no row found.
     */
    private int getRowAt(int x, int y) {
        int listY = y - getTranslateY();
        int heightSoFar = 0;
        int heightNext = 0;

        // Go through all items (pretend that list item heights can vary).
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                heightSoFar = heightNext;
                heightNext += itemHeight();

                if (listY >= heightSoFar && listY <= heightNext) {
                    return i;
                }
            }
        }

        return -1;
    }

    protected final void stopScrollAnimation() {
        if (scrollingActive) {
            stopScrolling();
        }
    }
}
