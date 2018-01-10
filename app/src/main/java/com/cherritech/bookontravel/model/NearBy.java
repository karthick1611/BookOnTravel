package com.cherritech.bookontravel.model;

import com.google.android.gms.maps.model.LatLng;

public class NearBy {

    private LatLng hotel_latitude_longitude;
    private String hotel_address,hotel_name,hotel_star,hotel_icon;
    private boolean hotel_open_now;

    public LatLng getHotel_latitude_longitude() {
        return hotel_latitude_longitude;
    }

    public void setHotel_latitude_longitude(LatLng hotel_latitude_longitude) {
        this.hotel_latitude_longitude = hotel_latitude_longitude;
    }

    public String getHotel_address() {
        return hotel_address;
    }

    public void setHotel_address(String hotel_address) {
        this.hotel_address = hotel_address;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public void setHotel_name(String hotel_name) {
        this.hotel_name = hotel_name;
    }

    public String getHotel_star() {
        return hotel_star;
    }

    public void setHotel_star(String hotel_star) {
        this.hotel_star = hotel_star;
    }

    public String getHotel_icon() {
        return hotel_icon;
    }

    public void setHotel_icon(String hotel_icon) {
        this.hotel_icon = hotel_icon;
    }

    public boolean getHotel_open_now() {
        return hotel_open_now;
    }

    public void setHotel_open_now(boolean hotel_open_now) {
        this.hotel_open_now = hotel_open_now;
    }
}
