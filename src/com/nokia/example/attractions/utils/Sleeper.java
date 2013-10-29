/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.utils;

/**
 * Simple class for threads to use for sleeping.
 */
public final class Sleeper {

    /**
     * Causes current thread to sleep until either another thread invokes 
     * the wakeup() method for this object, or a specified amount of time 
     * has elapsed.
     * @param delay 
     */
    public final synchronized void sleep(int delay) {
        try {
            wait(delay);
        }
        catch (InterruptedException e) {
        }
    }

    /**
     * Wakes up the speeling threads.
     */
    public final synchronized void wakeup() {
        notifyAll();
    }
}
