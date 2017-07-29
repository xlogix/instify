package com.instify.android.models;

/**
 * Created by Arjun Mahishi on 18-Dec-16.
 */

public class FirebaseUserDataModel {

    public String name, regNo, email, dept, image;
    public boolean cr;

    public FirebaseUserDataModel() {
    }

    /**
     * Constructor for a new user
     */
    public FirebaseUserDataModel(String name, String email, String image, String regno, String dept) {

        this.name = name;
        this.regNo = regno;
        this.email = email;
        this.image = image;
        this.dept = dept;
        this.cr = false;
    }

}
