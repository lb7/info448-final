package edu.uw.lbaker7.localtravelapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by Ryan Magee on 5/25/2017.
 */
public class PlaceItem implements Parcelable {
    public String placeName;
    public LatLng coordinates;
    public String icon;
    public String address;
    public String id;
    public Double rating;
    public int priceLevel;

    public PlaceItem() {

    }

    public PlaceItem(String placeName, LatLng coordinates, String icon, String address, String id, Double rating, int priceLevel) {
        this.placeName = placeName;
        this.coordinates = coordinates;
        this.icon = icon;
        this.address = address;
        this.id = id;
        this.rating = rating;
        this.priceLevel = priceLevel;
    }

    protected PlaceItem(Parcel in) {
        placeName = in.readString();
        coordinates = in.readParcelable(LatLng.class.getClassLoader());
        icon = in.readString();
        address = in.readString();
        id = in.readString();
        priceLevel = in.readInt();
    }

    public static final Creator<PlaceItem> CREATOR = new Creator<PlaceItem>() {
        @Override
        public PlaceItem createFromParcel(Parcel in) {
            return new PlaceItem(in);
        }

        @Override
        public PlaceItem[] newArray(int size) {
            return new PlaceItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeName);
        dest.writeParcelable(coordinates, flags);
        dest.writeString(icon);
        dest.writeString(address);
        dest.writeString(id);
        dest.writeInt(priceLevel);
    }
    public PlaceItem(String placeName, LatLng coordinates, String icon, String address) {
        this.placeName = placeName;
        this.coordinates = coordinates;
        this.icon = icon;
        this.address = address;
    }
    public static PlaceItem parseObjectFromJson(JSONObject response) {
        Log.v(TAG, response.toString());
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
            String id = result.getString("id");

            String address = result.getString("vicinity");
            Double rating = 0.0;
            if(!result.isNull("rating")){
                rating = result.getDouble("rating");
            }
            int priceLevel = 0;
            if(!result.isNull("price_level")){
                priceLevel =  result.getInt("price_level");
            }

            place = new PlaceItem(currentPlaceName, currentCoordinates, currentIcon, currentAddress, id,rating, priceLevel );

            } catch (JSONException e) {
            Log.v(TAG, "Error parsing json", e);
            return null;
        }
        return place;
    }
}


