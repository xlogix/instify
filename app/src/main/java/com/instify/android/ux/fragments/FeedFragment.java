package com.instify.android.ux.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.instify.android.R;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.CampusNewsModel;
import com.instify.android.ux.ChatActivity;
import com.instify.android.ux.MainActivity;
import com.instify.android.ux.UploadNewsActivity;
import timber.log.Timber;

/**
 * Created by Abhish3k on 2/23/2016.
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

  RecyclerView recyclerView;
  String currentUserRno, currentUserName, userDept, pathAll;
  @BindView(R.id.error_message) TextView errormessage;
  @BindView(R.id.placeholder_error) LinearLayout placeholderError;

  private Context mContext;
  // Firebase Declaration
  FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder> fAdapterAll;

  Unbinder unbinder;

  // Default Constructor
  public FeedFragment() {
  }

  public static FeedFragment newInstance() {
    FeedFragment frag = new FeedFragment();
    Bundle args = new Bundle();
    frag.setArguments(args);
    return frag;
  }

  @Override public void onStart() {
    super.onStart();
    if (fAdapterAll != null) fAdapterAll.startListening();
  }

  @Override public void onStop() {
    super.onStop();
    if (fAdapterAll != null) fAdapterAll.stopListening();
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SQLiteHandler db = new SQLiteHandler(getContext());
    currentUserRno = db.getUserDetails().getRegno();
    currentUserName = db.getUserDetails().getName();
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Get Context
    mContext = getContext();

    View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
    unbinder = ButterKnife.bind(this, rootView);
    // Tell the fragment that it can access the menu items
    setHasOptionsMenu(true);

    // Recycler view set up //
    recyclerView = rootView.findViewById(R.id.recycler_view_campus_news);
    recyclerView.setHasFixedSize(true);
    // Declare linear layout
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
    // Change the layout orientation to put new news on top
    linearLayoutManager.setReverseLayout(true);
    linearLayoutManager.setStackFromEnd(true);
    // Set layout
    recyclerView.setLayoutManager(linearLayoutManager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());

    // Debug
    Timber.d(userDept, "CampNewsFrag %d");

    // FAB //
    ((MainActivity) getActivity()).mSharedFab.setOnClickListener(v -> {
      Intent uploadNews = new Intent(getContext(), UploadNewsActivity.class);
      uploadNews.putExtra("userDept", userDept);
      startActivity(uploadNews);
    });

    // Paths //
    pathAll = "campusNews/all";

    showNews(pathAll);
    // Return the root view //
    return rootView;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (fAdapterAll != null) fAdapterAll.stopListening();
    unbinder.unbind();
  }

  public void showNews(String path) {
    // News Database reference
    Query query = FirebaseDatabase.getInstance().getReference().child(path);
    // The Firebase Database synchronizes and stores a local copy of the data for active listeners
    query.keepSynced(true);
    // Initialize
    FirebaseRecyclerOptions<CampusNewsModel> options =
        new FirebaseRecyclerOptions.Builder<CampusNewsModel>().setQuery(query,
            CampusNewsModel.class).build();
    // Adapter
    fAdapterAll = new FirebaseRecyclerAdapter<CampusNewsModel, CampusViewHolder>(options) {
      @Override public CampusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.card_view_feed, parent, false);

        return new CampusViewHolder(view);
      }

      @Override protected void onBindViewHolder(@NonNull CampusViewHolder holder, int position,
          @NonNull CampusNewsModel model) {
        holder.mCampusTitle.setText(model.title);
        holder.mCampusAuthor.setText(model.author);
        holder.mCampusDescription.setText(model.description);
        // Set click action for Comment button
        holder.mImageButton.setOnClickListener(view -> {
          Intent launchChat = new Intent(view.getContext(), ChatActivity.class);
          launchChat.putExtra("refPath", path + "/" + fAdapterAll.getRef(position).getKey());
          launchChat.putExtra("CampNewsModel", model);
          startActivity(launchChat);
        });
        // Set click action for Share button
        holder.mImageButton2.setOnClickListener(view -> {
          Intent sharingIntent = new Intent(Intent.ACTION_SEND);
          sharingIntent.setType("text/plain");
          String shareBodyText = "'"
              + model.getTitle().toUpperCase()
              + "',"
              + "\n"
              + model.getDescription()
              + "\n"
              + model.getAuthor()
              + " has shared a topic with you from Instify https://goo.gl/YRSMJa";
          sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
              model.getAuthor() + " has shared a topic with you from Instify");
          sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
          mContext.startActivity(Intent.createChooser(sharingIntent, "Share this topic on"));
        });
        // Check if the logged in user
        if (currentUserName.equals(model.getAuthor())) {
          holder.mDeleteButton.setVisibility(View.VISIBLE);
        }

        // Set click action for Delete Button
        holder.mDeleteButton.setOnClickListener(view -> {

          DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
          Query applesQuery = ref.child(pathAll).orderByChild("author").equalTo(currentUserName);

          applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
              for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                appleSnapshot.getRef().removeValue();
              }
            }

            @Override public void onCancelled(DatabaseError databaseError) {
              Timber.e(databaseError.toException(), "onCancelled");
            }
          });
        });
      }

      @Override public int getItemCount() {
        if (super.getItemCount() == 0) {
          showErrorPlaceholder("No News in Database");
        } else {
          hidePlaceHolder();
        }
        return super.getItemCount();
      }
    };

    // Finally, set the adapter
    recyclerView.setAdapter(fAdapterAll);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.filter_by_topics) {
      showNews(pathAll);
      return true;
    } else if (id == R.id.filter_by_time) {
      showNews(pathAll);
      return true;
    } else if (id == R.id.filter_by_most_liked) {
      showNews(pathAll);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void showErrorPlaceholder(String message) {
    if (placeholderError != null && errormessage != null) {
      if (placeholderError.getVisibility() != View.VISIBLE) {
        placeholderError.setVisibility(View.VISIBLE);
      }
      errormessage.setText(message);
    }
  }

  public void hidePlaceHolder() {
    if (placeholderError != null && errormessage != null) {
      if (placeholderError.getVisibility() == View.VISIBLE) {
        placeholderError.setVisibility(View.INVISIBLE);
      }
      errormessage.setText("Something went wrong. Try Again!");
    }
  }

  @Keep public static class CampusViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    @BindView(R.id.imageView2) ImageView mImageView2;
    @BindView(R.id.campusTitle) TextView mCampusTitle;
    @BindView(R.id.campusAuthor) TextView mCampusAuthor;
    @BindView(R.id.campusDescription) TextView mCampusDescription;
    @BindView(R.id.imageButton2) ImageButton mImageButton2;
    @BindView(R.id.imageButton) ImageButton mImageButton;
    @BindView(R.id.image_button_delete_feed_item) ImageButton mDeleteButton;

    public CampusViewHolder(View v) {
      super(v);
      ButterKnife.bind(this, v);
      mView = v;
    }
  }
}
