package com.instify.android.ux.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.instify.android.R;
import com.instify.android.ux.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by Abhish3k on 21/02/2016.
 * Thanks :)
 */

public class TimeTableFragment extends Fragment {

    private String timeTableUrl = "http://srmapi-hmm.rhcloud.com/get-attd.php";
    JSONObject erpAuthObj;
    JsonObjectRequest req;
    TextView timeTableData;
    EditText password;
    Button submit;

    public TimeTableFragment() {
    }

    public static TimeTableFragment newInstance() {
        TimeTableFragment frag = new TimeTableFragment();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_time_table, container, false);


        return rootView;
    }
}


