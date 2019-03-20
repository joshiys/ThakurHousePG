package com.example.thakurhousepg.OldFiles;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.thakurhousepg.R;

import java.util.Calendar;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener{

    Button masterDB, tenantDataButton;
    TextView totalExpectedRentValue, totalcollectedRentValue;
    DatabaseHelper dbHelper;

    public static final int COLUMN_0 = 0;
    public static final int COLUMN_1 = 1;
    public static final int COLUMN_5 = 5;
    public static final int COLUMN_6 = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new DatabaseHelper(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        masterDB = (Button) findViewById(R.id.masterDBButton);
        tenantDataButton = (Button) findViewById(R.id.updateRoom);

        totalExpectedRentValue = (TextView) findViewById(R.id.totalExpectedRentValue);
        totalcollectedRentValue = (TextView) findViewById(R.id.totalcollectedRentValue);

        masterDB.setOnClickListener(this);
        tenantDataButton.setOnClickListener(this);

        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        setTotalExpectedRent();
        setTotalCollectedRent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTotalExpectedRent();
        setTotalCollectedRent();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.masterDBButton:
                Intent masterIntent = new Intent(AdminActivity.this, MasterDBActivity.class);
                startActivity(masterIntent);
                break;
            case R.id.updateRoom:
                Intent tenantIntent = new Intent(AdminActivity.this, TenantDataActivity.class);
                startActivity(tenantIntent);
                break;
        }

    }


    void setTotalExpectedRent(){
        Cursor data = dbHelper.getMasterTableData("KEY_ALL");
        Calendar c = Calendar.getInstance();
        int totalExpectedRent = 0;
        while(data.moveToNext()) {
            totalExpectedRent = totalExpectedRent + data.getInt(COLUMN_1);
        }
        totalExpectedRentValue.setText(String.valueOf(totalExpectedRent));
    }
    void setTotalCollectedRent(){
        Cursor data = dbHelper.getTenantTableData("KEY_ALL");
        Calendar c = Calendar.getInstance();
        int totalRent = 0;
        while(data.moveToNext()) {
            totalRent = totalRent + data.getInt(COLUMN_5) + data.getInt(COLUMN_6);
        }
        totalcollectedRentValue.setText(String.valueOf(totalRent));
    }

}
