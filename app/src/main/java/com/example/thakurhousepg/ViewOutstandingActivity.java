package com.example.thakurhousepg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewOutstandingActivity extends AppCompatActivity {

    ArrayList<TableViewColumns> outstandingTableList;
    TableViewColumns outstandingTable;
    ListView listView;
    NetworkDataModule dataModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_outstanding);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Outstanding List");

        dataModule = NetworkDataModule.getInstance();

    }

    @Override
    protected void onResume() {
        super.onResume();
        showOutstandingIncoming();
    }

    private void showOutstandingIncoming(){

        outstandingTableList = new ArrayList<>();
        int i = 0;
        outstandingTable = new TableViewColumns("Room Number", "Rent","Deposit","Penalty");
        outstandingTableList.add(i, outstandingTable);
        i++;

        ArrayList<DataModel.Pending> pendingList = dataModule.getAllPendingEntries();

        for(DataModel.Pending pendingentry : pendingList) {
            outstandingTable = TableViewColumns.fromPendingEntry(pendingentry);
            outstandingTableList.add(i++, outstandingTable);
        }

        final MultiColumn_ListAdapter adapter = new MultiColumn_ListAdapter(this, 4,
                R.layout.list_adapter_view, outstandingTableList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent receiptIntent = new Intent(ViewOutstandingActivity.this, ReceiptActivity.class);
                DataModel.Pending pendingEntry = outstandingTableList.get((int)l).getPendingEntry();

                receiptIntent.putExtra("ROOM_NUMBER", outstandingTableList.get((int)l).getRoomNumber());
                receiptIntent.putExtra("PENDING_ID", outstandingTableList.get((int)l).getPendingEntry().id);

                switch (pendingEntry.type) {
                    case DEPOSIT:
                        receiptIntent.putExtra("DEPOSIT_AMOUNT", String.valueOf(pendingEntry.pendingAmt));
                        receiptIntent.putExtra("SECTION", "Deposit");
                        break;
                    case RENT:
                        receiptIntent.putExtra("RENT_AMOUNT", String.valueOf(pendingEntry.pendingAmt));
                        receiptIntent.putExtra("SECTION", "Rent");
                        break;
                    case PENALTY:
                        receiptIntent.putExtra("PENALTY_AMOUNT", String.valueOf(pendingEntry.pendingAmt));
                        receiptIntent.putExtra("SECTION", "Penalty");
                        break;
                }

                startActivity(receiptIntent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
