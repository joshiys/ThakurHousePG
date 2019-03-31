package com.example.thakurhousepg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SelectTenantActivity extends AppCompatActivity {
    private DataModule dataModule = DataModule.getInstance();
    Button selectButton;

    private static final String TAG = "SelectTenantActivity";
    private Intent returnIntent = new Intent();
    private ArrayList<DataModule.Tenant> tenants = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tenant);

        final ListView tenantsListView = findViewById(R.id.tenant_selection_list);
        selectButton = findViewById(R.id.tenant_selection_complete);

//        tenantNameList.setAdapter(new TenantListAdapter());

        tenants = dataModule.getAllTenants(false);

        final ArrayList<String> tenantNamesList = new ArrayList<String>();

        for(DataModule.Tenant tenant:tenants) {
            DataModule.Booking booking = dataModule.getBookingInfoForTenant(tenant.id);
            tenantNamesList.add(tenant.name + (tenant.isCurrent ? " , Current" :
                    (booking != null ? ", Old, Bed = " + booking.bedNumber :" New")
            ));
        }

        tenantsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, tenantNamesList));
        tenantsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        tenantsListView.setItemsCanFocus(false);

        tenantsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "Item selected " + String.valueOf(position));
            }

        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> checkedItems = new ArrayList<String>();
                SparseBooleanArray selectedItems = tenantsListView.getCheckedItemPositions();

                for(int i = 0; i < selectedItems.size(); i++) {
                    Log.i(TAG, "Selected Item is:" + selectedItems.keyAt(i) + " , value is: " +String.valueOf(i));
                    if(selectedItems.valueAt(i)) {
                        Log.i(TAG, "But only this is true : " + selectedItems.keyAt(i));
                        checkedItems.add(tenants.get((int) selectedItems.keyAt(i)).id);
                    }
                }

                returnIntent.putExtra("SELECTED_TENANT_IDS", checkedItems);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
