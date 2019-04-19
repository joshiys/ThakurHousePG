package com.example.thakurhousepg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectTenantActivity extends AppCompatActivity {
    private DataModule dataModule = DataModule.getInstance();
    Button selectButton;

    private static final String TAG = "SelectTenantActivity";
    private Intent returnIntent = new Intent();
    private ArrayList<DataModel.Tenant> tenants = null;
    int checkTenantSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tenant);

        setTitle("Tenant Selection");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ListView tenantsListView = findViewById(R.id.tenant_selection_list);
        selectButton = findViewById(R.id.tenant_selection_complete);
        Bundle bundle = getIntent().getExtras();

        if("MODIFY_FULLY_SELECTED_LIST".equals(bundle.getString("LIST_MODE"))) {
            tenants = (ArrayList<DataModel.Tenant>) bundle.getSerializable("TENANT_LIST");
            checkTenantSize = tenants.size();
            ArrayList<DataModule.Tenant> allTenants = dataModule.getAllTenants(false);
            for(DataModule.Tenant tenant:allTenants) {
                DataModule.Booking booking = dataModule.getBookingInfoForTenant(tenant.id);
                if(!tenant.isCurrent) {
                    tenants.add(tenant);
                }
            }
        } else {
            tenants = dataModule.getAllTenants(false);
        }

        final ArrayList<String> tenantNamesList = new ArrayList<String>();

        for(DataModel.Tenant tenant:tenants) {
            DataModel.Booking booking = dataModule.getBookingInfoForTenant(tenant.id);
            if((tenant.isCurrent) && (Integer.valueOf(tenant.parentId) != 0)) {
                    DataModule.Tenant parentTenant = dataModule.getTenantInfo(tenant.parentId);
                    booking = dataModule.getBookingInfoForTenant(parentTenant.id);
            }
            tenantNamesList.add(tenant.name + (tenant.isCurrent ? " , Current=" + booking.bedNumber :
                    (booking != null ? ", Old, Bed = " + booking.bedNumber : " New")
            ));
        }

        tenantsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, tenantNamesList));
        tenantsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        tenantsListView.setItemsCanFocus(false);

        //When the Mode is "MODIFY_PARTIALLY_SELECTED_LIST" we receive the Id's which have been selected.
        if("MODIFY_PARTIALLY_SELECTED_LIST".equals(bundle.getString("LIST_MODE"))) {
            ArrayList<String> tenantIds = (ArrayList<String>) bundle.getSerializable("TENANT_LIST");
            for (String id : tenantIds) {
                for (DataModel.Tenant t: tenants) {
                    if(t.id.equals(id))
                        tenantsListView.setItemChecked(tenants.indexOf(t), true);
                }
            }
        } else if("MODIFY_FULLY_SELECTED_LIST".equals(bundle.getString("LIST_MODE"))) {
            for (int i = 0; i < checkTenantSize; i++) {
                tenantsListView.setItemChecked(i, true);
            }
        }

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

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
