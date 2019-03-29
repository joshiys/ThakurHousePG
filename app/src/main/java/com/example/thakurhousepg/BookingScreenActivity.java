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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookingScreenActivity extends AppCompatActivity {

    private DataModule dataModule;
    private EditText rentAmount, depositAmount, bedNumber;

    private SeekBar roomSplitSeeker;

    private Button bookButton, saveButton;

    private ImageButton addTenantButton;
    private String action = null;
    private DataModule.Tenant tenant = null;
    private CheckBox reduceFirstRentCheckbox;
    private EditText firstRent;

    private String tenantId = null;
    private ArrayList<String> tenantNamesList = new ArrayList<String>();
    private ArrayAdapter tenantListAdapter = null;
    private ListView tenantsListView;

    private static final String TAG = "BookingScreenActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_screen);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Booking");

        Bundle bundle = getIntent().getExtras();

        dataModule = new DataModule(this);

        rentAmount = findViewById(R.id.booking_deposit);
        depositAmount = findViewById(R.id.booking_rent);
        bedNumber = findViewById(R.id.booking_bed_number);

        saveButton = findViewById(R.id.saveTenanButton);

        reduceFirstRentCheckbox = findViewById(R.id.booking_first_rent_checkbox);
        firstRent = findViewById(R.id.booking_first_rent);
        addTenantButton = findViewById(R.id.booking_add_tenant);

        tenantsListView = findViewById(R.id.booking_tenant_list);
        tenantListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tenantNamesList);
        tenantsListView.setAdapter(tenantListAdapter);

        rentAmount.setText(bundle.getString("RENT"));
        depositAmount.setText(bundle.getString("DEPOSIT"));

        bedNumber = findViewById(R.id.booking_bed_number);
        bedNumber.setText(bundle.getString("BED_NUMBER"));

        action = bundle.getString("ACTION");

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

                //dataModule.addNewTenant(tenantName.getText().toString(), tenantMobile.getText().toString(), "", tenantEmail.getText().toString(), tenantAddress.getText().toString());

                if(tenantId != null) {
                    String newBookingId = dataModule.createNewBooking(bedNumber.getText().toString(), tenantId, rentAmount.getText().toString(), depositAmount.getText().toString(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString());
                    Log.i(TAG, "result is " + newBookingId);
                    if(!newBookingId.equals("-1")) {
                        //TODO: Add Deposit and Rent entries to Pending table
                        String pendingRent = reduceFirstRentCheckbox.isChecked() ? firstRent.getText().toString():rentAmount.getText().toString();
                        dataModule.createPendingEntryForBooking(newBookingId, 1, pendingRent);
                        dataModule.createPendingEntryForBooking(newBookingId, 2, depositAmount.getText().toString());

                        BedsListContent.refresh();
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

                                        startActivity(receiptIntent);
                                        finish();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .show();
                    } else {
                        Toast.makeText(BookingScreenActivity.this, "Can not create the Booking", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Booking creation failed");
                        //TODO: Remove the new Tenant if the Booking Creation fails?
                    }
                } else {
                    Toast.makeText(BookingScreenActivity.this, "Can not create a new Tenant", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Tenant creation failed");
                }
            }
        });

        reduceFirstRentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    firstRent.setEnabled(true);
                    firstRent.setText(rentAmount.getText());
                } else {
                    firstRent.setEnabled(false);
                    firstRent.setText("0");
                }
            }
        });

        addTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTenantIntent = new Intent(BookingScreenActivity.this, TenantInformationActivity.class);
                addTenantIntent.putExtra("ACTION", "ADD_TENANT");
                startActivityForResult(addTenantIntent, 0);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String newTenanId = data.getStringExtra("TENANT_ID");
            if (tenantId == null) {
                tenantId = newTenanId;
            }
            tenantNamesList.add(dataModule.getTenantInfo(newTenanId).name);
            tenantListAdapter.notifyDataSetChanged();
        }

    }
}
