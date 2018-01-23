package com.sakurafish.exam.location.api;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by appy-saranya on 1/18/2018.
 */

public class LocationService extends Service {

    String time;
    private GoogleLocationService googleLocationService;

    //firebase initialisation
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String locationID;
    public static final String ACTION_LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    private static final String TAG = LocationService.class.getSimpleName();

  /*  public LocationService() {
        super("LocationService");
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        //start the handler for getting locations
        //create component
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateLocation(getApplicationContext());

        //firebase instance
        mFirebaseInstance = FirebaseDatabase.getInstance();
        //get reference to location node
        mFirebaseDatabase = mFirebaseInstance.getReference("Location");

        return Service.START_STICKY;
    }

    //get current location os user
    private void updateLocation(final Context context) {
        googleLocationService = new GoogleLocationService(context, new LocationUpdateListener() {
            @Override
            public void canReceiveLocationUpdates() {
            }

            @Override
            public void cannotReceiveLocationUpdates() {
            }

            //update location to our servers for tracking purpose
            @Override
            public void updateLocation(Location location) {
                if (location != null ) {

                    //Timber.e("updated location %1$s %2$s", location.getLatitude(), location.getLongitude());

                    sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

                  /*  Double lat=location.getLatitude();
                    Double lng=location.getLongitude();

                     time = "updated location %1$s %2$s"+ Double.toString(lat)+ Double.toString(lng);
                    Toast.makeText(context,time,Toast.LENGTH_LONG).show();


                    // firebase create every time new updation
                    if (TextUtils.isEmpty(locationID)){
                        createLocation(Double.toString(lat),Double.toString(lng));
                        locationID=null;
                    }*/
                }
            }

            private void sendMessageToUI(String lat, String lng) {

                Log.d(TAG, "Sending info...");
                Toast.makeText(getBaseContext(), "Updated location", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
                intent.putExtra(EXTRA_LATITUDE, lat);
                intent.putExtra(EXTRA_LONGITUDE, lng);
                //firebase create location every time new........
                if (TextUtils.isEmpty(locationID)){
                    createLocation(lat,lng);
                    locationID=null;
                }

                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

            }

            @Override
            public void updateLocationName(String localityName, Location location) {

                googleLocationService.stopLocationUpdates();
            }
        });
        googleLocationService.startUpdates();
    }

    private void createLocation(String lat, String lng) {

        if (TextUtils.isEmpty(locationID)){
            locationID= mFirebaseDatabase.push().getKey();

            LocationModel locationModel = new LocationModel(lat,lng);

            mFirebaseDatabase.child(locationID).setValue(locationModel);

            addLocationChangeListner();
        }

    }


    IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public LocationService getServerInstance() {
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

  /*  @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        updateLocation(getApplicationContext());

    }*/
  @Override
  public void onDestroy() {
      super.onDestroy();
      if (googleLocationService != null) {
          googleLocationService.stopLocationUpdates();
      }
  }

    private void addLocationChangeListner(){
        mFirebaseDatabase.child(locationID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);
                // Check for null
                if (locationModel == null) {
                    Log.e(TAG, "Location data is null!");
                    return;
                }

                Log.e(TAG, "Location data is changed!" + locationModel.latitude + ", " + locationModel.longitude);

                // Display newly updated latitude and longitude
                //txtDetails.setText(user.latitude + ", " + user.longitude);

//                // clear edit text
//                lt.setText("");
//                ilng.setText("");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", databaseError.toException());

            }
        });
    }
}