package com.instify.android.helpers;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String isCR;
    public String dept;
    public String email;
    public String name;
    public String regno;
    public String section;
    public String year;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String isCR, String dept, String email, String name, String regno, String section, String year) {
        this.isCR = isCR;
        this.dept = dept;
        this.name = name;
        this.email = email;
        this.regno = regno;
        this.section = section;
        this.year = year;
    }
}
