package com.example.brianr.rsslocation;

import android.content.pm.PackageManager;
import android.Manifest;
import android.content.*;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;


import android.view.View;
import android.widget.Toast;
import android.widget.*;
import android.location.*;
import android.app.Dialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

//Author: Brian Rawls


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    private Button button;
    private TextView textView;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(googleServicesAvailable()){
            Toast.makeText(this, "Perfect!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            initMap();
        }else{

        }




        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                textView.append("\n " + location.getLatitude() + " " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET

                }, 10);
                return;
            }
        } else {
            configureButton();


        }

    }


        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
            switch (requestCode) {
                case 10:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        configureButton();
                    return;
            }
        }


    private void configureButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
                  public void onClick(View view){
                locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
            }
        });
        }


    private void initMap(){
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }


 public boolean googleServicesAvailable() {
     GoogleApiAvailability api = GoogleApiAvailability.getInstance();

     int isAvailable = api.isGooglePlayServicesAvailable(this);
     if(isAvailable == ConnectionResult.SUCCESS){
         return true;

 } else if (api.isUserResolvableError(isAvailable)){
         Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
         dialog.show();
     } else {
         Toast.makeText(this, "Cant connect to play services", Toast.LENGTH_LONG).show();
     }
 return false;
 }

    public void onMapReady(GoogleMap googleMap){
        mGoogleMap = googleMap;
}
}
