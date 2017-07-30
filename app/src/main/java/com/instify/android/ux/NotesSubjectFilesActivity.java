package com.instify.android.ux;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.instify.android.R;
import com.instify.android.app.AppConfig;
import com.instify.android.app.AppController;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.NotesFileModel;
import com.instify.android.ux.adapters.ListExpandableAdapter;
import com.instify.android.ux.adapters.NotesFileAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class NotesSubjectFilesActivity extends AppCompatActivity {

  RecyclerView mRVFish;
  SearchView searchView = null;
  LinearLayout linearLayout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notes_subject_files);
    setTitle(Html.fromHtml("<small>" + getIntent().getStringExtra("code") + "</small>"));

    linearLayout = (LinearLayout) findViewById(R.id.pos);
    mRVFish = (RecyclerView) findViewById(R.id.recycler_view_notes);

    getNotes(getIntent().getStringExtra("code"));
  }

  private void getNotes(final String subjectCode) {

    // Handle UI
    // showRefreshing();

    // Tag used to cancel the request
    String tag_string_req = "req_files";

    StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_FILES, response -> {
      try {
        JSONArray user = new JSONArray(response);
        boolean error;// = jObj.getBoolean("error");
        error = false;

        // Handle UI
        //hideRefreshing();

        // Check for error node in json
        if (!error) {
          ListExpandableAdapter expListAdapter;

          // declare array List for all headers in list
          ArrayList<String> headersArrayList = new ArrayList<>();
          List<NotesFileModel> NotesFileModel = new ArrayList<>();
          // Declare Hash map for all headers and their corresponding values
          HashMap<String, ArrayList<String>> childArrayList = new HashMap<>();

          // expListView = (ExpandableListView)findViewById(R.id.expListView);
          //    JSONArray user = jObj.getJSONArray(response);

          //  int i;
          //                        mAdapter = new NotesAdapter(notes);
          //                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
          //                        recyclerView.setLayoutManager(mLayoutManager);
          //                        recyclerView.setItemAnimator(new DefaultItemAnimator());

          for (int i = 0; i < user.length(); i++) {
            JSONObject json_data = user.getJSONObject(i);
            //  JSONObject subs = user.getJSONObject(user.getString(i));

            //                            notes movie = notes(subs.getString("sub-desc"));
            //                            notes.add(movie);
            //   notes obj = new notes(subs.getString("sub-desc"));

            NotesFileModel fishData = new NotesFileModel();
            fishData.notename = json_data.getString("name");
            fishData.notedesc = json_data.getString("desc");
            fishData.notetime = json_data.getString("uploaded");
            fishData.notefile = json_data.getString("file");
            fishData.noteregno = json_data.getString("regno");
            fishData.noteposter = json_data.getString("author");

            //fishData.sizeName = json_data.getString("registration").trim();
            //fishData.price = json_data.getString("ID");
            // fishData.image = "https://hashbird.com/gogrit.in/workspace/srm-api/studentImages/" + json_data.getString("registration").trim() + ".jpg";
            NotesFileModel.add(fishData);
          }
          // Setup and Handover data to recycler view
          //  mRVFish = (RecyclerView) findViewById(R.id.);
          NotesFileAdapter mAdapter =
              new NotesFileAdapter(NotesSubjectFilesActivity.this, NotesFileModel);
          mRVFish.setAdapter(mAdapter);
          mRVFish.setLayoutManager(new LinearLayoutManager(NotesSubjectFilesActivity.this));

          //                        mAdapter = new NotesAdapter(getApplicationContext(), notes);
          //                        mRVFish.setAdapter(mAdapter);
          //                        mRVFish.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        } else {
          // Update UI
          //   hideRefreshing();
          // Error in login. Get the error message
          //                        String errorMsg = user.getString("error_msg");
        }
      } catch (JSONException e) {
        // Update UI
        //    hideRefreshing();
        linearLayout.setVisibility(View.VISIBLE);
      }
    }, error -> {
      Timber.e("Network Error: " + error.getMessage());
      Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }) {
      @Override protected Map<String, String> getParams() {
        // Posting parameters to login url
        Map<String, String> params = new HashMap<>();
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String regNo = db.getUserDetails().getRegno();
        // String pass = db.getUserDetails().get("created_at");

        params.put("regno", regNo);
        //  params.put("pass", pass);
        params.put("subject", subjectCode);
        return params;
      }
    };
    // Adding request to request queue
    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {

    // adds item to action bar
    getMenuInflater().inflate(R.menu.search_main, menu);

    // Get Search item from action bar and Get Search service
    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchManager searchManager =
        (SearchManager) NotesSubjectFilesActivity.this.getSystemService(Context.SEARCH_SERVICE);
    if (searchItem != null) {
      searchView = (SearchView) searchItem.getActionView();
    }
    if (searchView != null) {
      searchView.setSearchableInfo(
          searchManager.getSearchableInfo(NotesSubjectFilesActivity.this.getComponentName()));
      searchView.setIconified(true);
    }

    MenuItem enai = menu.findItem(R.id.action_add_note);

    enai.setOnMenuItemClickListener(item -> {
      Intent intent = new Intent(getApplicationContext(), NotesUploadActivity.class);
      intent.putExtra("code", getIntent().getStringExtra("code"));
      startActivity(intent);
      return true;
    });
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }
}
