package com.sanyog.thakurhousepg;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

//import android.support.design.widget.Snackbar;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.sanyog.thakurhousepg.Constants.*;

public class BedViewActivity extends AppCompatActivity {

    private TextView bedNumberLabel;
    private EditText tenantName;
    private EditText rentAmount;
    private EditText depositAmount;
    private EditText bookingDate;
    private Button bookButton, modifyButton;

    private NetworkDataModule dataModule;
    private boolean viewBookingMode = false;
    private ArrayList<DataModel.Tenant> dependentsList = new ArrayList<DataModel.Tenant>();
    private static final String TAG = "BedViewActivity";
    private DataModel.Tenant tenant;
    private String bedNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Bed View");

        bedNumberLabel = findViewById(R.id.bedview_bed_number);
        bookButton = findViewById(R.id.bedview_button_book);
        modifyButton = findViewById(R.id.bedview_button_modify);
        tenantName = findViewById(R.id.bedview_tenant_name);
        rentAmount = findViewById(R.id.bedview_rent);
        depositAmount = findViewById(R.id.bedview_deposit);
        bookingDate = findViewById(R.id.bedview_booking_date);

        dataModule = NetworkDataModule.getInstance();

        Bundle bundle = getIntent().getExtras();
        bedNumber = bundle.getString("BED_NUMBER");
        bedNumberLabel.setText(bedNumber);
        tenantName.setEnabled(false);
        bookingDate.setEnabled(false);
    }

    protected void onResume() {
        super.onResume();

        final DataModel.Bed bedInfo = dataModule.getBedInfo(bedNumber);
        if(bedInfo.isOccupied) {
            viewBookingMode = true;
            bookButton.setText("Close Booking");
//            bedNumber.setBackgroundColor(Color.BLACK);

            DataModel.Booking booking = dataModule.getBookingInfo(bedInfo.bookingId);
            Log.i(TAG, "Found Booking with Id " + bedInfo.bookingId);

            tenant = dataModule.getTenantInfoForBooking(bedInfo.bookingId);
            tenantName.setVisibility(View.VISIBLE);
            Log.i(TAG, "Found Tenat with Id " + tenant.id + " Name: " + tenant.name);

            // Add Dependents Names
            dependentsList = dataModule.getDependents(tenant.id);

            StringBuilder tempNameHolder = new StringBuilder(tenant.name);
            for (DataModel.Tenant dependent: dependentsList) {
                Log.i(TAG, "Found Dependent with Id " + dependent.id + " Name: " + dependent.name);
                tempNameHolder.append(" , ").append(dependent.name);
                modifyButton.setVisibility(View.VISIBLE);
            }
            modifyButton.setVisibility(View.VISIBLE);
            tenantName.setText(tempNameHolder.toString());

            rentAmount.setText(booking.rentAmount);
            depositAmount.setText(booking.depositAmount);
            bookingDate.setVisibility(View.VISIBLE);
            bookingDate.setText(booking.bookingDate);


            depositAmount.addTextChangedListener(amountWatcher);
            rentAmount.addTextChangedListener(amountWatcher);

        } else {
            tenantName.setVisibility(View.INVISIBLE);
            bookingDate.setVisibility(View.GONE);
//            bedNumber.setBackgroundColor(Color.GREEN);

            rentAmount.setText(bedInfo.rentAmount);
            depositAmount.setText(bedInfo.depositAmount);
        }

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button bookingButton = (Button) v;
                if(bookingButton.getText().toString().equals("Book")) {
                    Toast.makeText(BedViewActivity.this, "Create new Booking", Toast.LENGTH_SHORT).show();
                    Intent bookingIntent = new Intent(BedViewActivity.this, BookingScreenActivity.class);
                    bookingIntent.putExtra("BED_NUMBER", bedNumber);
                    bookingIntent.putExtra("RENT", rentAmount.getText().toString());
                    bookingIntent.putExtra("DEPOSIT", depositAmount.getText().toString());
                    startActivityForResult(bookingIntent, INTENT_REQUEST_CODE_BOOKING);
                } else if(bookingButton.getText().toString().equals("Update Booking")) {
                    dataModule.updateBooking(bedInfo.bookingId, rentAmount.getText().toString(), depositAmount.getText().toString(),
                            new NetworkDataModuleCallback<DataModel.DataModelClass>() {
                                @Override
                                public void onSuccess(DataModel.DataModelClass obj) {
                                    BedsListContent.refresh();
                                    finish();
                                }

                                @Override
                                public void onFailure() {
                                }
                    });
                } else {
                    AlertDialog.Builder bookingActionBuilder = new AlertDialog.Builder(BedViewActivity.this);
                    DialogInterface.OnClickListener itemsChoice = (DialogInterface dialog, int which) -> {
                            switch (which) {
                                case 0:
                                    closeBooking(bedInfo.bookingId, true);
                                    break;
                                case 1:
                                    closeBooking(bedInfo.bookingId, false);
                                    break;
                                case 2:
                                    Intent receiptIntent = new Intent(BedViewActivity.this, ReceiptActivity.class);
                                    receiptIntent.putExtra("SECTION", "Deposit");
                                    receiptIntent.putExtra("ROOM_NUMBER", bedNumber);
                                    receiptIntent.putExtra("RECEIPT_MODE", "DEPOSIT_CLOSE_BOOKING");

                                    startActivityForResult(receiptIntent, INTENT_REQUEST_CODE_RECEIPT);
                                    break;
                            }
                    };

                    if(dataModule.getPendingAmountForBooking(bedInfo.bookingId) > 0) {
                        bookingActionBuilder.setTitle("Dues Pending")
                            .setItems(new CharSequence[]{"Cancel Booking", "Close Booking", "Pay Dues"}, itemsChoice)
                            .create();
                    } else {
                        bookingActionBuilder.setItems(new CharSequence[]{"Cancel Booking", "Close Booking"}, itemsChoice)
                           .create();
                    }

                    bookingActionBuilder.show();
                }
            }
        });

        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(!dependentsList.isEmpty())*/ {
                    Intent selectTenantIntent = new Intent(BedViewActivity.this, SelectTenantActivity.class);
                    selectTenantIntent.putExtra("LIST_MODE", "MODIFY_FULLY_SELECTED_LIST");
                    selectTenantIntent.putExtra("TENANT_LIST", dependentsList);
                    startActivityForResult(selectTenantIntent, INTENT_REQUEST_CODE_SELECT_TENANT);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "Back from the Launched activity");

        if(requestCode == INTENT_REQUEST_CODE_BOOKING && resultCode == RESULT_OK) {
            sendBookingSMS();
        }

        if(requestCode == INTENT_REQUEST_CODE_RECEIPT && resultCode == RESULT_OK) {
            DataModel.Bed bedInfo = dataModule.getBedInfo(bedNumber);
            closeBooking(bedInfo.bookingId, false);
        } else if(requestCode == INTENT_REQUEST_CODE_SELECT_TENANT && resultCode == RESULT_OK) {
            modifyButton.setVisibility(View.INVISIBLE);
            ArrayList<String> selectedTenants = (ArrayList<String>) data.getSerializableExtra("SELECTED_TENANT_IDS");
            for (DataModel.Tenant t : dependentsList) {
                if(!selectedTenants.contains(t.id)) {
                    dataModule.updateTenant(t.id, "", "", "", "", "", false, "0", null);
                    selectedTenants.remove(t.id);
                }
            }
            for (String tid : selectedTenants) {
                dataModule.updateTenant(tid, "", "", "", "", "", true, tenant.id, null);
            }
        }

        depositAmount.removeTextChangedListener(amountWatcher);
        rentAmount.removeTextChangedListener(amountWatcher);
    }

    private TextWatcher amountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(viewBookingMode) {
                bookButton.setText("Update Booking");
            }
        }
    };

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void closeBooking(String id, boolean shouldCancel) {
        Toast.makeText(BedViewActivity.this, "Closing the Booking", Toast.LENGTH_SHORT).show();
        dataModule.closeBooking(id, new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString(), shouldCancel, new NetworkDataModuleCallback<DataModel.DataModelClass>() {
            @Override
            public void onSuccess(DataModel.DataModelClass obj) {
                BedsListContent.refresh();
                finish();
            }

            @Override
            public void onFailure() {
                //TODO: What?
            }
        });
    }

    private void sendBookingSMS() {
        final DataModel.Bed bedInfo = dataModule.getBedInfo(bedNumber);
        DataModel.Tenant tenant = dataModule.getTenantInfoForBooking(bedInfo.bookingId);
        if(!tenant.mobile.isEmpty()) {
            Snackbar.make(bookButton, "Sending BOOKING SMS to the Tenant: " + tenant.name, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            SMSManagement smsManagement = SMSManagement.getInstance();

            smsManagement.sendSMS(tenant.mobile,
                    smsManagement.getSMSMessage(bedInfo.bookingId,
                            tenant,
                            0,
                            SMSManagement.SMS_TYPE.BOOKING)
            );
        } else {
            Snackbar.make(bookButton, "Mobile number is not updated for Tenant: " + tenant.name, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
