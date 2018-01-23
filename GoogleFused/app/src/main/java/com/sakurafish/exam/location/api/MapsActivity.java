package com.sakurafish.exam.location.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    private TextView mMsgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        mMsgView = (TextView) findViewById(R.id.msgView);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationService.EXTRA_LONGITUDE);

                        if (latitude != null && longitude != null) {
                            mMsgView.setText(getString(R.string.project_id) + "\n Latitude : " + latitude + "\n Longitude: " + longitude);
                        }
                    }
                }, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(12.9354649, 77.6129076))
                .title("Marker"));

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(new LatLng(12.9354649, 77.6129076))
                .zoom(20)
                .bearing(0)
                .tilt(65)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(12.9354649, 77.6129076), 14));
    }

}
