package com.example.thakurhousepg.OldFiles;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thakurhousepg.R;

import java.util.Calendar;
import java.util.Locale;

public class TenantRecordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText textRoomNumber, textName, textMobile, textDate, textRent;
    EditText textOnlineRent, textCashRent, textPenalty, textPenaltyMonth;
    EditText textDecidedDeposit, textOnlineDeposit, textCashDeposit;
    Button save, delete;
    DatabaseHelper dbHelper;
    private String action;
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
        setContentView(R.layout.activity_tenant_record);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        Bundle tenantData = getIntent().getExtras();
        textRoomNumber = (EditText) findViewById(R.id.tenantRoomNum);
        textName = (EditText) findViewById(R.id.tenantName);
        textMobile = (EditText) findViewById(R.id.tenantMobile);
        textDate = (EditText) findViewById(R.id.tenantAdmDate);
        textRent = (EditText) findViewById(R.id.tenantRentAmout);

        textOnlineRent = (EditText) findViewById(R.id.tenantOnlineRent);
        textCashRent = (EditText) findViewById(R.id.tenantCashRent);
        textPenalty = (EditText) findViewById(R.id.tenantPenalty);
        textPenaltyMonth = (EditText) findViewById(R.id.tenantPenaltyMonth);
        textDecidedDeposit = (EditText) findViewById(R.id.tenantDecidedDeposit);
        textOnlineDeposit = (EditText) findViewById(R.id.tenantOnlineDeposit);
        textCashDeposit = (EditText) findViewById(R.id.tenantCashDeposit);

        save = (Button) findViewById(R.id.btnSave);
//        delete = (Button) findViewById(R.id.btnDelete);

        save.setOnClickListener(this);
//        delete.setOnClickListener(this);
//        delete.setEnabled(false);

//        textRoomNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(b == false){
//                    if(textRoomNumber.getText().toString().isEmpty() == false){
//                        textOutstanding.setText(getTenantOutstanding(textRoomNumber.getText().toString()));
//                    }
//
//                }
//
//            }
//        });


        dbHelper = new DatabaseHelper(this);

        if(tenantData != null) {
            String roomNumber = tenantData.getString("TENANT_ROOM_NUMBER");
//            String name = tenantData.getString("TENANT_NAME");
//            String mobile = tenantData.getString("TENANT_MOBILE");
//            String date = tenantData.getString("TENANT_ADM_DATE");
//            String outstanding = tenantData.getString("TENANT_OUTSTANDING");
            this.action = tenantData.getString("ACTION");

            if(action.compareTo("ADD")  != 0) { //VIEW ACTION
                Cursor data = getTenantRecord(roomNumber);


                while (data.moveToNext()) {
                    int outstanding = 0;
                    textRoomNumber.setText(data.getString(COLUMN_0));
                    textName.setText(data.getString(COLUMN_1));
                    textMobile.setText(data.getString(COLUMN_2));
                    textDate.setText(data.getString(COLUMN_3));

                    textRent.setText(data.getString(COLUMN_4));
                    textOnlineRent.setText(data.getString(COLUMN_5));
                    textCashRent.setText(data.getString(COLUMN_6));
                    textPenalty.setText(data.getString(COLUMN_7));
                    textPenaltyMonth.setText(data.getString(COLUMN_8));
                    textDecidedDeposit.setText(data.getString(COLUMN_9));
                    textOnlineDeposit.setText(data.getString(COLUMN_10));
                    textCashDeposit.setText(data.getString(COLUMN_11));
                }
            }else if(action.compareTo("ADD") == 0){
                textRoomNumber.setEnabled(true);
            }
        } else {
            delete.setEnabled(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    String getTenantOutstanding(String rNo){
//        dbHelper = new DatabaseHelper(this);
        Cursor data = dbHelper.getTenantTableData(rNo);


        if(data.moveToNext()) {
//            data.getString(COLUMN_0));
            return data.getString(COLUMN_1);
        }
        return "0";
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnSave:
                if(this.action.compareTo("ADD") == 0){
//                    textOutstanding.setText(getTenantOutstanding(textRoomNumber.getText().toString()));

                    boolean addresult = dbHelper.insertInTenant(Integer.valueOf(textRoomNumber.getText().toString()).toString(), textName.getText().toString(), textMobile.getText().toString(), textDate.getText().toString(), textRent.getText().toString(),
                            textOnlineRent.getText().toString(), textCashRent.getText().toString(), textPenalty.getText().toString(), textPenaltyMonth.getText().toString(),
                            textDecidedDeposit.getText().toString(), textOnlineDeposit.getText().toString(), textCashDeposit.getText().toString());
                        if (addresult == true) {
                        Toast.makeText(TenantRecordActivity.this, "Record Added Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TenantRecordActivity.this, "Record Added Failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int result = dbHelper.updateTenantRecord(textRoomNumber.getText().toString(), textName.getText().toString(), textMobile.getText().toString(), textDate.getText().toString(), textRent.getText().toString(),
                            textOnlineRent.getText().toString(), textCashRent.getText().toString(), textPenalty.getText().toString(), textPenaltyMonth.getText().toString(),
                            textDecidedDeposit.getText().toString(), textOnlineDeposit.getText().toString(), textCashDeposit.getText().toString());
                    if (result != 0) {
                        Toast.makeText(TenantRecordActivity.this, "Record Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TenantRecordActivity.this, "Record Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.btnDelete:
                int res = dbHelper.deleteTenantRecord(textRoomNumber.getText().toString());

                if (res != 0) {
                    Toast.makeText(TenantRecordActivity.this, "Record Deleted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TenantRecordActivity.this, "Record Delete Failed", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
    }

    Cursor getTenantRecord(String rNo){
        Cursor data = dbHelper.getTenantTableData(rNo);
        return data;
    }
}
