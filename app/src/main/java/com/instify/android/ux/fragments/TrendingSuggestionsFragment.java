package com.instify.android.ux.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Abhish3k on 08-03-2017.
 */

public class TrendingSuggestionsFragment extends Fragment {

    public TrendingSuggestionsFragment() {
    }

    public static TrendingSuggestionsFragment newInstance() {
        TrendingSuggestionsFragment frag = new TrendingSuggestionsFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
