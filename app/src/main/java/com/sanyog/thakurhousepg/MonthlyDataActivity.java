package com.sanyog.thakurhousepg;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class MonthlyDataActivity extends AppCompatActivity {

    private NetworkDataModule dataModule;
    TextView expectedRent, rentCash, depositCash, rentReceipts, totalOutstanding, depositReceipts, totalDepositOutstanding;
    Spinner showMonth;
    String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_data);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        expectedRent = findViewById(R.id.expectedRent);
        rentCash = findViewById(R.id.rentCash);
        depositCash = findViewById(R.id.depositCash);
        rentReceipts = findViewById(R.id.rentReceipts);
        totalOutstanding = findViewById(R.id.totalRentOutstanding);
        showMonth = findViewById(R.id.showMonth);
        depositReceipts = findViewById(R.id.depositReceipts);
        totalDepositOutstanding = findViewById(R.id.totalDepositOutstanding);


        setTitle("Monthly Data");
        rentCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent receivedIntent = new Intent(MonthlyDataActivity.this, ViewReceiptsActivity.class);
                receivedIntent.putExtra("MODE", 2);//Cash
                receivedIntent.putExtra("TYPE", 1);//Rent
                startActivity(receivedIntent);

            }
        });


        depositCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent receivedIntent = new Intent(MonthlyDataActivity.this, ViewReceiptsActivity.class);
                receivedIntent.putExtra("MODE", 2);//Cash
                receivedIntent.putExtra("TYPE", 2);//Deposit
                startActivity(receivedIntent);

            }
        });

        totalOutstanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pendingIntent = new Intent(MonthlyDataActivity.this, ViewOutstandingActivity.class);
                startActivity(pendingIntent);

            }
        });

        totalDepositOutstanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pendingIntent = new Intent(MonthlyDataActivity.this, ViewOutstandingActivity.class);
                startActivity(pendingIntent);
            }
        });

        dataModule = NetworkDataModule.getInstance();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        showMonth.setAdapter(adapter);
        //.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        showMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        showMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                (showMonth.getSelectedView()).setBackgroundColor(Color.LTGRAY);
                ShowMonthlyData(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ShowMonthlyData(Calendar.getInstance().get(Calendar.MONTH));
    }

    private void ShowMonthlyData(Integer forMonth) {
        expectedRent.setText(getString(R.string.rupees) + dataModule.getTotalExpectedRent());

        rentCash.setText(getString(R.string.rupees) + dataModule.getTotalCashReceipts(forMonth + 1,
                Calendar.getInstance().get(Calendar.YEAR),
                DataModel.ReceiptType.RENT));
        depositCash.setText(getString(R.string.rupees) + dataModule.getTotalCashReceipts(forMonth + 1,
                Calendar.getInstance().get(Calendar.YEAR),
                DataModel.ReceiptType.DEPOSIT));

        rentReceipts.setText(getString(R.string.rupees) + dataModule.getTotalReceivedAmountForMonth(forMonth + 1,
                Calendar.getInstance().get(Calendar.YEAR),
                DataModel.ReceiptType.RENT));
        totalOutstanding.setText(getString(R.string.rupees) + String.valueOf(dataModule.getTotalPendingAmount(DataModel.PendingType.RENT)));

        depositReceipts.setText(getString(R.string.rupees) + dataModule.getTotalReceivedAmountForMonth(forMonth + 1,
                Calendar.getInstance().get(Calendar.YEAR),
                DataModel.ReceiptType.DEPOSIT));

        totalDepositOutstanding.setText(getString(R.string.rupees)+ String.valueOf(dataModule.getTotalPendingAmount(DataModel.PendingType.DEPOSIT)));
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
