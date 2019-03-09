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

public class DepositActivity extends AppCompatActivity {

    Button btnSave;
    EditText roomNumberText, depositOnline, depositInCash;
    TextView pendingDeposit;
    CheckBox depositInCashCheckBox, depositOnlineCheckBox;
    DatabaseHelper dbHelper;
    String roomNumberStr;

    boolean onlineDepositChecked = false, cashDepositChecked = false;

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
        setContentView(R.layout.activity_deposit);

        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        roomNumberStr = bundle.getString(getString(R.string.KEY_ROOM_NUMBER));
        String outstandingStr = bundle.getString(getString(R.string.KEY_OUTSTANDING));

        dbHelper = new DatabaseHelper(this);

        roomNumberText = (EditText) findViewById(R.id.roomNumberText);

        depositOnlineCheckBox = (CheckBox) findViewById(R.id.depositOnlineCheckBox);
        depositOnline = (EditText) findViewById(R.id.depositOnlineValue);

        depositInCashCheckBox = (CheckBox) findViewById(R.id.depositInCashCheckBox);
        depositInCash = (EditText) findViewById(R.id.depositInCashValue);

        pendingDeposit = (TextView) findViewById(R.id.outstandingDepositAmount);

        btnSave = (Button) findViewById(R.id.saveDepositData);


        //roomNumberText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        roomNumberText.setText(roomNumberStr);
        pendingDeposit.setText(outstandingStr);//getRoomOutstanding(roomNumberStr));


        depositOnline.setText(pendingDeposit.getText().toString());
        depositOnline.setSelection(depositOnline.getText().length());

        depositInCash.setText(pendingDeposit.getText().toString());
        depositInCash.setSelection(depositInCash.getText().length());


        roomNumberText.setEnabled(false);
        roomNumberText.setFocusable(false);
        depositOnline.setEnabled(false);
//        depositOnline.setFocusable(false);
        depositInCash.setEnabled(false);
//        depositInCash.setFocusable(false);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor data = getTenantRecord(roomNumberStr);

                String onlineDeposit = null, cashDeposit = null, outstandingDeposit = null;

                while (data.moveToNext()) {

                    outstandingDeposit = data.getString(COLUMN_9);
                    onlineDeposit = data.getString(COLUMN_10);
                    cashDeposit = data.getString(COLUMN_11);
                    int currentOnlineDeposit = 0, currentCashDeposit = 0;

                    if(onlineDepositChecked) {
                        int previousOnlineDeposit = data.getInt(COLUMN_10);
                        if(depositOnline.getText().toString().isEmpty() == false) {
                            onlineDeposit = String.valueOf(previousOnlineDeposit + Integer.valueOf(depositOnline.getText().toString()));
                            currentOnlineDeposit = Integer.valueOf(depositOnline.getText().toString());
                        }
                    }

                    if(cashDepositChecked) {
                        int previousCashRent = data.getInt(COLUMN_11);
                        if(depositInCash.getText().toString().isEmpty() == false) {
                            cashDeposit = String.valueOf(previousCashRent + Integer.valueOf(depositInCash.getText().toString()));
                            currentCashDeposit = Integer.valueOf(depositInCash.getText().toString());
                        }
                    }

                    outstandingDeposit = String.valueOf(Integer.valueOf(outstandingDeposit) - (currentOnlineDeposit + currentCashDeposit));
                    if(Integer.valueOf(outstandingDeposit) < 0) {
                        Toast.makeText(DepositActivity.this, "Amount is less than zero.", Toast.LENGTH_SHORT).show();
                    } else {
                        int result = dbHelper.updateTenantRecord(roomNumberStr, data.getString(COLUMN_1), data.getString(COLUMN_2), data.getString(COLUMN_3),
                                data.getString(COLUMN_4), data.getString(COLUMN_5), data.getString(COLUMN_6), data.getString(COLUMN_7),
                                data.getString(COLUMN_8), outstandingDeposit, onlineDeposit, cashDeposit);

                        if (result != 0) {
                            pendingDeposit.setText(outstandingDeposit);
                            Toast.makeText(DepositActivity.this, "Record Updated Successfully", Toast.LENGTH_SHORT).show();
                            /* SAHIRE: Send SMS in case of Amount Change */
                        } else {
                            Toast.makeText(DepositActivity.this, "Record Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        depositOnlineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    depositOnline.setEnabled(true);
                    depositOnline.setText(pendingDeposit.getText().toString());
                    depositOnline.setSelection(depositOnline.getText().length());
                    depositOnline.requestFocus();
                    onlineDepositChecked = true;
                }else {
                    depositOnline.setEnabled(false);
                    depositOnline.setText("");
                    depositOnline.setSelection(depositOnline.getText().length());
                    onlineDepositChecked = false;
                }
            }
        });

        depositInCashCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    depositInCash.setEnabled(true);
                    depositInCash.setText(pendingDeposit.getText().toString());
                    depositInCash.setSelection(depositInCash.getText().length());
                    depositInCash.requestFocus();
                    cashDepositChecked = true;
                }else {
                    depositInCash.setEnabled(false);
                    cashDepositChecked = false;
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
