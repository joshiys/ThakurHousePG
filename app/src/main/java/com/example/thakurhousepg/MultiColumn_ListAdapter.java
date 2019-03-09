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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MultiColumn_ListAdapter extends ArrayAdapter<TableClass> {

    private LayoutInflater mInflater;
    private ArrayList<TableClass> masterTables = null;
    private ArrayList<TableClass> tenantTables = null;
    private int mViewResourceId;
    private int totalColumns;

    private Context parent;

    public MultiColumn_ListAdapter(Context context, int nCloumns, int textViewResourceId,
                                   ArrayList<TableClass> vTable) {
        super(context, textViewResourceId, vTable);
        if(nCloumns == 2) {
            this.masterTables = vTable;
        } else {
            this.tenantTables = vTable;
        }
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
        totalColumns = nCloumns;
        this.parent = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        TableClass mTable = null;
        TableClass tTable = null;

        if(masterTables != null) {
            Log.d("TAGG", "mTable is NOT NULL: ");
            mTable = masterTables.get(position);
        } else {
            Log.d("TAGG", "mTable is NULL: ");
        }
        if(tenantTables != null) {
            Log.d("TAGG", "tTable is NULL: ");
            tTable = tenantTables.get(position);
        }

        int colorSet = 0;

//        Log.d("TAGG", "Total Columns: "+totalColumns);
//        Toast.makeText(this.parent, "Total Columns: " +totalColumns, Toast.LENGTH_SHORT).show();

        if (mTable != null) {
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

        }else if(tTable != null){
//            Toast.makeText(this.parent, "Inside column  " +totalColumns, Toast.LENGTH_SHORT).show();
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
//                tenantMobile.setVisibility(View.GONE);
                admissionDate.setVisibility(View.GONE);
//                roomRent.setVisibility(View.GONE);
            }

            if (roomNumber != null) {
                roomNumber.setText(tTable.getRoomNumber());
            }
            if(tenantName != null) {
                tenantName.setText(tTable.getTenantName());
            }
            Log.d("TAGG", "Mobile: "+tTable.getTenantMobile());
            if(tenantMobile != null) {
                tenantMobile.setText(tTable.getTenantMobile());
            }
//            if(admissionDate != null) {
//                admissionDate.setText(tTable.getTenantAdmDate());
//            }
            if (roomRent != null) {
                roomRent.setText(tTable.getRoomRent());//getTenantOutstanding
            }


            if(tTable.getRoomNumber() == "Room Number") {
                roomNumber.setBackgroundColor(Color.LTGRAY);
                roomRent.setBackgroundColor(Color.LTGRAY);
                tenantName.setBackgroundColor(Color.LTGRAY);
                tenantMobile.setBackgroundColor(Color.LTGRAY);
                admissionDate.setBackgroundColor(Color.LTGRAY);
            }

        } else{
            Log.d("TAGG", "Inside Default ELSE ");
        }

        return convertView;
    }
}