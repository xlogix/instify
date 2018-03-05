package com.instify.android.ux.adapters;

/**
 * Created by krsnv on 01-May-17.
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.instify.android.R;
import com.instify.android.models.SubjectsModel;
import com.instify.android.models.TestPerformanceModel;
import java.util.List;

public class TestPerformanceAdapterParent
    extends RecyclerView.Adapter<TestPerformanceAdapterParent.ViewHolder> {

  private List<TestPerformanceModel> myItems;
  private Context mContext;

  public TestPerformanceAdapterParent(List<TestPerformanceModel> myItems, Context context) {
    this.myItems = myItems;
    mContext = context;
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.card_view_test_performance, parent, false));
  }

  @Override public int getItemCount() {
    return myItems.size();
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    holder.setData(myItems.get(position));
    initChildLayoutManager(holder.mToprecyc, myItems.get(position).getSubjects());
  }

  private void initChildLayoutManager(RecyclerView rv_child, List<SubjectsModel> childData) {
    LinearLayoutManager manager =
        new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
    rv_child.setLayoutManager(manager);
    rv_child.setHasFixedSize(true);

    TestPerformanceAdapterChild childAdapter = new TestPerformanceAdapterChild(childData);
    rv_child.setAdapter(childAdapter);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public TestPerformanceModel item;
    @BindView(R.id.testtype) TextView mTesttype;
    @BindView(R.id.toprecyc) RecyclerView mToprecyc;

    public ViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setData(TestPerformanceModel item) {
      this.item = item;
      mTesttype.setText(item.getName());
    }
  }
}