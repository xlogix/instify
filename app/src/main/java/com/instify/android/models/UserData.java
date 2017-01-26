package com.instify.android.models;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;

/**
 * Created by Arjun Mahishi on 18-Dec-16.
 */

public class UserData {

    public String name, regNo, section, dept;
    public int year;
    public boolean cr;

    public UserData() {
    }

    /**
     * Constructor for a new user
     */
    public UserData(String name, String regNo, String section) {

        //TODO : Fetch the name field data
        this.name = name;
        this.regNo = regNo.toLowerCase();
        this.section = section.toLowerCase();
        this.year = getYear();
        this.dept = getDept();
        this.cr = false;
        // Subscribe to the topic that the user belongs to
        subscribeToTopic();
    }

    /**
     * Constructor for updating all the info
     */
    public UserData(String name, String regNo, String section, String dept, String year) {
        this.name = name;
        this.regNo = regNo.toLowerCase();
        this.section = section.toLowerCase();
        this.year = getDigit(year.charAt(0));
        this.dept = dept.toUpperCase();
        this.cr = false;
    }

    private int getYear() {
        Calendar c = Calendar.getInstance();
        int userYear = c.get(Calendar.YEAR) - (2000 + (getDigit(this.regNo.charAt(2)) * 10 + getDigit(this.regNo.charAt(3))));
        if (c.get(Calendar.MONTH) + 1 >= 7) {
            return userYear + 1;
        }
        return userYear;
    }

    private String getDept() {
        switch (this.regNo.charAt(8)) {
            case '1':
                return "CIVIL";
            case '2':
                return "MECH";
            case '3':
                return "CSE";
            case '4':
                return "ECE";
            case '8':
                return "IT";
            default:
                return null;
        }
    }

    private String getSection() {
        return this.section;
    }

    private int getDigit(char c) {
        switch (c) {
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case '0':
                return 0;
            default:
                return -1;
        }
    }

    private void subscribeToTopic() {
        // Get token
        String token = FirebaseInstanceId.getInstance().getToken();
        // Get the topic to subscribe to
        String topic = getYear() + getDept() + getSection();
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
        // [END subscribe_topics]
    }
}
