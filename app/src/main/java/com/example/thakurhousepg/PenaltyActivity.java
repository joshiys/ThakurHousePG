package com.example.thakurhousepg;

import android.database.Cursor;
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

public class PenaltyActivity extends AppCompatActivity {

    Button btnSave;
    EditText roomNumberText, waiveOffAmount;
    TextView pendingPenalty;
    DatabaseHelper dbHelper;
    CheckBox waiveOffPenalty, payOnline, payCash;
    EditText onlineAmount, cashAmount;

    String roomNumberStr = null;

    boolean onlinePaymentChecked = false, cashPaymentChecked = false, waiveOffPaymentChecked = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penalty);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        dbHelper = new DatabaseHelper(this);
        Bundle bundle = getIntent().getExtras();
        roomNumberStr = bundle.getString(getString(R.string.KEY_ROOM_NUMBER));
        String outstandingStr = bundle.getString(getString(R.string.KEY_OUTSTANDING));

        roomNumberText = (EditText) findViewById(R.id.roomNumberText);
        waiveOffAmount = (EditText) findViewById(R.id.waiveOffPenaltyValue);
        pendingPenalty = (TextView) findViewById(R.id.pendingPenaltyAmount);

        waiveOffPenalty = (CheckBox) findViewById(R.id.waiveOffPenaltyCheckBox);

        payOnline = (CheckBox) findViewById(R.id.payOnlineCheckBox);
        onlineAmount = (EditText) findViewById(R.id.payOnlineValue);

        payCash = (CheckBox) findViewById(R.id.payInCashCheckBox);
        cashAmount = (EditText) findViewById(R.id.payInCashValue);

        btnSave = (Button) findViewById(R.id.savePenaltyData);

        roomNumberText.setText(roomNumberStr);
        if(outstandingStr.isEmpty()){
            pendingPenalty.setText("0");
        } else {
            pendingPenalty.setText(outstandingStr);
        }


        waiveOffAmount.setEnabled(false);
//        waiveOffAmount.setText(pendingPenalty.getText().toString());
//        waiveOffAmount.setSelection(waiveOffAmount.getText().length());


        roomNumberText.setEnabled(false);
        roomNumberText.setFocusable(false);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cursor data = getTenantRecord(roomNumberStr);

                if (waiveOffPaymentChecked = true) {
                    String newPenalty = "", newPenaltyMonth = "";
                    int waiveOffPenalty = 0;
                    int penalty = 0;

                    while (data.moveToNext()) {
                        newPenalty = data.getString(COLUMN_7);
                        newPenaltyMonth = data.getString(COLUMN_8);

                        if (newPenalty.isEmpty() == false) {
                            penalty = Integer.valueOf(newPenalty);
                        }

                        if(waiveOffPaymentChecked == true) {
                            if (waiveOffAmount.getText().toString().isEmpty() == false) {
                                waiveOffPenalty = Integer.valueOf(waiveOffAmount.getText().toString());
                            }
                            penalty = penalty - waiveOffPenalty;
                        } else {
//                            if(onlinePaymentChecked) {
//                                int previousOnlineRent = data.getInt(COLUMN_5);
//                                if(onlineAmount.getText().toString().isEmpty() == false) {
//                                    onlineRent = String.valueOf(previousOnlineRent + Integer.valueOf(rentOnline.getText().toString()));
//                                    currentOnlineRent = Integer.valueOf(rentOnline.getText().toString());
//                                }
//                            }
//
//                            if(cashPaymentChecked) {
//                                int previousCashRent = data.getInt(COLUMN_6);
//                                if(rentInCash.getText().toString().isEmpty() == false) {
//                                    cashRent = String.valueOf(previousCashRent + Integer.valueOf(rentInCash.getText().toString()));
//                                    currentCashRent = Integer.valueOf(rentInCash.getText().toString());
//                                }
//                            }
                        }

                        if (penalty > 0) {
                            newPenalty = String.valueOf(penalty);
                        }
                        if (penalty <= 0) {
                            newPenaltyMonth = "";
                        }

                        if (Integer.valueOf(penalty) < 0) {
                            Toast.makeText(PenaltyActivity.this, "Amount is less than zero.", Toast.LENGTH_SHORT).show();
                        } else {
                            int result = dbHelper.updateTenantRecord(roomNumberStr, data.getString(COLUMN_1), data.getString(COLUMN_2), data.getString(COLUMN_3),
                                    data.getString(COLUMN_4), data.getString(COLUMN_5), data.getString(COLUMN_6), newPenalty,
                                    newPenaltyMonth, data.getString(COLUMN_9), data.getString(COLUMN_10), data.getString(COLUMN_11));

                            if (result != 0) {
                                pendingPenalty.setText(String.valueOf(penalty));
                                Toast.makeText(PenaltyActivity.this, "Record Updated Successfully", Toast.LENGTH_SHORT).show();
                                /* SAHIRE: Send SMS in case of Amount Change */
                            } else {
                                Toast.makeText(PenaltyActivity.this, "Record Update Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
//                    while (data.moveToNext()) {
//                        outstandingRent = data.getString(COLUMN_4);
//                        onlineRent = data.getString(COLUMN_5);
//                        cashRent = data.getString(COLUMN_6);
//                        int currentOnlineRent = 0, currentCashRent = 0;
//
//                        if(onlinePaymentChecked) {
//                            int previousOnlineRent = data.getInt(COLUMN_5);
//                            if(onlineAmount.getText().toString().isEmpty() == false) {
//                                onlineRent = String.valueOf(previousOnlineRent + Integer.valueOf(rentOnline.getText().toString()));
//                                currentOnlineRent = Integer.valueOf(rentOnline.getText().toString());
//                            }
//                        }
//
//                        if(cashPaymentChecked) {
//                            int previousCashRent = data.getInt(COLUMN_6);
//                            if(rentInCash.getText().toString().isEmpty() == false) {
//                                cashRent = String.valueOf(previousCashRent + Integer.valueOf(rentInCash.getText().toString()));
//                                currentCashRent = Integer.valueOf(rentInCash.getText().toString());
//                            }
//                        }
//                        outstandingRent = String.valueOf(Integer.valueOf(outstandingRent) - (currentOnlineRent + currentCashRent));
//                        if(Integer.valueOf(outstandingRent) < 0){
//                            Toast.makeText(PenaltyActivity.this, "Amount is less than zero.", Toast.LENGTH_SHORT).show();
//
//                        } else {
//                            int result = dbHelper.updateTenantRecord(roomNumberStr, data.getString(COLUMN_1), data.getString(COLUMN_2), data.getString(COLUMN_3),
//                                    outstandingRent, onlineRent, cashRent, data.getString(COLUMN_7),
//                                    data.getString(COLUMN_8), data.getString(COLUMN_9), data.getString(COLUMN_10), data.getString(COLUMN_11));
//
//                            if (result != 0) {
//                                pendingRent.setText(outstandingRent);
//                                Toast.makeText(PenaltyActivity.this, "Record Updated Successfully", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(PenaltyActivity.this, "Record Update Failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
                }
            }
        });

        waiveOffPenalty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){ //checked
                    waiveOffAmount.setEnabled(true);
                    waiveOffAmount.setText(pendingPenalty.getText().toString());
                    waiveOffAmount.setSelection(waiveOffAmount.getText().length());
                    waiveOffAmount.requestFocus();
                    waiveOffPaymentChecked = true;


                    payOnline.setEnabled(false);
                    payCash.setEnabled(false);

                    payCash.setChecked(false);
                    payOnline.setChecked(false);

                    onlineAmount.setEnabled(false);
                    onlineAmount.setText("");
                    onlineAmount.setSelection(onlineAmount.getText().length());
                    //rentInCash.setText("");
                    onlinePaymentChecked = false;

                    cashAmount.setEnabled(false);
                    cashAmount.setText("");
                    cashAmount.setSelection(cashAmount.getText().length());
                    cashPaymentChecked = false;

//                    onlineAmount.setText(pendingPenalty.getText().toString());
//                    onlineAmount.setSelection(onlineAmount.getText().length());
//                    onlineAmount.requestFocus();


                }else {
                    waiveOffAmount.setEnabled(false);
//                    rentOnline.setFocusable(false);
                    waiveOffAmount.setText("");
                    waiveOffAmount.setSelection(waiveOffAmount.getText().length());


                    payOnline.setEnabled(true);
                    payCash.setEnabled(true);
                    waiveOffPaymentChecked = true;
                }
            }
        });

        payOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){ //checked
                    onlineAmount.setEnabled(true);
                    onlineAmount.setText(pendingPenalty.getText().toString());
                    onlineAmount.setSelection(onlineAmount.getText().length());
                    onlineAmount.requestFocus();
                    onlinePaymentChecked = true;

                }else {
                    onlineAmount.setEnabled(false);
//                    rentOnline.setFocusable(false);
                    onlineAmount.setText("");
                    onlineAmount.setSelection(onlineAmount.getText().length());
                    //rentInCash.setText("");
                    onlinePaymentChecked = false;
                }
            }
        });

        payCash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    cashAmount.setEnabled(true);
                    cashAmount.setText(pendingPenalty.getText().toString());
//                    rentInCash.setFocusable(true);
                    cashAmount.setSelection(cashAmount.getText().length());
                    cashAmount.requestFocus();
                    cashPaymentChecked = true;

                }else {
                    cashAmount.setEnabled(false);
                    cashAmount.setText("");
                    cashAmount.setSelection(cashAmount.getText().length());
                    cashPaymentChecked = false;

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
}
