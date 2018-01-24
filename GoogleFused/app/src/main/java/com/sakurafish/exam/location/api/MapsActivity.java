package com.sakurafish.exam.location.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private TextView mMsgView;
    String latitude;
    String longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        mMsgView = (TextView) findViewById(R.id.msgView);

        latitude = "0";
        longitude = "0";

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        latitude = intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                        longitude = intent.getStringExtra(LocationService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {
                            mMsgView.setText(getString(R.string.project_id) + "\n Latitude : " + latitude + "\n Longitude: " + longitude);

                            moveMarker();
                        }
                    }
                }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    Double lat,lng;
    Marker m;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        
        mMap = googleMap;
        mMap.isMyLocationEnabled();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if (latitude != null && longitude != null) {
            lat = Double.parseDouble(latitude);
            lng = Double.parseDouble(longitude);
        } else {
            lat = 0.0;
            lng = 0.0;
        }

       m = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Appiness"));
    }

     public void moveMarker()
     {
         if(mMap!=null && m!=null) {
             if (latitude != null && longitude != null) {
                 lat = Double.parseDouble(latitude);
                 lng = Double.parseDouble(longitude);
             } else {
                 lat = 0.0;
                 lng = 0.0;
             }

             m.setPosition(new LatLng(lat,lng));

             CameraPosition cameraPosition = CameraPosition.builder()
                     .target(new LatLng(lat, lng))
                     .zoom(50)
                     .bearing(0)
                     .tilt(65)
                     .build();
//     marker = mMap.addMarker(new MarkerOptions().position(mLatLng).title("My Title").snippet("My Snippet").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

             mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14));
         }
     }


}
