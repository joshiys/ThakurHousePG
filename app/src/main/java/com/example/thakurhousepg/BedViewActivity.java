package com.example.thakurhousepg;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BedViewActivity extends AppCompatActivity {

    private EditText bedNumber;
    private EditText tenantName;
    private EditText rentAmount;
    private EditText depositAmount;
    private EditText bookingDate;
    private FloatingActionButton smsButton;
    private Button bookButton;

    private DataModule dataModule;
    private boolean viewBookingMode = false;

    private static final String TAG = "BedViewActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Bed View");

        bedNumber = findViewById(R.id.bedview_bed_number);
        bookButton = findViewById(R.id.bedview_button_book);
        tenantName = findViewById(R.id.bedview_tenant_name);
        rentAmount = findViewById(R.id.bedview_rent);
        depositAmount = findViewById(R.id.bedview_deposit);
        bookingDate = findViewById(R.id.bedview_booking_date);
        smsButton = findViewById(R.id.bedview_floating_sms);

        dataModule = DataModule.getInstance();

        Bundle bundle = getIntent().getExtras();
        bedNumber.setText(bundle.getString("BED_NUMBER"));
        bedNumber.setEnabled(false);
        tenantName.setEnabled(false);
        bookingDate.setEnabled(false);
    }

    protected void onResume() {
        super.onResume();

        final DataModule.Bed bedInfo = dataModule.getBedInfo(bedNumber.getText().toString());
        if(bedInfo.isOccupied == true) {
            viewBookingMode = true;
            bookButton.setText("Close Booking");
            bedNumber.setBackgroundColor(Color.BLACK);

            DataModule.Booking booking = dataModule.getBookingInfo(bedInfo.bookingId);
            Log.i(TAG, "Found Booking with Id " + bedInfo.bookingId);

            DataModule.Tenant tenant = dataModule.getTenantInfoForBooking(bedInfo.bookingId);
            tenantName.setVisibility(View.VISIBLE);
            tenantName.setText(tenant.name);
            Log.i(TAG, "Found Tenat with Id " + tenant.id + " Name: " + tenant.name);

            rentAmount.setText(booking.rentAmount);
            depositAmount.setText(booking.depositAmount);
            bookingDate.setText(booking.bookingDate);

            smsButton.show();
            smsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Send SMS to the Tenant", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            depositAmount.addTextChangedListener(amountWatcher);
            rentAmount.addTextChangedListener(amountWatcher);

        } else {
            tenantName.setVisibility(View.INVISIBLE);
            bookingDate.setVisibility(View.GONE);
            smsButton.hide();
            bedNumber.setBackgroundColor(Color.GREEN);

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
                    bookingIntent.putExtra("BED_NUMBER", bedNumber.getText().toString());
                    bookingIntent.putExtra("RENT", rentAmount.getText().toString());
                    bookingIntent.putExtra("DEPOSIT", depositAmount.getText().toString());
                    startActivityForResult(bookingIntent, 0);
                } else if(bookingButton.getText().toString().equals("Update Booking")) {
                    if(dataModule.updateBooking(bedInfo.bookingId, rentAmount.getText().toString(), depositAmount.getText().toString())) {
                        //Update any Pending entries, now that the rent is update?
                    }
                    BedsListContent.refresh();
                    finish();
                } else {
                    Toast.makeText(BedViewActivity.this, "Closing the Booking", Toast.LENGTH_SHORT).show();
                    final int pendingAmount = dataModule.getPendingAmountForBooking(bedInfo.bookingId);
                    if(pendingAmount > 0) {
                        new AlertDialog.Builder(BedViewActivity.this)
                                .setTitle("Dues Pending")
                                .setMessage("Go to Receipt screen before closing the booking?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent receiptIntent = new Intent(BedViewActivity.this, ReceiptActivity.class);
                                        receiptIntent.putExtra("SECTION", "Rent");
                                        receiptIntent.putExtra("ROOM_NUMBER", bedNumber.getText().toString());
                                        receiptIntent.putExtra("AMOUNT", String.valueOf(pendingAmount));

                                        startActivityForResult(receiptIntent, 1);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        closeBooking(bedInfo.bookingId);
                                    }
                                })
                                .show();
                    } else {
                        closeBooking(bedInfo.bookingId);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "Back from the Booking activity");
        if(requestCode == 1) {
            DataModule.Bed bedInfo = dataModule.getBedInfo(bedNumber.getText().toString());
            closeBooking(bedInfo.bookingId);
        }
        finish();
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

    private void closeBooking(String id) {
        Boolean result = dataModule.closeBooking(id, new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString());
        if (result) {
            BedsListContent.refresh();
        }
        finish();
    }
}
