package com.example.brianr.rsslocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import android.widget.Toast;
import android.app.Dialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(googleServicesAvailable()){
            Toast.makeText(this, "Perfect!", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
            initMap();
        }else{

        }
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
