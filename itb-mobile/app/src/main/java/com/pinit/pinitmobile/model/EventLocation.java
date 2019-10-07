package com.pinit.pinitmobile.model;

public class EventLocation {

    private Long locationId;
    private String city;
    private String country;
    private float longitude;
    private float latitude;
    private String street;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long id) {
        this.locationId = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return  street+", "+city;
    }
}
