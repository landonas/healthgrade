package ics466.healthscore;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener,
        GoogleMap.OnMarkerClickListener{


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Parse.initialize(this, "oOWEk7eg4o8FOf0YlSMrYfRmLJznD8msIAZpVNGf", "rtd3rLy2QlWjsCWQobF0HEOVR99Pk93DLz8mif1O");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest= LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        ParseQueryMap();
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener)this);
        if(location == null){

        }
        else{
            handleNewLocation(location);

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Enable MyLocation Layer of Google Map

        mMap.addMarker(new MarkerOptions().position(new LatLng(50, 50)).title("WUBWUBWUB").snippet("Snippet"));

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void handleNewLocation(Location location){
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        Marker options = mMap.addMarker( new MarkerOptions()
                .position(latLng)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));


        //mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

        Log.d(TAG, location.toString());
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public void ParseQueryMap(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Store");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> stores, ParseException e) {
                double lat;
                double longi;
                String storeName;
                int tempScore;

                if (e == null) {
                    Log.d("score", "Retrieved " + stores.size() + " scores");
                    for(int i = 0; i < stores.size(); i++){
                        lat = stores.get(i).getParseGeoPoint("storeCoordinates").getLatitude();
                        longi = stores.get(i).getParseGeoPoint("storeCoordinates").getLongitude();
                        Location cooL = new Location("test");
                        storeName = stores.get(i).getString("storeName");
                        cooL.setLatitude(lat);
                        cooL.setLongitude(longi);
                        tempScore = stores.get(i).getInt("currentScore");
                        if(tempScore == 0){
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).title(storeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        }else if(tempScore == 1){
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).title(storeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                        }else if(tempScore == 2){
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).title(storeName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        }else {
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, longi)).title(storeName));
                        }
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    //Marker click action
    public boolean onMarkerClick(Marker marker) {
        File tempRed;
        File tempGreen;
        File tempYellow;


        if(!marker.getTitle().equalsIgnoreCase("You are here")) {
            ArrayList<String> tempArray = new ArrayList<String>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Store");
            query.whereEqualTo("storeName", marker.getTitle());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (object == null) {
                        Log.d("score", "The getFirst request failed.");
                    } else {
                        Log.d("score", "Retrieved the object.");
                        ParseImageView imageView = (ParseImageView) findViewById(R.id.parseImage);
                        ParseFile image = object.getParseFile("healthScore");
                        imageView.setParseFile(image);
                        imageView.loadInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Log.d("test", "We've got data in data.");
                                    // use data for something

                                } else {
                                    Log.d("test", "There was a problem downloading the data.");
                                }
                            }
                        });
////Testing out to add shit dynamically
// proof of concept for now
                       ArrayList<String> tempArray = new ArrayList<String>();
                       tempArray = (ArrayList<String>)object.get("history");
                       int loopSize = tempArray.size();


                       LinearLayout tempLayout = (LinearLayout) findViewById(R.id.idHistory);
                       TextView past = new TextView(MapsActivity.this);
                       past.setTypeface(null, Typeface.BOLD_ITALIC);
                       past.setText("Past History");

                       tempLayout.removeAllViews();
                        //idk wtf im doing
                       RelativeLayout.LayoutParams tempAlpha = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                       tempAlpha.addRule(RelativeLayout.CENTER_IN_PARENT);
                       tempLayout.addView(past,tempAlpha);
                       ImageView tempView = null;
                       ImageView previous = null;
                       TextView tempText = null;
                       String temp = "";
                       RelativeLayout.LayoutParams flp = null;
                       RelativeLayout.LayoutParams flz = null;

                       String[] result;
                       for(int t = 0; t < loopSize; t++){
                           result = tempArray.get(t).split("\\s+");
                            tempView = new ImageView(MapsActivity.this);
                           tempText = new TextView(MapsActivity.this);
                           tempText.setText(result[0]);
                           tempView.setId(t);
                            flp = new RelativeLayout.LayoutParams(200,200);

                           flz = new RelativeLayout.LayoutParams(
                                   RelativeLayout.LayoutParams.WRAP_CONTENT,
                                   RelativeLayout.LayoutParams.WRAP_CONTENT
                           );


                           //temp test
                           if(result[1].equalsIgnoreCase("red")){
                               tempView.setImageResource(R.drawable.red);
                           }
                           if(result[1].equalsIgnoreCase("green")) {
                               tempView.setImageResource(R.drawable.green);
                           }
                           if(result[1].equalsIgnoreCase("yellow")) {
                               tempView.setImageResource(R.drawable.yellow);
                           }

                           if(t != 0) {
                               flp.addRule(RelativeLayout.BELOW, previous.getId());
                               flz.addRule(RelativeLayout.BELOW, tempView.getId());
                           }else{
                               flp.addRule(RelativeLayout.CENTER_IN_PARENT);
                               flz.addRule(RelativeLayout.CENTER_IN_PARENT);

                           }

                           tempLayout.addView(tempText,flz);
                           tempLayout.addView(tempView, flp);
                           previous = tempView;
                        }
                        TextView vText = (TextView) findViewById(R.id.addressHolder);
                        vText.setText((String)object.get("Address"));

                        TextView zText = (TextView)findViewById(R.id.parkingHolder);
                        zText.setText((String)object.get("Parking"));
                    }
                }
            });

            TextView mText = (TextView) findViewById(R.id.nameHolder);
            mText.setText(marker.getTitle());

            //TextView mAddress = (TextView) findViewByID(R.id.add)
        }
        return false;
    }


    public void sendUpdate(View view){
        Intent intent = new Intent(this, UpdateActivty.class);
        startActivity(intent);
    }
}
