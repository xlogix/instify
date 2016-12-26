package com.instify.android.ux.fragments;

/**
 * Created by Abhish3k on 3/06/2016. Thanks to Ravi
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.instify.android.R;
import com.instify.android.helpers.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UnivNewsFragment extends Fragment {
    private static final String endpoint_final = "http://arjun-apis.herokuapp.com/srm-news-api/";
    String[] titles = new String[15];
    String[] snips = new String[15];
    String[] links = new String[15];
    private String TAG = UnivNewsFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleStringRecyclerViewAdapter mAdapter;
    private JsonObjectRequest req;

    public UnivNewsFragment() {
    }

    public static UnivNewsFragment newInstance() {
        UnivNewsFragment frag = new UnivNewsFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView;

        View rootView = inflater.inflate(R.layout.fragment_university_news, container, false);
        //((ActivityMain) getActivity()).showFloatingActionButton();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_university);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.red500, R.color.black, R.color.google_blue_900);

        mAdapter = new SimpleStringRecyclerViewAdapter(getContext(), titles, snips, links);

        // Calling another function which has the details
        setupRecyclerView(recyclerView);

        // Make it look like something is happening
        swipeRefreshLayout.setRefreshing(true);

        // Make the request!
        makeJSONRequest();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                makeJSONRequest();
            }
        });

        return rootView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    public void makeJSONRequest() {
        req = new JsonObjectRequest(endpoint_final, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONArray newsItems = response.getJSONArray("newsItems");
                            for (int j = 0; j < newsItems.length(); j++) {
                                JSONObject newsItem = newsItems.getJSONObject(j);
                                titles[j] = newsItem.getString("title");
                                snips[j] = newsItem.getString("snip");
                                links[j] = newsItem.getString("link");
                            }
                            // UI
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
                            Toast.makeText(getContext(), "JSON Parsing error", Toast.LENGTH_LONG).show();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(), "Error Receiving News", Toast.LENGTH_LONG).show();
            }
        });

        int socketTimeout = 10000;  // 10 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        req.setRetryPolicy(policy);
        MyApplication.getInstance().addToRequestQueue(req);

        // Adding request to request queue             Important : (Roll Back)
        //MyApplication.getInstance().addToRequestQueue(req);
    }

    public static class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {
        private String[] mTitles, mSnips, mLinks;
        private Context mContext;

        // Constructor
        private SimpleStringRecyclerViewAdapter(Context context, String[] Titles, String[] Snips, String[] Links) {
            mContext = context;
            mTitles = Titles;
            mSnips = Snips;
            mLinks = Links;
        }

        private String getTitleAt(int position) {
            return mTitles[position];
        }

        private String getSnipAt(int position) {
            return mSnips[position];
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_univ, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mTextViewTitle.setText(getTitleAt(position));
            holder.mTextViewSnip.setText(getSnipAt(position));
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startScan();
                    Context context = v.getContext();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLinks[holder.getAdapterPosition()]));
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mTitles.length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final View mView;
            private final TextView mTextViewTitle, mTextViewSnip;

            private ViewHolder(View view) {
                super(view);
                mView = view;
                mTextViewTitle = (TextView) view.findViewById(R.id.univ_news_title);
                mTextViewSnip = (TextView) view.findViewById(R.id.univ_news_snip);
            }
        }
    }
}
