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

/**
 * List view custom UI control with gesture controls.
 */
final class CompatibleList
    extends List
    implements ViewMaster.PointerEventListener {

    private Thread delayThread;
    private boolean enabled = false;
    private final ViewMaster view;
    private int oldY;
    private long oldTime;
    private int dragThreshold = 10;
    private boolean dragging = false;
    private Animator animator = null;
    private int friction = 100;
    private int maxScrollSpeed = 100;
    private int minScrollSpeed = 10;
    private int velocityY = 0;

    CompatibleList() {
        view = ViewMaster.getInstance();
    }

    public final void disable() {
        if (!enabled) {
            return;
        }
        enabled = false;
        view.setPointerEventListener(null);
        super.disable();
    }

    public final void enable() {
        if (enabled) {
            return;
        }
        enabled = true;
        super.enable();
        view.setPointerEventListener(this);
    }

    public final void resize(int x, int y, int width, int height) {
        super.resize(x, y, width, height);
        dragThreshold = height / 20;
        maxScrollSpeed = height;
        minScrollSpeed = height / 3;
        friction = height / 2;
    }

    private void animate(int deltaY, int velocityY, boolean lastFrame) {
        addTranslateY(deltaY);
        this.velocityY = velocityY;
        // Scrolling is no longer active if this is the last frame
        if (lastFrame) {
            stopScrollAnimation();
        }
        // Request repaint from ViewMaster
        view.forceDraw();
    }

    /**
     * Stops scrolling.
     */
    protected final void stopScrollAnimation() {
        velocityY = 0;
        if (animator != null) {
            animator.close();
            animator = null;
        }
    }

    public final void pointerDragged(int x, int y) {
        final long currentTime = System.currentTimeMillis();
        final int dy = y - oldY;
        final int dt = (int) (currentTime - oldTime);
        if (Math.abs(dy) > dragThreshold) {
            dragging = true;
        }
        if (dragging) {
            if (dt > 0) {
                velocityY = (velocityY + dy * 1000 / dt) / 2;
            }
            oldY = y;
            oldTime = currentTime;
        }
        if (delayThread == null && dragging) {
            if (animator != null) {
                stopScrollAnimation();
            }
            else {
                addTranslateY(dy);
                view.forceDraw();
            }
        }
    }

    public final void pointerPressed(int x, int y) {
        oldY = y;
        oldTime = System.currentTimeMillis();
    }

    public final void pointerReleased(int x, int y) {
        if (delayThread == null) {
            if (dragging) {
                if (animator != null) {
                    animator.close();
                    animator = null;
                }
                final int v = Math.min(Math.max(-maxScrollSpeed, velocityY),
                    maxScrollSpeed);
                if (Math.abs(v) > minScrollSpeed) {
                    animator = new Animator(v, friction);
                    animator.start();
                }
            }
            else {
                if (animator != null) {
                    stopScrollAnimation();
                }
                else {
                    handleSelectEvent(x, y);
                }
            }
        }
        dragging = false;
    }

    /**
     * Common handler function for selection events.
     * 
     * @param event Gesture event.
     */
    private void handleSelectEvent(int x, int y) {
        setFocusedRowIndex(getRowAt(x, y));
        if (isFocusedRowIndex()) {
            view.draw();
            // Delay the event handling, 
            // so that user gets a chance to see the focus.
            delayThread = new Thread() {

                public void run() {
                    try {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                    select();
                    resetFocusedRowIndex();
                    delayThread = null;
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

    /**
     * Animation thread. 
     */
    private class Animator
        extends Thread {

        private static final int MAX_FPS = 30;
        private volatile boolean run = true;
        private int startSpeed;
        private int friction;

        /**
         * Constructor.
         * 
         * @param startSpeed
         * @param friction
         */
        public Animator(final int startSpeed, final int friction) {
            if (friction < 0) {
                throw new IllegalArgumentException(
                    "Friction cannot be negative.");
            }
            this.startSpeed = startSpeed;
            this.friction = startSpeed > 0 ? friction : -friction;
        }

        /**
         * @see Thread#run()
         */
        public void run() {
            final long startTime = System.currentTimeMillis();
            final int tMax = Math.abs(startSpeed * 1000 / friction);
            if (tMax == 0) {
                animate(0, 0, true);
                return;
            }
            final int sMax = startSpeed / 2 * tMax;
            final int dtMin = 1000 / MAX_FPS;

            int t = 0;
            int sUpdated = 0;

            int ds = 0;
            boolean lastFrame = false;
            long frameEndTime = startTime;
            try {
                while (run) {
                    Thread.sleep(Math.max(dtMin - (int) (System.
                        currentTimeMillis() - frameEndTime), 0));

                    t = (int) (System.currentTimeMillis() - startTime);

                    if (t < tMax) {
                        ds += startSpeed * t - friction * t * t / 2000
                            - sUpdated;
                    }
                    else {
                        ds += sMax - sUpdated;
                        lastFrame = true;
                        run = false;
                    }
                    if (run && (lastFrame || Math.abs(ds / 1000) > 0)) {
                        final int v = startSpeed - friction * t / 2000;
                        animate(ds / 1000, v, lastFrame);
                        sUpdated += ds;
                        ds = 0;
                    }

                    frameEndTime = System.currentTimeMillis();
                }
            }
            catch (Exception e) {
                animate(0, 0, true);
            }
        }

        ;
		
		/**
		 * Closes this thread.
		 */
		public void close() {
            run = false;
        }
    }
}
