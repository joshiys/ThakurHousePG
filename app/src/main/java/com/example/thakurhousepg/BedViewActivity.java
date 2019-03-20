package com.example.thakurhousepg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BedViewActivity extends AppCompatActivity {

    private EditText bedNumberLabel;
    private Button bookButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_view);

        Bundle bundle = getIntent().getExtras();
        bedNumberLabel = (EditText) findViewById(R.id.bedview_bed_number);
        bedNumberLabel.setText(bundle.getString("BED_NUMBER"));

        bookButton = (Button) findViewById(R.id.bedview_button_book);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Implement and Launch Bed Booking Activity
                Toast.makeText(BedViewActivity.this, "Create new Booking", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
