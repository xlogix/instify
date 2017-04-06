package com.instify.android.ux.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.instify.android.R;
import com.instify.android.app.CustomVolleyRequest;
import com.instify.android.helpers.SQLiteHandler;
import com.instify.android.models.NotesFileModel;

import java.util.Collections;
import java.util.List;

public class NotesFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<NotesFileModel> data = Collections.emptyList();
    ImageLoader imageLoader;
    SQLiteHandler db;

    //NetworkImageView imageView;
    //   NetworkImageView imageView;
    // create constructor to initialize context and data sent from MainActivity
    public NotesFileAdapter(Context context, List<NotesFileModel> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_view_notes_files_in_subjects, parent, false);
        NotesFileAdapter.MyHolder holder = new NotesFileAdapter.MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        NotesFileAdapter.MyHolder myHolder = (NotesFileAdapter.MyHolder) holder;
        final NotesFileModel current = data.get(position);
        myHolder.notename.setText(current.notename);
        myHolder.notedesc.setText(current.notedesc);
        myHolder.notetime.setText(current.notetime);
        myHolder.noteurl.setText(current.notefile);
        myHolder.author.setText(current.noteposter);
        String url = "https://hashbird.com/gogrit.in/workspace/srm-api/studentImages/" + current.noteregno + ".jpg";
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(url, ImageLoader.getImageListener(myHolder.imageView,
                R.mipmap.ic_launcher, android.R.drawable
                        .ic_dialog_alert));
        myHolder.imageView.setImageUrl(url, imageLoader);

        if (current.noteregno.equals(db.getUserDetails().get("token"))) {

            myHolder.delb.setVisibility(View.VISIBLE);
        } else {
            myHolder.delb.setVisibility(View.INVISIBLE);
        }

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView notename;
        TextView notedesc;
        TextView notefile;
        TextView notetime;
        TextView noteurl;
        TextView author;
        Button down;
        Button delb;
        //TextView textPrice;
        NetworkImageView imageView;

        // create constructor to get widget reference
        public MyHolder(final View itemView) {
            super(itemView);
            notename = (TextView) itemView.findViewById(R.id.name);
            notedesc = (TextView) itemView.findViewById(R.id.desc);
            notetime = (TextView) itemView.findViewById(R.id.datetime);
            author = (TextView) itemView.findViewById(R.id.uname);
            noteurl = (TextView) itemView.findViewById(R.id.noteurl);
            down = (Button) itemView.findViewById(R.id.button3);
            delb = (Button) itemView.findViewById(R.id.delb);
            imageView = (NetworkImageView) itemView.findViewById(R.id.imageView);
            db = new SQLiteHandler(context);
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Uri uri = Uri.parse(noteurl.getText().toString()); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);


                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        // Click event for all items
        @Override
        public void onClick(View v) {
        }

    }
}