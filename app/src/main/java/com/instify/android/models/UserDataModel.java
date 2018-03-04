package com.instify.android.models;

/**
 * Created by Arjun Mahishi on 18-Dec-16.
 */

public class UserDataModel {

    public String name, regNo, email, dept, image;
    public boolean cr;

    public UserDataModel() {
    }

    /**
     * Constructor for a new user
     */
    public UserDataModel(String name, String email, String image, String regno, String dept) {

        this.name = name;
        this.regNo = regno;
        this.email = email;
        this.image = image;
        this.dept = dept;
        this.cr = false;
    }

}
