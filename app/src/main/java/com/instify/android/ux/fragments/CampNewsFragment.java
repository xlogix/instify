package com.instify.android.ux.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.instify.android.R;
import com.instify.android.upload.UploadNews;
import com.instify.android.ux.adapters.CampNewsAdapter;

/**
 * Created by Abhish3k on 2/23/2016.
 */

public class CampNewsFragment extends Fragment {

    public CampNewsFragment(){}

    public static CampNewsFragment newInstance(){
        CampNewsFragment frag = new CampNewsFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_campus_news, container, false);
        //((ActivityMain) getActivity()).showFloatingActionButton();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_campus_news);
        setupRecyclerView(recyclerView);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent i = new Intent(getActivity(), UploadNews.class);
                startActivity(i);
            }
        });
        return rootView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(), CampNewsAdapter.data));
    }

    public static class SimpleStringRecyclerViewAdapter extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {
        private String[] mValues;
        private Context mContext;

        public SimpleStringRecyclerViewAdapter(Context context, String[] items) {
            mContext = context;
            mValues = items;
        }

        public String getValueAt(int position) {
            return mValues[position];
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    //.inflate(R.layout.recycle_list, parent, false);
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mTextView.setText(mValues[position]);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, getValueAt(position), Snackbar.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.length;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTextView = (TextView) view.findViewById(android.R.id.text1);
            }
        }
    }
}