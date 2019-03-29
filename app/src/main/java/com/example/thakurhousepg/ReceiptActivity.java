package com.example.thakurhousepg;

import android.content.Context;
import android.graphics.Color;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

    private static final String TAG = "ReceiptActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Receipt");

        Bundle bundle = getIntent().getExtras();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        tabLayout.setTabTextColors(Color.WHITE, Color.CYAN);
        String selectedSection = bundle.getString("SECTION");

        if(selectedSection != null) {
            if (selectedSection.equals("Rent")) {
                if (bundle.getString("RENT_AMOUNT").isEmpty() && bundle.getString("DEPOSIT_AMOUNT").isEmpty() == false) {
                    mViewPager.setCurrentItem(1);
                } else {
                    mViewPager.setCurrentItem(0);
                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_payment, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ReceiptFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private EditText roomNumber, totalAmount, onlineAmt, cashAmt;
        private Button saveButton;
        private DataModule dbHelper;

        private CheckBox onlineCheckBox, cashCheckBox, advanceCheckBox;

//        String bookingRent = "", bookingDeposit = "", bookingPenalty = "";
        String dueAmount = "0";

        public ReceiptFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ReceiptFragment newInstance(int sectionNumber) {
            ReceiptFragment fragment = new ReceiptFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);

            Bundle bundle = getActivity().getIntent().getExtras();

            dbHelper = DataModule.getInstance();

            roomNumber = (EditText) rootView.findViewById(R.id.receipt_bed_number);
            onlineAmt = (EditText) rootView.findViewById(R.id.receipt_online_amt);
            cashAmt = (EditText) rootView.findViewById(R.id.receipt_cash_amt);
            totalAmount = (EditText) rootView.findViewById(R.id.receipt_total_amount);

            roomNumber.addTextChangedListener(fieldWatcher);
            onlineAmt.addTextChangedListener(fieldWatcher);
            cashAmt.addTextChangedListener(fieldWatcher);
            totalAmount.addTextChangedListener(fieldWatcher);

            onlineCheckBox = (CheckBox) rootView.findViewById(R.id.onlineCheckBox);
            cashCheckBox = (CheckBox) rootView.findViewById(R.id.cashCheckBox);
            onlineAmt.setEnabled(false);
            cashAmt.setEnabled(false);
            cashAmt.setText("0");

            advanceCheckBox = (CheckBox) rootView.findViewById(R.id.receipt_advance_checkbox);
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2 || getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
                advanceCheckBox.setVisibility(View.INVISIBLE);
            }

            if(bundle.getString("ROOM_NUMBER") != null) {
                roomNumber.setText(bundle.getString("ROOM_NUMBER"));
            }

            reloadDueAmount();

            roomNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.v(TAG, roomNumber.getText().toString());
                }
            });


            cashAmt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null || actionId == EditorInfo.IME_NULL) {
                    Log.v(TAG, v.getText().toString());
                } else {
                    Log.v(TAG, "performed some other action. ID = " + String.valueOf(actionId));
                }
                return true;
                }
            });


            roomNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.v(TAG, roomNumber.getText().toString());

                    /* SAHIRE Fetch Rent for room number from Booking Table */
                    if(roomNumber.getText().toString().isEmpty() == false){
                        Log.v(TAG, "Focus Changed: " + roomNumber.getText().toString());
                        DataModule.Bed bed = dbHelper.getBedInfo(roomNumber.getText().toString());
                        if(bed != null && bed.bookingId != null) {
                            reloadDueAmount();
                            totalAmount.setText(dueAmount);
                            totalAmount.setSelection(totalAmount.getText().length());
                            if(onlineCheckBox.isChecked()){ //checked
                                onlineAmt.setEnabled(true);
                                onlineAmt.setText(totalAmount.getText().toString());
                                onlineAmt.setSelection(onlineAmt.getText().length());
                            }else if(cashCheckBox.isChecked()) {
                                cashAmt.setEnabled(false);
                                cashAmt.setText("0");
                                cashAmt.setSelection(cashAmt.getText().length());
                            }
                        }
                    } else{
                        totalAmount.setText("0");
                        totalAmount.setSelection(totalAmount.getText().length());
                    }
                }
            });

            saveButton = (Button) rootView.findViewById(R.id.receipt_button_save);
            saveButton.setEnabled(false);
            saveButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange (View v, boolean hasFocus) {
                    if(hasFocus) {
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            });
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Save Button Tapped");

                    //SAHIRE: Do we need AlterDialog to confirm Room Payment
                    if(validate()) {
                        DataModule.Bed bedInfo = dbHelper.getBedInfo(roomNumber.getText().toString());

                        DataModule.ReceiptType type = DataModule.ReceiptType.RENT;
                        switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
                            case 2:
                                type = DataModule.ReceiptType.DEPOSIT;
                                break;
                            case 3:
                                type = DataModule.ReceiptType.PENALTY;
                                break;
                        }

                        if (advanceCheckBox.isChecked()) {
                            type = DataModule.ReceiptType.ADVANCE;
                        }

                        //TODO: Check if the cash+online amount exceeds total amount, and ask user if they want to create and advance payment entry
                        dbHelper.createReceipt(type, bedInfo.bookingId, onlineAmt.getText().toString(), cashAmt.getText().toString());
                        if(type != DataModule.ReceiptType.ADVANCE) {
                            dbHelper.updatePendingEntryForBooking(bedInfo.bookingId, type,
                                    String.valueOf(Integer.parseInt(onlineAmt.getText().toString()) + Integer.parseInt(cashAmt.getText().toString())));
                        }

                        BedsListContent.refresh();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Please Enter the Amounts Correctly", Toast.LENGTH_SHORT).show();
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
                    }else {
                        onlineAmt.setEnabled(false);
                        onlineAmt.setText("0");
                        onlineAmt.setSelection(onlineAmt.getText().length());
                    }
                }
            });

            cashCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){ //checked
                        cashAmt.setEnabled(true);
                        cashAmt.setText(String.valueOf(Integer.valueOf(totalAmount.getText().toString()) - Integer.valueOf(onlineAmt.getText().toString())));
                        cashAmt.setSelection(cashAmt.getText().length());
                        cashAmt.requestFocus();

                    }else {
                        cashAmt.setEnabled(false);
                        cashAmt.setText("0");
                        cashAmt.setSelection(cashAmt.getText().length());
                    }
                }
            });


            /*if(bundle.getString("RENT_AMOUNT") != null) */{
//                totalAmount.setText(bundle.getString("RENT_AMOUNT"));
                totalAmount.setText(dueAmount);
                onlineCheckBox.setChecked(true);
            }

            return rootView;
        }

        private boolean validate() {
            boolean isValid = true;

            if(!onlineCheckBox.isChecked() && !cashCheckBox.isChecked()) {
                isValid = false;
            }

            if(onlineAmt.getText().toString().isEmpty() && cashAmt.getText().toString().isEmpty()) {
                isValid = false;
            }

            if(Integer.parseInt(onlineAmt.getText().toString()) + Integer.parseInt(cashAmt.getText().toString())
                    > Integer.parseInt(totalAmount.getText().toString())) {
                if(totalAmount.getText().toString().equals("0") && advanceCheckBox.isChecked()){
                    isValid = true;
                } else {
                    isValid = false;
                }
            }

            return isValid;
        }

        private final TextWatcher fieldWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //TODO: Set TotalAmount to be equal to Online_Cash AMount
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(roomNumber.getText().length() != 0 && totalAmount.getText().length() != 0 &&
                        (onlineAmt.getText().length() != 0 || cashAmt.getText().length() != 0)) {
                    saveButton.setEnabled(true);
                }

            }
        };
        private void reloadDueAmount() {
            String bookingRent = "0", bookingDeposit = "0", bookingPenalty = "0";
            if(roomNumber.getText().toString().isEmpty() == false){
                DataModule.Bed bed = dbHelper.getBedInfo(roomNumber.getText().toString());
                if (bed != null && bed.bookingId != null) {

                    ArrayList<DataModule.Pending> entry = dbHelper.getPendingEntriesForBooking(bed.bookingId);
                    for (DataModule.Pending pendingEntry : entry) {
                        if (pendingEntry.isDeposit) {
                            bookingDeposit = String.valueOf(pendingEntry.pendingAmt);

                        }
                        if (pendingEntry.isPenalty) {
                            bookingPenalty = String.valueOf(pendingEntry.pendingAmt);
                        }
                        if (!pendingEntry.isDeposit && !pendingEntry.isPenalty) {
                            bookingRent = String.valueOf(pendingEntry.pendingAmt);
                        }
                    }
                }
            }
            switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    dueAmount = bookingRent;
                    break;
                case 2:
                    dueAmount = bookingDeposit;
                    break;
                case 3:
                    dueAmount = bookingPenalty;
                    break;
            }
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
            return ReceiptFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
