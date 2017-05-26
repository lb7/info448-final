package edu.uw.lbaker7.localtravelapp;


import java.util.HashMap;

public class ItineraryListItem {

    public String itineraryName;
    public String dateCreated;

    public HashMap<String, Boolean> places;

    public ItineraryListItem() {

    }

    public ItineraryListItem(String name, String date) {
        this.itineraryName = name;
        this.dateCreated = date;
    }

    //Getters needed by firebase
    public String getItineraryName() {
        return itineraryName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public HashMap<String, Boolean> getPlaces() {
        return places;
    }
}
