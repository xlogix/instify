package com.instify.android.models;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;

/**
 * Created by Arjun Mahishi on 18-Dec-16.
 */

public class UserDataFirebase {

    public String name, regNo, email, dept, image;
    public boolean cr;

    public UserDataFirebase() {
    }

    /**
     * Constructor for a new user
     */
    public UserDataFirebase(String name, String email, String image, String regno, String dept) {

        this.name = name;
        this.regNo = regno;
        this.email = email;
        this.image = image;
        this.dept = dept;
        this.cr = false;
    }

}
