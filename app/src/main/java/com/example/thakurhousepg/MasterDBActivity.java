package com.example.thakurhousepg;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MasterDBActivity extends AppCompatActivity implements View.OnClickListener{

    Button addRoom, btn0, btn1, btn2, btn3, btn4, btn5, btn6;
    ListView roomDBList;
    DatabaseHelper dbHelper;
    Cursor result;
    ArrayList<TableClass> roomTableList;
    ListView listView;
    TableClass roomTable;

    private Button currentSelectedButton;


    ArrayAdapter<String> adapter;


    public static final int COLUMN_0 = 0;
    public static final int COLUMN_1 = 1;
    public static final int COLUMN_2 = 2;
    public static final int COLUMN_3 = 3;
    public static final int COLUMN_4 = 4;

    public static final String KEY_ALL = "KEY_ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_db);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getTitle() + " - " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));

        addRoom = (Button) findViewById(R.id.addRoomRecord);
        btn0 = (Button) findViewById((R.id.btn0));
        btn1 = (Button) findViewById((R.id.btn1));
        btn2 = (Button) findViewById((R.id.btn2));
        btn3 = (Button) findViewById((R.id.btn3));
        btn4 = (Button) findViewById((R.id.btn4));
        btn5 = (Button) findViewById((R.id.btn5));
        btn6 = (Button) findViewById((R.id.btn6));


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

//        Toast.makeText(MasterDBActivity.this, "Master DB Operations", Toast.LENGTH_SHORT).show();

//        insertTenantData("101", "Sachindasdad Ahireadsadasdas", "1234567890", "02/03/2019", "0");
//        insertTenantData("102", "Sachindasdad Ahireadsadasdas", "1234", "02/03/2019", "8000");
//        insertTenantData("103", "Sachindasdad Ahireadsadasdas", "2", "02/03/2019", "8000");
//        insertTenantData("104", "Sachindasdad Ahireadsadasdas", "123", "02/03/2019", "8000");
//        insertTenantData("105", "Sachindasdad Ahireadsadasdas", "12", "02/03/2019", "9300");
//        insertTenantData("106", "Sachindasdad Ahireadsadasdas", "1", "02/03/2019", "5100");
//        insertTenantData("107", "Sachindasdad Ahireadsadasdas", "12", "02/03/2019", "9000");


        showMasterDBInListView();

        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent roomData = new Intent(MasterDBActivity.this, MasterRecordActivity.class);
                roomData.putExtra("ACTION", "ADD");
                startActivity(roomData);
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
//        Log.d("tagg","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showMasterDBInListView();
//        Log.d("tagg", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.d("tagg","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d("tagg","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Log.d("tagg","onDestroy");
    }


    private void insertMasterData(String roomNumber, String rent){
        boolean isInserted = dbHelper.insertInMaster(roomNumber, rent);


        if(isInserted == true){
            Toast.makeText(MasterDBActivity.this, "Room Added Successfully", Toast.LENGTH_SHORT).show();
//            Log.d("TAGG", "Insert in Master DB: "+roomNumber+ " Rent: "+rent);
        } else {
            Toast.makeText(MasterDBActivity.this, "Room Addition Failed", Toast.LENGTH_SHORT).show();
        }
    }

    boolean isButtonInRange(int rNo){
        int currentBtn = this.currentSelectedButton.getId();

        switch(currentBtn){
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

    void showMasterDBInListView(){
        //setContentView(R.layout.activity_master_db);

        roomTableList = new ArrayList<>();
        Cursor data = dbHelper.getMasterTableData(KEY_ALL);
        int numRows = data.getCount();
        int flag = 0;
        if(numRows == 0){
            Toast.makeText(MasterDBActivity.this,"The Database is empty  :(.",Toast.LENGTH_SHORT).show();
        }
        {
            int i = 0;
            if(flag == 0) {
                roomTable = new TableClass("Room No", null,null,"Rent");
                roomTableList.add(i, roomTable);
                flag = 1;
                i++;
            }

            while (data.moveToNext()) {
                int rNo = data.getInt(COLUMN_0);
                boolean addInListView = isButtonInRange(rNo);
                if(addInListView == true) {
                    roomTable = new TableClass(data.getString(COLUMN_0), null, null, data.getString(COLUMN_1));
                    roomTableList.add(i, roomTable);
                    i++;
                }
            }
            final MultiColumn_ListAdapter adapter = new MultiColumn_ListAdapter(this, 2,
                    R.layout.list_adapter_view, roomTableList);
            listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i != 0) {
                        TableClass item = adapter.getItem(i);
                        Intent roomData = new Intent(MasterDBActivity.this, MasterRecordActivity.class);
                        roomData.putExtra("ROOM_NUMBER", item.getRoomNumber());
                        roomData.putExtra("EXPECTED_RENT", item.getRoomRent());
                        roomData.putExtra("ACTION", "VIEW_ONLY");
                        startActivity(roomData);
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

//    private Button getSelectedButton(){
//        switch(this.currentSelectedButton){
//            case R.id.btn0:
//                return btn0;
//            case R.id.btn1:
//                return btn1;
//            case R.id.btn2:
//                return btn2;
//            case R.id.btn3:
//                return btn3;
//            case R.id.btn4:
//                return btn4;
//            case R.id.btn5:
//                return btn5;
//            case R.id.btn6:
//                return btn6;
//        }
//        return null;
//    }

    @Override
    public void onClick(View view) {
        Button selectedButton = this.currentSelectedButton;
//        selectedButton.setBackgroundColor(Color.LTGRAY);
        selectedButton.setTextColor(Color.BLACK);
        selectedButton.setBackgroundResource(android.R.drawable.btn_default);

        Button btn = (Button) view;
        btn.setBackgroundColor(Color.DKGRAY);
        btn.setTextColor(Color.WHITE);
        this.currentSelectedButton = btn;
        showMasterDBInListView();

//        switch (view.getId()){
//            case R.id.btn0:
//                selectedButton.setBackgroundColor(Color.LTGRAY);
//                selectedButton.setTextColor(Color.BLACK);
//
//                btn0.setBackgroundColor(Color.DKGRAY);
//                btn0.setTextColor(Color.WHITE);
//                this.currentSelectedButton = 0;
//                showMasterDBInListView();
//                break;
//            case R.id.btn1:
//                selectedButton.setBackgroundColor(Color.LTGRAY);
//                selectedButton.setTextColor(Color.BLACK);
//
//                btn1.setBackgroundColor(Color.DKGRAY);
//                btn1.setTextColor(Color.WHITE);
//                this.currentSelectedButton = 1;
//                showMasterDBInListView();
//                break;
//            case R.id.btn2:
//                selectedButton.setBackgroundColor(Color.LTGRAY);
//                selectedButton.setTextColor(Color.BLACK);
//
//                btn2.setBackgroundColor(Color.DKGRAY);
//                btn2.setTextColor(Color.WHITE);
//                this.currentSelectedButton = 2;
//                showMasterDBInListView();
//                break;
//            case R.id.btn3:
//                selectedButton.setBackgroundColor(Color.LTGRAY);
//                selectedButton.setTextColor(Color.BLACK);
//
//                btn3.setBackgroundColor(Color.DKGRAY);
//                btn3.setTextColor(Color.WHITE);
//                this.currentSelectedButton = 3;
//                showMasterDBInListView();
//                break;
//            case R.id.btn4:
//                selectedButton = getSelectedButton();
//                selectedButton.setTextColor(Color.BLACK);
//
//                btn4.setBackgroundColor(Color.DKGRAY);
//                btn4.setTextColor(Color.WHITE);
//                this.currentSelectedButton = 4;
//                showMasterDBInListView();
//                break;
//            case R.id.btn5:
//                selectedButton = getSelectedButton();
//                selectedButton.setBackgroundColor(Color.LTGRAY);
//                selectedButton.setTextColor(Color.BLACK);
//
//                btn5.setBackgroundColor(Color.DKGRAY);
//                btn5.setTextColor(Color.WHITE);
//                this.currentSelectedButton = 5;
//                showMasterDBInListView();
//                break;
//            case R.id.btn6:
//                selectedButton = getSelectedButton();
//                selectedButton.setBackgroundColor(Color.LTGRAY);
//                selectedButton.setTextColor(Color.BLACK);
//
//                btn6.setBackgroundColor(Color.DKGRAY);
//                btn6.setTextColor(Color.WHITE);
//                this.currentSelectedButton = 6;
//                showMasterDBInListView();
//                break;
//        }
    }
}



/*
    TableLayout masterDBTable;
    ListView masterDBList;
    DatabaseHelper dbHelper, myDB;
    Cursor result;
    ArrayList<TableClass> masterTableList;
    ListView listView;
    TableClass masterTable;


    ArrayAdapter<String> adapter;


    public static final int COLUMN_0 = 0;
    public static final int COLUMN_1 = 1;
    public static final String KEY_ALL = "KEY_ALL";

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        return super.onContextItemSelected(item);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_db);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DatabaseHelper(this);


        Toast.makeText(MasterDBActivity.this, "Master DB Operations", Toast.LENGTH_SHORT).show();

        masterDBTable = (TableLayout) findViewById(R.id.masterDBTable);
//        masterDBList = (ListView) findViewById(R.id.masterDBList);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked);

        masterDBTable.removeAllViewsInLayout();


        insertMasterData("101", "0");
        insertMasterData("102", "8000");
        insertMasterData("103", "8000");
        insertMasterData("104", "8000");
        insertMasterData("105", "9300");
        insertMasterData("106", "5100");
        insertMasterData("107", "9000");




        // when i=-1, loop will display heading of each column
        // then usually data will be display from i=0 to jArray.length()



        //showMasterDB();
        showMasterDBInListView();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void insertMasterData(String roomNumber, String rent){
        boolean isInserted = dbHelper.insertInMaster(roomNumber, rent);

//        Log.d("TAGG", "Insert in Master DB: "+roomNumber+ " Rent: "+rent);
        if(isInserted == true){
            Toast.makeText(MasterDBActivity.this, "Master DB Data Inserted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MasterDBActivity.this, "Master DB Data Not Inserted", Toast.LENGTH_SHORT).show();
        }
    }

    void showMasterDB(){
        Cursor result = dbHelper.getMasterTableData(KEY_ALL);
        int count = result.getCount();
        int flag=1;

        for(int i=-1; i<count;i++) {
            TableRow tr=new TableRow(MasterDBActivity.this);

            tr.setLayoutParams(new TableLayout.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            // this will be executed once
            if(flag==1){

                TextView b3=new TextView(MasterDBActivity.this);
                b3.setText("Room Number");
                b3.setTextColor(Color.BLUE);
                b3.setTextSize(25);
                tr.addView(b3);

                TextView b4=new TextView(MasterDBActivity.this);
                b4.setPadding(5, 0, 0, 0);
                b4.setTextSize(25);
                b4.setText("  ");
//                b4.setTextColor(Color.BLUE);
                tr.addView(b4);

                TextView b5=new TextView(MasterDBActivity.this);
                b5.setPadding(10, 0, 0, 0);
                b5.setTextSize(25);
                b5.setText("Rent");
                b5.setTextColor(Color.BLUE);
                tr.addView(b5);

                masterDBTable.addView(tr);

                final View vline = new View(MasterDBActivity.this);
                vline.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                vline.setBackgroundColor(Color.BLUE);
                masterDBTable.addView(vline); // add line below heading
                flag=0;
            } else {
                //JSONObject json_data = jArray.getJSONObject(i);
                result.moveToNext();

                TextView b = new TextView(MasterDBActivity.this);
                //String str=String.valueOf(json_data.getInt("column1"));
                b.setText(result.getString(COLUMN_0));
                b.setTextColor(Color.RED);
                b.setTextSize(17);
                tr.addView(b);

                TextView b2=new TextView(MasterDBActivity.this);
                b2.setPadding(5, 0, 0, 0);
                b2.setTextSize(17);
                b2.setText("  ");
//                b4.setTextColor(Color.BLUE);
                tr.addView(b2);

                TextView b1 = new TextView(MasterDBActivity.this);
                b1.setPadding(10, 0, 0, 0);
                b1.setTextSize(17);

                b1.setText(result.getString(COLUMN_1));
                b1.setTextColor(Color.BLACK);
                tr.addView(b1);

                masterDBTable.addView(tr);
                final View vline1 = new View(MasterDBActivity.this);
                vline1.setLayoutParams(new
                        TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1));
                vline1.setBackgroundColor(Color.WHITE);
                masterDBTable.addView(vline1);  // add line below each row
            }
        }
    }

    void showMasterDBInListView(){

//        setContentView(R.layout.viewcontents_layout);
        setContentView(R.layout.activity_master_db);

        masterTableList = new ArrayList<>();
        Cursor data = dbHelper.getMasterTableData(KEY_ALL);
        int numRows = data.getCount();
        int flag = 0;
        if(numRows == 0){
            Toast.makeText(MasterDBActivity.this,"The Database is empty  :(.",Toast.LENGTH_SHORT).show();
        }
        {
            int i = 0;
            if(flag == 0) {
                masterTable = new TableClass("Room Number", null,null,null,"Room Rent");
                masterTableList.add(i, masterTable);
                flag = 1;
                i++;
            }
            while (data.moveToNext()) {
                masterTable = new TableClass(data.getString(COLUMN_0), null, null, null, data.getString(COLUMN_1));
                masterTableList.add(i, masterTable);
                i++;
            }
            MultiColumn_ListAdapter adapter = new MultiColumn_ListAdapter(this, 2,
                    R.layout.list_adapter_view, masterTableList);
            listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);
        }
    }
}
*/
