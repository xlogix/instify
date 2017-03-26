/*
package com.instify.android.ux.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import ux.MoreDetailActivity;

*/
/**
 * A simple {@link Fragment} subclass.
 *//*


public class RecyclerGetFragment extends Fragment {

    private static final String FRAGMENTTAG = "RecyclerViewFragment";
    private static FirebaseRecyclerAdapter<worker, userholder> mAdapter;
    private static boolean cookpresent = false;

    public RecyclerGetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        View rootView = inflater.inflate(R.layout.fragment_recyclerget, container, false);
        rootView.setTag(FRAGMENTTAG);
        //Bundle bundle = this.getArguments();
        //String sortstring = bundle.getString("sortby");
        String sortstring;
        if (getArguments() != null)
            sortstring = getArguments().getString("sortby");
        else sortstring = "Name";

        // Snackbar.make(getActivity().findViewById(android.R.id.content), sortstring, Snackbar.LENGTH_SHORT).show();


        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.keepSynced(true);


        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                User user = datasnapshot.getValue(User.class);
                cookpresent = user.getMycook() != null;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        DatabaseReference ref = mDatabase.child("workers");
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.userRecycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(getContext());

        Query myQuery;
        switch (sortstring) {
            case "Name":
                myQuery = ref.orderByChild("name");
                break;
            case "LowPrice":
                myQuery = ref.orderByChild("workerpreferences/threeshift");
                break;
            case "HighPrice":
                myQuery = ref.orderByChild("workerpreferences/threeshift");
                LayoutManager.setReverseLayout(true);
                LayoutManager.setStackFromEnd(true);
                break;
            case "Age":
                myQuery = ref.orderByChild("age");
                break;
            default:
                myQuery = ref.orderByChild("name");
                break;
        }
        recyclerView.setLayoutManager(LayoutManager);

        mAdapter = new FirebaseRecyclerAdapter<worker, userholder>(worker.class, R.layout.cardlayout, userholder.class, myQuery) {
            @Override
            public void populateViewHolder(userholder userViewHolder, worker users, int position) {
                if (users.workerpreferences != null) {
                    userViewHolder.mNameField.setText(users.getName());
                    if (users.getGender().equals("M")) {
                        Glide.with(getActivity())
                                .load(R.drawable.male)
                                .crossFade()
                                .into(userViewHolder.mGenderField);

                    } else {
                        Glide.with(getActivity())
                                .load(R.drawable.female)
                                .crossFade()
                                .into(userViewHolder.mGenderField);


                    }
                    userViewHolder.mphoneimg.setVisibility(View.GONE);
                    userViewHolder.mPricefield.setText("₹" + users.workerpreferences.getThreeshift());
                    userViewHolder.mAgeField.setText(users.getAge());
                    userViewHolder.mAreaField.setText(users.getAddress2());
                    switch (users.workerpreferences.getCuisine()) {
                        case "North":
                            userViewHolder.mCuisinefield.setText("Cuisines : North Indian");
                            break;
                        case "South":
                            userViewHolder.mCuisinefield.setText("Cuisines : South Indian");
                            break;
                        default:
                            userViewHolder.mCuisinefield.setText("Cuisines : North,South Indian");
                            break;
                    }
                    if (users.workerpreferences.getFoodtype().equals("Veg") || users.workerpreferences.getFoodtype().equals("Both")) {
                        Glide.with(getActivity())
                                .load(R.drawable.veg)
                                .crossFade()
                                .into(userViewHolder.mVegfield);
                    }
                    if (users.workerpreferences.getFoodtype().equals("Non-Veg") || users.workerpreferences.getFoodtype().equals("Both")) {

                        Glide.with(getActivity())
                                .load(R.drawable.nveg)
                                .crossFade()
                                .into(userViewHolder.mNonvegfield);
                    }


                    if (users.getProfilepicture() == null) {
                        Glide.with(getActivity())
                                .load("").placeholder(R.drawable.emptyprof)
                                .into(userViewHolder.mPersonPhoto);

                    } else {
                        Glide.with(getActivity())
                                .load(users.getProfilepicture())
                                .thumbnail(0.3f)
                                .centerCrop()
                                .crossFade()
                                .placeholder(R.drawable.emptyprof)
                                .into(userViewHolder.mPersonPhoto);

                    }
                } else {
                    mAdapter.getRef(position).removeValue();
                }
            }


        };
        mAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

    public static class userholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mNameField;
        final TextView mAgeField;
        final TextView mPricefield;
        final ImageView mGenderField;
        final ImageView mVegfield;
        final ImageView mNonvegfield;
        final TextView mAreaField;
        final ImageView mphoneimg;
        final ImageView mPersonPhoto;
        final ImageView mSomeimg;
        final TextView mCuisinefield;
        final CardView cv;

        public userholder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_view);
            mNameField = (TextView) itemView.findViewById(R.id.person_name);
            mAgeField = (TextView) itemView.findViewById(R.id.person_age);
            mAreaField = (TextView) itemView.findViewById(R.id.person_area);
            mGenderField = (ImageView) itemView.findViewById(R.id.person_gender);
            mPersonPhoto = (ImageView) itemView.findViewById(R.id.person_photo);
            mPricefield = (TextView) itemView.findViewById(R.id.person_price);
            mVegfield = (ImageView) itemView.findViewById(R.id.veg);
            mNonvegfield = (ImageView) itemView.findViewById(R.id.nonveg);
            mCuisinefield = (TextView) itemView.findViewById(R.id.person_cuisine);
            mphoneimg = (ImageView) itemView.findViewById(R.id.phoneimg);

            mSomeimg = (ImageView) itemView.findViewById(R.id.someimg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();

            Intent intent = new Intent(context, MoreDetailActivity.class);

            int clickPosition = getAdapterPosition();  // get position of clicked item

            worker newObject = mAdapter.getItem(clickPosition);   // get clicked new object from news(news is an ArrayList)
            intent.putExtra("Userid", newObject.getUserid());
            intent.putExtra("displayclass", "workers");
            intent.putExtra("edit", false);
            intent.putExtra("Alreadypresent", cookpresent);

            context.startActivity(intent);
        }


    }

}
*/
