package com.sanyog.thakurhousepg;

import android.content.Context;
import android.content.Intent;
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

import static com.sanyog.thakurhousepg.Constants.RECEIPT_ACTIVITY_NUM_SECTIONS;
import static com.sanyog.thakurhousepg.Constants.RECEIPT_SECTION_DEPOSIT;
import static com.sanyog.thakurhousepg.Constants.RECEIPT_SECTION_PENALTY;
import static com.sanyog.thakurhousepg.Constants.RECEIPT_SECTION_RENT;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Receipt");

        Bundle bundle = getIntent().getExtras();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(RECEIPT_ACTIVITY_NUM_SECTIONS);
        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        tabLayout.setTabTextColors(Color.WHITE, Color.CYAN);
        String selectedSection = bundle.getString("SECTION");

        if("Rent".equals(selectedSection)) {
            mViewPager.setCurrentItem(RECEIPT_SECTION_RENT - 1);
/*
                YSJ - I JUST DONT UNDERSTAND WHAT IS THIS FOR, COMMENTING IT FOR NOW

                if ((bundleArgument_Rent != null && bundleArgument_Rent.isEmpty()) && (bundleArgument_Deposit != null && !bundleArgument_Deposit.isEmpty())) {
                    mViewPager.setCurrentItem(1);
                } else {
                    mViewPager.setCurrentItem(0);
                }
*/
        } else if("Deposit".equals(selectedSection)) {
            mViewPager.setCurrentItem(RECEIPT_SECTION_DEPOSIT - 1);
        } else {
            mViewPager.setCurrentItem(RECEIPT_SECTION_PENALTY - 1);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        super.onBackPressed();
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
        private EditText roomNumber, totalAmount, onlineAmt, cashAmt;
        private Button saveButton;
        private NetworkDataModule dbHelper;

        private CheckBox onlineCheckBox, cashCheckBox, advanceCheckBox, waiveOffCheckBox;
        private FloatingActionButton fab;

        private String dueAmount = "0";
        private DataModel.Pending pendingEntry = null;

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
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_receipt, container, false);

            Bundle bundle = getActivity().getIntent().getExtras();

            dbHelper = NetworkDataModule.getInstance();

            roomNumber = rootView.findViewById(R.id.receipt_bed_number);
            onlineAmt = rootView.findViewById(R.id.receipt_online_amt);
            cashAmt = rootView.findViewById(R.id.receipt_cash_amt);
            totalAmount = rootView.findViewById(R.id.receipt_total_amount);

            roomNumber.addTextChangedListener(fieldWatcher);
            onlineAmt.addTextChangedListener(fieldWatcher);
            cashAmt.addTextChangedListener(fieldWatcher);
            totalAmount.addTextChangedListener(fieldWatcher);

            onlineCheckBox = rootView.findViewById(R.id.onlineCheckBox);
            cashCheckBox = rootView.findViewById(R.id.cashCheckBox);
            fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

            onlineAmt.setEnabled(false);
            cashAmt.setEnabled(false);
            cashAmt.setText("0");

            advanceCheckBox = rootView.findViewById(R.id.receipt_advance_checkbox);
            waiveOffCheckBox = rootView.findViewById(R.id.waiveOffCheckBox);
            waiveOffCheckBox.setVisibility(View.GONE);
            if (getArguments().getInt(ARG_SECTION_NUMBER) == RECEIPT_SECTION_RENT ){
                advanceCheckBox.setVisibility(View.VISIBLE);
            }
            if (getArguments().getInt(ARG_SECTION_NUMBER) == RECEIPT_SECTION_DEPOSIT ){
                advanceCheckBox.setVisibility(View.INVISIBLE);
            }
            if(getArguments().getInt(ARG_SECTION_NUMBER) == RECEIPT_SECTION_PENALTY) {
                advanceCheckBox.setVisibility(View.GONE);
                waiveOffCheckBox.setVisibility(View.VISIBLE);
            }

            if(bundle.getString("ROOM_NUMBER") != null) {
                roomNumber.setText(bundle.getString("ROOM_NUMBER"));
            }

            if(bundle.getString("PENDING_ID") != null) {
                pendingEntry = dbHelper.getPendingEntryByID(bundle.getString("PENDING_ID"));
                advanceCheckBox.setVisibility(View.INVISIBLE);
                waiveOffCheckBox.setVisibility(View.VISIBLE);
            }

            if(bundle.getString("RECEIPT_MODE") != null &&
                bundle.getString("RECEIPT_MODE").equals("DEPOSIT_CLOSE_BOOKING") &&
                bundle.getString("SECTION").equals("DEPOSIT")) {

                waiveOffCheckBox.setVisibility(View.VISIBLE);
            }

            reloadDueAmount();

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
                    Log.v(TAG, "Section Number: " + getArguments().getInt(ARG_SECTION_NUMBER) + " and room number: " + roomNumber.getText().toString());

                    /* SAHIRE Fetch Rent for room number from Booking Table */
                    if(!roomNumber.getText().toString().isEmpty()){
                        Log.v(TAG, "Focus Changed: " + roomNumber.getText().toString());
                        DataModel.Bed bed = dbHelper.getBedInfo(roomNumber.getText().toString());
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


            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataModel.Bed bedInfo = dbHelper.getBedInfo(roomNumber.getText().toString());
                    if (bedInfo.bookingId == null) {
                        Snackbar.make(view, "Room has not been Booked yet.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        DataModel.Tenant tenant = dbHelper.getTenantInfoForBooking(bedInfo.bookingId);
                        if (!tenant.mobile.isEmpty()) {
                            Snackbar.make(view, "Sending REMINDER SMS to the Tenant: " + tenant.name, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            SMSManagement smsManagement = SMSManagement.getInstance();

                            smsManagement.sendSMS(tenant.mobile,
                                    smsManagement.getSMSMessage(bedInfo.bookingId,
                                            tenant,
                                            0,
                                            SMSManagement.SMS_TYPE.DUE_REMINDER)
                            );
                        } else {
                            Snackbar.make(view, "Mobile number is not updated for Tenant: " + tenant.name, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }
            });

            saveButton = rootView.findViewById(R.id.receipt_button_save);
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

                    SMSManagement.SMS_TYPE smsType = SMSManagement.SMS_TYPE.DEFAULT;
                    //SAHIRE: Do we need AlterDialog to confirm Room Payment
                    if(validate()) {
                        final DataModel.Bed bedInfo = dbHelper.getBedInfo(roomNumber.getText().toString());

                        DataModel.ReceiptType type = DataModel.ReceiptType.RENT;

                        switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
                            case RECEIPT_SECTION_DEPOSIT:
                                type = DataModel.ReceiptType.DEPOSIT;
                                smsType = SMSManagement.SMS_TYPE.DEFAULT;
                                break;
                            case RECEIPT_SECTION_PENALTY:
                                type = DataModel.ReceiptType.PENALTY;
                                smsType = SMSManagement.SMS_TYPE.PENALTY_GENERATED;
                                break;
                        }

                        if (advanceCheckBox.isChecked()) {
                            type = DataModel.ReceiptType.ADVANCE;
                        }

                        NetworkDataModuleCallback<DataModel.Pending> updatePendingEntryCallBack = new NetworkDataModuleCallback<DataModel.Pending>() {
                            @Override
                            public void onSuccess(DataModel.Pending obj) {
                                Toast.makeText(getActivity(), "Receipt Generated.", Toast.LENGTH_SHORT).show();
                                BedsListContent.refresh();
                                sendSMSAndFInish();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getActivity(), "Receipt Generation Failed.", Toast.LENGTH_SHORT).show();
                            }
                        };

                        if (pendingEntry != null) {
                            dbHelper.createReceiptForPendingEntry(pendingEntry.id, onlineAmt.getText().toString(), cashAmt.getText().toString(),
                                    (type == DataModel.ReceiptType.PENALTY && waiveOffCheckBox.isChecked()), new NetworkDataModuleCallback<DataModel.DataModelClass>() {
                                        @Override
                                        public void onSuccess(DataModel.DataModelClass obj) {
                                            Toast.makeText(getActivity(), "Receipt Generated.", Toast.LENGTH_SHORT).show();
                                            BedsListContent.refresh();
                                            sendSMSAndFInish();
                                        }

                                        @Override
                                        public void onFailure() {
                                            Toast.makeText(getActivity(), "Receipt Generation Failed.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            //TODO: Check if the cash+online amount exceeds total amount, and ask user if they want to create an advance payment entry
                            dbHelper.createReceipt(type, bedInfo.bookingId, onlineAmt.getText().toString(), cashAmt.getText().toString(),
                                    (type == DataModel.ReceiptType.PENALTY && waiveOffCheckBox.isChecked()), new NetworkDataModuleCallback<DataModel.Receipt>() {
                                        @Override
                                        public void onSuccess(DataModel.Receipt obj) {
                                            if (obj.type != DataModel.ReceiptType.ADVANCE) {
                                                dbHelper.updatePendingEntryForBooking(bedInfo.bookingId, DataModel.PendingType.values()[obj.type.getIntValue()],
                                                        String.valueOf(Integer.parseInt(onlineAmt.getText().toString()) + Integer.parseInt(cashAmt.getText().toString())),
                                                        updatePendingEntryCallBack);
                                            }
                                        }

                                        @Override
                                        public void onFailure() {

                                        }
                                    });
                        }
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

                        if(onlineAmt.getText().toString().isEmpty())
                            onlineAmt.setText("0");
                        if(totalAmount.getText().toString().isEmpty())
                            totalAmount.setText("0");

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

            totalAmount.setText(dueAmount);
            onlineCheckBox.setChecked(true);


            return rootView;
        }

        private boolean validate() {
            boolean isValid = true;

            DataModel.Bed bedInfo = dbHelper.getBedInfo(roomNumber.getText().toString());
            if(bedInfo == null && pendingEntry != null) {
                Toast.makeText(getActivity(), "Please enter a correct bed number", Toast.LENGTH_SHORT).show();
                isValid = false;
                return isValid;
            } else if(bedInfo.bookingId == null && pendingEntry != null) {
                Toast.makeText(getActivity(), "No booking exists for this Room number", Toast.LENGTH_SHORT).show();
                isValid = false;
                return isValid;
            }

            if(!onlineCheckBox.isChecked() && !cashCheckBox.isChecked()) {
                Toast.makeText(getActivity(), "Please select either online or cash amount check box", Toast.LENGTH_SHORT).show();
                isValid = false;
                return isValid;
            }

            if((onlineCheckBox.isChecked() && onlineAmt.getText().toString().isEmpty()) ||
                    (cashCheckBox.isChecked() && cashAmt.getText().toString().isEmpty())) {
                Toast.makeText(getActivity(), "Please enter a amount greater than 0", Toast.LENGTH_SHORT).show();
                isValid = false;
                return isValid;
            }

            if(Integer.parseInt(onlineAmt.getText().toString()) + Integer.parseInt(cashAmt.getText().toString())
                    > Integer.parseInt(totalAmount.getText().toString())) {
                if(!advanceCheckBox.isChecked() && totalAmount.getText().toString().equals("0")){
                    isValid = true;
                } else {
                    Toast.makeText(getActivity(), "Please enter correct amount. If your total is more cash + online please select the Advance payment checkbox", Toast.LENGTH_LONG).show();
                    isValid = false;
                    return isValid;
                }
            }

            if (Integer.parseInt(onlineAmt.getText().toString()) == 0 && Integer.parseInt(cashAmt.getText().toString()) == 0) {
                Toast.makeText(getActivity(), "Please Make sure that either Online or Cash text box has Non-zero amount. ", Toast.LENGTH_LONG).show();
                isValid = false;
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

            if(!roomNumber.getText().toString().isEmpty()) {
                DataModel.Bed bed = dbHelper.getBedInfo(roomNumber.getText().toString());
                ArrayList<DataModel.Pending> entry = null;

                if (bed != null && bed.bookingId != null) {
                    entry = dbHelper.getPendingEntriesForBooking(bed.bookingId);
                } else if(pendingEntry != null) {
                    entry = new ArrayList<DataModel.Pending>();
                    entry.add(pendingEntry);
                }

                for (DataModel.Pending pending : entry) {
                    if (pending.type == DataModel.PendingType.DEPOSIT) {
                        bookingDeposit = String.valueOf(pending.pendingAmt);
                    }
                    if (pending.type == DataModel.PendingType.PENALTY) {
                        bookingPenalty = String.valueOf(pending.pendingAmt);
                    }
                    if (pending.type == DataModel.PendingType.RENT) {
                        bookingRent = String.valueOf(pending.pendingAmt);
                    }
                }
            }

            switch(getArguments().getInt(ARG_SECTION_NUMBER)) {
                case RECEIPT_SECTION_RENT:
                    dueAmount = bookingRent;
                    break;
                case RECEIPT_SECTION_DEPOSIT:
                    dueAmount = bookingDeposit;
                    break;
                case RECEIPT_SECTION_PENALTY:
                    dueAmount = bookingPenalty;
                    break;
            }
        }

        private void sendSMSAndFInish() {
            SMSManagement smsManagement = SMSManagement.getInstance();
            DataModel.Bed bedInfo = dbHelper.getBedInfo(roomNumber.getText().toString());

            DataModel.Tenant tenant = dbHelper.getTenantInfoForBooking((pendingEntry != null) ? pendingEntry.bookingId : bedInfo.bookingId);
            /* mobile Number should be mandatory */
            if(!tenant.mobile.isEmpty()) {
                //SAHIRE : Handle waive-off payments.
                smsManagement.sendSMS(tenant.mobile,
                        smsManagement.getSMSMessage(bedInfo.bookingId,
                                tenant,
                                Integer.valueOf(onlineAmt.getText().toString()) + Integer.valueOf(cashAmt.getText().toString()),
                                SMSManagement.SMS_TYPE.RECEIPT)
                );
            }
            BedsListContent.refresh();
            Intent returnIntent = new Intent();
            getActivity().setResult(RESULT_OK, returnIntent);
            getActivity().finish();
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
            return RECEIPT_ACTIVITY_NUM_SECTIONS;
        }
    }
}
