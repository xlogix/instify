package xyz.fnplus.instify.ux.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.fnplus.instify.R;
import xyz.fnplus.instify.models.FeePaymentHistoryModel;

/**
 * Created by Abhish3k on 23-03-2017.
 */

public class FeePaymentHistoryAdapter extends RecyclerView.Adapter<FeePaymentHistoryAdapter.DataObjectHolder> {

    private static final String TAG = FeePaymentHistoryAdapter.class.getSimpleName();

    private ArrayList<FeePaymentHistoryModel> mDataSet;

    public FeePaymentHistoryAdapter(ArrayList<FeePaymentHistoryModel> myDataset) {
        mDataSet = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_fee_payment_history, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label.setText(mDataSet.get(position).getmText1());
        holder.dateTime.setText(mDataSet.get(position).getmText2());
        holder.amount.setText(mDataSet.get(position).getmText3());
    }

    public void addItem(FeePaymentHistoryModel dataObj, int index) {
        mDataSet.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataSet.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView label;
        TextView dateTime;
        TextView amount;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);
            amount = (TextView) itemView.findViewById(R.id.textView3);
        }
    }
}
