package com.instify.android.models;

import java.util.HashMap;

/**
 * Created by krsnv on 1/27/2018.
 */

public class ExperiencesModel {
  private String title, description, author, imageUrl, category;
  private HashMap<String, Boolean> votes;

  public ExperiencesModel() {
  }

  public ExperiencesModel(String title, String description, String author, String imageUrl,
      String category, HashMap<String, Boolean> votes) {
    this.title = title;
    this.description = description;
    this.author = author;
    this.imageUrl = imageUrl;
    this.category = category;
    this.votes = votes;
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
