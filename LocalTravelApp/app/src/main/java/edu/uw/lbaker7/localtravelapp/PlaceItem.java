package edu.uw.lbaker7.localtravelapp;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

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

    public static PlaceItem parseObjectFromJson(JSONObject response) {
        PlaceItem place;
        try {
            JSONObject result = response.getJSONObject("result");

            String currentPlaceName = result.getString("name");
            String currentIcon = result.getString("icon");
            String currentAddress = result.getString("formatted_address");

            JSONObject geometry = result.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            Double lat = location.getDouble("lat");
            Double lng = location.getDouble("lng");
            LatLng currentCoordinates = new LatLng(lat, lng);

            place = new PlaceItem(currentPlaceName, currentCoordinates, currentIcon, currentAddress);
        } catch (JSONException e) {
            Log.v(TAG, "Error parsing json", e);
            return null;
        }
        return place;
    }
}
