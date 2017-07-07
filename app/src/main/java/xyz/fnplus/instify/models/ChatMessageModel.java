package xyz.fnplus.instify.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Abhish3k on 7/2/2016. // Using Firebase Database
 */

public class ChatMessageModel {

    private String id;
    private String text;
    private String email;
    private String photoUrl;
    private String imageUrl;
    private long time;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String text, String name, String photoUrl, String imageUrl) {
        this.text = text;
        this.email = name;
        this.photoUrl = photoUrl;
        this.imageUrl = imageUrl;

        // Initialize to current time
        time = new Date().getTime();
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getdatefromstamp() {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

        return simpleDateFormat.format(date);
    }

    public String gettimefromstamp() {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.UK);

        return simpleDateFormat.format(date);
    }
}