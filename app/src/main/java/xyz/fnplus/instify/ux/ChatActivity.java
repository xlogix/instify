package xyz.fnplus.instify.ux;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;
import xyz.fnplus.instify.R;
import xyz.fnplus.instify.app.AppConfig;
import xyz.fnplus.instify.helpers.SQLiteHandler;
import xyz.fnplus.instify.models.CampusNewsModel;
import xyz.fnplus.instify.models.ChatMessageModel;

/**
 * Created by Abhish3k on 6/22/2016. //Using Firebase real-time database
 */

public class ChatActivity extends AppCompatActivity {

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 100;
    public static final String ANONYMOUS = "anonymous";
    private static final int REQUEST_IMAGE = 1;
    private static final String TAG = "ChatActivity";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static String MESSAGES_CHILD = "messages";
    @BindView(R.id.placeholder)
    LinearLayout mPlaceholder;
    String refPath;
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
    @BindView(R.id.cardView)
    CardView mCardView;
    private String mUsername;
    private String mPhotoUrl;
    private Button mSendButton;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUsername = ANONYMOUS;

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get data
        if (mFirebaseUser != null) {
            try {
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
        setdata(model);
        MESSAGES_CHILD = refPath + "/discussion";

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_trending);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessageModel, MessageViewHolder>(
                ChatMessageModel.class,
                R.layout.card_view_chat_item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(MESSAGES_CHILD)) {

            @Override
            protected ChatMessageModel parseSnapshot(DataSnapshot snapshot) {
                ChatMessageModel friendlyMessage = super.parseSnapshot(snapshot);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(snapshot.getKey());
                }
                return friendlyMessage;
            }

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, ChatMessageModel friendlyMessage, int position) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (friendlyMessage.getText() != null) {

                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    viewHolder.messageDateTextView.setText(friendlyMessage.getdatefromstamp());
                    viewHolder.messageTimeTextView.setText(friendlyMessage.gettimefromstamp());

                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
                } else {
                    String imageUrl = friendlyMessage.getImageUrl();
                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl);
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                task -> {
                                    if (task.isSuccessful()) {
                                        String downloadUrl = task.getResult().toString();
                                        Glide.with(viewHolder.messageImageView.getContext())
                                                .load(downloadUrl)
                                                .into(viewHolder.messageImageView);
                                    } else {
                                        Timber.w(TAG, "Getting download url was not successful.",
                                                task.getException());
                                    }
                                });
                    } else {
                        Glide.with(viewHolder.messageImageView.getContext())
                                .load(friendlyMessage.getImageUrl())
                                .into(viewHolder.messageImageView);
                    }
                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                    viewHolder.messageTextView.setVisibility(TextView.GONE);
                }

                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(ChatActivity.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .placeholder(R.drawable.ic_account_circle_black_36dp)
                            .into(viewHolder.messengerImageView);
                }

                if (friendlyMessage.getText() != null) {
                    // write this message to the on-device index
                    FirebaseAppIndex.getInstance().update(getMessageIndexable(friendlyMessage));
                }

                // log a view action on it
                FirebaseUserActions.getInstance().end(getMessageViewAction(friendlyMessage));
            }

            @Override
            public int getItemCount() {
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
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
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
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        // Define default config values. Defaults are used when fetched config values are not
        // available. Eg: if an error occurred fetching values from the server.
        Map<String, Object> defaultConfigMap = new HashMap<>();
        defaultConfigMap.put("friendly_msg_length", 1000L);

        // Apply config settings and default values.
        mFirebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        mFirebaseRemoteConfig.setDefaults(defaultConfigMap);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSharedPreferences
                .getInt(AppConfig.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))});
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);

                } else {
                    mSendButton.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(view -> {
            ChatMessageModel friendlyMessage = new ChatMessageModel(mMessageEditText.getText().toString(),
                    mUsername,
                    mPhotoUrl, null);
            mFirebaseDatabaseReference.child(MESSAGES_CHILD).push().setValue(friendlyMessage);
            mMessageEditText.setText("");
            mFirebaseAnalytics.logEvent(MESSAGE_SENT_EVENT, null);
        });
    }

    private void setdata(CampusNewsModel model) {
        mCampusTitle.setText(model.title);
        mCampusAuthor.setText(model.author);
        mCampusDescription.setText(model.description);

        // Set click action for Share button
        mImageButton2.setOnClickListener(view -> {
            SQLiteHandler db = new SQLiteHandler(this);
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "'" + model.title.toUpperCase() + "'," + "\n" + model.description + "\n\n" + db.getUserDetails().getName() + " has shared a topic with you from Instify https://goo.gl/YRSMJa";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, db.getUserDetails().getName() + " has shared a topic with you from Instify");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Share this topic on"));
        });
    }

    private Action getMessageViewAction(ChatMessageModel friendlyMessage) {
        return new Action.Builder(Action.Builder.VIEW_ACTION)
                .setObject(friendlyMessage.getName(), MESSAGE_URL.concat(friendlyMessage.getId()))
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Timber.d(TAG, "Uri: " + uri.toString());

                    ChatMessageModel tempMessage = new ChatMessageModel(null, mUsername, mPhotoUrl,
                            LOADING_IMAGE_URL);
                    mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                            .setValue(tempMessage, (databaseError, databaseReference) -> {
                                if (databaseError == null) {
                                    String key = databaseReference.getKey();
                                    StorageReference storageReference =
                                            FirebaseStorage.getInstance()
                                                    .getReference(mFirebaseUser.getUid())
                                                    .child(key)
                                                    .child(uri.getLastPathSegment());

                                    putImageInStorage(storageReference, uri, key);
                                } else {
                                    Timber.w(TAG, "Unable to write message to database.",
                                            databaseError.toException());
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ChatMessageModel friendlyMessage =
                                    new ChatMessageModel(null, mUsername, mPhotoUrl,
                                            task.getResult().getMetadata().getDownloadUrl()
                                                    .toString());
                            mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                    .setValue(friendlyMessage);
                        } else {
                            Timber.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    /**
     * Another class to display the chat items in the UI
     */
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public ImageView messageImageView;
        public TextView messengerTextView;
        public TextView messageDateTextView;
        public TextView messageTimeTextView;
        public CircleImageView messengerImageView;


        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageimage);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messageDateTextView = (TextView) itemView.findViewById(R.id.messagedat);
            messageTimeTextView = (TextView) itemView.findViewById(R.id.message_time_textView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        }
    }
}
