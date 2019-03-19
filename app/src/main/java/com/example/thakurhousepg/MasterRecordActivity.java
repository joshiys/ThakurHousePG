package com.example.thakurhousepg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MasterRecordActivity extends AppCompatActivity implements View.OnClickListener {

    EditText textRoomNumber, textExpectedRent;
    Button save, delete;
    DatabaseHelper dbHelper;
    private String action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_record);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        Bundle masterData = getIntent().getExtras();
        textRoomNumber = (EditText) findViewById(R.id.tenantRoomNum);
        textExpectedRent = (EditText) findViewById(R.id.textExpectedRent);

        save = (Button) findViewById(R.id.btnSave);
        delete = (Button) findViewById(R.id.btnDelete);

        save.setOnClickListener(this);
        delete.setOnClickListener(this);

        dbHelper = new DatabaseHelper(this);

        if(masterData != null) {
            String roomNumber = masterData.getString("TENANT_ROOM_NUMBER_COLUMN");
            String expectedRent = masterData.getString("EXPECTED_RENT");
            this.action = masterData.getString("ACTION");

            textRoomNumber.setText(roomNumber);

//            if(expectedRent == null)
//            {
//                expectedRent = "0";
//            } else if (expectedRent.isEmpty() == true){
//                expectedRent = "0";
//            }
            textExpectedRent.setText(expectedRent);

            if(action.compareTo("ADD") == 0){
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

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnSave:
                if(this.action.compareTo("ADD") == 0){
                    boolean addresult = dbHelper.insertInMaster(Integer.valueOf(textRoomNumber.getText().toString()).toString(), textExpectedRent.getText().toString());
                    if (addresult == true) {
                        Toast.makeText(MasterRecordActivity.this, "Record Added Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MasterRecordActivity.this, "Record Added Failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int result = dbHelper.updateMasterRecord(textRoomNumber.getText().toString(), textExpectedRent.getText().toString());
                    if (result != 0) {
                        Toast.makeText(MasterRecordActivity.this, "Record Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MasterRecordActivity.this, "Record Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.btnDelete:
                int res = dbHelper.deleteMasterRecord(textRoomNumber.getText().toString());

                if (res != 0) {
                    Toast.makeText(MasterRecordActivity.this, "Record Deleted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MasterRecordActivity.this, "Record Delete Failed", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
    }
}
