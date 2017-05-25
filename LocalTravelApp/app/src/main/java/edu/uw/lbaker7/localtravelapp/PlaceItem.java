package edu.uw.lbaker7.localtravelapp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ryan Magee on 5/25/2017.
 */

public class PlaceItem {
    public String placeName;
    public LatLng coordinates;
    public String icon;
    public String address;

    public PlaceItem() {

    }

    public PlaceItem(String placeName, LatLng coordinates, String icon, String address) {
        this.placeName = placeName;
        this.coordinates = coordinates;
        this.icon = icon;
        this.address = address;
    }
}
