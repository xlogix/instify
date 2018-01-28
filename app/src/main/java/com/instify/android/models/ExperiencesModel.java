package com.instify.android.models;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by krsnv on 1/27/2018.
 */

public class ExperiencesModel {
  private String id, title, description, author, imageUrl, category;
  private HashMap<String, Boolean> votes;
  @ServerTimestamp private Date timestamp;
  public ExperiencesModel() {
  }

  public ExperiencesModel(String id, String title, String description, String author,
      String imageUrl,
      String category, HashMap<String, Boolean> votes) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.author = author;
    this.imageUrl = imageUrl;
    this.category = category;
    this.votes = votes;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
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

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public HashMap<String, Boolean> getVotes() {
    return votes;
  }

  public void setVotes(HashMap<String, Boolean> votes) {
    this.votes = votes;
  }
}
