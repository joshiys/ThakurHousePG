package com.example.thakurhousepg;

import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class ViewReceiptsActivity extends AppCompatActivity implements ReceiptsListFragment.OnListFragmentInteractionListener {

    DataModule datamodule;
    TableLayout tableLayout;

    TextView roomNoTextView, rIdTextView, onlineAmountTextView, cashAmountTextView, typeForTextView;

    Spinner monthSpiner, roomSpinner, modeSpiner, typeSpiner;

    int currentRowNumber = 1;
    ReceiptsListFragment fragment = null;

    String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    String[] modes = {"All", "Online", "Cash"};
    String[] types = {"All", "Rent", "Deposit"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipts);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("View Receipts");

        datamodule = DataModule.getInstance();

        monthSpiner = findViewById(R.id.spinner);
        modeSpiner = findViewById(R.id.spinnerMode);
        typeSpiner = findViewById(R.id.spinnerType);



        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        monthSpiner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpiner.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        monthSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) monthSpiner.getSelectedView()).setBackgroundColor(Color.LTGRAY);
                currentRowNumber = 1;
                if(fragment != null) {
                    fragment.refreshForMonth(i + 1);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Bundle bundle = getIntent().getExtras();

        ArrayAdapter<String> adapterMode = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, modes);
        modeSpiner.setAdapter(adapterMode);
        adapterMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpiner.setSelection(bundle.getInt("MODE"));
        modeSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) modeSpiner.getSelectedView()).setBackgroundColor(Color.LTGRAY);
//                currentRowNumber = 1;
                if(fragment != null) {
                    fragment.refreshForMode(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        typeSpiner.setAdapter(adapterType);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpiner.setSelection(bundle.getInt("TYPE"));
        typeSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) typeSpiner.getSelectedView()).setBackgroundColor(Color.LTGRAY);
//                currentRowNumber = 1;
                if(fragment != null) {
                    fragment.refreshForType(i);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if (savedInstanceState == null) {
            DataModule.Tenant tenantInfoForModification = null;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new ReceiptsListFragment();
            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putString("MONTH_NUMBER", String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1));
            fragment.setArguments(fragmentBundle);
            transaction.replace(R.id.receiptlist_container, fragment);
            transaction.commit();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void onListFragmentInteraction(DataModule.Receipt item) {

    }
}
