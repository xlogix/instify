package com.instify.android.ux.adapters;

/**
 * Created by Abhish3k on 05-04-2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.instify.android.R;
import com.instify.android.models.NotesModel;
import com.instify.android.ux.NotesSubjectFilesActivity;

import java.util.Collections;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<NotesModel> data = Collections.emptyList();

    // create constructor to initialize context and data sent from MainActivity
    public NotesAdapter(Context context, List<NotesModel> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_view_notes_subjects, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        NotesModel current = data.get(position);
        myHolder.textFishName.setText(current.fishName);
        myHolder.textType.setText(current.catName);
//        myHolder.textSize.setText("REG-NO: " + current.sizeName);
//        myHolder.textType.setText("YEAR: " + current.catName);
//        myHolder.textPrice.setText(current.price);
//        myHolder.textPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        //    new DownloadImage(imageView).execute("");
        //  myHolder.onClick(new );

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textFishName;
        TextView textType;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textFishName = (TextView) itemView.findViewById(R.id.title);
            textType = (TextView) itemView.findViewById(R.id.code);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, NotesSubjectFilesActivity.class);
                    intent.putExtra("subject", textFishName.getText().toString());
                    intent.putExtra("code", textType.getText().toString());

                    context.startActivity(intent);
                }
            });
        }

        // Click event for all items
        @Override
        public void onClick(View v) {

        }
    }
}