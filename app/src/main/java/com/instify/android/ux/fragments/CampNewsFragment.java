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
import android.widget.ImageButton;
import android.widget.ImageView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Abhish3k on 2/23/2016.
 */

public class CampNewsFragment extends Fragment {

    RecyclerView recyclerView;
    // Firebase Declarations
    DatabaseReference newsRef;
    FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder> fAdapterAll;
    String userRegNo, userDept, pathAll, pathDept, pathSec;

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

        // Student details from dB //
        SQLiteHandler db = new SQLiteHandler(getContext());
        userRegNo = db.getUserDetails().getRegno();
        userDept = db.getUserDetails().getDept().replace(".", "-");
        Timber.d("CampNewsFrag", userDept);

        // FAB //
        ((MainActivity) getActivity()).mSharedFab.setOnClickListener(v -> {
            Intent uploadNews = new Intent(getContext(), UploadNewsActivity.class);
            uploadNews.putExtra("userDept", userDept);
            startActivity(uploadNews);
        });

        // Paths //
        pathAll = "campusNews/all";
        pathDept = "campusNews/dept/" + userDept + "/all";
        pathSec = "campusNews/dept/" + userDept + "/all";

        showNews(pathAll);
        // Return the root view //
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
                holder.mCampusTitle.setText(model.title);
                holder.mCampusAuthor.setText(model.author);
                holder.mCampusDescription.setText(model.description);
                // Set click action for Comment button
                holder.mImageButton.setOnClickListener(view -> {
                    Intent launchChat = new Intent(view.getContext(), ChatActivity.class);
                    launchChat.putExtra("refPath", path + "/" + fAdapterAll.getRef(position).getKey());
                    launchChat.putExtra("CampNewsModel", model);
                    startActivity(launchChat);
//                    Pair<View, String> p1 = Pair.create(holder.mImageView2, "newstype");
//                    Pair<View, String> p2 = Pair.create(holder.mCampusTitle, "campusTitle");
//                    Pair<View, String> p3 = Pair.create(holder.mCampusAuthor, "campusAuthor");
//                    Pair<View, String> p4 = Pair.create(holder.mCampusDescription, "campusDescription");
//                    ActivityOptionsCompat options = ActivityOptionsCompat.
//                            makeSceneTransitionAnimation(getActivity(), p1, p2, p3, p4);
//                    startActivity(launchChat, options.toBundle());
                });
                // Set click action for Share button
                holder.mImageButton2.setOnClickListener(view -> {
                    SQLiteHandler db = new SQLiteHandler(getContext());
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBodyText = "'" + model.title.toUpperCase() + "'," + "\n" + model.description + "\n\n" + db.getUserDetails().getName() + " has shared a topic with you from Instify https://goo.gl/YRSMJa";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, db.getUserDetails().getName() + " has shared a topic with you from Instify");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                    startActivity(Intent.createChooser(sharingIntent, "Share this topic on"));
                });
            }
        };
        // Finally, set the adapter
        recyclerView.setAdapter(fAdapterAll);
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

    public static class CampusViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        @BindView(R.id.imageView2)
        ImageView mImageView2;
        @BindView(R.id.campusTitle)
        TextView mCampusTitle;
        @BindView(R.id.campusAuthor)
        TextView mCampusAuthor;
        @BindView(R.id.campusDescription)
        TextView mCampusDescription;
        @BindView(R.id.imageButton2)
        ImageButton mImageButton2;
        @BindView(R.id.imageButton)
        ImageButton mImageButton;

        public CampusViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            mView = v;
        }
    }
}
