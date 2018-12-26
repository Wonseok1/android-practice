package com.example.student.mapex;

/**
 * Created by student on 2018-12-26.
 */

public class LocationVO {
    double lat;
    double lng;
    String name;
    String address;

    public LocationVO(double lat, double lng, String name, String address) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
