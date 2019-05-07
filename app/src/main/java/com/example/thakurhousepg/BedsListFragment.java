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
    private static final String ARG_SECTION_NUMBER = "section_number";
    // TODO: Customize parameters
    private OnBedsListInteractionListener mListener;
    private RecyclerView recyclerView;
    private BedsListRecyclerViewAdapter mAdapter;

    private static final String TAG = BedsListFragment.class.getName();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BedsListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static BedsListFragment newInstance(int sectionNumber) {
        BedsListFragment fragment = new BedsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BedsListContent.create();
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
        View view = inflater.inflate(R.layout.fragment_beds_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            mAdapter = new BedsListRecyclerViewAdapter(BedsListContent.itemMap.get(getArguments().getInt(ARG_SECTION_NUMBER)),
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
            mAdapter.setmValues(BedsListContent.itemMap.get(getArguments().getInt(ARG_SECTION_NUMBER)));
            mAdapter.notifyDataSetChanged();
        }
    }
}
