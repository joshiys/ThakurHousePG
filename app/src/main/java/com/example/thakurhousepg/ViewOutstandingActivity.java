package com.example.thakurhousepg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewOutstandingActivity extends AppCompatActivity {

    ArrayList<TableViewColumns> outstandingTableList;
    TableViewColumns outstandingTable;
    ListView listView;
    DataModule dataModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_outstanding);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Outstanding List");

        dataModule = DataModule.getInstance();

        showOutstandingIncoming();
    }

    private void showOutstandingIncoming(){

        outstandingTableList = new ArrayList<>();
        int i = 0;
        outstandingTable = new TableViewColumns("Room Number", "Rent","Deposit","Penalty");
        outstandingTableList.add(i, outstandingTable);
        i++;

        ArrayList<DataModule.Booking> bookingEntries = dataModule.getAllBookingInfo();

        for(DataModule.Booking entry : bookingEntries){
            ArrayList<DataModule.Pending> pendingList = dataModule.getPendingEntriesForBooking(entry.id);
            for(DataModule.Pending pendingentry : pendingList) {
                String rent = "0", deposit = "0", penalty = "0";
                if (pendingentry.type == DataModule.PendingType.RENT) {
                    rent = String.valueOf(pendingentry.pendingAmt);
                } else if (pendingentry.type == DataModule.PendingType.DEPOSIT) {
                    deposit = String.valueOf(pendingentry.pendingAmt);
                } else {
                    penalty = String.valueOf(pendingentry.pendingAmt);
                }
                outstandingTable = new TableViewColumns(entry.bedNumber, rent, deposit, penalty);
                outstandingTableList.add(i++, outstandingTable);
            }
        }
        final MultiColumn_ListAdapter adapter = new MultiColumn_ListAdapter(this, 4,
                R.layout.list_adapter_view, outstandingTableList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
