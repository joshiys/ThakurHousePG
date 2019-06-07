package com.sanyog.thakurhousepg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import static com.sanyog.thakurhousepg.Constants.THAKURHOUSEPG_BASE_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private SMSManagement smsHandle;
    private Button sendSMS;
    private Button receivedRentValue;
    private Button outstandingRentValue;
    private Button btn_receipt;
    private Button btn_occupancy;
    private Button btn_payment;
    private EditText roomNumber;
    private ImageButton reloadButton;
    private TextView headerView;

    private ProgressDialog progress;
    private static final String TAG = "MainActivity";
    private NetworkDataModule restService = null;

    private boolean connectionStatus = false;
    String baseURL = THAKURHOUSEPG_BASE_URL;

    private final int PERMISSION_ALL = 1;
    private final String[] PERMISSIONS = {
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

        if(isNetworkAvailable()) {
//            restService = NetworkDataModule.getInstance();

            btn_receipt = findViewById(R.id.receipt_button);
            btn_occupancy = findViewById(R.id.occupancy_button);
            btn_payment = findViewById(R.id.payments_button);

            roomNumber = findViewById(R.id.roomNumberText);
            sendSMS = findViewById(R.id.sendSMSButton);

            receivedRentValue = findViewById(R.id.receivedRent);
            outstandingRentValue = findViewById(R.id.outstandingRent);
            reloadButton = findViewById(R.id.reload_button);

            headerView = findViewById(R.id.main_monthButton);

            btn_receipt.setOnClickListener(this);
            btn_occupancy.setOnClickListener(this);
            btn_payment.setOnClickListener(this);

            receivedRentValue.setOnClickListener(this);
            outstandingRentValue.setOnClickListener(this);
            reloadButton.setOnClickListener(this);

            roomNumber.setSelection(roomNumber.getText().length());

            SMSManagement.setContext(this);


            headerView.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
            headerView.setOnClickListener(this);

            sendSMS.setOnClickListener(this);

            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

            confirmServerAddress();
        }
    }

    private void confirmServerAddress() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        EditText editText = new EditText(this);
        editText.setText(baseURL);
        editText.setSingleLine();
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
//        alertDialog.setMessage("Enter URL");
        alertDialog.setTitle("Confirm Server URL");

        alertDialog.setView(editText);

        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int whichButton) {
                if(!editText.getText().toString().isEmpty()) {
                    baseURL = editText.getText().toString();
                }
                restService = NetworkDataModule.getInstance(baseURL);
                smsHandle = SMSManagement.getInstance();
                reloadButton.setEnabled(false);
                fetch();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
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

        if (restService != null && !progress.isShowing()) {
            setTotalOutstandingRent();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.sendSMSButton:
                if(!roomNumber.getText().toString().isEmpty()) {
                    DataModel.Bed bedInfo = restService.getBedInfo(roomNumber.getText().toString());
                    if (bedInfo.bookingId == null) {
                        Snackbar.make(view, "Room has not been Booked yet.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        break;
                    }
                    DataModel.Tenant tenant = restService.getTenantInfoForBooking(bedInfo.bookingId);
                    if(!tenant.mobile.isEmpty()) {
                        Snackbar.make(view, "Sending DEFAULT SMS to the Tenant: " + tenant.name, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        SMSManagement smsManagement = SMSManagement.getInstance();

                        smsManagement.sendSMS(tenant.mobile,
                                smsManagement.getSMSMessage(bedInfo.bookingId,
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
            case R.id.reload_button:
                /*if(connectionStatus == false) {
                 confirmServerAddress();
                } else*/ {
                    fetch();
                    reloadButton.setEnabled(false);
                }
                break;
        }
    }

    private void fetch() {
        progress.show();

        NetworkDataModuleCallback initialDataFetchCompletionCallBack = new NetworkDataModuleCallback() {
            @Override
            public void onSuccess(Object obj) {
                progress.dismiss();
                connectionStatus = true;
                setPendingAmountEntries();
                setTotalOutstandingRent();
                reloadButton.setEnabled(true);
            }

            @Override
            public void onFailure() {
                progress.dismiss();
                connectionStatus = false;
                reloadButton.setEnabled(true);
                Toast toast = Toast.makeText(MainActivity.this, "Call To Network Service Failed", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
            }
        };

        restService.loadData(initialDataFetchCompletionCallBack);
    }

    private void setTotalOutstandingRent() {
        receivedRentValue.setText(restService.getTotalReceivedAmountForMonth(Calendar.getInstance().get(Calendar.MONTH) + 1,
                DataModel.ReceiptType.RENT));
        outstandingRentValue.setText(String.valueOf(restService.getTotalPendingAmount(DataModel.PendingType.RENT)));
    }

    private void setPendingAmountEntries() {
        Calendar rightNow = Calendar.getInstance();
        int monthUpdated = restService.getPendingEntriesUpdatedForMonth();

        if(monthUpdated == 0 || monthUpdated != (rightNow.get(Calendar.MONTH) + 1)) {
            Log.i(TAG, "Creating Pending Entries for the month of " + rightNow.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
            if(!restService.getCurrentBookings().isEmpty()) {
                restService.createMonthlyPendingEntries((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED),
                        new NetworkDataModuleCallback<DataModel.Pending>() {
                            @Override
                            public void onSuccess(DataModel.Pending obj) {
                                restService.setPendingEntriesUpdatedForMonth(rightNow.get(Calendar.MONTH) + 1);
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(MainActivity.this, "Could not create Monthly Pending Entries", Toast.LENGTH_LONG).show();
                            }
                        }
                );
            } else {
                restService.setPendingEntriesUpdatedForMonth(rightNow.get(Calendar.MONTH) + 1);
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
