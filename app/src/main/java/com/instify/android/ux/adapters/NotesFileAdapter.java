package com.instify.android.ux.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.instify.android.R;
import com.instify.android.models.NotesFileModel;
import java.util.Collections;
import java.util.List;

public class NotesFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private List<NotesFileModel> data = Collections.emptyList();
  private Context context;
  private LayoutInflater inflater;

  // create constructor to initialize context and data sent from MainActivity
  public NotesFileAdapter(Context context, List<NotesFileModel> data) {
    this.context = context;
    inflater = LayoutInflater.from(context);
    this.data = data;
  }

  // Inflate the layout when ViewHolder created
  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // Inflate View
    View view = inflater.inflate(R.layout.card_view_notes_subjects_item, parent, false);
    // Set adapter
    return new NotesFileAdapter.MyHolder(view);
  }

  // Bind data
  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    // Get current position of item in RecyclerView to bind data and assign values from list
    NotesFileAdapter.MyHolder myHolder = (NotesFileAdapter.MyHolder) holder;
    final NotesFileModel current = data.get(position);
    myHolder.setDataToView(current);
  }

  // return total item from List
  @Override public int getItemCount() {
    return data.size();
  }

  public static class MyHolder extends RecyclerView.ViewHolder {

    TextView noteName;
    TextView noteDesc;
    TextView noteTime;
    TextView author;
    ImageView imageView;
    ImageButton shareButton;
    String link;
    public CardView cv;

    // create constructor to get widget reference
    public MyHolder(View itemView) {
      super(itemView);
      cv = itemView.findViewById(R.id.cardViewSubjectsItem);
      noteName = itemView.findViewById(R.id.name);
      noteDesc = itemView.findViewById(R.id.desc);
      noteTime = itemView.findViewById(R.id.datetime);
      author = itemView.findViewById(R.id.uname);
      imageView = itemView.findViewById(R.id.imageView);
      shareButton = itemView.findViewById(R.id.share_button);
    }

    public void setDataToView(NotesFileModel current) {
      noteName.setText(current.notename);
      noteDesc.setText(current.notedesc);
      long now = System.currentTimeMillis();
      noteTime.setText(
          DateUtils.getRelativeTimeSpanString(current.getUnixtime(), now, DateUtils.DAY_IN_MILLIS));
      author.setText(current.noteposter);
      shareButton.setOnClickListener(v -> {
        // TODO: SHare Link via text
      });
      switch (current.notetype) {
        case "doc":
          imageView.setImageResource(R.drawable.ic_doc);
          break;
        case "pdf":
          imageView.setImageResource(R.drawable.ic_pdf);
          break;
        case "audio":
          imageView.setImageResource(R.drawable.ic_music_player);
          break;
        case "video":
          imageView.setImageResource(R.drawable.ic_video_camera);
          break;
        case "image":
          imageView.setImageResource(R.drawable.ic_image);
          break;
        case "other":
          imageView.setImageResource(R.drawable.ic_attach_file_black_24dp);
          break;
        default:
          imageView.setImageResource(R.drawable.ic_attach_file_black_24dp);
      }
      link = current.getNotefile();
    }
  }
}