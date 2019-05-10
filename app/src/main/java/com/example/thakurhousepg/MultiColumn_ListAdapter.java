package com.example.thakurhousepg;

/**
 * Created by Mitch on 2016-05-13.
 */

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MultiColumn_ListAdapter extends ArrayAdapter<TableViewColumns> {

    private LayoutInflater mInflater;
    private ArrayList<TableViewColumns> outstandingList = null;
//    private ArrayList<TableClass> tenantTables = null;
    private int mViewResourceId;
    private int totalColumns;

    private Context parent;

    public MultiColumn_ListAdapter(Context context, int nCloumns, int textViewResourceId,
                                   ArrayList<TableViewColumns> vTable) {
        super(context, textViewResourceId, vTable);
        if(nCloumns == 2) {
//            this.masterTables = vTable;
        } else {
            this.outstandingList = vTable;
        }
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
        totalColumns = nCloumns;
        this.parent = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        TableViewColumns mTable = null;
        TableViewColumns outstandingTable = null;

        /*if(masterTables != null) {
            Log.d("TAGG", "mTable is NOT NULL: ");
            mTable = masterTables.get(position);
        } else {
            Log.d("TAGG", "mTable is NULL: ");
        }*/
        if(outstandingList != null) {
            Log.d("TAGG", "tTable is NULL: ");
            outstandingTable = outstandingList.get(position);
        }

        int colorSet = 0;

       /* if (mTable != null) {
            Log.d("TAGG", "Inside Total Columns 2: ");
            TextView roomNumber = (TextView) convertView.findViewById(R.id.textRoomNumber);
            TextView tenantName = (TextView) convertView.findViewById(R.id.textTenantName);
            TextView tenantMobile = (TextView) convertView.findViewById(R.id.textTenantMobile);
            TextView admissionDate = (TextView) convertView.findViewById(R.id.textAdmDate);
            TextView roomRent = (TextView) convertView.findViewById(R.id.textRoomRent);

            if(totalColumns == 2){
                tenantName.setVisibility(View.GONE);
                tenantMobile.setVisibility(View.GONE);
                admissionDate.setVisibility(View.GONE);
            } else {
                tenantName.setVisibility(View.VISIBLE);
                tenantMobile.setVisibility(View.VISIBLE);
                admissionDate.setVisibility(View.VISIBLE);
            }

            if (roomNumber != null) {
                roomNumber.setText("  "+mTable.getRoomNumber());
            }
            if (roomRent != null) {
                roomRent.setText(("  "+mTable.getRoomRent()));
            }

            if(mTable.getRoomNumber() == "Room No"){
                roomNumber.setBackgroundColor(Color.LTGRAY);
                roomRent.setBackgroundColor(Color.LTGRAY);
                colorSet ++;
            }

        }else */
       if(outstandingTable != null){
            TextView roomNumber = (TextView) convertView.findViewById(R.id.roomNumberText);
            TextView rent = (TextView) convertView.findViewById(R.id.outstandingRentText);
            TextView deposit = (TextView) convertView.findViewById(R.id.outstandingDepositText);
            TextView penalty = (TextView) convertView.findViewById(R.id.outstandingPenaltyText);

            if(totalColumns == 2){
//                tenantName.setVisibility(View.GONE);
//                tenantMobile.setVisibility(View.GONE);
//                admissionDate.setVisibility(View.GONE);
            } else {
//                tenantMobile.setVisibility(View.GONE);
//                admissionDate.setVisibility(View.GONE);
//                roomRent.setVisibility(View.GONE);
            }

            if (roomNumber != null) {
                roomNumber.setText(outstandingTable.getRoomNumber());
            }
            if(rent != null) {
                rent.setText(outstandingTable.getOutstandingRent());
            }
            Log.d("TAGG", "Mobile: "+outstandingTable.getOutstandingRent());
            if(deposit != null) {
                deposit.setText(outstandingTable.getOutstandingDeposit());
            }
            if (penalty != null) {
                penalty.setText(outstandingTable.getOutstandingPenalty());
            }

            DataModel.Pending pending = outstandingTable.getPendingEntry();
            if(pending != null && NetworkDataModule.getInstance().getBookingInfo(pending.bookingId).closingDate != null) {
                convertView.setBackgroundColor(Color.RED);
            }

            if(outstandingTable.getRoomNumber() == "Room Number") {
                roomNumber.setBackgroundColor(Color.LTGRAY);
                rent.setBackgroundColor(Color.LTGRAY);
                deposit.setBackgroundColor(Color.LTGRAY);
                penalty.setBackgroundColor(Color.LTGRAY);
            }
        }
        return convertView;
    }
}