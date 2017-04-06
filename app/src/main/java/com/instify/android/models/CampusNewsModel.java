package com.instify.android.models;

import com.klinker.android.link_builder.LinkBuilder;

/**
 * Created by arjun on 26/12/16.
 */

public class CampusNewsModel {

    public String title, description, author, dept, sec;

    public int level, year;

    public CampusNewsModel() {
    }

    public CampusNewsModel(CampusNewsModel snap) {
        this.title = snap.title;
        this.description = snap.description;
        this.author = snap.author;
    }

    public CampusNewsModel(String t, String d, int level, int year, String dept, String sec, String user) {
        this.title = t;
        this.description = d;
        this.author = user;
        this.level = level;
        this.year = year;
        this.dept = dept;
        this.sec = sec;
    }

}
