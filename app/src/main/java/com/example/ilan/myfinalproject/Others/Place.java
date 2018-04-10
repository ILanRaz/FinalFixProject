package com.example.ilan.myfinalproject.Others;


import android.location.Location;

import com.example.ilan.myfinalproject.Requests.Data.ImageDataBind;


public class Place {
    private String googleId, name, address, phone, website;
    private ImageDataBind iconImage;
    private boolean isFavourite;
    private double lat, lng;
    private int distance;
    private float rating;
    private long id;


    public Place(Place place) {
        this(place.id, place.googleId, place.name, place.address, place.lat, place.lng, place.rating, place.iconImage.getUrl(), place.phone, place.website, place.isFavourite);

        distance = place.distance;
    }


    public Place(long id, String googleId, String name, String address, double lat, double lng, float rating, String iconUrl, String phone, String website, boolean isFavourite) {
        this.id = id;
        this.googleId = googleId;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.isFavourite = isFavourite;
        this.phone = phone;
        this.website = website;

        distance = AppData.NULL_DATA;

        iconImage = new ImageDataBind(iconUrl);
    }


    public void updateDistance(double lat, double lng) {
        float[] distance = new float[1];
        ;

        Location.distanceBetween(lat, lng, this.lat, this.lng, distance);

        this.distance = (int) distance[0];
    }


    public void removeIconImage() {
        iconImage.remove();
    }


    public String getPhone() {
        return phone;
    }


    public ImageDataBind getIconImage() {
        return iconImage;
    }


    public String getWebsite() {
        return website;
    }


    public int getDistance() {
        return distance;
    }


    public String getGoogleId() {
        return googleId;
    }


    public String getName() {
        return name;
    }


    public String getAddress() {
        return address;
    }


    public float getRating() {
        return rating;
    }


    public long getId() {
        return id;
    }


    public double getLat() {
        return lat;
    }


    public double getLng() {
        return lng;
    }


    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }


    public boolean isFavourite() {
        return isFavourite;
    }


}
