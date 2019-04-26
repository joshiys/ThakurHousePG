package com.example.thakurhousepg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class OccupancyAndBookingActivity extends AppCompatActivity implements BedsListFragment.OnBedsListInteractionListener {
    public TabLayout tabLayout;
    public static int currentSelectedTab = 0;
    private NetworkDataModule restService;

    private static final String TAG = "OccupancyAndBookingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_occupancy_and_booking);

        setTitle("Occupancy");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.floor_tabs_id);

        tabLayout.setTabTextColors(Color.WHITE, Color.CYAN);

        restService = NetworkDataModule.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        currentSelectedTab = 0;
        tabLayout.getTabAt(currentSelectedTab).select();


        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                Log.e("TAGG", "OccupancyAnd... onTabSelected: " + tab.getPosition());
                currentSelectedTab = tab.getPosition();
//                BedsListContent.refresh();
                /* SAHIRE Need to find someother way to update Fragment here */
                BedsListFragment bedsFrag = (BedsListFragment) getSupportFragmentManager().findFragmentById(R.id.beds_fragment);
                bedsFrag.reloadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                Log.e("TAGG", "OccupancyAnd... onTabUnSelected");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                Log.e("TAGG", "OccupancyAnd... onTabReselected");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
//        currentSelectedTab = 0;
        BedsListContent.refresh();
        //tabLayout.removeOnTabSelectedListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.menu_actionAddPenalty:
                Calendar rightNow = Calendar.getInstance();

                SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                int monthUpdated = settings.getInt("penaltyAddedForMonth", 0);
                if(monthUpdated == 0 || monthUpdated != (rightNow.get(Calendar.MONTH) + 1)) {
                    Log.i(TAG, "Adding Penalties for the month of " + rightNow.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US));
                    restService.addPenaltyToOutstandingPayments(new NetworkDataModulCallback<DataModel.Pending>() {
                        @Override
                        public void onSuccess(DataModel.Pending obj) {
                        }

                        @Override
                        public void onFailure() {
                        }

                        @Override
                        public void onResult() {
                            SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
                            Calendar rightNow = Calendar.getInstance();
                            SharedPreferences.Editor settingsEditor = settings.edit();
                            settingsEditor.putInt("penaltyAddedForMonth", (rightNow.get(Calendar.MONTH) + 1));
                            settingsEditor.commit();

                            BedsListContent.refresh();
                            BedsListFragment bedsFrag = (BedsListFragment) getSupportFragmentManager().findFragmentById(R.id.beds_fragment);
                            bedsFrag.reloadData();
                        }

                    });


                } else {
                    Toast.makeText(OccupancyAndBookingActivity.this, "Penalty has already been added for this Month.", Toast.LENGTH_SHORT).show();
                }
                //SAHIRE : Send SMS to All
                break;
            case R.id.menu_actionShowOutstandings:
                Intent pendingIntent = new Intent(OccupancyAndBookingActivity.this, ViewOutstandingActivity.class);
                startActivity(pendingIntent);
                break;
            case R.id.menu_actionShowMonthly:
                Intent monthIntent = new Intent(OccupancyAndBookingActivity.this, MonthlyDataActivity.class);
                startActivity(monthIntent);
                break;
            case R.id.menu_actionShowReceipts:
                Intent receivedIntent = new Intent(OccupancyAndBookingActivity.this, ViewReceiptsActivity.class);
                startActivity(receivedIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBedItemClick(BedsListContent.BedsListItem item) {
        Toast toast = Toast.makeText(OccupancyAndBookingActivity.this, "Launching Bed View for: "+ item.bedNumber, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0); toast.show();

        Intent bedViewIntent = new Intent(OccupancyAndBookingActivity.this, BedViewActivity.class);
        bedViewIntent.putExtra("BED_NUMBER", item.bedNumber);
        startActivity(bedViewIntent);
    }

    public void onTenantClick(BedsListContent.BedsListItem item) {
        Toast toast;

        DataModel.Bed bedInfo = restService.getBedInfo(item.bedNumber);
        if(bedInfo.bookingId != null) {
            final DataModel.Tenant mainTenant = restService.getTenantInfoForBooking(bedInfo.bookingId);
            final ArrayList<DataModel.Tenant> dependentsList = restService.getDependents(mainTenant.id);
            if(!dependentsList.isEmpty()) {
                final ArrayList<String> tenants = new ArrayList<String>();
                tenants.add(mainTenant.name);
                for(DataModel.Tenant t: dependentsList) {
                    tenants.add(t.name);
                }

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);

                builder.setTitle("Select Tenant to view/modify")
                        .setSingleChoiceItems(tenants.toArray(new String[0]), -1, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                DataModel.Tenant temp;
                                if(item == 0) {
                                    temp = mainTenant;
                                } else {
                                    temp = dependentsList.get(item - 1);
                                }
                                showTenantActivity(temp);
                                dialog.dismiss();
                            }
                        });
                builder.show();
            } else {

                toast = Toast.makeText(OccupancyAndBookingActivity.this, "Modify Tenant " + item.tenantName, Toast.LENGTH_SHORT);
                toast.show();
                showTenantActivity(mainTenant);
            }
        } else {
            toast = Toast.makeText(OccupancyAndBookingActivity.this, "Empty Tenant: "+ item.bedNumber, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void showTenantActivity(DataModel.Tenant t) {
        Intent modifyTenantIntent = new Intent(OccupancyAndBookingActivity.this, TenantInformationActivity.class);
        modifyTenantIntent.putExtra("TENANT_ID", t.id);
        modifyTenantIntent.putExtra("ACTION", "MODIFY_TENANT");
        startActivity(modifyTenantIntent);
    }

    public void onRentClick(BedsListContent.BedsListItem item){
        DataModel.Bed bed = restService.getBedInfo(item.bedNumber);

        if(bed != null && bed.bookingId != null) {
            Toast.makeText(OccupancyAndBookingActivity.this, "Launching Rent Payment", Toast.LENGTH_SHORT).show();
            Intent receiptIntent = new Intent(OccupancyAndBookingActivity.this, ReceiptActivity.class);
            receiptIntent.putExtra("SECTION", "Rent");
            receiptIntent.putExtra("ROOM_NUMBER", item.bedNumber);

            //XXX : Assuming there will be maximum three entries
            ArrayList<DataModel.Pending> pendingEntries = restService.getPendingEntriesForBooking(bed.bookingId);
            String rent = "", deposit = "";
            for (DataModel.Pending pendingEntry : pendingEntries) {
                if(pendingEntry.type == DataModel.PendingType.DEPOSIT){
                    deposit = String.valueOf(pendingEntry.pendingAmt);
                } else if(pendingEntry.type == DataModel.PendingType.RENT){
                    rent = String.valueOf(pendingEntry.pendingAmt);
                }
            }
            //XXX : Booking should not be null because bed is already booked.
            receiptIntent.putExtra("RENT_AMOUNT", rent);
            receiptIntent.putExtra("DEPOSIT_AMOUNT", deposit);

            startActivity(receiptIntent);
        } else {
            Toast.makeText(OccupancyAndBookingActivity.this, "Room Not Booked Yet", Toast.LENGTH_SHORT).show();
        }
    }

    public static Integer getSelectedTab(){
        return Integer.valueOf(currentSelectedTab);
    }

    /* SAHIRE Make this function generic. Use tab infor in switch case */
    public static boolean isRoomForSelectedTab(int rNo, int floor){
        switch(floor){
            case 0:
                if(rNo >= 0 && rNo <= 100) {
                    return true;
                }
                break;
            case 1:
                if(rNo >= 101 && rNo <= 200) {
                    return true;
                }
                break;
            case 2:
                if(rNo >= 201 && rNo <= 300) {
                    return true;
                }
                break;
            case 3:
                if(rNo >= 301 && rNo <= 400) {
                    return true;
                }
                break;
            case 4:
                if(rNo >= 401 && rNo <= 500) {
                    return true;
                }
                break;
            case 5:
                if(rNo >= 501 && rNo <= 600) {
                    return true;
                }
                break;
            case 6:
                if(rNo >= 601 && rNo <= 700) {
                    return true;
                }
                break;
        }
        return false;
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
