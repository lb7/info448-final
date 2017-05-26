package edu.uw.lbaker7.localtravelapp.activitites;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

import edu.uw.lbaker7.localtravelapp.PlacesRequestQueue;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.fragments.PlaceListFragment;


public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback, LocationListener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PlaceListFragment.OnMapButtonClickedListener {
    private static final int LOCATION_REQUEST_CODE = 1;
    private ArrayList<PlaceItem> places;
    private static final String TAG = "MapsActivity";
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng last;
    private PlaceListFragment placeListFragment;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        placeListFragment = PlaceListFragment.newInstance();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

    }

@Override
protected void onStart() {
    Log.v(TAG, "Started!!!");

    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    mGoogleApiClient.connect();
    super.onStart();
}

    @Override
    protected void onStop() {
        Log.v(TAG, "Stoped!!!");

        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "onConnectionFailed");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "onConnection Here!!!");

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //have permission, can go ahead and do stuff
            Log.v(TAG, "got permission");
            //PlaceDetectionApi
            //assumes location settings enabled
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest , this);

        }
        else {
            //request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case LOCATION_REQUEST_CODE: { //if asked for location
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onConnected(null); //do whatever we'd do when first connecting (try again)
                }
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "onConnectionSuspended");


    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            String types = "restaurant|aquarium|amusement_park|art_gallery|bakery|bar|beauty_salon|cafe|bowling_alley|clothing_store|hair_care|jewelry_store|library|meal_takeaway|movie_theater|museum|night_club|park|shopping_mall|zoo|spa";
            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location.getLatitude()+","+location.getLongitude()+"&radius=500&type="+types+"&key=" + getString(R.string.google_place_key);
            last = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(last));
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET,url, null, new Response.Listener<JSONObject>() {
                        //handling response
                        @Override
                        public void onResponse(JSONObject response) {
                            setRecent(response);//calling function to present data
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.v(TAG, "There was an error:" + error);
                        }
                    });

            // Adding the request to the NewsRequestQueue
            PlacesRequestQueue.getInstance(this).addToRequestQueue(jsObjRequest);
        }

    }
    /*
   [{"geometry":{
   "location":{"lat":-33.8709434,"lng":151.1903114}

     */
    public void setRecent(JSONObject response){
        mMap.clear();
        try {
            JSONArray jsonResults = response.getJSONArray("results"); //response.results
            places = new ArrayList<PlaceItem>();
            for(int i=0; i<jsonResults.length(); i++) {

                JSONObject resultItemObj = jsonResults.getJSONObject(i);
                JSONObject location = resultItemObj.getJSONObject("geometry").getJSONObject("location");
                LatLng ltlg = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                String placeName = resultItemObj.getString("name");
                String id = resultItemObj.getString("id");
                String icon = resultItemObj.getString("icon");
                String address = resultItemObj.getString("vicinity");
                Double rating = 0.0;
                if(!resultItemObj.isNull("rating")){
                    rating = resultItemObj.getDouble("rating");
                }
                int priceLevel = 0;
                if(!resultItemObj.isNull("price_level")){
                    priceLevel =  resultItemObj.getInt("price_level");
                }
                PlaceItem place = new PlaceItem(placeName, ltlg , icon, address, id, rating, priceLevel);
                places.add(place);
                Marker marker = mMap.addMarker(new MarkerOptions().position(ltlg).title(placeName).snippet("Click to see more!"));
                marker.setTag(place);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ltlg));

                Log.v(TAG, ltlg.toString());


            }
        }catch (JSONException e ){
            Log.v(TAG, e.toString());
        }
    }
    public void handleSearch(View v){
        EditText editText = (EditText) findViewById(R.id.search);
        String search = URLEncoder.encode(editText.getText().toString());
        Log.v(TAG, search);


        String types = "restaurant|aquarium|amusement_park|art_gallery|bakery|bar|beauty_salon|cafe|bowling_alley|clothing_store|hair_care|jewelry_store|library|meal_takeaway|movie_theater|museum|night_club|park|shopping_mall|zoo|spa";
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+last.latitude+","+last.longitude+"&keyword="+search+"&radius=500&key=" + getString(R.string.google_place_key);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET,url, null, new Response.Listener<JSONObject>() {
                    //handling response
                    @Override
                    public void onResponse(JSONObject response) {
                        setRecent(response);//calling function to present data
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "There was an error:" + error);
                    }
                });

        // Adding the request to the NewsRequestQueue
        PlacesRequestQueue.getInstance(this).addToRequestQueue(jsObjRequest);


    }
    public void handleFilter(View v){
        Log.v(TAG, "you filtered");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;

        return true; //we've provided a menu!
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.itineraries:
                startActivity(new Intent(MapsActivity.this, ItineraryActivity.class));
                return true; //handled
            case R.id.mapList:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.map_container, placeListFragment);
                ft.commit();
                item.setVisible(false);
                return true; //handled
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public class PlaceItem {
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

    }

    public ArrayList<PlaceItem> getPlaceList() {
        return places;
    }

    @Override
    public void onMapButtonClicked() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(placeListFragment);
        ft.commit();
        MenuItem item = menu.findItem(R.id.mapList);
        item.setVisible(true);
    }
}
