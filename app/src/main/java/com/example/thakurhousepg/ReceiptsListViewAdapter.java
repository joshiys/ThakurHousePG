package com.example.thakurhousepg;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thakurhousepg.ReceiptsListFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;

public class ReceiptsListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<DataModule.Receipt> mValues;
    private final OnListFragmentInteractionListener mListener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public ReceiptsListViewAdapter(ArrayList<DataModule.Receipt> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_receipts_list, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_receipts_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder vHolder, int position) {

        if (vHolder instanceof ItemViewHolder) {
            final ItemViewHolder holder = (ItemViewHolder) vHolder;

            //Decrement it by 1 because position 0 is designated for Header
            position -= 1;

            holder.mItem = mValues.get(position);
            holder.cashRentAmount.setText(holder.mItem.cashAmount);
            holder.onlineRentAmount.setText(holder.mItem.onlineAmount);
            holder.receiptDate.setText(holder.mItem.date);
            holder.receiptType.setText(holder.mItem.type.toString());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);

        if (position == 0)
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView onlineRentAmount;
        public final TextView cashRentAmount;
        public final TextView receiptDate;
        public final TextView receiptType;
        public DataModule.Receipt mItem;

        public ItemViewHolder(View view) {
            super(view);
            mView = view;
            onlineRentAmount = (TextView) view.findViewById(R.id.receipt_list_online_amount);
            cashRentAmount = (TextView) view.findViewById(R.id.receipt_list_cash_amount);
            receiptDate = (TextView) view.findViewById(R.id.receipt_list_date);
            receiptType = (TextView) view.findViewById(R.id.receipt_list_type);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + cashRentAmount.getText() + "'";
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView onlineRentAmountLabel;
        public final TextView cashRentAmountLabel;
        public final TextView receiptDateLabel;
        public final TextView receiptTypeLabel;

        public HeaderViewHolder(View view) {
            super(view);
            mView = view;
            onlineRentAmountLabel = (TextView) view.findViewById(R.id.receipt_header_online_amount);
            cashRentAmountLabel = (TextView) view.findViewById(R.id.receipt_header_cash_amount);
            receiptDateLabel = (TextView) view.findViewById(R.id.receipt_header_date);
            receiptTypeLabel = (TextView) view.findViewById(R.id.receipt_header_type);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}
