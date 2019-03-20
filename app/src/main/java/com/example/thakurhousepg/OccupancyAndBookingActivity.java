package com.example.thakurhousepg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;
import com.example.thakurhousepg.BedViewActivity;

public class OccupancyAndBookingActivity extends AppCompatActivity implements BedsListFragment.OnBedsListInteractionListener {
    public DataModule datamodule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datamodule = new DataModule(this);

        setContentView(R.layout.activity_occupancy_and_booking);
    }

    public void onBedItemClick(BedsListContent.BedsListItem item) {
        Toast.makeText(OccupancyAndBookingActivity.this, "Launching Bed View for: "+ item.bedNumber, Toast.LENGTH_SHORT).show();
        Intent bedViewIntent = new Intent(OccupancyAndBookingActivity.this, BedViewActivity.class);
        bedViewIntent.putExtra("BED_NUMBER", item.bedNumber);
        startActivity(bedViewIntent);
    }
    public void onTenantClick(BedsListContent.BedsListItem item) {
        Toast.makeText(OccupancyAndBookingActivity.this, "Clicked Tenant "+ item.tenantName, Toast.LENGTH_SHORT).show();
    }
    public void onNewBookingClick(BedsListContent.BedsListItem item){
        Toast.makeText(OccupancyAndBookingActivity.this, "Clicked New Booking ", Toast.LENGTH_SHORT).show();
    }
}
