/**
* Copyright (c) 2012-2013 Nokia Corporation. All rights reserved.
* Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
* Oracle and Java are trademarks or registered trademarks of Oracle and/or its
* affiliates. Other product and company names mentioned herein may be trademarks
* or trade names of their respective owners. 
* See LICENSE.TXT for license information.
*/
package com.nokia.example.attractions.models;

import com.nokia.maps.common.GeoCoordinate;

/**
 * Describes an attraction. 
 */
public final class Attraction {

    private Guide guide;
    private String id;
    private String name;
    private String thumbnailUrl = "";
    private String imageUrl = "";
    private String guideId;
    private String street;
    private String latitude = "0";
    private String longitude = "0";
    private String description = "";
    private String distance;  // distance to user's current position
    private String type;

    /**
     * @return location of the attraction
     */
    public final GeoCoordinate getLocation() {
        return new GeoCoordinate(Double.parseDouble(latitude), Double.
            parseDouble(longitude), 0);
    }

    /**
     * Helper method to get url to the thumbnail image.
     * Adds guide.urlPrefix to imageUrl if needed.
     * @return url to the thumbnail image of the attraction
     */
    public final String getThumbnailUrl() {
        return addPrefixIfNeeded(thumbnailUrl);
    }

    /**
     * Helper method to get url to the image.
     * Adds guide.urlPrefix to imageUrl if needed.
     * @return url to the image of the attraction
     */
    public final String getImageUrl() {
        return addPrefixIfNeeded(imageUrl);
    }

    private String addPrefixIfNeeded(String url) {
        if (!url.equals("") && guide != null) {
            return guide.getUrlPrefix() + url;
        }
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getDistance() {
        return distance;
    }

    public Guide getGuide() {
        return guide;
    }

    public String getGuideId() {
        return guideId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setGuide(Guide guide) {
        this.guide = guide;
    }

    public void setGuideId(String guideId) {
        this.guideId = guideId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setType(String type) {
        this.type = type;
    }
}
