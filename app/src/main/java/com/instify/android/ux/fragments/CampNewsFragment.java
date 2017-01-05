package com.instify.android.ux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.helpers.CampusNewsData;
import com.instify.android.upload.UploadNews;
import com.instify.android.ux.MainActivity;

/**
 * Created by Abhish3k on 2/23/2016.
 */

public class CampNewsFragment extends Fragment {

    RecyclerView recyclerView;
    // Firebase declarations
    DatabaseReference dbRef, campusRef;
    FirebaseRecyclerAdapter<CampusNewsData, CampusViewHolder> fAdapter;

    public CampNewsFragment() {
    }

    public static CampNewsFragment newInstance() {
        CampNewsFragment frag = new CampNewsFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).mSharedFab = null; // To avoid keeping/leaking the reference of the FAB
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campus_news, container, false);

        // Recycler view set up //
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_campus_news);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        // Firebase database setup //
        dbRef = FirebaseDatabase.getInstance().getReference();
        campusRef = dbRef.child("CampusNews");
        fAdapter = new FirebaseRecyclerAdapter<CampusNewsData, CampusViewHolder>(
                CampusNewsData.class, R.layout.card_view_campus, CampusViewHolder.class, campusRef) {
            @Override
            protected void populateViewHolder(final CampusViewHolder viewHolder, CampusNewsData model, int position) {
                viewHolder.campusTitle.setText(model.title);
                viewHolder.campusDescription.setText(model.description);
                viewHolder.campusAuthor.setText(model.author);
            }
        };

        ((MainActivity) getActivity()).mSharedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UploadNews.class);
                i.putExtra("username", ((MainActivity) getActivity()).userInfoObject.name);
                startActivity(i);
            }
        });

        recyclerView.setAdapter(fAdapter);
        return rootView;
    }

    private static class CampusViewHolder extends RecyclerView.ViewHolder {
        TextView campusTitle, campusDescription, campusAuthor;

        public CampusViewHolder(View v) {
            super(v);
            campusTitle = (TextView) v.findViewById(R.id.campusTitle);
            campusAuthor = (TextView) v.findViewById(R.id.campusAuthor);
            campusDescription = (TextView) v.findViewById(R.id.campusDescription);
        }
    }
}