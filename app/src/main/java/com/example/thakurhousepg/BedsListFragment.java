package com.example.thakurhousepg;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thakurhousepg.BedsListContent.BedsListItem;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnBedsListInteractionListener}
 * interface.
 */
public class BedsListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 3;
    private OnBedsListInteractionListener mListener;
    private DataModule datamodule;
    private RecyclerView recyclerView;
    private static BedsListRecyclerViewAdapter mAdapter;

    private static final String TAG = "BedsListFragment";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BedsListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BedsListFragment newInstance(int columnCount) {
        BedsListFragment fragment = new BedsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
//        datamodule = DataModule.getInstance();

        BedsListContent.create();
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume()");
        super.onResume();
        reloadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_beds_list, container, true);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            mAdapter = new BedsListRecyclerViewAdapter(BedsListContent.itemMap.get(OccupancyAndBookingActivity.getSelectedTab()),
                    mListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBedsListInteractionListener) {
            mListener = (OnBedsListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBedsListInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnBedsListInteractionListener {
        // TODO: Update argument type and name
        //void onListFragmentInteraction
        void onBedItemClick(BedsListItem item);
        void onTenantClick(BedsListItem item);
        void onRentClick(BedsListItem item);
    }

    public void reloadData(){
        if(mAdapter != null) {
            mAdapter.setmValues(BedsListContent.itemMap.get(OccupancyAndBookingActivity.getSelectedTab()));
            mAdapter.notifyDataSetChanged();
        }
    }
}
