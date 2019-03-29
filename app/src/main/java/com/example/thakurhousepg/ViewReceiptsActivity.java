package com.example.thakurhousepg;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewReceiptsActivity extends AppCompatActivity {

    DataModule datamodule;
    TableLayout tableLayout;

    TextView roomNoTextView, rIdTextView, onlineAmountTextView, cashAmountTextView, typeForTextView;

    Spinner monthSpiner;

    int currentRowNumber = 1;

    String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipts);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("View Receipts");

        datamodule = DataModule.getInstance();

        tableLayout = (TableLayout) findViewById(R.id.receiptTable);
        monthSpiner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        monthSpiner.setAdapter(adapter);
//        monthSpiner.setBackgroundColor(Color.LTGRAY);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpiner.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        monthSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) monthSpiner.getSelectedView()).setBackgroundColor(Color.LTGRAY);
                tableLayout.removeViews(1, currentRowNumber -1);
                currentRowNumber = 1;
                ArrayList<DataModule.Receipt> receiptArray = datamodule.getAllReceipts(i + 1);
                for(DataModule.Receipt receipt : receiptArray){
                    DataModule.Booking booking = datamodule.getBookingInfo(receipt.bookingId);
                    addNewTableRow(booking.bedNumber, receipt.id, receipt.onlineAmount, receipt.cashAmount,
                            receipt.type.toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayList<DataModule.Receipt> receiptArray = datamodule.getAllReceipts(Calendar.getInstance().get(Calendar.MONTH) + 1);
        for(DataModule.Receipt receipt : receiptArray){
            DataModule.Booking booking = datamodule.getBookingInfo(receipt.bookingId);
            addNewTableRow(booking.bedNumber, receipt.id, receipt.onlineAmount, receipt.cashAmount, receipt.type.toString());
        }

    }

    private void addNewTableRow(String roomNo, String rID, String onlineAmount, String cashAmount, String typeFor){
        TableRow row= new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(lp);
        roomNoTextView = new TextView(this);
        roomNoTextView.setText(roomNo);
        rIdTextView = new TextView(this);
        rIdTextView.setText(rID);
        onlineAmountTextView = new TextView(this);
        onlineAmountTextView.setText(onlineAmount);
        cashAmountTextView = new TextView(this);
        cashAmountTextView.setText(cashAmount);
        typeForTextView = new TextView(this);
        typeForTextView.setText(typeFor);

        row.addView(roomNoTextView);
        row.addView(rIdTextView);
        row.addView(onlineAmountTextView);
        row.addView(cashAmountTextView);
        row.addView(typeForTextView);

        tableLayout.addView(row,currentRowNumber++);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
