package com.example.thakurhousepg;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;
import com.example.thakurhousepg.BedViewActivity;

public class OccupancyAndBookingActivity extends AppCompatActivity implements BedsListFragment.OnBedsListInteractionListener {
    public DataModule datamodule;
    public TabLayout tabLayout;
    public static int currentSelectedTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datamodule = DataModule.getInstance();

        setContentView(R.layout.activity_occupancy_and_booking);

        tabLayout = (TabLayout) findViewById(R.id.floor_tabs_id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentSelectedTab = 0;

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
                break;
            case R.id.menu_actionShowOutstandings:
                break;
            case R.id.menu_actionShowReceipts:
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

        DataModule.Bed bedInfo = datamodule.getBedInfo(item.bedNumber);
        if(bedInfo.bookingId != null) {
            toast = Toast.makeText(OccupancyAndBookingActivity.this, "Modify Tenant "+ item.tenantName, Toast.LENGTH_SHORT);
            toast.show();

            Intent modifyTenantIntent = new Intent(OccupancyAndBookingActivity.this, TenantInformationActivity.class);
            modifyTenantIntent.putExtra("BED_NUMBER", item.bedNumber);
            modifyTenantIntent.putExtra("ACTION", "MODIFY_TENANT");
            startActivity(modifyTenantIntent);
        } else {
            toast = Toast.makeText(OccupancyAndBookingActivity.this, "Empty Tenant: "+ item.bedNumber, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onRentClick(BedsListContent.BedsListItem item){
        DataModule.Bed bed = datamodule.getBedInfo(item.bedNumber);

        if(bed != null && bed.bookingId != null) {
            Toast.makeText(OccupancyAndBookingActivity.this, "Launching Rent Payment", Toast.LENGTH_SHORT).show();
            Intent receiptIntent = new Intent(OccupancyAndBookingActivity.this, ReceiptActivity.class);
            receiptIntent.putExtra("SECTION", "rent");
            receiptIntent.putExtra("ROOM_NUMBER", item.bedNumber);
            receiptIntent.putExtra("AMOUNT", item.rentPayble);

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
}
