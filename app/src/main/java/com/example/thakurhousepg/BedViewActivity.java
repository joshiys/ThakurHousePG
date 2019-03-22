package com.example.thakurhousepg;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private static final String TAG = "BedViewActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bedNumber = findViewById(R.id.bedview_bed_number);
        bookButton = findViewById(R.id.bedview_button_book);
        tenantName = findViewById(R.id.bedview_tenant_name);
        rentAmount = findViewById(R.id.bedview_rent);
        depositAmount = findViewById(R.id.bedview_deposit);
        bookingDate = findViewById(R.id.bedview_booking_date);
        smsButton = findViewById(R.id.bedview_floating_sms);

        dataModule = new DataModule(this);

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

        } else {
            tenantName.setVisibility(View.INVISIBLE);
            bookingDate.setVisibility(View.INVISIBLE);
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
                    bookingIntent.putExtra("RENT", bedInfo.rentAmount);
                    bookingIntent.putExtra("DEPOSIT", bedInfo.rentAmount);
                    startActivity(bookingIntent);
                } else {
                    Toast.makeText(BedViewActivity.this, "Closing the Booking", Toast.LENGTH_SHORT).show();

                    dataModule.closeBooking(bedInfo.bookingId, new SimpleDateFormat("dd/mm/yyyy").format(new Date()).toString(), false, false);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
