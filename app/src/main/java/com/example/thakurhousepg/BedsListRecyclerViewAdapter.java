package com.example.thakurhousepg;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.thakurhousepg.BedsListFragment.OnBedsListInteractionListener;
import com.example.thakurhousepg.BedsListContent.BedsListItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BedsListItem} and makes a call to the
 * specified {@link BedsListFragment.OnBedsListInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BedsListRecyclerViewAdapter extends RecyclerView.Adapter<BedsListRecyclerViewAdapter.ViewHolder> {

    private final List<BedsListItem> mValues;
    private final BedsListFragment.OnBedsListInteractionListener mListener;

    public BedsListRecyclerViewAdapter(List<BedsListItem> items, OnBedsListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mBedNumber.setText(mValues.get(position).bedNumber);
        holder.mTenantName.setText(mValues.get(position).tenantName);
        holder.mRentView.setText(mValues.get(position).rentPending);

        holder.mBedNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBedItemClick(holder.mItem);
            }
        });

        holder.mTenantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onTenantClick(holder.mItem);
            }
        });
        holder.mRentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Do we need to do something here?
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final Button mBedNumber;
        public final Button mTenantName;
        public final Button mRentView;
        public BedsListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBedNumber = (Button) view.findViewById(R.id.bed_number);
            mTenantName = (Button) view.findViewById(R.id.tenant_name);
            mRentView = (Button) view.findViewById(R.id.pending_amount);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTenantName.getText() + "'";
        }
    }
}
