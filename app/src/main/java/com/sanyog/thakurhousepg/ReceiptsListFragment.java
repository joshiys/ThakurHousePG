package com.sanyog.thakurhousepg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.util.DisplayMetrics.DENSITY_DEFAULT;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReceiptsListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private NetworkDataModule dataModule = NetworkDataModule.getInstance();
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipts_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            adapter = new ReceiptsListViewAdapter(getAdapterValues(), mListener);
            recyclerView.setAdapter(adapter);

            ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                    int index = viewHolder.getAdapterPosition() - 1;
                    Log.i(TAG, "i is " + i);
                    mListener.onListItemSwipeRight(adapter.mValues.get(index));
                    adapter.mValues.remove(index);
                    adapter.notifyItemRemoved(index);
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    c.clipRect(0, viewHolder.itemView.getTop(), dX, viewHolder.itemView.getBottom());

                    if(dX < viewHolder.itemView.getWidth() / 2)
                        c.drawColor(Color.parseColor("#FFAA00"));
                    else
                        c.drawColor(Color.RED);

                    Drawable deleteIcon = getResources().getDrawableForDensity(R.drawable.ic_delete_black_24dp, DENSITY_DEFAULT, null);
                    Integer textMargin = ((int) getResources().getDimension(R.dimen.text_margin));

                    assert deleteIcon != null;
                    deleteIcon.setBounds(viewHolder.itemView.getPaddingLeft() + viewHolder.itemView.getLeft(), viewHolder.itemView.getTop() + 8,
                            deleteIcon.getIntrinsicWidth(),viewHolder.itemView.getTop() + deleteIcon.getIntrinsicHeight() + 8);
                    deleteIcon.draw(c);
                }
            });

            touchHelper.attachToRecyclerView(recyclerView);
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
        void onListItemTouch(DataModel.Receipt item);
        void onListItemSwipeRight(DataModel.Receipt item);
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

    private ArrayList<DataModel.Receipt> getAdapterValues () {
        Bundle bundle = getArguments();
        ArrayList<DataModel.Receipt> receiptList = null, testReceiptList = null;

        if(forMonth > 0) {
            testReceiptList = dataModule.getAllReceipts(forMonth);
            receiptList = new ArrayList<>();
            for(DataModel.Receipt receipt: testReceiptList){
                if(forMode > 0 || forType > 0 ){
                    if((forMode == 0) ||
                            (forMode == 1 && (!receipt.onlineAmount.isEmpty() && Integer.valueOf(receipt.onlineAmount) > 0)) ||
                            (forMode == 2 && (!receipt.cashAmount.isEmpty() && Integer.valueOf(receipt.cashAmount) > 0))){
                        if((forType == 0) ||
                                (forType == 1 && (receipt.type == DataModel.ReceiptType.RENT)) ||
                                (forType == 2 && (receipt.type == DataModel.ReceiptType.DEPOSIT))){
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
