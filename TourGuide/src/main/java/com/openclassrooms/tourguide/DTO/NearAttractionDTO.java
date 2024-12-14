package com.openclassrooms.tourguide.DTO;


import gpsUtil.location.Location;

public class NearAttractionDTO {

    private String attractionName;
    private Location attractionLocation;
    private Location userLocation;
    private Double userAttractionDistance;
    private int rewardPoints;

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public Location getAttractionLocation() {
        return attractionLocation;
    }

    public void setAttractionLocation(Double latitude, Double longitude) {
        this.attractionLocation = new Location(latitude, longitude);
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public Double getUserAttractionDistance() {
        return userAttractionDistance;
    }

    public void setUserAttractionDistance(Double userAttractionDistance) {
        this.userAttractionDistance = userAttractionDistance;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
