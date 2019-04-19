package com.example.thakurhousepg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;

public class TenantInformationActivity extends AppCompatActivity implements ReceiptsListFragment.OnListFragmentInteractionListener {
    private EditText tenantName;
    private EditText tenantEmail;
    private EditText tenantMobile;
    private EditText tenantAddress;
    private Button saveButton;

    private DataModule dataModule;
    public DataModel.Tenant tenantInfoForModification = null;
    private String tenantId = null;

    private Intent returnIntent = new Intent();

    private boolean addTenantMode = true;
    private NetworkDataModule restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_information);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataModule = DataModule.getInstance();
        restService = NetworkDataModule.getInstance();
        final Bundle bundle = getIntent().getExtras();

        tenantName = findViewById(R.id.add_tenant_name);
        tenantEmail = findViewById(R.id.add_tenant_email);
        tenantMobile = findViewById(R.id.add_tenant_mobile);
        tenantAddress = findViewById(R.id.add_tenant_address);

        if(bundle != null) {
            if(bundle.getString("TENANT_ID") != null) {
                String tenantId = bundle.getString("TENANT_ID");
                tenantInfoForModification = restService.getTenantInfo(tenantId);

                tenantName.setText(tenantInfoForModification.name);
                tenantEmail.setText(tenantInfoForModification.email);
                tenantMobile.setText(tenantInfoForModification.mobile);
                tenantAddress.setText(tenantInfoForModification.address);
            }

            if("MODIFY_TENANT".equals(bundle.getString("ACTION"))) {
                addTenantMode = false;
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(tenantName.getWindowToken(), 0);
            }
        }

        if (savedInstanceState == null && tenantInfoForModification != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ReceiptsListFragment fragment = new ReceiptsListFragment();
            Bundle fragmentBundle = new Bundle();
            fragmentBundle.putString("TENANT_ID", tenantInfoForModification.id);
            fragment.setArguments(fragmentBundle);
            transaction.replace(R.id.tenant_receipts_list_container, fragment);
            transaction.commit();
        } else {
            findViewById(R.id.tenant_receipts_label).setVisibility(View.INVISIBLE);
        }

        if(bundle != null && ("MODIFY_TENANT".equals(bundle.getString("ACTION")))) {
            addTenantMode = false;
            setTitle("Modify Tenant");
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(tenantName.getWindowToken(), 0);
        }else {
            setTitle("Add Tenant");
        }

        this.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        saveButton = findViewById(R.id.add_tenant_save);
        saveButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange (View v, boolean hasFocus) {
                if(hasFocus) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addTenantMode) {
                    tenantId = dataModule.addNewTenant(tenantName.getText().toString(),
                            tenantMobile.getText().toString(),
                            "",
                            tenantEmail.getText().toString(),
                            tenantAddress.getText().toString(), "0");

                    //Just to make sure call goes successfully to the server
                    //Todo: Implement Callback mechanism
                    restService.addNewTenant(tenantName.getText().toString(),
                            tenantMobile.getText().toString(),
                            tenantEmail.getText().toString(),
                            tenantAddress.getText().toString(), "0", false);

                    if(tenantId != null) {
                        returnIntent.putExtra("TENANT_ID", tenantId);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(TenantInformationActivity.this, "Can not create new Tenant", Toast.LENGTH_SHORT);
                    }
                } else {
                    boolean status = false;
                    if(tenantInfoForModification != null) {
                        status = dataModule.updateTenant(tenantInfoForModification.id, tenantName.getText().toString(), tenantMobile.getText().toString(),
                                "", tenantEmail.getText().toString(), tenantAddress.getText().toString(), tenantInfoForModification.isCurrent, tenantInfoForModification.parentId);
                    }

                    if (status == true) {
                        tenantId = tenantInfoForModification.id;
                        Toast.makeText(TenantInformationActivity.this, "Tenant Record Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(TenantInformationActivity.this, "Tenant Record Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (tenantId == null) {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void onListFragmentInteraction(DataModel.Receipt item) {

    }
}
