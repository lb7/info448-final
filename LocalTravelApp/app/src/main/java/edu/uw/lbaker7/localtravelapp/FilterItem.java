package edu.uw.lbaker7.localtravelapp;

/**
 * Created by KarYin on 5/29/2017.
 */

public class FilterItem {

    public String type;
    public boolean isSelected;

    public FilterItem(String type, boolean isSelected) {
        this.type = type;
        this.isSelected = isSelected;
    }

    public String getType() {
        return this.type.toLowerCase().replace(" ", "_");
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
