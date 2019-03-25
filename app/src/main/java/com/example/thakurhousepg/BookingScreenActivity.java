package com.example.thakurhousepg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BookingScreenActivity extends AppCompatActivity {

    private DataModule dataModule;
    private EditText tenantName;
    private EditText tenantEmail;
    private EditText tenantMobile;
    private EditText tenantAddress;
    private EditText rentAmount;
    private EditText depositAmount;
    private EditText bedNumber;

    private SeekBar roomSplitSeeker;
    private Button bookButton;

    private static final String TAG = "BookingScreenActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_screen);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        dataModule = new DataModule(this);

        tenantName = findViewById(R.id.booking_tenant_name);
        tenantEmail = findViewById(R.id.booking_tenant_email);
        tenantMobile = findViewById(R.id.booking_tenant_mobile);
        tenantAddress = findViewById(R.id.booking_tenant_address);
        rentAmount = findViewById(R.id.booking_deposit);
        depositAmount = findViewById(R.id.booking_rent);
        bedNumber = findViewById(R.id.booking_bed_number);

        rentAmount.setText(bundle.getString("RENT"));
        depositAmount.setText(bundle.getString("DEPOSIT"));

        bedNumber = findViewById(R.id.booking_bed_number);
        bedNumber.setText(bundle.getString("BED_NUMBER"));

        roomSplitSeeker = findViewById(R.id.booking_split_room_seek_bar);

        bookButton = findViewById(R.id.booking_book_button);
        bookButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange (View v, boolean hasFocus) {
                if(hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numRooms = roomSplitSeeker.getProgress();
                if(numRooms > 1) {
                    dataModule.splitRoom(bedNumber.getText().toString(), numRooms, rentAmount.getText().toString(), depositAmount.getText().toString());
                }

                String tenantId = dataModule.addNewTenant(tenantName.getText().toString(), tenantMobile.getText().toString(), "", tenantEmail.getText().toString(), tenantAddress.getText().toString());

                if(tenantId != null) {
                    Boolean result = dataModule.createNewBooking(bedNumber.getText().toString(), tenantId, rentAmount.getText().toString(), depositAmount.getText().toString(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString());
                    Log.i(TAG, "result is " + String.valueOf(result));
                    if(result) {
                        //TODO: Add Deposit and Rent entries to Pending table
                        BedsListContent.refresh(dataModule);
                        new AlertDialog.Builder(BookingScreenActivity.this)
                                .setTitle("Created new Booking successfully")
                                .setMessage("Do you want to pay the deposit for this booking?")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent receiptIntent = new Intent(BookingScreenActivity.this, ReceiptActivity.class);
                                        receiptIntent.putExtra("SECTION", "Deposit");
                                        receiptIntent.putExtra("ROOM_NUMBER", bedNumber.getText().toString());
                                        receiptIntent.putExtra("AMOUNT", depositAmount .getText().toString());

                                        startActivity(receiptIntent);                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    } else {
                        //TODO: Remove the new Tenant if the Booking Creation fails?
                    }
                } else {
                    Toast.makeText(BookingScreenActivity.this, "Can not create a new Tenant", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Tenant creation failed");
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
