package com.example.googlemap.EmergencyLocationRecyclerView;

public class EmergencyLocation implements Comparable<EmergencyLocation> {

    private String iconUrl, locationName, address, locationImage, openNow;

    private double rating, longitude, latitude, userLatitude, userLongitude;

    private int totalUserReview;

    private float distance;

    public EmergencyLocation(String iconUrl, String locationName, String address, double rating, int totalUserReview, double latitude, double longitude, float distance, String locationImage, String openNow, double userLatitude, double userLongitude) {
        this.iconUrl = iconUrl;
        this.locationName = locationName;
        this.address = address;
        this.rating = rating;
        this.totalUserReview = totalUserReview;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.locationImage = locationImage;
        this.openNow = openNow;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }

    public double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public String getOpenNow() {
        return openNow;
    }

    public void setOpenNow(String openNow) {
        this.openNow = openNow;
    }

    public String getLocationImage() {
        return locationImage;
    }

    public void setLocationImage(String locationImage) {
        this.locationImage = locationImage;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalUserReview() {
        return totalUserReview;
    }

    public void setTotalUserReview(int totalUserReview) {
        this.totalUserReview = totalUserReview;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public int compareTo(EmergencyLocation o) {
        return Float.compare(this.distance, o.distance);
    }
}
