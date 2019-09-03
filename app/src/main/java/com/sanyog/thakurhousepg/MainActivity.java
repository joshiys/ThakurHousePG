package com.sanyog.thakurhousepg;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
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


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import static com.sanyog.thakurhousepg.Constants.THAKURHOUSEPG_BASE_URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private SMSManagement smsHandle;
    private TextView receivedRentValue;
    private TextView outstandingRentValue;
    private MaterialButton reloadButton;
    private MaterialCardView outstandingRentCard;
    private MaterialCardView receivedRentCard;
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

//            MaterialButton btn_receipt = findViewById(R.id.receipt_button);
//            MaterialButton btn_occupancy = findViewById(R.id.occupancy_button);
//            MaterialButton btn_payment = findViewById(R.id.payments_button);

            MaterialCardView receiptCard = findViewById(R.id.receiptCard);
            MaterialCardView occupancyCard = findViewById(R.id.occupantsCard);
            MaterialCardView paymentsCard = findViewById(R.id.paymentsCard);

            receivedRentValue = findViewById(R.id.receivedRent);
            outstandingRentValue = findViewById(R.id.outstandingRent);
            outstandingRentCard = findViewById(R.id.outstandingRentCard);
            receivedRentCard = findViewById(R.id.receivedRentCard);
            reloadButton = findViewById(R.id.reload_button);

            headerView = findViewById(R.id.main_monthButton);

            receiptCard.setOnClickListener(this);
            occupancyCard.setOnClickListener(this);
            paymentsCard.setOnClickListener(this);

            outstandingRentCard.setOnClickListener(this);
            receivedRentCard.setOnClickListener(this);
            reloadButton.setOnClickListener(this);

            SMSManagement.setContext(this);


            headerView.setText(Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
            headerView.setOnClickListener(this);

            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

//            confirmServerAddress();
            restService = NetworkDataModule.getInstance(baseURL);
            smsHandle = SMSManagement.getInstance();
            fetch();
        }
    }

    private void confirmServerAddress() {

        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(this);

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
            case R.id.outstandingRentCard:
                Intent pendingIntent = new Intent(MainActivity.this, ViewOutstandingActivity.class);
                startActivity(pendingIntent);
                break;

            case R.id.main_monthButton:
                Intent monthIntent = new Intent(MainActivity.this, MonthlyDataActivity.class);
                startActivity(monthIntent);
                break;

            case R.id.receivedRentCard:
                Intent receivedIntent = new Intent(MainActivity.this, ViewReceiptsActivity.class);
                receivedIntent.putExtra("MODE", 0);//ALL
                receivedIntent.putExtra("TYPE", 0);//ALL
                startActivity(receivedIntent);
                break;

            case R.id.receiptCard:
                Toast.makeText(MainActivity.this, "Launching Receipts", Toast.LENGTH_SHORT).show();

                Intent receiptIntent = new Intent(MainActivity.this, ReceiptActivity.class);
                receiptIntent.putExtra("SECTION", "Rent");
                receiptIntent.putExtra("RENT_AMOUNT", "");
                receiptIntent.putExtra("DEPOSIT_AMOUNT", "");

                startActivity(receiptIntent);
                break;

            case R.id.occupantsCard:
                Toast.makeText(MainActivity.this, "Launching Occupancy & Booking", Toast.LENGTH_SHORT).show();
                Intent occupancyIntent = new Intent(MainActivity.this, OccupancyAndBookingActivity.class);
//                adminIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
                startActivity(occupancyIntent);
                break;

            case R.id.paymentsCard:
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
