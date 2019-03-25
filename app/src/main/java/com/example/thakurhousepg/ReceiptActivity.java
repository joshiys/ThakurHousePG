package com.example.thakurhousepg;

import android.content.Context;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiptActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    Button btnSave;
    EditText roomNumberText, totalAmount, onlineAmt, cashAmt, penaltyAmt;
    CheckBox rentInCashCheckBox, rentOnlineCheckBox;
    DataModule dbHelper;

    boolean confirm = false;

    boolean onlineRentChecked = false, cashRentChecked = false;

    Cursor data = null;

    private static final String TAG = "ReceiptActivity";
    public String bedNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();

        bedNumber = bundle.getString("BED_NUMBER");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

//        getBookingInfo
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ReceiptFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private EditText roomNumberText, totalAmount, onlineAmt, cashAmt, penaltyAmt;
        private CheckBox onlineCheckBox, cashCheckBox;
        private CheckBox penaltySwitch;
        private Button saveButton;

        public DataModule datamodule;

        public boolean onlineRentChecked = false, cashRentChecked = false;

        public String bedNumber = null;
        public Context myContext;

        public ReceiptFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ReceiptFragment newInstance(int sectionNumber, String bedNumber) {
            ReceiptFragment fragment = new ReceiptFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("BED_NUMBER", bedNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);

            myContext = getActivity().getApplicationContext();
            datamodule = new DataModule(myContext);

            bedNumber = getArguments().getString("BED_NUMBER");

            //TODO: Implement Bed number Validation and Automatic filling of the Rent amount
            roomNumberText = (EditText) rootView.findViewById(R.id.receipt_te_bed_number);
            roomNumberText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event != null || actionId == EditorInfo.IME_NULL) {
                        Log.v(TAG, "Sachin : " + v.getText().toString());
                    } else {
                        Log.v(TAG, "performed some other action. ID = " + String.valueOf(actionId));
                    }
                    return true;
                }
            });
            roomNumberText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {

                    /* SAHIRE Fetch Rent for room number from Booking Table */
                    if(roomNumberText.getText().toString().isEmpty() == false){
                        Log.v(TAG, "Focus Changed: " + roomNumberText.getText().toString());
                        DataModule.Bed bed = datamodule.getBedInfo(roomNumberText.getText().toString());
                        if(bed != null && bed.bookingId != null) {
                            DataModule.Booking booking = datamodule.getBookingInfo(bed.bookingId);
                            if (booking != null) {
                                /* SAHIRE: Do I have to take pending rent from pending table or from Booking table?????? */
                                totalAmount.setText(booking.rentAmount);
                                totalAmount.setSelection(totalAmount.getText().length());

//                                onlineAmt.setText(booking.rentAmount);
//                                onlineAmt.setSelection(onlineAmt.getText().length());

//                                cashAmt.setText(booking.rentAmount);
//                                cashAmt.setSelection(cashAmt.getText().length());
                            }
                        }
                    } else{
                        totalAmount.setText("");
                        totalAmount.setSelection(totalAmount.getText().length());
                    }
                }
            });

            onlineAmt = (EditText) rootView.findViewById(R.id.receipt_te_online_amt);
            cashAmt = (EditText) rootView.findViewById(R.id.receipt_te_cash_amt);
            penaltyAmt = (EditText) rootView.findViewById(R.id.receipt_te_penalty);
            totalAmount = (EditText) rootView.findViewById(R.id.receipt_te_rent);
            onlineCheckBox = (CheckBox) rootView.findViewById(R.id.onlineCheckBox);
            cashCheckBox = (CheckBox) rootView.findViewById(R.id.cashCheckBox);

            if(bedNumber != null) {
                roomNumberText.setText(bedNumber);
                roomNumberText.setSelection(roomNumberText.getText().length());
            }

            onlineAmt.setEnabled(false);
            cashAmt.setEnabled(false);

            penaltySwitch = (CheckBox) rootView.findViewById(R.id.receipt_cb_penalty);
            penaltySwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean isEnabled = penaltyAmt.isEnabled();
                    if (isEnabled) {
                        penaltyAmt.setText("");
                    }
                    penaltyAmt.setEnabled(!isEnabled);
                }
            });

            saveButton = (Button) rootView.findViewById(R.id.receipt_button_save);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!onlineRentChecked && !cashRentChecked){
                        Toast.makeText(myContext, "Please Select Receipt Type", Toast.LENGTH_SHORT).show();
                    } else if(onlineAmt.getText().toString().isEmpty() && cashAmt.getText().toString().isEmpty()) {
                        Toast.makeText(myContext, "Please Enter Receipt Amount", Toast.LENGTH_SHORT).show();
                    } else {
                        //SAHIRE: which all tables to modify???
                        Log.i(TAG, "Amount present");
                    }
                }
            });

            onlineCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){ //checked
                        onlineAmt.setEnabled(true);
                        onlineAmt.setText(totalAmount.getText().toString());
                        onlineAmt.setSelection(onlineAmt.getText().length());
                        onlineAmt.requestFocus();
                        onlineRentChecked = true;

                    }else {
                        onlineAmt.setEnabled(false);
                        onlineAmt.setText("");
                        onlineAmt.setSelection(onlineAmt.getText().length());
                        onlineRentChecked = false;
                    }
                }
            });
            cashCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){ //checked
                        cashAmt.setEnabled(true);
                        cashAmt.setText(totalAmount.getText().toString());
                        cashAmt.setSelection(cashAmt.getText().length());
                        cashAmt.requestFocus();
                        cashRentChecked = true;

                    }else {
                        cashAmt.setEnabled(false);
                        cashAmt.setText("");
                        cashAmt.setSelection(cashAmt.getText().length());
                        cashRentChecked = false;
                    }
                }
            });
//            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ReceiptFragment (defined as a static inner class below).
            return ReceiptFragment.newInstance(position + 1, bedNumber);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
