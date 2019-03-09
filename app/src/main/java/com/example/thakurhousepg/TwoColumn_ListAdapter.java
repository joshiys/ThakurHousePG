package com.example.thakurhousepg;

/**
 * Created by Mitch on 2016-05-13.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class TwoColumn_ListAdapter extends ArrayAdapter<MasterTable> {

    private LayoutInflater mInflater;
    private ArrayList<MasterTable> masterTables;
    private int mViewResourceId;

    public TwoColumn_ListAdapter(Context context, int textViewResourceId, ArrayList<MasterTable> mTable) {
        super(context, textViewResourceId, mTable);
        this.masterTables = mTable;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        MasterTable mTable = masterTables.get(position);
        int colorSet = 0;

        if (mTable != null) {
            TextView roomNumber = (TextView) convertView.findViewById(R.id.textRoomNumber);
            TextView roomRent = (TextView) convertView.findViewById(R.id.textRoomRent);

            if (roomNumber != null) {
                roomNumber.setText("  "+mTable.getRoomNumber());
            }
            if (roomRent != null) {
                roomRent.setText(("  "+mTable.getRoomRent()));
            }
            if(colorSet <= 2 && (mTable.getRoomNumber() == "Room Number") || (mTable.getRoomRent() == "Room Rent")){
                roomNumber.setBackgroundColor(Color.LTGRAY);
                roomRent.setBackgroundColor(Color.LTGRAY);
                colorSet ++;
            }

        }

        return convertView;
    }
}