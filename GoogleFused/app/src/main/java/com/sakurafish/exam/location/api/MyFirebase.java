package com.sakurafish.exam.location.api;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by appy-saranya on 1/18/2018.
 */

public class MyFirebase {

    MyFirebase firebase=new MyFirebase();

    public void newFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        DatabaseReference myNewRef = myRef.child("Events").push();
        String pushedKey = myNewRef.getKey();
        // myNewRef.setValue(event);
    }
}
