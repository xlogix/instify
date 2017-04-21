package com.instify.android.ux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.instify.android.R;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.CampusNewsModel;
import com.instify.android.ux.ChatActivity;
import com.instify.android.ux.MainActivity;
import com.instify.android.ux.UploadNewsActivity;

/**
 * Created by Abhish3k on 2/23/2016.
 */

public class CampNewsFragment extends Fragment {

    // Default Constructor
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

    RecyclerView recyclerView;
    // Firebase Declarations
    DatabaseReference newsRef;
    FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder> fAdapterAll;
    String userRegNo, userDept, pathAll, pathDept, pathSec;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_campus_news, container, false);
        // Tell the fragment that it can access the menu items
        setHasOptionsMenu(true);

        // Recycler view set up //
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_campus_news);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // FAB //
        ((MainActivity) getActivity()).mSharedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadNews = new Intent(getContext(), UploadNewsActivity.class);
                uploadNews.putExtra("userDept", userDept);
                startActivity(uploadNews);
            }
        });

        // Student details from dB //
        SQLiteHandler db = new SQLiteHandler(getContext());
        userRegNo = db.getUserDetails().get("token");
        userDept = db.getUserDetails().get("regno").replace(".", "-");

        // Paths //
        pathAll = "campusNews/all";
        pathDept = "campusNews/dept/" + userDept + "/all";
        pathSec = "campusNews/dept/" + userDept + "/all";

        showNews(pathAll);

        return rootView;
    }

    private void showNews(final String path) {

        newsRef = FirebaseDatabase.getInstance().getReference().child(path);
        fAdapterAll = new FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder>(
                CampusNewsModel.class,
                R.layout.card_view_campus,
                CampusViewHolder.class,
                newsRef) {

            @Override
            protected void populateViewHolder(CampusViewHolder holder, CampusNewsModel model, final int position) {
                holder.campusTitle.setText(model.title);
                holder.campusAuthor.setText(model.author);
                holder.campusDescription.setText(model.description);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent launchChat = new Intent(view.getContext(), ChatActivity.class);
                        launchChat.putExtra("refPath", path + "/" + fAdapterAll.getRef(position).getKey());
                        startActivity(launchChat);
                    }
                });
            }
        };
        recyclerView.setAdapter(fAdapterAll);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.filter_by_university) {
            showNews(pathAll);
            return true;
        } else if (id == R.id.filter_by_department) {
            showNews(pathDept);
            return true;
        } else if (id == R.id.filter_by_class) {
            showNews(pathSec);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
