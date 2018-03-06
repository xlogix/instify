package com.instify.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arjun on 26/12/16.
 */

public class CampusNewsModel implements Parcelable {

  public String title, description, author;
  public Integer level;

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

  public static final Parcelable.Creator<CampusNewsModel> CREATOR =
      new Parcelable.Creator<CampusNewsModel>() {
        @Override public CampusNewsModel createFromParcel(Parcel source) {
          return new CampusNewsModel(source);
        }

        @Override public CampusNewsModel[] newArray(int size) {
          return new CampusNewsModel[size];
        }
      };

  protected CampusNewsModel(Parcel in) {
    this.title = in.readString();
    this.description = in.readString();
    this.author = in.readString();
    this.level = in.readInt();
  }

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.title);
    dest.writeString(this.description);
    dest.writeString(this.author);
    dest.writeInt(this.level);
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }
}
