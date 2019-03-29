package com.example.thakurhousepg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DataModule dbHelper;
    Button sendSMS, adminScreen, viewTenant;
    Button btn_receipt, btn_occupancy, btn_payment;
    EditText roomNumber;
    Button receivedRentValue, outstandingRentValue, totalExpectedRentValue;
    Button receivedDepositValue, outstandingDepositValue, totalExpectedDepositValue;
    TextView headerView;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_receipt = findViewById(R.id.receipt_button);
        btn_occupancy = findViewById(R.id.occupancy_button);
        btn_payment = findViewById(R.id.payments_button);

        adminScreen = findViewById(R.id.adminScreen);
        viewTenant = findViewById(R.id.viewRoomButton);

        roomNumber = findViewById(R.id.roomNumberText);
        sendSMS = findViewById(R.id.sendSMSButton);

        receivedRentValue = (Button) findViewById(R.id.receivedRent);
        outstandingRentValue = (Button) findViewById(R.id.outstandingRent);
        totalExpectedRentValue = (Button) findViewById(R.id.totalRent);

        headerView = findViewById(R.id.main_month);

        btn_receipt.setOnClickListener(this);
        btn_occupancy.setOnClickListener(this);
        btn_payment.setOnClickListener(this);
        adminScreen.setOnClickListener(this);
        viewTenant.setOnClickListener(this);

        receivedRentValue.setOnClickListener(this);
        outstandingRentValue.setOnClickListener(this);

        roomNumber.setSelection(roomNumber.getText().length());

        sendSMS.setEnabled(false);
        DataModule.setContext(this);
        dbHelper = DataModule.getInstance();

        setPendingAmountEntries();
        headerView.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTotalOutstandingRent();
    }

    @Override
    public void onClick(View view) {
        String outstandingRent = "", outstandingDeposit = "", outstandingPenalty = "";

        switch(view.getId()){
            case R.id.receivedRent:
                Intent receivedIntent = new Intent(MainActivity.this, ViewReceiptsActivity.class);
                startActivity(receivedIntent);
                break;
            case R.id.receipt_button:

                Toast.makeText(MainActivity.this, "Launching Receipts", Toast.LENGTH_SHORT).show();

                Intent receiptIntent = new Intent(MainActivity.this, ReceiptActivity.class);
                receiptIntent.putExtra("SECTION", "Rent");
                receiptIntent.putExtra("RENT_AMOUNT", "");
                receiptIntent.putExtra("DEPOSIT_AMOUNT", "");

                startActivity(receiptIntent);
                break;
            case R.id.occupancy_button:
                Toast.makeText(MainActivity.this, "Launching Occupancy & Booking", Toast.LENGTH_SHORT).show();
                Intent occupancyIntent = new Intent(MainActivity.this, OccupancyAndBookingActivity.class);
//                adminIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
                startActivity(occupancyIntent);
                break;

            case R.id.payments_button:

                Toast.makeText(MainActivity.this, "Launching Payments", Toast.LENGTH_SHORT).show();

                Intent paymentIntent = new Intent(MainActivity.this, ReceiptActivity.class);
                paymentIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
                paymentIntent.putExtra(getString(R.string.KEY_OUTSTANDING), outstandingPenalty);
                startActivity(paymentIntent);
                break;
            case R.id.viewRoomButton:
            case R.id.adminScreen:
                //TODO: Decide if we need this feature
                Toast.makeText(MainActivity.this, "This functionality is not implemented yet", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    void setTotalOutstandingRent(){
        receivedRentValue.setText(dbHelper.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModule.ReceiptType.RENT));
        outstandingRentValue.setText(String.valueOf(dbHelper.getTotalPendingAmount()));
        totalExpectedRentValue.setText(dbHelper.getTotalExpectedRent());
    }

    private void setPendingAmountEntries() {
        Calendar rightNow = Calendar.getInstance();

        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        int monthUpdated = settings.getInt("pendingEntriesUpdatedForMonth", 0);

        if(monthUpdated == 0 || monthUpdated != (rightNow.get(Calendar.MONTH) + 1)) {
            Log.i(TAG, "Creating Pending Entries for the month of " + rightNow.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

            if(dbHelper.createMonthlyPendingEntries()) {
                SharedPreferences.Editor settingsEditor = settings.edit();
                    /* Calendar object in Java starts the month entries from 0, (as in 0 for Jauary to 11 for December)
                        But SQlite starts them from 1, so make calculation in DataModule easier, add 1 here
                    */
                settingsEditor.putInt("pendingEntriesUpdatedForMonth", (rightNow.get(Calendar.MONTH) + 1));
                settingsEditor.commit();
            }
        }
    }

}
