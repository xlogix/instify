package com.instify.android.ux;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.CampusNewsModel;
import com.instify.android.models.ChatMessageModel;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

/**
 * Created by Abhish3k on 6/22/2016.
 */

public class ChatActivity extends AppCompatActivity {
  private static final String TAG = ChatActivity.class.getSimpleName();

  public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
  public static final String ANONYMOUS = "anonymous";
  private static final int REQUEST_IMAGE = 1;
  private static final String MESSAGE_SENT_EVENT = "message_sent";
  private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
  private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
  // Firebase Database Location
  public static String MESSAGES_CHILD;

  @BindView(R.id.placeholder) LinearLayout mPlaceholder;
  String refPath;
  @BindView(R.id.imageView2) ImageView mImageView2;
  @BindView(R.id.campusTitle) TextView mCampusTitle;
  @BindView(R.id.campusAuthor) TextView mCampusAuthor;
  @BindView(R.id.campusDescription) TextView mCampusDescription;
  @BindView(R.id.imageButton2) ImageButton mImageButton2;
  @BindView(R.id.cardView) CardView mCardView;

  private String mUsername;
  private String mPhotoUrl;
  private FloatingActionButton mSendButton;
  private RecyclerView mMessageRecyclerView;
  private LinearLayoutManager mLinearLayoutManager;
  private FirebaseAnalytics mFirebaseAnalytics;
  private FirebaseRecyclerAdapter<ChatMessageModel, MessageViewHolder> mFirebaseAdapter;
  private ProgressBar mProgressBar;
  private DatabaseReference mFirebaseDatabaseReference;
  private FirebaseUser mFirebaseUser;
  private EditText mMessageEditText;
  private FirebaseRemoteConfig mFirebaseRemoteConfig;
  private CampusNewsModel model;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chat);
    ButterKnife.bind(this);

    SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    mUsername = ANONYMOUS;

    mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    // Get data
    if (mFirebaseUser != null) {
      try {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
          mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        } else {
          mPhotoUrl = "";
        }
      } catch (Exception e) {
        mUsername = ANONYMOUS;
      }
    }

    refPath = getIntent().getStringExtra("refPath");
    model = getIntent().getParcelableExtra("CampNewsModel");
    setData(model);
    // Set location for storage
    MESSAGES_CHILD = refPath + "/discussion";

    mProgressBar = findViewById(R.id.progressBar);
    mMessageRecyclerView = findViewById(R.id.recycler_view_trending);
    mLinearLayoutManager = new LinearLayoutManager(this);
    mLinearLayoutManager.setStackFromEnd(true);

    Query query = FirebaseDatabase.getInstance().getReference().child(MESSAGES_CHILD);

    FirebaseRecyclerOptions<ChatMessageModel> options =
        new FirebaseRecyclerOptions.Builder<ChatMessageModel>().setQuery(query,
            ChatMessageModel.class).build();

    mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessageModel, MessageViewHolder>(options) {
      @Override public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.card_view_chat_item_message, parent, false);

        return new MessageViewHolder(view);
      }

      @Override protected void onBindViewHolder(@NonNull MessageViewHolder viewHolder, int position,
          @NonNull ChatMessageModel model) {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        if (model.getText() != null) {

          viewHolder.messageTextView.setText(model.getText());
          viewHolder.messageDateTextView.setText(model.getdatefromstamp());
          viewHolder.messageTimeTextView.setText(model.gettimefromstamp());

          viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
          viewHolder.messageImageView.setVisibility(ImageView.GONE);
        } else {
          String imageUrl = model.getImageUrl();
          if (imageUrl.startsWith("gs://")) {
            StorageReference storageReference =
                FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            storageReference.getDownloadUrl().addOnCompleteListener(task -> {
              if (task.isSuccessful()) {
                String downloadUrl = task.getResult().toString();
                Glide.with(viewHolder.messageImageView.getContext())
                    .load(downloadUrl)
                    .into(viewHolder.messageImageView);
              } else {
                Timber.w(task.getException(), "Getting download url was not successful.");
              }
            });
          } else {
            Glide.with(viewHolder.messageImageView.getContext())
                .load(model.getImageUrl())
                .into(viewHolder.messageImageView);
          }
          viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
          viewHolder.messageTextView.setVisibility(TextView.GONE);
        }

        viewHolder.messengerTextView.setText(model.getName());
        if (model.getPhotoUrl() == null) {
          viewHolder.messengerImageView.setImageDrawable(
              ContextCompat.getDrawable(ChatActivity.this, R.drawable.ic_account_circle_black));
        } else {
          GlideApp.with(ChatActivity.this)
              .load(model.getPhotoUrl())
              .placeholder(R.drawable.ic_account_circle_black)
              .into(viewHolder.messengerImageView);
        }
      }

      @Override public int getItemCount() {
        //Hide Progress Bar When no items
        if (super.getItemCount() == 0) {
          mProgressBar.setVisibility(ProgressBar.INVISIBLE);
          mPlaceholder.setVisibility(View.VISIBLE);
        } else {
          mPlaceholder.setVisibility(View.GONE);
        }
        return super.getItemCount();
      }
    };

    mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        int friendlyMessageCount = mFirebaseAdapter.getItemCount();
        int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
        // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
        // to the bottom of the list to show the newly added message.
        if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1)
            && lastVisiblePosition == (positionStart - 1))) {
          mMessageRecyclerView.scrollToPosition(positionStart);
        }
      }
    });

    mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
    mMessageRecyclerView.setAdapter(mFirebaseAdapter);
    // Initialize Firebase Measurement.
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    // Initialize Firebase Remote Config.
    mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

    // Define Firebase Remote Config Settings.
    FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
        new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(false).build();

    // Define default config values. Defaults are used when fetched config values are not
    // available. Eg: if an error occurred fetching values from the server.
    Map<String, Object> defaultConfigMap = new HashMap<>();
    defaultConfigMap.put("friendly_msg_length", 10L);

    // Apply config settings and default values.
    mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
    mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

    mMessageEditText = findViewById(R.id.messageEditText);
    mMessageEditText.setFilters(new InputFilter[] {
        new InputFilter.LengthFilter(
            mSharedPreferences.getInt(AppConfig.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))
    });
    mMessageEditText.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
      }

      @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().trim().length() > 0) {
          mSendButton.setEnabled(true);
        } else {
          mSendButton.setEnabled(false);
        }
      }

      @Override public void afterTextChanged(Editable editable) {
      }
    });

    mSendButton = findViewById(R.id.sendButton);
    mSendButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        ChatMessageModel friendlyMessage =
            new ChatMessageModel(mMessageEditText.getText().toString(), mUsername, mPhotoUrl, null);
        mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
        mMessageEditText.setText("");
        mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
      }
    });
  }

  private void setData(CampusNewsModel model) {
    mCampusTitle.setText(model.title);
    mCampusAuthor.setText(model.author);
    mCampusDescription.setText(model.description);

    // Set click action for Share button
    mImageButton2.setOnClickListener(view -> {
      SQLiteHandler db = new SQLiteHandler(this);
      Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
      sharingIntent.setType("text/plain");
      String shareBodyText = "'"
          + model.title.toUpperCase()
          + "',"
          + "\n"
          + model.description
          + "\n\n"
          + db.getUserDetails().getName()
          + " has shared a topic with you from Instify https://goo.gl/YRSMJa";
      sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
          db.getUserDetails().getName() + " has shared a topic with you from Instify");
      sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
      startActivity(Intent.createChooser(sharingIntent, "Share this topic on"));
    });
  }

  private Action getMessageViewAction(ChatMessageModel friendlyMessage) {
    return new Action.Builder(Action.Builder.VIEW_ACTION).setObject(friendlyMessage.getName(),
        MESSAGE_URL.concat(friendlyMessage.getId()))
        .setMetadata(new Action.Metadata.Builder().setUpload(false))
        .build();
  }

  private Indexable getMessageIndexable(ChatMessageModel friendlyMessage) {
    PersonBuilder sender = Indexables.personBuilder()
        .setIsSelf(mUsername.equals(friendlyMessage.getName()))
        .setName(friendlyMessage.getName())
        .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/sender"));

    PersonBuilder recipient = Indexables.personBuilder()
        .setName(mUsername)
        .setUrl(MESSAGE_URL.concat(friendlyMessage.getId() + "/recipient"));

    return Indexables.messageBuilder()
        .setName(friendlyMessage.getText())
        .setUrl(MESSAGE_URL.concat(friendlyMessage.getId()))
        .setSender(sender)
        .setRecipient(recipient)
        .build();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Timber.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

    if (requestCode == REQUEST_IMAGE) {
      if (resultCode == RESULT_OK) {
        if (data != null) {
          final Uri uri = data.getData();
          Timber.d("Uri: %s", uri.toString());

          ChatMessageModel tempMessage =
              new ChatMessageModel(null, mUsername, mPhotoUrl, LOADING_IMAGE_URL);
          mFirebaseDatabaseReference.child(MESSAGES_CHILD)
              .push()
              .setValue(tempMessage, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                  String key = databaseReference.getKey();
                  StorageReference storageReference = FirebaseStorage.getInstance()
                      .getReference(mFirebaseUser.getUid())
                      .child(key)
                      .child(uri.getLastPathSegment());

                  putImageInStorage(storageReference, uri, key);
                } else {
                  Timber.w(databaseError.toException(), "Unable to write message to database.");
                }
              });
        }
      }
    }
  }

  @Override public void onPause() {
    if (mFirebaseAdapter != null) mFirebaseAdapter.stopListening();
    super.onPause();
  }

  @Override public void onStart() {
    if (mFirebaseAdapter != null) mFirebaseAdapter.startListening();
    super.onStart();
  }

  @Override public void onStop() {
    if (mFirebaseAdapter != null) mFirebaseAdapter.stopListening();
    super.onStop();
  }

  private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
    storageReference.putFile(uri)
        .addOnCompleteListener(ChatActivity.this,
            new OnCompleteListener<UploadTask.TaskSnapshot>() {
              @Override @SuppressWarnings("VisibleForTests")
              public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                  ChatMessageModel friendlyMessage =
                      new ChatMessageModel(null, mUsername, mPhotoUrl,
                              task.getResult().toString());
                  mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                      .child(key)
                      .setValue(friendlyMessage);
                } else {
                  Timber.w(task.getException(), "Image upload task was not successful.");
                }
              }
            });
  }

  /**
   * Another class to display the chat items in the UI
   */
  @Keep public static class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView messageTextView;
    ImageView messageImageView;
    TextView messengerTextView;
    TextView messageDateTextView;
    TextView messageTimeTextView;
    CircleImageView messengerImageView;

    public MessageViewHolder(View v) {
      super(v);
      messageTextView = itemView.findViewById(R.id.messageTextView);
      messageImageView = itemView.findViewById(R.id.messageimage);
      messengerTextView = itemView.findViewById(R.id.messengerTextView);
      messageDateTextView = itemView.findViewById(R.id.messagedat);
      messageTimeTextView = itemView.findViewById(R.id.message_time_textView);
      messengerImageView = itemView.findViewById(R.id.messengerImageView);
    }
  }
}
