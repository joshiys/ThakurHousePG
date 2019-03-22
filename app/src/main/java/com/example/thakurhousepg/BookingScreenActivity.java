package com.example.thakurhousepg;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

    private Switch wholeRoomSwitch;
    private Button bookButton;

    private static final String TAG = "BookingScreenActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_screen);

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

        wholeRoomSwitch = findViewById(R.id.booking_book_whole_room);

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
                String tenantId = dataModule.addNewTenant(tenantName.getText().toString(), tenantMobile.getText().toString(), "", tenantEmail.getText().toString(), tenantAddress.getText().toString());
                if(tenantId != null) {
                    Boolean result = dataModule.createNewBooking(bedNumber.getText().toString(), tenantId, rentAmount.getText().toString(), depositAmount.getText().toString(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString(), wholeRoomSwitch.isChecked());
                    Log.i(TAG, "result is " + String.valueOf(result));
                    if(result) {
                        Toast.makeText(BookingScreenActivity.this,"Created new Booking successfully", Toast.LENGTH_SHORT).show();
                        BedsListContent.update(bedNumber.getText().toString(), tenantName.getText().toString(), rentAmount.getText().toString());
                        finish();
                    } else {
                        //TODO: Remove the new Tenant if the Booking Creation fails?
                    }
                } else {
                    Log.i(TAG, "Tenant creation failed");
                }
            }
        });

    }
}
