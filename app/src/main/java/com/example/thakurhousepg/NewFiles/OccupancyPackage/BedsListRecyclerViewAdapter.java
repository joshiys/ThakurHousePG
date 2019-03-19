package com.example.thakurhousepg.NewFiles.OccupancyPackage;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.thakurhousepg.NewFiles.OccupancyPackage.ItemFragment.OnListFragmentInteractionListener;
import com.example.thakurhousepg.R;
import com.example.thakurhousepg.NewFiles.OccupancyPackage.BedsListContent.BedsListItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BedsListItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BedsListRecyclerViewAdapter extends RecyclerView.Adapter<BedsListRecyclerViewAdapter.ViewHolder> {

    private final List<BedsListItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public BedsListRecyclerViewAdapter(List<BedsListItem> items, OnListFragmentInteractionListener listener) {
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
        holder.mIdView.setText(mValues.get(position).bedNumber);
        holder.mContentView.setText(mValues.get(position).tenantName);
        holder.mRentView.setText(mValues.get(position).rentPending);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mRentView;
        public BedsListItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.bed_number);
            mContentView = (TextView) view.findViewById(R.id.tenant_name);
            mRentView = view.findViewById(R.id.pending_amount);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
