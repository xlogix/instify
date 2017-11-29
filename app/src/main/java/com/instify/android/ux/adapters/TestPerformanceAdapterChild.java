package com.instify.android.ux.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.instify.android.R;
import com.instify.android.models.SubjectsModel;

/**
 * Created by krsnv on 01-May-17.
 */

public class TestPerformanceAdapterChild extends RecyclerView.Adapter<TestPerformanceAdapterChild.ViewHolder> {
    private List<SubjectsModel> myItems;

    public TestPerformanceAdapterChild(List<SubjectsModel> items) {
        myItems = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_test_performance_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return myItems.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(myItems.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public SubjectsModel item;
        @BindView(R.id.title)
        TextView mTitle;
        @BindView(R.id.code)
        TextView mCode;
        @BindView(R.id.marks)
        TextView mMarks;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);


        }

        public void setData(SubjectsModel item) {
            this.item = item;
            mTitle.setText(item.getNAME());
            mCode.setText(item.getCODE());
            mMarks.setText(item.getMARKS());
        }


    }


}
