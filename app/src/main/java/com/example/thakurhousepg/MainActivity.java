package com.example.thakurhousepg;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DataModule dbHelper;
    Button sendSMS, adminScreen, viewTenant;
    Button btn_receipt, btn_occupancy, btn_payment;
    EditText roomNumber;
    Button receivedRentValue, outstandingRentValue, totalExpectedRentValue;
    TextView headerView;

    private static final String TAG = "MainActivity";

    private static final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

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

        receivedRentValue = findViewById(R.id.receivedRent);
        outstandingRentValue = findViewById(R.id.outstandingRent);
//        totalExpectedRentValue = (Button) findViewById(R.id.totalRent);

        headerView = findViewById(R.id.main_monthButton);

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
        headerView.setOnClickListener(this);

        checkForPermissions();
    }

    private void checkForPermissions(){
        String[] ss = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else{
            adminScreen.setVisibility(View.VISIBLE);
        }
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
            case R.id.outstandingRent:
                Intent pendingIntent = new Intent(MainActivity.this, ViewOutstandingActivity.class);
                startActivity(pendingIntent);
                break;
            case R.id.main_monthButton:
                Intent monthIntent = new Intent(MainActivity.this, MonthlyDataActivity.class);
                startActivity(monthIntent);

                break;
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
                Toast.makeText(MainActivity.this, "This functionality is not implemented yet", Toast.LENGTH_SHORT).show();
//                Toast.makeText(MainActivity.this, "Launching Payments", Toast.LENGTH_SHORT).show();
//
//                Intent paymentIntent = new Intent(MainActivity.this, ReceiptActivity.class);
//                paymentIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
//                paymentIntent.putExtra(getString(R.string.KEY_OUTSTANDING), outstandingPenalty);
//                startActivity(paymentIntent);
                break;
            case R.id.viewRoomButton:
                //TODO: Decide if we need this feature
                Toast.makeText(MainActivity.this, "This functionality is not implemented yet", Toast.LENGTH_SHORT).show();

            case R.id.adminScreen:

                try {
                    dbHelper.copyDatabaseToExternalStorage();
                    Toast.makeText(MainActivity.this, "Database Backup Complete.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Database Backup Failed: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    void setTotalOutstandingRent(){
        receivedRentValue.setText(dbHelper.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModule.ReceiptType.RENT));
//        outstandingRentValue.setText(dbHelper.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
//                DataModule.ReceiptType.DEPOSIT));
        outstandingRentValue.setText(String.valueOf(dbHelper.getTotalPendingAmount(DataModule.PendingType.RENT)));
//        totalExpectedRentValue.setText(dbHelper.getTotalExpectedRent());
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
