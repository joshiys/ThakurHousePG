package com.sanyog.thakurhousepg;

import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class MonthlyDataActivity extends AppCompatActivity {

    private NetworkDataModule dataModule;
    TextView expectedRent, rentCash, depositCash, rentReceipts, totalOutstanding, depositReceipts, totalDepositOutstanding;
    Button showMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        showMonth.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        dataModule = NetworkDataModule.getInstance();



        expectedRent.setText("Rs." + dataModule.getTotalExpectedRent());

        rentCash.setText("Rs." + dataModule.getTotalCashReceipts(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModel.ReceiptType.RENT));
        depositCash.setText("Rs." + dataModule.getTotalCashReceipts(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModel.ReceiptType.DEPOSIT));

        rentReceipts.setText("Rs." + dataModule.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModel.ReceiptType.RENT));
        totalOutstanding.setText("Rs." + String.valueOf(dataModule.getTotalPendingAmount(DataModel.PendingType.RENT)));

        depositReceipts.setText("Rs." + dataModule.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModel.ReceiptType.DEPOSIT));

        totalDepositOutstanding.setText("Rs." + String.valueOf(dataModule.getTotalPendingAmount(DataModel.PendingType.DEPOSIT)));
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
