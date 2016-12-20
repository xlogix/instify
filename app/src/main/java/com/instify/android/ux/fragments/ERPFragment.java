package com.instify.android.ux.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.instify.android.R;

/**
 * Created by Abhish3k on 2/23/2016.
 */

public class ERPFragment extends Fragment {

    //WebView webView;

    //WebView webView = (WebView) v.findViewById(R.id.webview);
    //WebSettings webSettings = webView.getSettings();
    //webSettings.setJavaScriptEnabled(true);
    //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
    //webSettings.setSupportMultipleWindows(true);
    //webView.loadUrl("http://evarsity.srmuniv.ac.in/srmswi/usermanager/youLogin.jsp");
    //}*/

    public ERPFragment(){}

    public static ERPFragment newInstance(){
        ERPFragment frag = new ERPFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_erp, container, false);
        //((ActivityMain) getActivity()).hideFloatingActionButton();
        Button button = (Button) rootView.findViewById(R.id.erp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://evarsity.srmuniv.ac.in/srmswi/usermanager/youLogin.jsp"));
                startActivity(browser);
            }
        });
        return rootView;
    }
}