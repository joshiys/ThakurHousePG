package com.example.thakurhousepg;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MiscActivity extends AppCompatActivity implements View.OnClickListener {

    EditText resetMonth, resetFlag, emailID1, emailID2, mobile1, mobile2, mobile3;
    Button save, delete;

    DatabaseHelper dbHelper;

    public static final int COLUMN_0 = 0;
    public static final int COLUMN_1 = 1;
    public static final int COLUMN_2 = 2;
    public static final int COLUMN_3 = 3;
    public static final int COLUMN_4 = 4;
    public static final int COLUMN_5 = 5;
    public static final int COLUMN_6 = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misc);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        resetMonth = (EditText) findViewById(R.id.resetMonth);
        resetFlag = (EditText) findViewById(R.id.resetFlag);
        emailID1 = (EditText) findViewById(R.id.emailID1);
        emailID2 = (EditText) findViewById(R.id.emailID2);
        mobile1 = (EditText) findViewById(R.id.mobileNumber1);
        mobile2 = (EditText) findViewById(R.id.mobileNumber2);
        mobile3 = (EditText) findViewById(R.id.mobileNumber3);

        save = (Button) findViewById(R.id.btnSave);


        save.setOnClickListener(this);


        dbHelper = new DatabaseHelper(this);

        Cursor data = dbHelper.getMiscTableData();

        while (data.moveToNext()) {
            resetMonth.setText(data.getString(COLUMN_0));
            resetFlag.setText(data.getString(COLUMN_1));
            emailID1.setText(data.getString(COLUMN_2));
            emailID2.setText(data.getString(COLUMN_3));

            mobile1.setText(data.getString(COLUMN_4));
            mobile2.setText(data.getString(COLUMN_5));
            mobile3.setText(data.getString(COLUMN_6));
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

                boolean addresult = dbHelper.insertInMisc(resetMonth.getText().toString(), resetFlag.getText().toString(), emailID1.getText().toString(), emailID2.getText().toString(),
                        mobile1.getText().toString(), mobile2.getText().toString(), mobile3.getText().toString());
                if (addresult == true) {
                    Toast.makeText(MiscActivity.this, "Misc Data Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MiscActivity.this, "Misc Data Update Failed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
