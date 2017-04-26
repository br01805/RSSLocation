package com.example.brianr.rsslocation;
import com.example.brianr.rsslocation.adapter.PostItemAdapter;
import com.example.brianr.rsslocation.vo.PostData;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.Dialog;
import android.location.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.*;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.*;
import android.content.res.*;
import android.util.*;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.support.v7.widget.*;
import java.io.IOException;
import java.util.*;



/*Team: Brian Rawls
        Jayla Greely
        Tierney Ridley

        */

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnMapLongClickListener {
    private PostData[] listData;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String knownName;
    private String MapKeySearch;

    GoogleMap mGoogleMap;
    private static final LatLng MAVELIKARA = new LatLng(9.251086, 76.538452);
    LatLng currentlocation;
    Geocoder geocoder;
    List<Address> addresses;
    Marker m;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleServicesAvailable()) {
            Toast.makeText(this, "Perfect!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            initMap();

        } else {

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.morebuttons, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rss:
                Intent intent = new Intent(this, RSSMainActivity.class);
                if(MapKeySearch!=null) {
                    MapKeySearch = MapKeySearch.toLowerCase();
                    intent.putExtra("keysearch", MapKeySearch);
                }
                startActivity(intent);
               // finish();

                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();

        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;

        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void onMapLongClick(LatLng point) {
        if (m == null) { //if marker exists (not null or whatever)
            m = mGoogleMap.addMarker(new MarkerOptions()
                    .position(point)
                    .draggable(true)
                    .title("Tap To Delete")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            LatLng position = m.getPosition();//

            Toast.makeText(
                    MainActivity.this,
                    getAddress(position),
                    Toast.LENGTH_LONG).show();
            //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 21));
        } else {
            m.setPosition(point);
            LatLng position = m.getPosition();//

            Toast.makeText(
                    MainActivity.this,
                    getAddress(position),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void onMapReady(GoogleMap googleMap) {


        mGoogleMap = googleMap;



        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mGoogleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }


        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mGoogleMap.setOnMapLongClickListener(this);

        mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                // Remove the marker
                marker.remove();
                m = null;
            }
        });


        /*mGoogleMap.addMarker(new MarkerOptions().position(MAVELIKARA)
                .title("Marker")
                .draggable(true)
                .snippet("Hello")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MAVELIKARA, 13));*/


        mGoogleMap.setOnMarkerDragListener(new OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
                // TODO Auto-generated method stub
                // Here your code
                Toast.makeText(MainActivity.this, "Dragging Start",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // TODO Auto-generated method stub
                LatLng position = marker.getPosition();//

                Toast.makeText(
                        MainActivity.this,
                        getAddress(position),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                // TODO Auto-generated method stub
                // Toast.makeText(MainActivity.this, "Dragging",
                // Toast.LENGTH_SHORT).show();
                System.out.println("Draagging");
            }
        });
    }

    public String getAddress(LatLng position2) {

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(position2.latitude, position2.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Can't find address",
                    Toast.LENGTH_LONG).show();
        }

        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        city = addresses.get(0).getLocality();
        state = addresses.get(0).getAdminArea();
        country = addresses.get(0).getCountryName();
        postalCode = addresses.get(0).getPostalCode();
        knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        if(country != null)
        MapKeySearch = country;


        return city + ", " + state + ", " + country;

    }

    public boolean onMarkerClick(final Marker marker) {

        if (marker.equals(mGoogleMap)) {
            // handle click here
            // map.getMyLocation();
            System.out.println("Clicked");
            double lat = mGoogleMap.getMyLocation().getLatitude();
            System.out.println("Lat" + lat);
            Toast.makeText(MainActivity.this,
                    "Current location " + mGoogleMap.getMyLocation().getLatitude(),
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void generateDummyData() {
        PostData data = null;
        listData = new PostData[10];
        for (int i = 0; i < 10; i++) { //please ignore this comment :>
            data = new PostData();
            data.postDate = "May 20, 2013";
            data.postTitle = "Post " + (i + 1) + " Title: This is the Post Title from RSS Feed";
            data.postThumbUrl = null;
            listData[i] = data;
        }
    }


}