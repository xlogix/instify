package com.instify.android.models;

/**
 * Created by krsnv on 01-May-17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TestPerformanceModel {

  @SerializedName("name") @Expose private String name;
  @SerializedName("subjects") @Expose private List<SubjectsModel> subjects = null;

  public TestPerformanceModel(String name, List<SubjectsModel> subjects) {
    this.name = name;
    this.subjects = subjects;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<SubjectsModel> getSubjects() {
    return subjects;
  }

  public void setSubjects(List<SubjectsModel> subjects) {
    this.subjects = subjects;
  }
}