package com.example.thakurhousepg.NewFiles.OccupancyPackage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Toast;

import com.example.thakurhousepg.NewFiles.DataModule;
import com.example.thakurhousepg.R;

public class OccupancyAndBooking extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {
    public DataModule datamodule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        datamodule = new DataModule(this);

        setContentView(R.layout.activity_occupancy_and_booking);
    }

    public void onListFragmentInteraction(BedsListContent.BedsListItem item) {
        Toast.makeText(OccupancyAndBooking.this, "Clicked Item "+ item.toString(), Toast.LENGTH_SHORT).show();
    }
}
