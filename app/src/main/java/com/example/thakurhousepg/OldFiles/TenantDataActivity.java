package com.example.thakurhousepg.OldFiles;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.thakurhousepg.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class TenantDataActivity extends AppCompatActivity implements View.OnClickListener{

    Button addTenant, btn0, btn1, btn2, btn3, btn4, btn5, btn6;
    ListView tenantDBList;
    DatabaseHelper dbHelper;
    Cursor result;
    ArrayList<TableClass> tenantTableList;
    ListView listView;
    TableClass tenantTable;

    private Button currentSelectedButton;


    ArrayAdapter<String> adapter;


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


    public static final String KEY_ALL = "KEY_ALL";

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        return super.onContextItemSelected(item);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        addTenant = (Button) findViewById(R.id.addtenantRecord);
        btn0 = (Button) findViewById((R.id.btn0));
        btn1 = (Button) findViewById((R.id.btn1));
        btn2 = (Button) findViewById((R.id.btn2));
        btn3 = (Button) findViewById((R.id.btn3));
        btn4 = (Button) findViewById((R.id.btn4));
        btn5 = (Button) findViewById((R.id.btn5));
        btn6 = (Button) findViewById((R.id.btn6));

        addTenant.setEnabled(false);


        dbHelper = new DatabaseHelper(this);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);

        btn0.setBackgroundResource(android.R.drawable.btn_default);
        btn1.setBackgroundResource(android.R.drawable.btn_default);
        btn2.setBackgroundResource(android.R.drawable.btn_default);
        btn3.setBackgroundResource(android.R.drawable.btn_default);
        btn4.setBackgroundResource(android.R.drawable.btn_default);
        btn5.setBackgroundResource(android.R.drawable.btn_default);
        btn6.setBackgroundResource(android.R.drawable.btn_default);


        btn0.setBackgroundColor(Color.DKGRAY);
        btn0.setTextColor(Color.WHITE);
        currentSelectedButton = btn0;

        Toast.makeText(TenantDataActivity.this, "Tenant Room Operations", Toast.LENGTH_SHORT).show();

//        insertTenantData("101", "Sachindasdad Ahireadsadasdas", "1234567890", "02/03/2019", "0");
//        insertTenantData("102", "Sachindasdad Ahireadsadasdas", "1234", "02/03/2019", "8000");
//        insertTenantData("103", "Sachindasdad Ahireadsadasdas", "2", "02/03/2019", "8000");
//        insertTenantData("104", "Sachindasdad Ahireadsadasdas", "123", "02/03/2019", "8000");
//        insertTenantData("105", "Sachindasdad Ahireadsadasdas", "12", "02/03/2019", "9300");
//        insertTenantData("106", "Sachindasdad Ahireadsadasdas", "1", "02/03/2019", "5100");
//        insertTenantData("107", "Sachindasdad Ahireadsadasdas", "12", "02/03/2019", "9000");


        showTenantDBInListView();

        addTenant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tenantData = new Intent(TenantDataActivity.this, TenantRecordActivity.class);
                tenantData.putExtra("ACTION", "ADD");
                startActivity(tenantData);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("tagg","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTenantDBInListView();
        Log.d("tagg", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("tagg","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d("tagg","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("tagg","onDestroy");
    }



    private void insertTenantData(String roomNumber, String name, String mobile, String date, String rent,
                                  String onlineRent, String cashRent, String penalty, String penaltyMonth,
                                  String decidedDeposit, String onlineDeposit, String cashDeposit) {

        boolean isInserted = dbHelper.insertInTenant(roomNumber, name, mobile, date, rent,
                    onlineRent,  cashRent,  penalty,  penaltyMonth, decidedDeposit, onlineDeposit, cashDeposit);


            if(isInserted == true){
            Toast.makeText(TenantDataActivity.this, "Tenant DB Data Inserted", Toast.LENGTH_SHORT).show();
            Log.d("TAGG", "Insert in Master DB: "+roomNumber+ " Rent: "+rent);
        } else {
            Toast.makeText(TenantDataActivity.this, "Tenant DB Data Not Inserted", Toast.LENGTH_SHORT).show();
        }
    }

    boolean isButtonInRange(int rNo){
        int floor = this.currentSelectedButton.getId();
        switch(floor){
            case R.id.btn0:
                if(rNo >= 0 && rNo <= 100) {
                    return true;
                }
                break;
            case R.id.btn1:
                if(rNo >= 101 && rNo <= 200) {
                    return true;
                }
                break;
            case R.id.btn2:
                if(rNo >= 201 && rNo <= 300) {
                    return true;
                }
                break;
            case R.id.btn3:
                if(rNo >= 301 && rNo <= 400) {
                    return true;
                }
                break;
            case R.id.btn4:
                if(rNo >= 401 && rNo <= 500) {
                    return true;
                }
                break;
            case R.id.btn5:
                if(rNo >= 501 && rNo <= 600) {
                    return true;
                }
                break;
            case R.id.btn6:
                if(rNo >= 601 && rNo <= 100) {
                    return true;
                }
                break;
        }
        return false;
    }

    void showTenantDBInListView(){
        //setContentView(R.layout.activity_master_db);

        tenantTableList = new ArrayList<>();
        Cursor data = dbHelper.getTenantTableData(KEY_ALL);
        int numRows = data.getCount();
        int flag = 0;
        if(numRows == 0){
            Toast.makeText(TenantDataActivity.this,"The Database is empty  :(.",Toast.LENGTH_SHORT).show();
        }
         {
            int i = 0;
            if(flag == 0) {
                tenantTable = new TableClass("Room Number", "Penalty","Deposit","Rent");
                tenantTableList.add(i, tenantTable);
                flag = 1;
                i++;
            }

            while (data.moveToNext()) {
                int rNo = data.getInt(COLUMN_0);
                boolean addInListView = isButtonInRange(rNo);
                if(addInListView == true) {
                    int outstandingRent = data.getInt(COLUMN_4);// - (data.getInt(COLUMN_5) + data.getInt(COLUMN_6)));
                    int outstandingDeposit = data.getInt(COLUMN_9);// - (data.getInt(COLUMN_10) + data.getInt(COLUMN_11)));
                    String penalty = "0";
                    if(data.getString(COLUMN_7) != null && data.getString(COLUMN_7).isEmpty() == false){
                        penalty = data.getString(COLUMN_7);
                    }
                            tenantTable = new TableClass(data.getString(COLUMN_0),
                            penalty, String.valueOf(outstandingDeposit), String.valueOf(outstandingRent));
                    tenantTableList.add(i, tenantTable);
                    i++;
                }
            }
            final MultiColumn_ListAdapter adapter = new MultiColumn_ListAdapter(this, 5,
                    R.layout.list_adapter_view, tenantTableList);
            listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i != 0) {
                        TableClass item = adapter.getItem(i);
                        Intent tenantData = new Intent(TenantDataActivity.this, TenantRecordActivity.class);
                        tenantData.putExtra("TENANT_ROOM_NUMBER", item.getRoomNumber());
                        /*tenantData.putExtra("TENANT_NAME", item.getTenantName());
                        tenantData.putExtra("TENANT_MOBILE", item.getTenantMobile());
                        tenantData.putExtra("TENANT_ADM_DATE", item.getTenantAdmDate());
                        tenantData.putExtra("TENANT_OUTSTANDING", item.getRoomRent());*/
                        tenantData.putExtra("ACTION", "VIEW_ONLY");
                        startActivity(tenantData);
//                    switch(item){
//                        case "Rent":
//                            Toast.makeText(this, "List View Click...Rent", Toast.LENGTH_SHORT).show();
//                            break;
//                        case "Deposit":
//                            Toast.makeText(this, "List View Click...Deposit", Toast.LENGTH_SHORT).show();
//                            break;
//                    }
                    }
                }
            });
        }
    }



    @Override
    public void onClick(View view) {
        Button selectedButton = null;

        selectedButton = this.currentSelectedButton;
        selectedButton.setBackgroundColor(Color.LTGRAY);
        selectedButton.setTextColor(Color.BLACK);

        selectedButton.setBackgroundResource(android.R.drawable.btn_default);


        Button btn = (Button) view;
        btn.setBackgroundColor(Color.DKGRAY);
        btn.setTextColor(Color.WHITE);
        this.currentSelectedButton = btn;
        showTenantDBInListView();
    }
}
