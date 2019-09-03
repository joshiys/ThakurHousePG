package com.sanyog.thakurhousepg;

//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sanyog.thakurhousepg.BedsListFragment.OnBedsListInteractionListener;
import com.sanyog.thakurhousepg.BedsListContent.BedsListItem;


import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BedsListItem} and makes a call to the
 * specified {@link BedsListFragment.OnBedsListInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BedsListRecyclerViewAdapter extends RecyclerView.Adapter<BedsListRecyclerViewAdapter.ViewHolder> {

    private List<BedsListItem> mValues;
    private final BedsListFragment.OnBedsListInteractionListener mListener;

    BedsListRecyclerViewAdapter(List<BedsListItem> items, OnBedsListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_bed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mBedNumber.setText(mValues.get(position).bedNumber);
        holder.mTenantName.setText(mValues.get(position).tenantName);
        holder.mRentView.setText(mValues.get(position).rentPayble);

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
                mListener.onRentClick(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final MaterialButton mBedNumber;
        final MaterialButton mTenantName;
        final MaterialButton mRentView;
        BedsListItem mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mBedNumber = view.findViewById(R.id.bed_number);
            mTenantName = view.findViewById(R.id.tenant_name);
            mRentView = view.findViewById(R.id.pending_amount);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mTenantName.getText() + "'";
        }
    }

    public void setmValues(List<BedsListItem> newValues) {
        mValues = newValues;
    }
}
