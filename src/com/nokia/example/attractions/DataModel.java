/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions;

import com.nokia.example.attractions.models.Attraction;
import com.nokia.example.attractions.models.Guide;
import com.nokia.maps.common.GeoCoordinate;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Contains the data used in the application:
 * Guide and Attraction objects.
 * 
 * @see com.nokia.example.attractions.models.Attraction
 * @see com.nokia.example.attractions.models.Guide
 * 
 */
public final class DataModel {

    private static DataModel self = null;
    private Vector guides;
    private int currentGuideIndex = -1;  // selected guide
    private Vector attractions;
    private int currentAttractionIndex = -1;  // selected attraction
    private GeoCoordinate currentPosition = null;
    private int accuracy = 10;  // accuracy of the position in meters

    /**
     * @return DataModel singleton
     */
    public static DataModel getInstance() {
        if (self == null) {
            self = new DataModel();
        }
        return self;
    }

    public final void setAttractions(Vector attractions) {
        if (this.attractions == attractions) {
            return;
        }
        this.attractions = attractions;
        updateDistances();
    }

    public final Vector getGuides() {
        return guides;
    }

    public final Vector getAttractions() {
        return attractions;
    }

    public final void setCurrentGuideIndex(int index) {
        if (index != this.currentGuideIndex) {
            this.currentGuideIndex = index;
            setAttractions(null);
        }
    }

    public final int getCurrentGuideIndex() {
        return currentGuideIndex;
    }

    public final Guide getCurrentGuide() {
        return (Guide) guides.elementAt(currentGuideIndex);
    }

    public final void setCurrentAttractionIndex(int index) {
        this.currentAttractionIndex = index;
    }

    public final int getCurrentAttractionIndex() {
        return currentAttractionIndex;
    }

    public final Attraction getCurrentAttraction() {
        try {
            return (Attraction) attractions.elementAt(currentAttractionIndex);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public final void setCurrentPosition(GeoCoordinate position, int accuracy) {
        this.currentPosition = position;
        this.accuracy = accuracy;
        updateDistances();
    }

    public final GeoCoordinate getCurrentPosition() {
        return currentPosition;
    }

    public final int getAccuracy() {
        return accuracy;
    }

    /**
     * Returns a string with the distance from the current position
     * to the given attraction.
     * @param attraction
     * @return 
     */
    private String distanceToAttraction(Attraction attraction) {
        final GeoCoordinate position = currentPosition;
        if (position == null) {
            return null;
        }
        double distance = position.distanceTo(attraction.getLocation());
        if (distance < 1000.0) {
            return ((int) (distance)) + " m";
        }
        else {
            String d = String.valueOf(distance % 1000).substring(0, 1);
            return (int) (distance / 1000) + "." + d + " km";
        }
    }

    /**
     * Iterates over the attractions and updates the distance to them.
     */
    private void updateDistances() {
        Vector attrs = attractions;
        if (attrs != null) {
            for (int i = 0; i < attrs.size(); i++) {
                Attraction a = (Attraction) attrs.elementAt(i);
                a.setDistance(distanceToAttraction(a));
            }
        }
    }

    /**
     * Save guides to RMS.
     */
    public final void saveGuides() {
        try {
            RecordStore store = RecordStore.openRecordStore("Guides", true);
            if (store.getNumRecords() == 0) {
                store.addRecord(null, 0, 0);
            }
            byte[] data = guidesState();
            store.setRecord(getRecordId(store), data, 0, data.length);
            store.closeRecordStore();
        }
        catch (Exception e) {
            try {
                RecordStore.deleteRecordStore("Guides");
            }
            catch (RecordStoreException rse) {
            }
        }
    }

    private int getRecordId(RecordStore store)
        throws RecordStoreException {
        RecordEnumeration e = store.enumerateRecords(null, null, false);
        try {
            return e.nextRecordId();
        }
        finally {
            e.destroy();
        }
    }

    private byte[] guidesState() {
        ByteArrayOutputStream bout = null;
        try {
            bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            dout.writeInt(guides.size());
            for (int i = 0, size = guides.size(); i < size; i++) {
                ((Guide) guides.elementAt(i)).writeTo(dout);
            }
            dout.writeInt(currentGuideIndex);
            return bout.toByteArray();
        }
        catch (IOException e) {
        }
        finally {
            try {
                if (bout != null) {
                    bout.close();
                }
            }
            catch (IOException e) {
            }
        }
        return new byte[0];
    }

    /**
     * Load guides from RMS.
     */
    public final void loadGuides() {
        try {
            RecordStore store = RecordStore.openRecordStore("Guides", true);
            if (store.getNumRecords() == 0
                || !loadGuides(store.getRecord(getRecordId(store)))) {
                guides = new Vector();
            }
            store.closeRecordStore();
        }
        catch (RecordStoreException e) {
            guides = new Vector();
        }
    }

    private boolean loadGuides(byte[] record) {
        if (record == null) {
            return false;
        }
        try {
            DataInputStream din = new DataInputStream(new ByteArrayInputStream(
                record));
            guides = new Vector();
            int size = din.readInt();
            while (size > 0) {
                guides.addElement(Guide.readFrom(din));
                size--;
            }
            currentGuideIndex = din.readInt();
            return true;
        }
        catch (IOException e) {
        }
        return false;
    }
}
