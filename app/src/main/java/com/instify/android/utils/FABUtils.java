package com.instify.android.utils;

/**
 * Created by Abhish3k on 6/30/2016.
 */

import android.content.Context;
import android.content.res.TypedArray;

import com.instify.android.R;

public class FABUtils {

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }
}
