package com.instify.android.ux.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.instify.android.R;

public class planeTestFrag extends Fragment {

    public planeTestFrag() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static planeTestFrag newInstance() {
        planeTestFrag fragment = new planeTestFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plane_test, container, false);
    }
}
