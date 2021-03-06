package com.sakurafish.exam.location.api;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      startService(new Intent(getBaseContext(), LocationService.class));
      showMap();

    }

    private void showMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}