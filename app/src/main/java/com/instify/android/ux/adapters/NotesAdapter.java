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
import com.instify.android.R;
import com.instify.android.models.NotesModel;
import com.instify.android.ux.NotesSubjectFilesActivity;
import java.util.Collections;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private Context context;
  private List<NotesModel> data = Collections.emptyList();

  // Create constructor to initialize context and data sent from MainActivity
  public NotesAdapter(Context context, List<NotesModel> data) {
    this.context = context;
    this.data = data;
  }

  // Inflate the layout when ViewHolder created
  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new MyHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.card_view_notes_subjects, parent, false));
  }

  // Bind data
  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    // Get current position of item in RecyclerView to bind data and assign values from list
    MyHolder myHolder = (MyHolder) holder;
    NotesModel current = data.get(position);
    myHolder.textSubjectName.setText(current.subjectName);
    myHolder.textSubjectCode.setText(current.subjectCode);
  }

  // Get total item from List
  @Override public int getItemCount() {
    return data.size();
  }

  public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView textSubjectName;
    TextView textSubjectCode;

    // create constructor to get widget reference
    public MyHolder(View itemView) {
      super(itemView);
      textSubjectName = itemView.findViewById(R.id.title);
      textSubjectCode = itemView.findViewById(R.id.code);

      itemView.setOnClickListener(v -> {
        Intent intent = new Intent(context, NotesSubjectFilesActivity.class);
        intent.putExtra("subject", textSubjectName.getText().toString());
        intent.putExtra("code", textSubjectCode.getText().toString());
        context.startActivity(intent);
      });
    }

    // Click event for all items
    @Override public void onClick(View v) {
      // Do your thing!
    }
  }
}