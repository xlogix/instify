package com.instify.android.models;

/**
 * Created by Abhish3k on 7/2/2016. // Using Firebase Database
 */

public class ChatModelMessage {

    private String id;
    private String text;
    private String email;
    private String photoUrl;

    public ChatModelMessage() {
    }

    public ChatModelMessage(String text, String name, String photoUrl) {
        this.text = text;
        this.email = name;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return email;
    }

    public void setName(String name) {
        this.email = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}