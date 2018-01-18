package com.sakurafish.exam.location.api;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by appy-saranya on 1/18/2018.
 */

public class LocationService extends IntentService {

    String time;
    private GoogleLocationService googleLocationService;

    public LocationService() {
        super("LocationService");
    }
LocationService locationService=new LocationService();

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

                     time = "updated location %1$s %2$s"+ Double.toString(location.getLatitude())+ Double.toString(location.getLongitude());
                    Toast.makeText(context,time,Toast.LENGTH_LONG).show();
                    


                }
            }

            @Override
            public void updateLocationName(String localityName, Location location) {

                googleLocationService.stopLocationUpdates();
            }
        });
        googleLocationService.startUpdates();
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

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        updateLocation(getApplicationContext());

    }


}
