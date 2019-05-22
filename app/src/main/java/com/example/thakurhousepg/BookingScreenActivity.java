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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
import static com.example.thakurhousepg.Constants.*;

public class BookingScreenActivity extends AppCompatActivity {

    private NetworkDataModule dataModule;
    private NetworkDataModule restService;
    private EditText rentAmount, depositAmount, bedNumber, bookingDate;

    private SeekBar roomSplitSeeker;

    private Button bookButton;

    private ImageButton addTenantButton, selectTenantButton;
    private String action = null;
    private DataModel.Tenant tenant = null;
    private CheckBox reduceFirstRentCheckbox;
    private EditText firstRent;

    private String tenantId = null;
    private ArrayList<String> tenantNamesList = new ArrayList<String>();
    ArrayList<String> dependentsIdList = new ArrayList<String>();
    private ArrayAdapter tenantListAdapter = null;
    private ListView tenantsListView;

    private static final String TAG = BookingScreenActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_screen);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Booking");

        Bundle bundle = getIntent().getExtras();

        dataModule = NetworkDataModule.getInstance();

        rentAmount = findViewById(R.id.booking_rent);
        depositAmount = findViewById(R.id.booking_deposit);
        bedNumber = findViewById(R.id.booking_bed_number);
        bookingDate = findViewById(R.id.bookingDate);

//        saveButton = findViewById(R.id.saveTenanButton);

        reduceFirstRentCheckbox = findViewById(R.id.booking_first_rent_checkbox);
        firstRent = findViewById(R.id.booking_first_rent);
        addTenantButton = findViewById(R.id.booking_add_tenant);
        selectTenantButton = findViewById(R.id.booking_select_tenant);

        tenantsListView = findViewById(R.id.booking_tenant_list);
        tenantListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, tenantNamesList);
        tenantsListView.setAdapter(tenantListAdapter);
        tenantsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        rentAmount.setText(bundle.getString("RENT"));
        depositAmount.setText(bundle.getString("DEPOSIT"));

        bedNumber = findViewById(R.id.booking_bed_number);
        bedNumber.setText(bundle.getString("BED_NUMBER"));

        action = bundle.getString("ACTION");

        roomSplitSeeker = findViewById(R.id.booking_split_room_seek_bar);

        bookingDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString());

        bookButton = findViewById(R.id.booking_book_button);
        bookButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange (View v, boolean hasFocus) {
                if(hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        this.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    int numRooms = roomSplitSeeker.getProgress();
                    //TODO: Sanity check, If room is split makes sure the new room is booked with only 1 tenant
                    // Also check the room number for CreateNewBooking is correct after the split
                    if(numRooms > 1) {
                        dataModule.splitRoom(bedNumber.getText().toString(), numRooms, rentAmount.getText().toString(), depositAmount.getText().toString());
                    }

                    int mainTenant = tenantsListView.getCheckedItemPosition();
                    if (mainTenant > 0) {
                        String tempId = tenantId;
                        tenantId = dependentsIdList.get(mainTenant - 1);
                        dependentsIdList.set(mainTenant - 1, tempId);
                    }

                    dataModule.createNewBooking(bedNumber.getText().toString(), tenantId, rentAmount.getText().toString(), depositAmount.getText().toString(), bookingDate.getText().toString(),
                            new NetworkDataModuleCallback<DataModel.Booking>() {
                        @Override
                        public void onSuccess(DataModel.Booking obj) {
                            String newBookingId = obj.id;
                            Log.i(TAG, "result is " + newBookingId);
                            String pendingRent = reduceFirstRentCheckbox.isChecked() ? firstRent.getText().toString():rentAmount.getText().toString();

                            //XXX : Do not create pending entry if Booking date is advance because penalty will be added automatically if month changes
                            try {
                                // काय बकवास API design केली आहे ... All of this just to get the month out of a date?
                                // अजुन काहीतरी चांगला मार्ग असणार
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.setTime(dateFormat.parse(bookingDate.getText().toString()));
                                Log.i(TAG, "Booking Month: " + c.get(Calendar.MONTH) + " , And currentMonth: " + Calendar.getInstance().get(Calendar.MONTH));
                                if(c.get(Calendar.MONTH) != (Calendar.getInstance().get(Calendar.MONTH) + 1)) {
                                    dataModule.createPendingEntryForBooking(newBookingId, DataModel.PendingType.RENT, pendingRent, Calendar.getInstance().get(Calendar.MONTH) + 1, null);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            dataModule.createPendingEntryForBooking(newBookingId, DataModel.PendingType.DEPOSIT, depositAmount.getText().toString(), Calendar.getInstance().get(Calendar.MONTH) + 1, null);

                            for(String id: dependentsIdList) {
                                dataModule.updateTenant(id, "", "", "", "", "", true, tenantId, new NetworkDataModuleCallback<DataModel.Tenant>() {
                                    @Override
                                    public void onSuccess(DataModel.Tenant obj) {

                                    }

                                    @Override
                                    public void onFailure() {

                                    }
                                    @Override
                                    public void onResult() {
                                        BedsListContent.refresh();
                                    }
                                });
                            }

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
                                            receiptIntent.putExtra("DEPOSIT_AMOUNT", depositAmount .getText().toString());

                                            startActivity(receiptIntent);
                                            finish();
                                        }
                                    })

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .setNegativeButton(R.string.negative_button_no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(BookingScreenActivity.this, "Can not create the Booking", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "Booking creation failed");
                            //TODO: Remove the new Tenant if the Booking Creation fails?
                        }
                    });
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
                    firstRent.setText("");
                }
            }
        });

        addTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTenantIntent = new Intent(BookingScreenActivity.this, TenantInformationActivity.class);
                addTenantIntent.putExtra("ACTION", "ADD_TENANT");
                startActivityForResult(addTenantIntent, INTENT_REQUEST_CODE_ADD_TENANT);
            }
        });

        selectTenantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectTenantIntent = new Intent(BookingScreenActivity.this, SelectTenantActivity.class);
                if (tenantId != null) {
                    selectTenantIntent.putExtra("LIST_MODE", "MODIFY_PARTIALLY_SELECTED_LIST");
                    // When the Mode is "MODIFY_PARTIALLY_SELECTED_LIST" we send only the Id's.
                    // But Main tenant Id is not in the dependentsIdList. So we add it first.
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(tenantId);
                    temp.addAll(dependentsIdList);
                    selectTenantIntent.putExtra("TENANT_LIST", temp);
                } else {
                    selectTenantIntent.putExtra("LIST_MODE", "SELECT_FROM_BLANK_LIST");
                }
                startActivityForResult(selectTenantIntent, INTENT_REQUEST_CODE_SELECT_TENANT);
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
            if (requestCode == INTENT_REQUEST_CODE_ADD_TENANT) {
                String newTenanId = data.getStringExtra("TENANT_ID");
                if (tenantId == null) {
                    tenantId = newTenanId;
                } else {
                    dependentsIdList.add(newTenanId);
                }
                tenantNamesList.add(dataModule.getTenantInfo(newTenanId).name);
                tenantListAdapter.notifyDataSetChanged();
            }
            else {
                ArrayList<String> selectedTenants = (ArrayList<String>) data.getSerializableExtra("SELECTED_TENANT_IDS");
                if (selectedTenants.size() > 0) {
                    tenantId = selectedTenants.get(0);
                    tenantNamesList.clear();
                    dependentsIdList.clear();
                    for (String id : selectedTenants) {
                        DataModel.Tenant newTenant = dataModule.getTenantInfo(id);
                        if(newTenant.isCurrent) {
                            new AlertDialog.Builder(BookingScreenActivity.this)
                                    .setTitle("Tenat Already has a booking")
                                    .setMessage("Tenant, " + newTenant.name + ", already had a booking. Please make sure that booking is closed, otherwise this booking operation will not succeed")

                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // DO Nothing for now
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                        if(!id.equals(tenantId)) {
                            dependentsIdList.add(id);
                        }
                        tenantNamesList.add(newTenant.name);
                    }
                    tenantListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private boolean validate() {
        boolean validationSuccessful = true;

        if (tenantId != null) {
            DataModel.Tenant tenant = dataModule.getTenantInfo(tenantId);
            if(tenant .isCurrent || !tenant.parentId.equals("0")) {
                // The main tenant already has a booking
                validationSuccessful = false;
                Toast.makeText(BookingScreenActivity.this, "The main tenant, " + tenant.name + ", already has a booking", Toast.LENGTH_SHORT).show();
            }
            for(String id: dependentsIdList) {
                tenant = dataModule.getTenantInfo(id);
                if(tenant.isCurrent || !tenant.parentId.equals("0")) {
                    // Pne of the dependents already has a booking
                    validationSuccessful = false;
                    Toast.makeText(BookingScreenActivity.this, "The dependent, " + tenant.name + ", already has a booking", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        } else {
            validationSuccessful = false;
        }

        return validationSuccessful;
    }
}
