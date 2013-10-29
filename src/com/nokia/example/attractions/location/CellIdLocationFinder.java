/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.location;

import com.nokia.mid.location.LocationUtil;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

/**
 * Location finder which uses Cell ID.
 */
class CellIdLocationFinder
    extends AbstractLocationFinder {

    private static final String TEST_URL = "http://promptNetworkAccess.com";

    CellIdLocationFinder() {
        interval = 15 * 60000;
    }

    /**
     * @see AbstractLocationFinder#getLocationProvider() 
     */
    protected final LocationProvider getLocationProvider()
        throws LocationException, SecurityException {
        // Prompt first for network connection 
        // to prevent bugs in Cell ID service.
        HttpConnection conn = null;
        try {
            conn = (HttpConnection) Connector.open(TEST_URL);
        }
        catch (SecurityException e) {
            throw e;
        }
        catch (IOException e) {
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (IOException e) {
                }
            }
        }

        int[] methods = {(Location.MTA_ASSISTED | Location.MTE_CELLID
            | Location.MTE_SHORTRANGE | Location.MTY_NETWORKBASED)};
        return LocationUtil.getLocationProvider(methods, null);
    }

    /**
     * @see AbstractLocationFinder#shouldQuit(javax.microedition.location.LocationException) 
     */
    protected boolean shouldQuit(LocationException e) {
        return true;
    }
}
