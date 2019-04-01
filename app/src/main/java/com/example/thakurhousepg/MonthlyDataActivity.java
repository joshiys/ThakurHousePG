package com.example.thakurhousepg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MonthlyDataActivity extends AppCompatActivity {

    private DataModule dataModule;
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

        showMonth.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        dataModule = DataModule.getInstance();



        expectedRent.setText("Rs." + dataModule.getTotalExpectedRent());

        rentCash.setText("Rs." + dataModule.getTotalCashReceipts(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModule.ReceiptType.RENT));
        depositCash.setText("Rs." + dataModule.getTotalCashReceipts(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModule.ReceiptType.DEPOSIT));

        rentReceipts.setText("Rs." + dataModule.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModule.ReceiptType.RENT));
        totalOutstanding.setText("Rs." + String.valueOf(dataModule.getTotalPendingAmount(DataModule.PendingType.RENT)));

        depositReceipts.setText("Rs." + dataModule.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModule.ReceiptType.DEPOSIT));

        totalDepositOutstanding.setText("Rs." + String.valueOf(dataModule.getTotalPendingAmount(DataModule.PendingType.DEPOSIT)));
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
