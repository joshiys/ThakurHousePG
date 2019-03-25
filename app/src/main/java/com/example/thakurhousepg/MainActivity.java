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
    TextView totalOutstandinValue;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


         btn_receipt = (Button) findViewById(R.id.receipt_button);
         btn_occupancy = (Button) findViewById(R.id.occupancy_button);
         btn_payment = (Button) findViewById(R.id.payments_button);

        adminScreen = (Button) findViewById(R.id.adminScreen);
        viewTenant = (Button) findViewById(R.id.viewRoomButton);

        roomNumber = (EditText) findViewById(R.id.roomNumberText);
        sendSMS = (Button) findViewById(R.id.sendSMSButton);
        totalOutstandinValue = (TextView) findViewById(R.id.totalOutstandinValue);

        btn_receipt.setOnClickListener(this);
        btn_occupancy.setOnClickListener(this);
        btn_payment.setOnClickListener(this);
        adminScreen.setOnClickListener(this);
        viewTenant.setOnClickListener(this);

        roomNumber.setSelection(roomNumber.getText().length());

        sendSMS.setEnabled(false);
        dbHelper = new DataModule(this);

        setPendingAmountEntries();
        setTotalOutstandingRent();
        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setTotalOutstandingRent();
    }

    @Override
    public void onClick(View view) {
        String outstandingRent = "", outstandingDeposit = "", outstandingPenalty = "";

        switch(view.getId()){
            case R.id.receipt_button:

                Toast.makeText(MainActivity.this, "Launching Receipts", Toast.LENGTH_SHORT).show();

                Intent receiptIntent = new Intent(MainActivity.this, ReceiptActivity.class);
                receiptIntent.putExtra("SECTION", "Rent");

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
        totalOutstandinValue.setText(dbHelper.getTotalOutstandingRent());
    }

    private void setPendingAmountEntries() {
        Calendar rightNow = Calendar.getInstance();

        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        int monthUpdated = settings.getInt("pendingEntriesUpdatedForMonth", 0);

        if(monthUpdated == 0 || monthUpdated != rightNow.get(Calendar.MONTH)) {
            Log.i(TAG, "Creating Pending Entries for the month of " + rightNow.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.SHORT, Locale.US));

            if(dbHelper.createMonthlyPendingEntries()) {
                SharedPreferences.Editor settingsEditor = settings.edit();
                settingsEditor.putInt("pendingEntriesUpdatedForMonth", rightNow.get(Calendar.MONTH));
                settingsEditor.commit();
            }
        }
    }

}
