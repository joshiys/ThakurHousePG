package com.example.thakurhousepg;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import static android.graphics.Typeface.ITALIC;

public class MonthlyDataActivity extends AppCompatActivity {

    private DataModule dataModule;
    TextView expectedRent, rentCash, depositCash, receiptsPayment, totalOutstanding;
    Button showMonth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        expectedRent = findViewById(R.id.expectedRent);
        rentCash = findViewById(R.id.rentCash);
        depositCash = findViewById(R.id.depositCash);
        receiptsPayment = findViewById(R.id.receiptsPayment);
        totalOutstanding = findViewById(R.id.totalOutstanding);
        showMonth = findViewById(R.id.showMonth);


        setTitle("Monthly Data");

        showMonth.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        dataModule = DataModule.getInstance();



        expectedRent.setText("Rs." + dataModule.getTotalExpectedRent());

        rentCash.setText("Rs." + dataModule.getTotalCashReceipts(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModule.ReceiptType.RENT));
        depositCash.setText("Rs." + dataModule.getTotalCashReceipts(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModule.ReceiptType.DEPOSIT));

        receiptsPayment.setText("Rs." + dataModule.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModule.ReceiptType.RENT));
        totalOutstanding.setText("Rs." + String.valueOf(dataModule.getTotalPendingAmount(DataModule.PendingType.RENT)));
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
