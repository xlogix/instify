package com.instify.android.models;

/**
 * Created by krsnv on 23-Apr-17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewsItemModel {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("snip")
    @Expose
    private String snip;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("date")
    @Expose
    private String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnip() {
        return snip;
    }

    public void setSnip(String snip) {
        this.snip = snip;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}


