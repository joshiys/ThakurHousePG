package com.example.thakurhousepg;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DatabaseHelper dbHelper;
    Button rentButton, depositButton, penaltyButton, sendSMS, adminScreen, viewTenant;
    EditText roomNumber;
    TextView totalOutstandinValue, totalOutstandingLabel;

    public static final int COLUMN_0 = 0;
    public static final int COLUMN_1 = 1;
    public static final int COLUMN_2 = 2;
    public static final int COLUMN_3 = 3;
    public static final int COLUMN_4 = 4;
    public static final int COLUMN_7 = 7;
    public static final int COLUMN_9 = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rentButton = (Button) findViewById(R.id.rentButton);
        depositButton = (Button) findViewById(R.id.depositButton);
        penaltyButton = (Button) findViewById(R.id.penaltyButton);
        adminScreen = (Button) findViewById(R.id.adminScreen);
        viewTenant = (Button) findViewById(R.id.viewRoomButton);

        roomNumber = (EditText) findViewById(R.id.roomNumberText);
        sendSMS = (Button) findViewById(R.id.sendSMSButton);
        totalOutstandinValue = (TextView) findViewById(R.id.totalOutstandinValue);
        totalOutstandingLabel = (TextView) findViewById(R.id.totalOutstandingLabel);

        rentButton.setOnClickListener(this);
        depositButton.setOnClickListener(this);
        penaltyButton.setOnClickListener(this);
        adminScreen.setOnClickListener(this);
        viewTenant.setOnClickListener(this);

        roomNumber.setSelection(roomNumber.getText().length());

        sendSMS.setEnabled(false);
        dbHelper = new DatabaseHelper(this);

        setTotalOutstandingRent();
        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTotalOutstandingRent();
    }

    @Override
    public void onClick(View view) {
        String outstandingRent = "", outstandingDeposit = "", outstandingPenalty = "";
        if((view.getId() != R.id.adminScreen ) && (view.getId() != R.id.viewRoomButton)) {
            if (roomNumber.getText().toString().isEmpty() == true) {
                Toast.makeText(MainActivity.this, "Enter Room Number", Toast.LENGTH_SHORT).show();
                return;
            } /*else if ((outstanding = getRoomOutstanding(roomNumber.getText().toString())) == null) {
                Toast.makeText(MainActivity.this, "No Such Room - " + roomNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                return;
            }*/
        }
        switch(view.getId()){
            case R.id.rentButton:
                if ((outstandingRent = getRoomOutstandingRent(roomNumber.getText().toString())) == null) {
                    Toast.makeText(MainActivity.this, "No Such Room - " + roomNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "Launching Rent Activity", Toast.LENGTH_SHORT).show();
                Intent rentIntent = new Intent(MainActivity.this, RentActivity.class);
                rentIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
                rentIntent.putExtra(getString(R.string.KEY_OUTSTANDING), outstandingRent);
                startActivity(rentIntent);
                break;
            case R.id.depositButton:
                if ((outstandingDeposit = getRoomOutstandingDeposit(roomNumber.getText().toString())) == null) {
                    Toast.makeText(MainActivity.this, "No Such Room - " + roomNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "Launching Deposit Activity", Toast.LENGTH_SHORT).show();
                Intent depositIntent = new Intent(MainActivity.this, DepositActivity.class);
                depositIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
                depositIntent.putExtra(getString(R.string.KEY_OUTSTANDING), outstandingDeposit);
                startActivity(depositIntent);

                break;
            case R.id.penaltyButton:
                if ((outstandingPenalty = getRoomOutstandingPenalty(roomNumber.getText().toString())) == null) {
                    Toast.makeText(MainActivity.this, "No Such Room - " + roomNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "Launching Penalty Activity", Toast.LENGTH_SHORT).show();
                Intent penaltyIntent = new Intent(MainActivity.this, PenaltyActivity.class);
                penaltyIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
                penaltyIntent.putExtra(getString(R.string.KEY_OUTSTANDING), outstandingPenalty);
                startActivity(penaltyIntent);
                break;
            case R.id.adminScreen:
                Toast.makeText(MainActivity.this, "Launching Admin Activity", Toast.LENGTH_SHORT).show();
                Intent adminIntent = new Intent(MainActivity.this, AdminActivity.class);
                adminIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
                startActivity(adminIntent);
                break;
            case R.id.viewRoomButton:
                //fetch Tenant recod using Key ROOM_NUMBER and pass
//                dbHelper = new DatabaseHelper(this);
                if(roomNumber.getText().toString().isEmpty() == false) {
                    Cursor data = dbHelper.getTenantTableData(roomNumber.getText().toString());

                    if (data.moveToNext()) {
                        Intent tenantData = new Intent(MainActivity.this, TenantRecordActivity.class);
                        tenantData.putExtra("TENANT_ROOM_NUMBER", data.getString(COLUMN_0));
                        tenantData.putExtra("TENANT_NAME", data.getString(COLUMN_1));
                        tenantData.putExtra("TENANT_MOBILE", data.getString(COLUMN_2));
                        tenantData.putExtra("TENANT_ADM_DATE", data.getString(COLUMN_3));
                        tenantData.putExtra("TENANT_OUTSTANDING", data.getString(COLUMN_4));
                        tenantData.putExtra("ACTION", "VIEW_ONLY");
                        startActivity(tenantData);
                    } else {
                        Toast.makeText(MainActivity.this, "No Record Found with Room Number: " + roomNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Enter Room Number", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    String getRoomOutstandingRent(String rNo){
        Cursor data = dbHelper.getTenantTableData(rNo);

        if(data.moveToNext()) {
            return data.getString(COLUMN_4);
        }
        return null;
    }
    String getRoomOutstandingDeposit(String rNo){
        Cursor data = dbHelper.getTenantTableData(rNo);

        if(data.moveToNext()) {
            return data.getString(COLUMN_9);
        }
        return null;
    }
    String getRoomOutstandingPenalty(String rNo){
        Cursor data = dbHelper.getTenantTableData(rNo);

        if(data.moveToNext()) {
            return data.getString(COLUMN_7);
        }
        return null;
    }

    void setTotalOutstandingRent(){
        Cursor data = dbHelper.getTenantTableData("KEY_ALL");
        Calendar c = Calendar.getInstance();
        int totalRent = 0;
        while(data.moveToNext()) {
            totalRent = totalRent + data.getInt(COLUMN_4);
        }
//        totalOutstandingLabel.setText(c.getDisplayName(c.MONTH, c.LONG, Locale.US) + " Outstanding: ");
        totalOutstandinValue.setText(String.valueOf(totalRent));
    }
}
