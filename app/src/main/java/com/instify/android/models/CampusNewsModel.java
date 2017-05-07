package com.instify.android.models;

/**
 * Created by arjun on 26/12/16.
 */

public class CampusNewsModel {

    public String title, description, author, dept, sec;

    public int level;

    public CampusNewsModel() {
    }

    public CampusNewsModel(CampusNewsModel snap) {
        this.title = snap.title;
        this.description = snap.description;
        this.author = snap.author;
    }

    public CampusNewsModel(String title, String description, String author, int level) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.level = level;
    }

    public CampusNewsModel(String t, String d, int level, String dept, String sec, String user) {
        this.title = t;
        this.description = d;
        this.author = user;
        this.level = level;

        this.dept = dept;
        this.sec = sec;
    }
}
