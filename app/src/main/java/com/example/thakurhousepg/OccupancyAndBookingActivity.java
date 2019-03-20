package com.example.thakurhousepg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;

public class OccupancyAndBookingActivity extends AppCompatActivity implements BedsListItemFragment.OnListFragmentInteractionListener {
    public DataModule datamodule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datamodule = new DataModule(this);

        setContentView(R.layout.activity_occupancy_and_booking);
    }

    public void onListFragmentInteraction(BedsListContent.BedsListItem item) {
        Toast.makeText(OccupancyAndBookingActivity.this, "Clicked Item "+ item.toString(), Toast.LENGTH_SHORT).show();
    }
}
