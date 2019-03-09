package com.example.thakurhousepg;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class RentActivity extends AppCompatActivity  {

    Button btnSave;
    EditText roomNumberText, rentOnline, rentInCash;
    TextView pendingRent;
    CheckBox rentInCashCheckBox, rentOnlineCheckBox;
    DatabaseHelper dbHelper;


    boolean confirm = false;

    boolean onlineRentChecked = false, cashRentChecked = false;

    public static final int COLUMN_0 = 0;
    public static final int COLUMN_1 = 1;
    public static final int COLUMN_2 = 2;
    public static final int COLUMN_3 = 3;
    public static final int COLUMN_4 = 4;
    public static final int COLUMN_5 = 5;
    public static final int COLUMN_6 = 6;
    public static final int COLUMN_7 = 7;
    public static final int COLUMN_8 = 8;
    public static final int COLUMN_9 = 9;
    public static final int COLUMN_10 = 10;
    public static final int COLUMN_11 = 11;

    String roomNumberStr = null;

    Cursor data = null;

    String onlineRent = null, cashRent = null, outstandingRent = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        Bundle bundle = getIntent().getExtras();
        roomNumberStr = bundle.getString(getString(R.string.KEY_ROOM_NUMBER));
        String outstandingStr = bundle.getString(getString(R.string.KEY_OUTSTANDING));

        dbHelper = new DatabaseHelper(this);

        roomNumberText = (EditText) findViewById(R.id.roomNumberText);

        rentOnlineCheckBox = (CheckBox) findViewById(R.id.rentOnlineCheckBox);
        rentOnline = (EditText) findViewById(R.id.rentOnlineValue);

        rentInCashCheckBox = (CheckBox) findViewById(R.id.rentInCashCheckBox);
        rentInCash = (EditText) findViewById(R.id.rentInCashValue);

        pendingRent = (TextView) findViewById(R.id.outstandingRentAmount);

        btnSave = (Button) findViewById(R.id.saveRentData);



        //roomNumberText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        roomNumberText.setText(roomNumberStr);
        //Fetch Outstanding from Tenant Table
        pendingRent.setText(outstandingStr);//getRoomOutstanding(roomNumberStr));



//        rentOnline.setText(pendingRent.getText().toString());
//        rentOnline.setSelection(rentOnline.getText().length());
//
//        rentInCash.setText(pendingRent.getText().toString());
//        rentInCash.setSelection(rentInCash.getText().length());


        roomNumberText.setEnabled(false);
        roomNumberText.setFocusable(false);
        rentOnline.setEnabled(false);
//        rentOnline.setFocusable(false);
        rentInCash.setEnabled(false);
//        rentInCash.setFocusable(false);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                data = getTenantRecord(roomNumberStr);
//
//                String onlineRent = null, cashRent = null, outstandingRent = null;

                while (data.moveToNext()) {

                    outstandingRent = data.getString(COLUMN_4);
                    onlineRent = data.getString(COLUMN_5);
                    cashRent = data.getString(COLUMN_6);
                    int currentOnlineRent = 0, currentCashRent = 0;

                    if(onlineRentChecked) {
                        int previousOnlineRent = data.getInt(COLUMN_5);
                        if(rentOnline.getText().toString().isEmpty() == false) {
                            onlineRent = String.valueOf(previousOnlineRent + Integer.valueOf(rentOnline.getText().toString()));
                            currentOnlineRent = Integer.valueOf(rentOnline.getText().toString());
                        }
                    }

                    if(cashRentChecked) {
                        int previousCashRent = data.getInt(COLUMN_6);
                        if(rentInCash.getText().toString().isEmpty() == false) {
                            cashRent = String.valueOf(previousCashRent + Integer.valueOf(rentInCash.getText().toString()));
                            currentCashRent = Integer.valueOf(rentInCash.getText().toString());
                        }
                    }

                    outstandingRent = String.valueOf(Integer.valueOf(outstandingRent) - (currentOnlineRent + currentCashRent));
                    if(Integer.valueOf(outstandingRent) < 0){
                        Toast.makeText(RentActivity.this, "Amount is less than zero.", Toast.LENGTH_SHORT).show();

//                        AlertDialog.Builder builder = new AlertDialog.Builder(RentActivity.this);
//                        builder.setCancelable(true);
//                        builder.setTitle("Confirmation");
//                        builder.setMessage("Amount is less than. Do you want to continue?");
//                        builder.setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        int result = dbHelper.updateTenantRecord(roomNumberStr, data.getString(COLUMN_1), data.getString(COLUMN_2), data.getString(COLUMN_3),
//                                                outstandingRent, onlineRent, cashRent, data.getString(COLUMN_7),
//                                                data.getString(COLUMN_8), data.getString(COLUMN_9), data.getString(COLUMN_10), data.getString(COLUMN_11));
//
//                                        if (result != 0) {
//                                            pendingRent.setText(outstandingRent);
//                                            Toast.makeText(RentActivity.this, "Record Updated Successfully", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(RentActivity.this, "Record Update Failed", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//                        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                Toast.makeText(RentActivity.this, "Confirm false", Toast.LENGTH_LONG).show();
//                            }
//                        });
//
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
                    } else {
                        int result = dbHelper.updateTenantRecord(roomNumberStr, data.getString(COLUMN_1), data.getString(COLUMN_2), data.getString(COLUMN_3),
                                outstandingRent, onlineRent, cashRent, data.getString(COLUMN_7),
                                data.getString(COLUMN_8), data.getString(COLUMN_9), data.getString(COLUMN_10), data.getString(COLUMN_11));

                        if (result != 0) {
                            pendingRent.setText(outstandingRent);
                            Toast.makeText(RentActivity.this, "Record Updated Successfully", Toast.LENGTH_SHORT).show();
                            /* SAHIRE: Send SMS in case of Amount Change */
                        } else {
                            Toast.makeText(RentActivity.this, "Record Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        rentOnlineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){ //checked
                    rentOnline.setEnabled(true);
                    rentOnline.setText(pendingRent.getText().toString());
                    rentOnline.setSelection(rentOnline.getText().length());
//                    rentOnline.setFocusable(true);
                    rentOnline.requestFocus();
                    onlineRentChecked = true;

                }else {
                    rentOnline.setEnabled(false);
//                    rentOnline.setFocusable(false);
                    rentOnline.setText("");
                    rentOnline.setSelection(rentOnline.getText().length());
                    //rentInCash.setText("");
                    onlineRentChecked = false;
                }
            }
        });

        rentInCashCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    rentInCash.setEnabled(true);
                    rentInCash.setText(pendingRent.getText().toString());
//                    rentInCash.setFocusable(true);
                    rentInCash.setSelection(rentInCash.getText().length());
                    rentInCash.requestFocus();
                    cashRentChecked = true;

                }else {
                    rentInCash.setEnabled(false);
                    rentInCash.setText("");
                    rentInCash.setSelection(rentInCash.getText().length());
                    cashRentChecked = false;

//                    rentInCash.setFocusable(false);
                    //rentInCash.setText("");
                }
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    Cursor getTenantRecord(String rNo){
        Cursor data = dbHelper.getTenantTableData(rNo);
        return data;
    }

    void showConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirmation");
        builder.setMessage("Amount is 0. Do you want to contyinue?");
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirm = true;

                    }
                });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirm = false;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
