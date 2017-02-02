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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.models.CampusNewsModel;
import com.instify.android.upload.UploadNews;
import com.instify.android.ux.ChatActivity;
import com.instify.android.ux.MainActivity;

/**
 * Created by Abhish3k on 2/23/2016.
 */

public class CampNewsFragment extends Fragment {

    RecyclerView recyclerView;
    // Firebase declarations
    DatabaseReference campusRefAll;
    FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder> fAdapter, fAdapter1;

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
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // Firebase database setup //
        campusRefAll = FirebaseDatabase.getInstance().getReference().child("campusNews/all");
        fAdapter = new FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder>(
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
                        launchChat.putExtra("localNewsId", fAdapter.getRef(position).getKey());
                        startActivity(launchChat);
                    }
                });

            }
        };

        ///  >><<
//        fAdapter1 = new FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder>(
//                CampusNewsModel.class,
//                R.layout.card_view_campus,
//                CampusViewHolder.class,
//                campusRefAll) {
//            @Override
//            protected void populateViewHolder(final CampusViewHolder holder, final CampusNewsModel model, final int position) {
//                holder.campusTitle.setText(model.title);
//                holder.campusDescription.setText(model.description);
//                holder.campusAuthor.setText(model.author);
//                holder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent launchChat = new Intent(view.getContext(), ChatActivity.class);
//                        launchChat.putExtra("localNewsId", fAdapter.getRef(position).getKey());
//                        startActivity(launchChat);
//                    }
//                });
//
//            }
//        };
        /// >><<

        ((MainActivity) getActivity()).mSharedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), UploadNews.class);
                try{
                    i.putExtra("username", ((MainActivity) getActivity()).userInfoObject.name);
                    startActivity(i);
                }catch (NullPointerException e){
                    Toast.makeText(getActivity(), "Not ready to announce news yet. Check your internet connection",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(fAdapter);
        return rootView;
    }

    private static class CampusViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView campusTitle, campusDescription, campusAuthor;

        public CampusViewHolder(View v) {
            super(v);
            mView = v;
            campusTitle = (TextView) v.findViewById(R.id.campusTitle);
            campusAuthor = (TextView) v.findViewById(R.id.campusAuthor);
            campusDescription = (TextView) v.findViewById(R.id.campusDescription);
        }
    }
}