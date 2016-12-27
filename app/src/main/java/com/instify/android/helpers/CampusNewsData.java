package com.instify.android.helpers;

import android.widget.TextView;

/**
 * Created by arjun on 26/12/16.
 */

public class CampusNewsData {

    public String title, description;

    public CampusNewsData() {
    }

    public CampusNewsData(CampusNewsData snap) {
        this.title = snap.title;
        this.description = snap.description;
    }

    public CampusNewsData(TextView t, TextView d) {
        this.title = t.getText().toString();
        this.description = d.getText().toString();
    }

}
