package edu.uw.lbaker7.localtravelapp;


public class ItineraryListItem {

    public String itineraryName;
    public String dateCreated;

    public ItineraryListItem() {

    }

    public ItineraryListItem(String name, String date) {
        this.itineraryName = name;
        this.dateCreated = date;
    }
}
