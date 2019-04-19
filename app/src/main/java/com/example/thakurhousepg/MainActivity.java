package com.example.thakurhousepg;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DataModule dbHelper;
    SMSManagement smsHandle;
    Button sendSMS, adminScreen, viewTenant;
    Button btn_receipt, btn_occupancy, btn_payment;
    EditText roomNumber;
    Button receivedRentValue, outstandingRentValue, totalExpectedRentValue;
    TextView headerView;

    private static final String TAG = "MainActivity";
    private NetworkDataModule restService;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.SEND_SMS,
//            Manifest.permission.INTERNET,
//            android.Manifest.permission.READ_CONTACTS,
//            android.Manifest.permission.WRITE_CONTACTS,
//            android.Manifest.permission.READ_SMS,
//            android.Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_receipt = findViewById(R.id.receipt_button);
        btn_occupancy = findViewById(R.id.occupancy_button);
        btn_payment = findViewById(R.id.payments_button);

        adminScreen = findViewById(R.id.adminScreen);
        viewTenant = findViewById(R.id.load_database_button);

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

        //sendSMS.setEnabled(false);
        DataModule.setContext(this);
        dbHelper = DataModule.getInstance();

        SMSManagement.setContext(this);
        smsHandle = SMSManagement.getInstance();

        setPendingAmountEntries();
        headerView.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
        headerView.setOnClickListener(this);

        sendSMS.setOnClickListener(this);
        restService = NetworkDataModule.getInstance();
    }

    private void checkForPermissions() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permission},
                        PERMISSION_ALL);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForPermissions();
        setTotalOutstandingRent();
    }

    @Override
    public void onClick(View view) {
        String outstandingRent = "", outstandingDeposit = "", outstandingPenalty = "";

        switch(view.getId()){
            case R.id.sendSMSButton:
                if(!roomNumber.getText().toString().isEmpty()) {
                    DataModel.Bed bedInfo = dbHelper.getBedInfo(roomNumber.getText().toString());
                    if (bedInfo.bookingId == null) {
                        Snackbar.make(view, "Room has not been Booked yet.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    }
                    DataModel.Tenant tenant = dbHelper.getTenantInfoForBooking(bedInfo.bookingId);
                    if(!tenant.mobile.isEmpty()) {
                        Snackbar.make(view, "Sending DEFAULT SMS to the Tenant: " + tenant.name, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        SMSManagement smsManagement = SMSManagement.getInstance();

                        smsManagement.sendSMS(tenant.mobile,
                                dbHelper.getSMSMessage(bedInfo.bookingId,
                                        tenant,
                                        0,
                                        SMSManagement.SMS_TYPE.DEFAULT)
                        );
                    } else {
                        Snackbar.make(view, "Mobile number is not updated for Tenant: " + tenant.name, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                break;
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
                receivedIntent.putExtra("MODE", 0);//ALL
                receivedIntent.putExtra("TYPE", 0);//ALL
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
            case R.id.load_database_button:
                //TODO: Decide if we need this feature
                Toast.makeText(MainActivity.this, "Loading Database from External Storage", Toast.LENGTH_SHORT).show();
                try {
                    dbHelper.loadDatabaseFromExternalStorage();
                    Toast.makeText(MainActivity.this, "Load Database Complete", Toast.LENGTH_SHORT).show();
                    onResume();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Load Database Failed: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;

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
                DataModel.ReceiptType.RENT));
//        outstandingRentValue.setText(dbHelper.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
//                DataModule.ReceiptType.DEPOSIT));
        outstandingRentValue.setText(String.valueOf(dbHelper.getTotalPendingAmount(DataModel.PendingType.RENT)));
//        totalExpectedRentValue.setText(dbHelper.getTotalExpectedRent());
    }

    private void setPendingAmountEntries() {
        Calendar rightNow = Calendar.getInstance();

        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        int monthUpdated = settings.getInt("pendingEntriesUpdatedForMonth", 0);

        if(monthUpdated == 0 || monthUpdated != (rightNow.get(Calendar.MONTH) + 1)) {
            Log.i(TAG, "Creating Pending Entries for the month of " + rightNow.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

            if(dbHelper.createMonthlyPendingEntries((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED))) {
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
