package com.instify.android.models;

import android.widget.TextView;

import java.util.Date;

/**
 * Created by arjun on 26/12/16.
 */

public class CampusNewsModel {

    public String title, description, author;
    private long time;
    private int level;

    public CampusNewsModel() {
    }

    public CampusNewsModel(CampusNewsModel snap) {
        this.title = snap.title;
        this.description = snap.description;
        this.author = snap.author;
        // Initialize to current time
        time = new Date().getTime();
    }

    public CampusNewsModel(TextView t, TextView d, int level, String user) {
        this.title = t.getText().toString();
        this.description = d.getText().toString();
        this.author = user;
        this.level = level;
    }

}
