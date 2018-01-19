package com.sakurafish.exam.location.api;

/**
 * Created by appy-saranya on 1/19/2018.
 */

public class LocationModel {

    public String latitude;
    public String longitude;

    public LocationModel() {
    }

    public LocationModel(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
