package com.instify.android.ux.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.instify.android.R;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.ExperiencesModel;
import com.instify.android.ux.MainActivity;
import com.instify.android.ux.UploadExperiencesActivity;
import java.util.HashMap;
import java.util.Map;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExperiencesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExperiencesFragment extends Fragment {

  @BindView(R.id.recycler_view_experience) RecyclerView recyclerViewExperiences;
  @BindView(R.id.error_message) TextView errorMessage;
  @BindView(R.id.placeholder_error) LinearLayout placeholderError;
  Unbinder unbinder;
  FirestoreRecyclerAdapter adapter;
  String currentUserRno;
  // TODO: Rename and change types of parameters

  public ExperiencesFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @return A new instance of fragment ExperiencesFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static ExperiencesFragment newInstance() {
    ExperiencesFragment fragment = new ExperiencesFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onStart() {
    super.onStart();
    if (adapter != null) adapter.startListening();
  }

  @Override public void onStop() {
    super.onStop();
    if (adapter != null) adapter.stopListening();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SQLiteHandler db = new SQLiteHandler(getContext());
    currentUserRno = db.getUserDetails().getRegno();
  }

  @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_experiences, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // FAB //
    ((MainActivity) getActivity()).mSharedFab.setOnClickListener(v -> {
      Intent uploadExperience = new Intent(getContext(), UploadExperiencesActivity.class);
      startActivity(uploadExperience);
    });

    // Query the database
    Query query = FirebaseFirestore.getInstance().collection("experiences");
    FirestoreRecyclerOptions<ExperiencesModel> options =
        new FirestoreRecyclerOptions.Builder<ExperiencesModel>().setQuery(query,
            ExperiencesModel.class).build();
    adapter = new FirestoreRecyclerAdapter<ExperiencesModel, ViewHolder>(options) {
      @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.card_view_experiences, parent, false);

        return new ViewHolder(view);
      }

      @Override protected void onBindViewHolder(@NonNull ViewHolder holder, int position,
          @NonNull ExperiencesModel model) {
        holder.setBindDataToView(model, getContext());
        holder.upVoteButton.setOnCheckedChangeListener(
            new CompoundButton.OnCheckedChangeListener() {
              @Override public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                voteExperience(model.getId(), true);
              }
            });
      }
    };

    recyclerViewExperiences.setHasFixedSize(true);
    // Declare linear layout
    LinearLayoutManager linearLayoutManager =
        new LinearLayoutManager(recyclerViewExperiences.getContext());
    recyclerViewExperiences.setLayoutManager(linearLayoutManager);
    // Change the layout orientation to put new news on top
    linearLayoutManager.setReverseLayout(true);
    linearLayoutManager.setStackFromEnd(true);
    recyclerViewExperiences.setItemAnimator(new DefaultItemAnimator());
    recyclerViewExperiences.setAdapter(adapter);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  public void showErrorPlaceholder(String message) {
    if (placeholderError != null) {
      if (placeholderError.getVisibility() != View.VISIBLE) {
        placeholderError.setVisibility(View.VISIBLE);
      }
      errorMessage.setText(message);
    }
  }

  public void hidePlaceHolder() {
    if (placeholderError != null) {
      if (placeholderError.getVisibility() == View.VISIBLE) {
        placeholderError.setVisibility(View.INVISIBLE);
      }
      errorMessage.setText("Something went wrong. Try Again!");
    }
  }

  public void voteExperience(String id, Boolean value) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Boolean> vote = new HashMap<>();
    vote.put(currentUserRno, value);
    DocumentReference sfDocRef = db.collection("experiences").document(id);

    db.runTransaction(transaction -> {
      DocumentSnapshot snapshot = transaction.get(sfDocRef);
      HashMap<String, Boolean> currentVotes = (HashMap<String, Boolean>) snapshot.get("votes");
      currentVotes.put(currentUserRno, value);
      transaction.update(sfDocRef, "votes", currentVotes);
      return currentVotes;
    })
        .addOnSuccessListener(result -> Timber.d("Transaction success: %s", result))
        .addOnFailureListener(e -> Timber.w(e, "Transaction failure."));
  }

  @Keep public static class ViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.imageView2) ImageView imageView2;
    @BindView(R.id.campusTitle) TextView campusTitle;
    @BindView(R.id.campusAuthor) TextView campusAuthor;
    @BindView(R.id.imagePost) ImageView imagePost;
    @BindView(R.id.campusDescription) TextView campusDescription;
    @BindView(R.id.imageButton) ImageButton imageButton;
    @BindView(R.id.btnLike) CheckBox upVoteButton;
    @BindView(R.id.scoreText) TextView scoreText;
    @BindView(R.id.shareButtonExperiences) ImageButton imageButton2;

    public ViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }

    public void setBindDataToView(ExperiencesModel model, Context context) {
      campusTitle.setText(model.getTitle());
      campusAuthor.setText(model.getAuthor());
      campusDescription.setText(model.getDescription());
      scoreText.setText(String.valueOf(model.getVotes().size()));
      Glide.with(context)
          .load(model.getImageUrl())
          .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
          .into(imagePost);
    }
  }
}
