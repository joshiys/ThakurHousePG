package com.example.thakurhousepg;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReceiptsListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private DataModule dataModule = DataModule.getInstance();
    private ReceiptsListViewAdapter adapter = null;
    private static final String TAG = "ReceiptListFragment";

    private int forMonth = 0, forMode = 0, forType = 0;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReceiptsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipts_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            adapter = new ReceiptsListViewAdapter(getAdapterValues(), mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(DataModule.Receipt item);
    }

    public void refresh() {
        if (adapter != null) {
            adapter.mValues = getAdapterValues();
            adapter.notifyDataSetChanged();
        }
    }

    public void refreshForMonth(int month) {
        forMonth = month;
        refresh();
    }

    public void refreshForMode(int mode) {
        forMode = mode;
        refresh();
    }

    public void refreshForType(int type) {
        forType = type;
        refresh();
    }

    private ArrayList<DataModule.Receipt> getAdapterValues () {
        Bundle bundle = getArguments();
        ArrayList<DataModule.Receipt> receiptList = null, testReceiptList = null;

        if(forMonth > 0) {
            testReceiptList = dataModule.getAllReceipts(forMonth);
            receiptList = new ArrayList<>();
            for(DataModule.Receipt receipt: testReceiptList){
                if(forMode > 0 || forType > 0 ){
                    if((forMode == 0) ||
                            (forMode == 1 && (!receipt.onlineAmount.isEmpty() && Integer.valueOf(receipt.onlineAmount) > 0)) ||
                            (forMode == 2 && (!receipt.cashAmount.isEmpty() && Integer.valueOf(receipt.cashAmount) > 0))){
                        if((forType == 0) ||
                                (forType == 1 && (receipt.type == DataModule.ReceiptType.RENT)) ||
                                (forType == 2 && (receipt.type == DataModule.ReceiptType.DEPOSIT))){
                            receiptList.add(receipt);
                        }

                    }
                } else {
                    receiptList.add(receipt);
                }
            }
        } else if(bundle != null) {
            if(bundle.getString("TENANT_ID") != null) {
                String tenantId = bundle.getString("TENANT_ID");
                receiptList = dataModule.getReceiptsForTenant(tenantId);
            } else if(bundle.getString("MONTH_NUMBER") != null) {
                forMonth = Integer.parseInt(bundle.getString("MONTH_NUMBER"));

                receiptList = dataModule.getAllReceipts(forMonth);
            }
        }

        return receiptList;
    }
}
