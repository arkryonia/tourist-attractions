/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.location;

import javax.microedition.location.Criteria;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;

/**
 * Location finder which uses GPS.
 */
class GpsLocationFinder
    extends AbstractLocationFinder {

    private final Criteria criteria;

    GpsLocationFinder() {
        criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setPreferredPowerConsumption(Criteria.NO_REQUIREMENT);
        criteria.setSpeedAndCourseRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setAddressInfoRequired(false);
    }

    /**
     * @see AbstractLocationFinder#getLocationProvider() 
     */
    protected final LocationProvider getLocationProvider()
        throws LocationException {
        return LocationProvider.getInstance(criteria);
    }

    /**
     * @see AbstractLocationFinder#shouldQuit(javax.microedition.location.LocationException) 
     */
    protected boolean shouldQuit(LocationException e) {
        return false;
    }
}
