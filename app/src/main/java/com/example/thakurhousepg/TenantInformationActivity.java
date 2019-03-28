package com.example.thakurhousepg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TenantInformationActivity extends AppCompatActivity {
    private DataModule dataModule;
    private EditText tenantName;
    private EditText tenantEmail;
    private EditText tenantMobile;
    private EditText tenantAddress;

    private Button saveButton;
    private Intent returnIntent = new Intent();

    private String tenantId = null;
    private DataModule.Tenant tenantInfoForModification = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_information);

        dataModule = DataModule.getInstance();
        final Bundle bundle = getIntent().getExtras();

        tenantName = findViewById(R.id.add_tenant_name);
        tenantEmail = findViewById(R.id.add_tenant_email);
        tenantMobile = findViewById(R.id.add_tenant_mobile);
        tenantAddress = findViewById(R.id.add_tenant_address);

        if(bundle.getString("BED_NUMBER") != null) {
            String bedNumber = bundle.getString("BED_NUMBER");
            tenantInfoForModification = dataModule.getTenantInfoForBooking(dataModule.getBedInfo(bedNumber).bookingId);

            tenantName.setText(tenantInfoForModification.name);
            tenantEmail.setText(tenantInfoForModification.email);
            tenantMobile.setText(tenantInfoForModification.mobile);
            tenantAddress.setText(tenantInfoForModification.address);
        }

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
                if("MODIFY_TENANT".equals(bundle.getString("ACTION"))) {
                    boolean status = false;
                    if(tenantInfoForModification != null) {
                        status = dataModule.updateTenant(tenantInfoForModification.id, tenantName.getText().toString(), tenantMobile.getText().toString(),
                                "", tenantEmail.getText().toString(), tenantAddress.getText().toString(), false);
                    }

                    if (status == true) {
                        tenantId = tenantInfoForModification.id;
                        Toast.makeText(TenantInformationActivity.this, "Tenant Record Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(TenantInformationActivity.this, "Tenant Record Update Failed", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    tenantId = dataModule.addNewTenant(tenantName.getText().toString(),
                            tenantMobile.getText().toString(),
                            "",
                            tenantEmail.getText().toString(),
                            tenantAddress.getText().toString());

                    if(tenantId != null) {
                        returnIntent.putExtra("TENANT_ID", tenantId);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(TenantInformationActivity.this, "Can not create new Tenant", Toast.LENGTH_SHORT);
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

}
