package com.sakurafish.exam.location.api;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    ArrayList markerPoints= new ArrayList();
    private TextView mMsgView;
    String latitude;
    String longitude;
   /* LatLng origin;
    LatLng dest;
    Polyline line;*/

    //     List<Overlay> mapOverlays;
//    GeoPoint point1, point2;
    LocationManager locManager;
    Drawable drawable;
    Document document;
    GMapV2GetRouteDirection v2GetRouteDirection;
    LatLng fromPosition;
    LatLng toPosition;
    MarkerOptions markerToOptions;
    MarkerOptions markerFromOptions;
    Location location;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        mMsgView = (TextView) findViewById(R.id.msgView);


        //spinner element
        spinner= (Spinner) findViewById(R.id.spinner);

        //spinner clik listener
        spinner.setOnItemSelectedListener(this);

        //spinner drop down list
        List<String> categories=new ArrayList<String>();
        categories.add("walking");
        categories.add("driving");
        categories.add("bicycle");
        categories.add("transit");

        //attaching data adapter to spinner
        ArrayAdapter<String> dataAdapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, categories);

        //drop down layout style- list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to the spinner
        spinner.setAdapter(dataAdapter);


        latitude = "0";
        longitude = "0";

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                latitude = intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                longitude = intent.getStringExtra(LocationService.EXTRA_LONGITUDE);
                if (latitude != null && longitude != null) {
                    mMsgView.setText(getString(R.string.project_id) + "\n Latitude : " + latitude + "\n Longitude: " + longitude);

//                    String a = GMapV2GetRouteDirection.

                    moveMarker();
                }
            }}, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST)
        );

        v2GetRouteDirection = new GMapV2GetRouteDirection();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //  mMap = mapFragment.getMap();
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        GetRouteTask getRoute = new GetRouteTask(spinner.getSelectedItem().toString());
        getRoute.execute();

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    Double lat, lng;
    Marker m;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setAllGesturesEnabled(true);
                mMap.setTrafficEnabled(true);
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                markerToOptions = new MarkerOptions();
                markerFromOptions = new MarkerOptions();
        /*fromPosition = new LatLng(12.9354649, 77.6129076);
        toPosition = new LatLng(11.723512, 78.466287);*/

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = (LatLng) markerPoints.get(0);
                    LatLng dest = (LatLng) markerPoints.get(1);

                    fromPosition = origin;
                    toPosition = dest;

                   // String url = getDirectionsUrl(origin, dest);

                    GetRouteTask getRoute = new GetRouteTask(spinner.getSelectedItem().toString());
                    getRoute.execute();

                }
                    mMap.isMyLocationEnabled();
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    if (latitude != null && longitude != null) {
                        lat = Double.parseDouble(latitude);
                        lng = Double.parseDouble(longitude);
                    } else {
                        lat = 0.0;
                        lng = 0.0;
                    }
                 /*   m = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title("Appiness"));*/
            }
        });


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {


            }

            @Override
            public void onMarkerDrag(Marker marker) {


                LatLng pos = marker.getPosition();
                toPosition = pos;


            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                // String url = getDirectionsUrl(origin, dest);

                GetRouteTask getRoute = new GetRouteTask(spinner.getSelectedItem().toString());
                getRoute.execute();


            }
        });



            }

    public void moveMarker() {
                if (mMap != null && m != null) {
                    if (latitude != null && longitude != null) {
                        lat = Double.parseDouble(latitude);
                        lng = Double.parseDouble(longitude);
                    } else {
                        lat = 0.0;
                        lng = 0.0;
                    }


                    m.setPosition(new LatLng(lat, lng));

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

    private class GetRouteTask extends AsyncTask<String, Void, String> {

        private ProgressDialog Dialog;
        String response = "";

        String mode1;

        public GetRouteTask(String mode)
        {
            this.mode1 = mode;
        }

        @Override
        protected void onPreExecute() {
            Dialog = new ProgressDialog(MapsActivity.this);
            Dialog.setMessage("Loading route...");
            Dialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            //Get All Route values
            if(fromPosition!=null && toPosition!=null) {
                document = v2GetRouteDirection.getDocument(fromPosition, toPosition, mode1);
                response = "Success";
            }

            return response;

        }

        @Override
        protected void onPostExecute(String result) {

            if(response.equalsIgnoreCase("Success")){
                drawPolyline();
            }

            Dialog.dismiss();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void drawPolyline()
    {

        mMap.clear();
        ArrayList points = null;


        ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
        PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.BLUE);

        for (int i = 0; i < directionPoint.size(); i++) {
            rectLine.add(directionPoint.get(i));

        }
        // Adding route on the map
        mMap.addPolyline(rectLine);

        markerFromOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerFromOptions.draggable(true);
        markerToOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerToOptions.draggable(true);


        markerToOptions.position(toPosition);
        markerFromOptions.position(fromPosition);
        mMap.addMarker(markerToOptions);
        mMap.addMarker(markerFromOptions);
    }


}

