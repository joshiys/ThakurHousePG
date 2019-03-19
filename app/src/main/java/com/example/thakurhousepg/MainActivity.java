package com.example.thakurhousepg;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thakurhousepg.NewFiles.DataModule;
import com.example.thakurhousepg.NewFiles.OccupancyPackage.OccupancyAndBooking;
import com.example.thakurhousepg.NewFiles.Payment;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DataModule dbHelper;
    Button rentButton, depositButton, penaltyButton, sendSMS, adminScreen, viewTenant;
    Button btn_receipt, btn_occupancy, btn_payment;
    EditText roomNumber;
    TextView totalOutstandinValue;

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

//        rentButton = (Button) findViewById(R.id.rentButton);
//        depositButton = (Button) findViewById(R.id.depositButton);
//        penaltyButton = (Button) findViewById(R.id.penaltyButton);
         btn_receipt = (Button) findViewById(R.id.receipt_button);
         btn_occupancy = (Button) findViewById(R.id.occupancy_button);
         btn_payment = (Button) findViewById(R.id.payments_button);

        adminScreen = (Button) findViewById(R.id.adminScreen);
        viewTenant = (Button) findViewById(R.id.viewRoomButton);

        roomNumber = (EditText) findViewById(R.id.roomNumberText);
        sendSMS = (Button) findViewById(R.id.sendSMSButton);
        totalOutstandinValue = (TextView) findViewById(R.id.totalOutstandinValue);

        btn_receipt.setOnClickListener(this);
        btn_occupancy.setOnClickListener(this);
        btn_payment.setOnClickListener(this);
        adminScreen.setOnClickListener(this);
        viewTenant.setOnClickListener(this);

        roomNumber.setSelection(roomNumber.getText().length());

        sendSMS.setEnabled(false);
        dbHelper = new DataModule(this);

        setTotalOutstandingRent();
        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setTotalOutstandingRent();
    }

    @Override
    public void onClick(View view) {
        String outstandingRent = "", outstandingDeposit = "", outstandingPenalty = "";
//        if((view.getId() != R.id.adminScreen ) && (view.getId() != R.id.viewRoomButton)) {
//            if (roomNumber.getText().toString().isEmpty() == true) {
//                Toast.makeText(MainActivity.this, "Enter Room Number", Toast.LENGTH_SHORT).show();
//                return;
//            } /*else if ((outstanding = getRoomOutstanding(roomNumber.getText().toString())) == null) {
//                Toast.makeText(MainActivity.this, "No Such Room - " + roomNumber.getText().toString(), Toast.LENGTH_SHORT).show();
//                return;
//            }*/
//        }
        switch(view.getId()){
            case R.id.receipt_button:
//                if ((outstandingRent = getRoomOutstandingRent(roomNumber.getText().toString())) == null) {
//                    Toast.makeText(MainActivity.this, "No Such Room - " + roomNumber.getText().toString(), Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Toast.makeText(MainActivity.this, "Launching Receipts", Toast.LENGTH_SHORT).show();

                Intent rentIntent = new Intent(MainActivity.this, Payment.class);
                rentIntent.putExtra("section", "rent");
//                rentIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
//                rentIntent.putExtra(getString(R.string.rent_amount), outstandingRent);
                startActivity(rentIntent);
                break;
            case R.id.occupancy_button:
                Toast.makeText(MainActivity.this, "Launching Occupancy & Booking", Toast.LENGTH_SHORT).show();
                Intent occupancyIntent = new Intent(MainActivity.this, OccupancyAndBooking.class);
//                adminIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
                startActivity(occupancyIntent);
                break;

            case R.id.payments_button:
                if ((outstandingPenalty = getRoomOutstandingPenalty(roomNumber.getText().toString())) == null) {
                    Toast.makeText(MainActivity.this, "No Such Room - " + roomNumber.getText().toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "Launching Payments", Toast.LENGTH_SHORT).show();

                Intent paymentIntent = new Intent(MainActivity.this, Payment.class);
                paymentIntent.putExtra(getString(R.string.KEY_ROOM_NUMBER), roomNumber.getText().toString());
                paymentIntent.putExtra(getString(R.string.KEY_OUTSTANDING), outstandingPenalty);
                startActivity(paymentIntent);
                break;
            case R.id.viewRoomButton:
                //fetch Tenant recod using Key TENANT_ROOM_NUMBER_COLUMN and pass
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
