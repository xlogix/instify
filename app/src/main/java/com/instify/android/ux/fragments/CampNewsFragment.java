package com.instify.android.ux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.models.CampusNewsModel;
import com.instify.android.ux.ChatActivity;
import com.instify.android.ux.MainActivity;
import com.instify.android.ux.UploadNewsActivity;

/**
 * Created by Abhish3k on 2/23/2016.
 */

public class CampNewsFragment extends Fragment {

    RecyclerView recyclerView;

    // Firebase Declarations
    DatabaseReference dbRef, campusRefAll, userRef, campusRefDeptAll;
    FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder> fAdapterAll, fAdapterDeptAll, fAdapterDeptSec;
    FirebaseUser currentUser;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_campus_news, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Recycler view set up //
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_campus_news);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Firebase database setup //

//        createAdapter("campusNews/all", fAdapterAll);
//        createAdapter("campusNews/IT/all", fAdapterDeptAll);

        campusRefAll = FirebaseDatabase.getInstance().getReference().child("campusNews/all");
        fAdapterAll = new FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder>(
                CampusNewsModel.class,
                R.layout.card_view_campus,
                CampusViewHolder.class,
                campusRefAll) {

            @Override
            protected void populateViewHolder(final CampusViewHolder holder, final CampusNewsModel model, final int position) {
                holder.campusTitle.setText(model.title);
                holder.campusDescription.setText(model.description);
                holder.campusAuthor.setText(model.author);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchChat = new Intent(view.getContext(), ChatActivity.class);
                        launchChat.putExtra("localNewsId", fAdapterAll.getRef(position).getKey());
                        startActivity(launchChat);
                    }
                });
            }
        };

        ///  >><<
        // After the user has logged in
//        if(currentUser != null) {
//            dbRef = FirebaseDatabase.getInstance().getReference();
//            userRef = dbRef.child("users").child(currentUser.getUid());
//            userRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    UserData userData = dataSnapshot.getValue(UserData.class);
//                    Toast.makeText(getActivity(), "data collected locally", Toast.LENGTH_SHORT).show();
//
//                    String address = "campusNews/" + userData.dept + "/all";
//                    Toast.makeText(getActivity(), address, Toast.LENGTH_SHORT).show();
//                    campusRefDeptAll = FirebaseDatabase.getInstance().getReference().child("campusNews/IT/all");
//                    fAdapterAll = new FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder>(
//                            CampusNewsModel.class,
//                            R.layout.card_view_campus,
//                            CampusViewHolder.class,
//                            campusRefDeptAll) {
//                        @Override
//                        protected void populateViewHolder(final CampusViewHolder holder, final CampusNewsModel model, final int position) {
//                            holder.campusTitle.setText(model.title);
//                            holder.campusDescription.setText(model.description);
//                            holder.campusAuthor.setText(model.author);
//                            holder.mView.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Intent launchChat = new Intent(view.getContext(), ChatActivity.class);
//                                    launchChat.putExtra("localNewsId", fAdapterAll.getRef(position).getKey());
//                                    startActivity(launchChat);
//                                }
//                            });
//
//                        }
//                    };
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
        /// >><<

        /*((MainActivity) getActivity()).mSharedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UploadNewsActivity.class);
                try {
                    i.putExtra("username", ((MainActivity) getActivity()).userInfoObject.name);
                    startActivity(i);
                } catch (NullPointerException e) {
                    Toast.makeText(getActivity(), "Not ready to announce news yet. Check your internet connection",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        recyclerView.setAdapter(fAdapterAll);
        return rootView;
    }

    public static class CampusViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView campusTitle;
        public TextView campusDescription;
        public TextView campusAuthor;

        public CampusViewHolder(View v) {
            super(v);
            mView = v;
            campusTitle = (TextView) v.findViewById(R.id.campusTitle);
            campusAuthor = (TextView) v.findViewById(R.id.campusAuthor);
            campusDescription = (TextView) v.findViewById(R.id.campusDescription);
        }
    }
}
