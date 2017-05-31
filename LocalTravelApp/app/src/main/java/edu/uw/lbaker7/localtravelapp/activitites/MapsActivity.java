package edu.uw.lbaker7.localtravelapp.activitites;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.uw.lbaker7.localtravelapp.AddPlaceDialog;
import edu.uw.lbaker7.localtravelapp.FilterItem;
import edu.uw.lbaker7.localtravelapp.PlaceItem;
import edu.uw.lbaker7.localtravelapp.PlacesDialog;
import edu.uw.lbaker7.localtravelapp.PlacesRequestQueue;
import edu.uw.lbaker7.localtravelapp.R;
import edu.uw.lbaker7.localtravelapp.fragments.FilterFragment;
import edu.uw.lbaker7.localtravelapp.fragments.PlaceListFragment;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener ,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, PlaceListFragment.OnMapButtonClickedListener, PlacesDialog.OnItineraryChooseListener, FilterFragment.OnFilterButtonClickedListener {

    private static final int LOCATION_REQUEST_CODE = 1;
    private ArrayList<PlaceItem> places;
    private ArrayList<FilterItem> placeTypeArray;
    private static final String TAG = "MapsActivity";
    private LocationRequest mLocationRequest;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng last;
    private PlaceListFragment placeListFragment;
    private FilterFragment filterFragment;
    private Menu menu;
    private ArrayList<PlaceItem>  stuff;

    private String[] placeTypes;
    private SupportMapFragment mapFragment;
    private View controls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (ItineraryActivity.ACTION_DRAW.equals(action) ) {
            stuff = intent.getExtras().getParcelableArrayList("places");
            Log.v(TAG, "places "+ stuff);

        }else{
            stuff= new ArrayList();
        }
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        findViewById(R.id.btn_clear_dir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                findViewById(R.id.btn_clear_dir).setVisibility(View.INVISIBLE);

            }
        });
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

        placeTypes = getResources().getStringArray(R.array.place_types);
        createFilterArray();

        EditText editText = (EditText) findViewById(R.id.search);
        final String search = URLEncoder.encode(editText.getText().toString());
        Log.v(TAG, search);

        ImageButton searchButton = (ImageButton) findViewById(R.id.btn_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSearch(search);
            }
        });

        controls = findViewById(R.id.controls_container);
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


        if(stuff.size() >0){
            String waypoint = "";

            for (int i = 0; i< stuff.size()-1; i++){
                waypoint += "place_id:"+stuff.get(i).id +"|";
                Marker marker = mMap.addMarker(new MarkerOptions().position(stuff.get(i).coordinates).title(stuff.get(i).placeName).snippet("Click to see more!"));
                marker.setTag(stuff.get(i));
            }
            findViewById(R.id.btn_clear_dir).setVisibility(View.VISIBLE);
            Marker marker = mMap.addMarker(new MarkerOptions().position(stuff.get(stuff.size()-1).coordinates).title(stuff.get(stuff.size()-1).placeName).snippet("Click to see more!"));
            marker.setTag(stuff.get(stuff.size()-1));
            String url="https://maps.googleapis.com/maps/api/directions/json?mode=walking&origin=place_id:"+stuff.get(0).id+"&destination=place_id:"+stuff.get(stuff.size()-1).id+"&waypoints="+waypoint+"place_id:"+stuff.get(stuff.size()-1).id+"&key=AIzaSyB8Ui2WT4bSCv5JLwFx2FAkR1wUrdUlgtM";
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET,url, null, new Response.Listener<JSONObject>() {
                        //handling response
                        @Override
                        public void onResponse(JSONObject response) {
                            setPoly(response);//calling function to present data
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.v(TAG, "There was an error:" + error);
                        }
                    });

            // Adding the request to the PlacesRequestQueue
            PlacesRequestQueue.getInstance(this).addToRequestQueue(jsObjRequest);
        }
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                PlaceItem placeItem = (PlaceItem) marker.getTag();
                PlacesDialog placesDialog = PlacesDialog.newInstance(placeItem);

                placesDialog.newInstance(placeItem).show(getSupportFragmentManager(), "PlacesDialog");
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

    }
    public void setPoly(JSONObject response){
        Log.v(TAG, response.toString());
        try {
            JSONArray jsonResults = response.getJSONArray("routes"); //response.results
            String points = jsonResults.getJSONObject(0).getJSONObject("overview_polyline").getString("points");
            Log.v(TAG, points);
            List<LatLng> LatLngs = PolyUtil.decode(points);
            LatLngBounds.Builder bounds = LatLngBounds.builder();

            PolylineOptions polylineOptions = new PolylineOptions();

            for (int i = 0; i< LatLngs.size(); i++){
                polylineOptions.add(LatLngs.get(i));
                bounds.include(LatLngs.get(i));
            }
            mMap.addPolyline(polylineOptions.color(Color.BLUE).width(10));
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),5));

            Log.v(TAG, LatLngs.toString());
        }catch (JSONException e){

        }

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
        Log.v(TAG, "Stopped!!!");

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

            String types = buildTypeString();
            Log.v(TAG, "onLocationChanged types= " + types);

            String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location.getLatitude()+","+location.getLongitude()+"&radius=500&type="+types+"&key=" + getString(R.string.google_place_key);
            Log.v(TAG, "url on location changed = " + url);
            last = new LatLng(location.getLatitude(), location.getLongitude());
            if(stuff.size() == 0){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(last));
            }
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

            // Adding the request to the PlacesRequestQueue
            PlacesRequestQueue.getInstance(this).addToRequestQueue(jsObjRequest);
        }
    }
    /*
   [{"geometry":{
   "location":{"lat":-33.8709434,"lng":151.1903114}

     */
    public void setRecent(JSONObject response){
        if(stuff.size() == 0) {
            mMap.clear();
            try {
                JSONArray jsonResults = response.getJSONArray("results"); //response.results
                places = new ArrayList<PlaceItem>();
                for (int i = 0; i < jsonResults.length(); i++) {

                    JSONObject resultItemObj = jsonResults.getJSONObject(i);
                    JSONObject location = resultItemObj.getJSONObject("geometry").getJSONObject("location");
                    LatLng ltlg = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                    String placeName = resultItemObj.getString("name");
                    String id = resultItemObj.getString("place_id");
                    String icon = resultItemObj.getString("icon");
                    String address = resultItemObj.getString("vicinity");
                    Double rating = 0.0;
                    if (!resultItemObj.isNull("rating")) {
                        rating = resultItemObj.getDouble("rating");
                    }
                    int priceLevel = 0;
                    if (!resultItemObj.isNull("price_level")) {
                        priceLevel = resultItemObj.getInt("price_level");
                    }
                    PlaceItem place = new PlaceItem(placeName, ltlg, icon, address, id, rating, priceLevel);
                    places.add(place);
                    Marker marker = mMap.addMarker(new MarkerOptions().position(ltlg).title(placeName).snippet("Click to see more!"));
                    marker.setTag(place);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(places.get(i).coordinates));


                }
            } catch (JSONException e) {
                Log.v(TAG, e.toString());
            }
        }
    }


    public void handleSearch(String search){
        stuff = new ArrayList<>();
        findViewById(R.id.btn_clear_dir).setVisibility(View.INVISIBLE);

        String url = "";
        String types = buildTypeString();
        Log.v(TAG, "handle search types = " + types);
        if (search != null) {
            url += "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+last.latitude+","+last.longitude+"&keyword="+search+"&radius=500&type="+types+"&key=" + getString(R.string.google_place_key);
        } else {
            url += "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + last.latitude + "," + last.longitude + "&radius=500&type=" + types + "&key=" + getString(R.string.google_place_key);
        }
        Log.v(TAG, "url on handle search = " + url);


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
        createFilterArray();
        controls.setVisibility(View.INVISIBLE);
        filterFragment = FilterFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map, filterFragment);
        ft.addToBackStack("Filter Fragment");
        ft.commit();
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
                placeListFragment = PlaceListFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.map, placeListFragment);
                ft.addToBackStack(null);
                ft.commit();
                item.setVisible(false);
                controls.setVisibility(View.INVISIBLE);
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

    @Override

    public void onApplyFilterButtonClicked() {
        controls.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map, mapFragment);
        ft.commit();
        handleSearch(null);
    }

    public void onItineraryChoose(PlaceItem item) {
        Log.v(TAG, "Should work");
        Log.v(TAG, getFragmentManager().toString());
        AddPlaceDialog.newInstance(item).show(getSupportFragmentManager(),"ChooseItinerary");

    }

    public ArrayList<PlaceItem> getPlaceList() {
        return places;
    }

    public ArrayList<FilterItem> getFilterList() {
        return placeTypeArray;
    }

    @Override
    public void onMapButtonClicked() {
        controls.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.map, mapFragment);
        ft.commit();
        MenuItem item = menu.findItem(R.id.mapList);
        item.setVisible(true);
    }

    private void createFilterArray() {

        placeTypeArray = new ArrayList<>();
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        for (String s : placeTypes) {
            boolean isChecked = sharedPref.getBoolean(s, true);
            Log.v(TAG, s + " " + isChecked);
            placeTypeArray.add(new FilterItem(s, isChecked));
        }
    }

    private String buildTypeString() {
        // get all place types that are checked
        List<String> filterStrings = new ArrayList<>();
        for (FilterItem item: placeTypeArray) {
            if(item.isSelected) {
                filterStrings.add(item.getType());
            }
        }

        String types = "";
        for (int i = 0; i < filterStrings.size() - 1; i++) {
            types += filterStrings.get(i).toLowerCase().replace(" ", "_") + "|";
        }
        types += filterStrings.get(filterStrings.size() - 1).toLowerCase().replace(" ", "_");
        Log.v(TAG, "types =" + types);

        return types;
    }

}
