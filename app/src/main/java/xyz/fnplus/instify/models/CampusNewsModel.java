package xyz.fnplus.instify.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arjun on 26/12/16.
 */

public class CampusNewsModel implements Parcelable {

    public static final Parcelable.Creator<CampusNewsModel> CREATOR = new Parcelable.Creator<CampusNewsModel>() {
        @Override
        public CampusNewsModel createFromParcel(Parcel source) {
            return new CampusNewsModel(source);
        }

        @Override
        public CampusNewsModel[] newArray(int size) {
            return new CampusNewsModel[size];
        }
    };
    public String title, description, author, dept, sec;
    private int level;

    public CampusNewsModel() {
    }

    public CampusNewsModel(CampusNewsModel snap) {
        this.title = snap.title;
        this.description = snap.description;
        this.author = snap.author;
    }

    public CampusNewsModel(String title, String description, String author, Integer level) {
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

    protected CampusNewsModel(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.author = in.readString();
        this.level = in.readInt();
        this.sec = in.readString();
        this.dept = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.author);
        dest.writeInt(this.level);
        dest.writeString(this.sec);
        dest.writeString(this.dept);
    }
}
