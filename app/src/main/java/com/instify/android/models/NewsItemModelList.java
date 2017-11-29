package com.instify.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by krsnv on 23-Apr-17.
 */

public class NewsItemModelList {
    @SerializedName("newsItems")
    @Expose
    private List<NewsItemModel> newsItems = null;

    public List<NewsItemModel> getNewsItems() {
        return newsItems;
    }

    public void setNewsItems(List<NewsItemModel> newsItems) {
        this.newsItems = newsItems;
    }
}