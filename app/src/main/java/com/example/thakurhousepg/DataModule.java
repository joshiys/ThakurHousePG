package com.example.thakurhousepg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class DataModule extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ThakurHouse.db";

    public static final String TENANT_TABLE_NAME = "tenant_table";
    public static final String PENDING_AMOUNT_TABLE_NAME = "pending_table";
    public static final String BEDS_TABLE_NAME = "beds_table";
    public static final String RECEIPTS_TABLE_NAME = "receipts_table";
    public static final String BOOKING_TABLE_NAME = "bookings_table";


    private static final String TENANT_ID = "TENANT_ID";
    private static final String TENANT_NAME = "TENANT_NAME";
    private static final String TENANT_EMAIL = "TENANT_EMAIL";
    private static final String TENANT_MOBILE = "TENANT_MOBILE";
    private static final String TENANT_MOBILE_2 = "TENANT_MOBILE_2";
    private static final String TENANT_ADDRESS = "TENANT_ADDRESS";
    private static final String TENANT_IS_CURRENT = "TENANT_IS_CURRENT";
    private static final String PARENT_TENANT_ID = "PARENT_TENANT_ID";

    private static final String BOOKING_ID = "BOOKING_ID";
    private static final String BOOKING_RENT_AMT = "BOOKING_RENT_AMT";
    private static final String BOOKING_DEPOSIT_AMT = "BOOKING_DEPOSIT_AMT";
    private static final String BOOKING_DATE = "BOOKING_DATE";
    private static final String BOOKING_IS_WHOLE_ROOM = "BOOKING_IS_WHOLE_ROOM";
    private static final String BOOKING_CLOSE_DATE = "BOOKING_CLOSE_DATE";

    private static final String PENDING_TYPE = "PENDING_TYPE";
    private static final String PENDING_MONTH = "PENDING_MONTH";//Month number 1..12
    private static final String PENDING_AMOUNT = "PENDING_AMOUNT";

    private static final String BED_NUMBER = "BED_NUMBER";
    private static final String BED_DEPOSIT = "BED_DEPOSIT";
    private static final String BED_RENT = "BED_RENT";
    private static final String BED_IS_OCCUPIED = "BED_IS_OCCUPIED";

    private static final String RECEIPT_ID = "RECEIPT_ID";
    private static final String RECEIPT_ONLINE_AMOUNT = "RECEIPT_ONLINE_AMOUNT";
    private static final String RECEIPT_CASH_AMOUNT = "RECEIPT_CASH_AMOUNT";
    private static final String RECEIPT_DATE = "RECEIPT_DATE";
    private static final String RECEIPT_TYPE = "RECEIPT_TYPE";

    //XXX : We might just need flag if the receipt is paid or waived off
    private static final String RECEIPT_PENALTY_WAIVE_OFF = "RECEIPT_PENALTY_WAIVE_OFF";

    private static final String TAG = "DataModule";

    private static DataModule _instance = null;
    private static Context _context = null;
    private SQLiteDatabase theDatabase;

    // Use this to convert int values to actual enum values
    private final DataModel.ReceiptType[] receiptTypeValues = DataModel.ReceiptType.values();
    private final DataModel.PendingType[] pendingTypeValues = DataModel.PendingType.values();


    //Callers  must make sure that context is set first using DataModule.setContext()
    public static DataModule getInstance() {
        if(_instance == null && _context != null) {
            //TODO: remove this try/catch, and make this method throw an IOException.
            //All the calling code should then be modified to handle that exception.
            try {
                _instance = new DataModule(_context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return _instance;
    }

    public static void setContext(Context context) {
        _context = context;
    }

    public DataModule(Context context) throws IOException {
        super(context, DATABASE_NAME, null, 1);

        createDataBase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "In Database onCreate. What to do here ???");
    }

    // Keeping this here so that we have the DB structure handy whenever needed
    public void OldonCreate(SQLiteDatabase db) {
        String query;

        query = "create table IF NOT EXISTS " + BEDS_TABLE_NAME +
                " (BED_NUMBER TEXT PRIMARY KEY," +
                " BED_RENT INTEGER NOT NULL," +
                " BED_DEPOSIT INTEGER NOT NULL," +
                " BED_IS_OCCUPIED BOOLEAN," +
                " BOOKING_ID INTEGER," +
                 " FOREIGN KEY (BOOKING_ID) REFERENCES " + BOOKING_TABLE_NAME + "(BOOKING_ID))";
        db.execSQL(query);


        query = "create table IF NOT EXISTS " + TENANT_TABLE_NAME +
                " (TENANT_ID INTEGER PRIMARY KEY," +
                " TENANT_NAME TEXT," +
                " TENANT_EMAIL TEXT," +
                " TENANT_MOBILE TEXT," +
                " TENANT_MOBILE_2 TEXT," +
                " TENANT_ADDRESS TEXT," +
                " TENANT_IS_CURRENT BOOLEAN," +
                " PARENT_TENANT_ID INTEGER," +
                " UNIQUE (TENANT_NAME, TENANT_MOBILE) ON CONFLICT ABORT" +
                ")";
        db.execSQL(query);


        query = "create table IF NOT EXISTS " + BOOKING_TABLE_NAME +
                " (BOOKING_ID INTEGER PRIMARY KEY," +
                " BOOKING_RENT_AMT INTEGER NOT NULL," +
                " BOOKING_DEPOSIT_AMT INTEGER NOT NULL," +
                " BOOKING_DATE DATE NOT NULL," +
                " BOOKING_IS_WHOLE_ROOM BOOLEAN NOT NULL," +
                " BOOKING_CLOSE_DATE TEXT," +
                " TENANT_ID INTEGER," +
                " BED_NUMBER TEXT," +
                " FOREIGN KEY (TENANT_ID) REFERENCES " + TENANT_TABLE_NAME + "(TENANT_ID)" +
                ")";
        db.execSQL(query);


        query = "create table IF NOT EXISTS " + RECEIPTS_TABLE_NAME +
                " (RECEIPT_ID INTEGER PRIMARY KEY," +
                " RECEIPT_ONLINE_AMOUNT INTEGER," +
                " RECEIPT_CASH_AMOUNT INTEGER," +
                " RECEIPT_DATE TEXT," +
                " RECEIPT_TYPE INTEGER," +
                " RECEIPT_PENALTY_WAIVE_OFF BOOLEAN," +
                " BOOKING_ID INTEGER," +
                " FOREIGN KEY (BOOKING_ID) REFERENCES " + BOOKING_TABLE_NAME + "(BOOKING_ID)" +
                ")";
        db.execSQL(query);



        //Create Pending table
        query = "create table IF NOT EXISTS " + PENDING_AMOUNT_TABLE_NAME +
                "(PENDING_AMOUNT INTEGER," +
                " PENDING_TYPE INTEGER," +
                " PENDING_MONTH INTEGER," +
                " BOOKING_ID INTEGER," +
                " TENANT_ID INTEGER," +
                " FOREIGN KEY (BOOKING_ID) REFERENCES " + BOOKING_TABLE_NAME + "(BOOKING_ID)" +
                ")";
        db.execSQL(query);
    }

    public void createDataBase() throws IOException{

        SQLiteDatabase checkDB = null;

        try{
            String fullDBName = _context.getDatabasePath(DATABASE_NAME).toString();
            checkDB = SQLiteDatabase.openDatabase(fullDBName, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){
            //database does't exist yet. By calling this method, an empty database will be created into the default system path
            // And then we will copy our Asset database onto this database
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException copyException) {
                throw new Error("Error copying database");
            }
        }

    }

    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream dbInputStream = _context.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = _context.getDatabasePath(DATABASE_NAME).toString();

        //Open the empty db as the output stream
        OutputStream dbOutputStream = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = dbInputStream.read(buffer))>0){
            dbOutputStream.write(buffer, 0, length);
        }

        //Close the streams
        dbOutputStream.flush();
        dbOutputStream.close();
        dbInputStream.close();
    }

    public void copyDatabaseToExternalStorage() throws IOException {
        this.close();
        InputStream dbInputStream = new FileInputStream(_context.getDatabasePath(DATABASE_NAME).toString());
        OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/"+ DIRECTORY_DOWNLOADS + "/thakurhouse.db");

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = dbInputStream.read(buffer))>0){
            output.write(buffer, 0, length);
        }

        // Close the streams
        output.flush();
        output.close();
        dbInputStream.close();
    }

    public void loadDatabaseFromExternalStorage() throws IOException {
        this.close();

        InputStream dbInputStream = new FileInputStream(Environment.getExternalStorageDirectory() + "/"+ DIRECTORY_DOWNLOADS + "/thakurhouse.db");
        OutputStream output = new FileOutputStream(_context.getDatabasePath(DATABASE_NAME).toString());

        // Transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = dbInputStream.read(buffer))>0){
            output.write(buffer, 0, length);
        }

        // Close the streams
        output.flush();
        output.close();
        dbInputStream.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /* Should ONLY be called on the first launch of the app every month.
     * This function DOES NOT make date validations. MUST be done by the caller. */
    public boolean createMonthlyPendingEntries(boolean smsPermission) {
        boolean opSuccess = false;

        ArrayList<DataModel.Booking> bookings = getCurrentBookings();

        for (DataModel.Booking booking: bookings) {
            int rentAmount = Integer.parseInt(booking.rentAmount);
            // First check, if last month, there has been an advance Receipt for this Booking
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + RECEIPTS_TABLE_NAME + " WHERE BOOKING_ID = ?" + " and RECEIPT_TYPE = ?" +
                    " and strftime('%m',date('now', '-1 month')) = strftime('%m', RECEIPT_DATE)",
                    new String[]{booking.id, String.valueOf(DataModel.ReceiptType.ADVANCE.getIntValue())});

            while (cursor.moveToNext())
                rentAmount -= cursor.getInt(cursor.getColumnIndex(RECEIPT_ONLINE_AMOUNT)) + cursor.getInt(cursor.getColumnIndex(RECEIPT_CASH_AMOUNT));

            opSuccess = createPendingEntryForBooking(booking.id, DataModel.PendingType.RENT, String.valueOf(rentAmount), Calendar.getInstance().get(Calendar.MONTH) + 1);

            if(smsPermission) {
                DataModel.Tenant tenant = DataModule.getInstance().getTenantInfoForBooking(booking.id);
                if (tenant.mobile.isEmpty() == false) {
                    SMSManagement smsManagement = SMSManagement.getInstance();

                    smsManagement.sendSMS(tenant.mobile,
                            DataModule.getInstance().getSMSMessage(booking.id,
                                    tenant,
                                    0,
                                    SMSManagement.SMS_TYPE.MONTHLY_RENT)
                    );
                }
            }
        }

        return opSuccess;
    }

    public boolean createPendingEntryForBooking(String id, DataModel.PendingType type, String amount, int month) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        boolean opSuccess = false;

        contentValues.put(PENDING_AMOUNT, amount);
        contentValues.put(BOOKING_ID, id);
        contentValues.put(PENDING_TYPE, type.getIntValue());
        contentValues.put(PENDING_MONTH, month);

        String query = "select * from " + PENDING_AMOUNT_TABLE_NAME + " where BOOKING_ID = ? AND PENDING_TYPE = ?";
        Cursor checkRecord = db.rawQuery(query, new String[]{id, String.valueOf(type.getIntValue())});
        if(checkRecord.moveToNext()) {
            int totalPendingAmount = checkRecord.getInt(checkRecord.getColumnIndex("PENDING_AMOUNT"));
            totalPendingAmount += Integer.parseInt(amount);

            contentValues.put(PENDING_AMOUNT, totalPendingAmount);

            db.update(PENDING_AMOUNT_TABLE_NAME, contentValues, BOOKING_ID + " = ? AND PENDING_TYPE = ?" , new String[]{id, String.valueOf(type.getIntValue())});
        } else if(db.insert(PENDING_AMOUNT_TABLE_NAME, null, contentValues) != -1) {
            opSuccess = true;
        }

        return opSuccess;
    }

    public void updatePendingEntryForBooking(String id, DataModel.ReceiptType type, String amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(BOOKING_ID, id);

        String query = "select * from " + PENDING_AMOUNT_TABLE_NAME + " where BOOKING_ID = ? AND PENDING_TYPE = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id, String.valueOf(type.getIntValue())});
        if (cursor.moveToNext()) {
            int totalPendingAmount = cursor.getInt(cursor.getColumnIndex("PENDING_AMOUNT"));
            totalPendingAmount -= Integer.parseInt(amount);

            if (totalPendingAmount <= 0) {
                db.delete(PENDING_AMOUNT_TABLE_NAME, BOOKING_ID + " = ? AND PENDING_TYPE = ?" , new String[]{id, String.valueOf(type.getIntValue())});
            } else {
                contentValues.put(PENDING_AMOUNT, totalPendingAmount);
                int numRowsUpdated = db.update(PENDING_AMOUNT_TABLE_NAME, contentValues, BOOKING_ID + " = ? AND PENDING_TYPE = ?", new String[]{id, String.valueOf(type.getIntValue())});
                Log.d(TAG, "Updated " + String.valueOf(numRowsUpdated) + "pending table rows");
            }
        }

        cursor.close();
    }

    public boolean addNewBed(String bedNumber, String rent, String deposit) {
        Boolean opSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(BED_NUMBER, bedNumber);
        contentValues.put(BED_RENT, rent);
        contentValues.put(BED_DEPOSIT, deposit);
        contentValues.put(BED_IS_OCCUPIED, false);

        String query = "select * from " + BEDS_TABLE_NAME + " where BED_NUMBER = " + bedNumber;
        Cursor checkRecord = db.rawQuery(query, null);

        if (checkRecord.getCount() == 0) {
            long result = db.insert(BEDS_TABLE_NAME, null, contentValues);
            if (result != -1) {
                Log.i(TAG, "Successfully added new Bed. Number: " + bedNumber);
                opSuccess = true;
            } else {
                Log.i(TAG, "Can not add new Bed Number: " + bedNumber);
            }
        }

        checkRecord.close();
        return opSuccess;
    }

    public String addNewTenant(String name, String mobile, String mobile2, String email, String address, String parentId) {
        Boolean opSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Integer highestTenantId = createNewTenantId();

        contentValues.put(TENANT_ID, highestTenantId);

        contentValues.put(TENANT_NAME, name);
        contentValues.put(TENANT_EMAIL, email);
        contentValues.put(TENANT_MOBILE, mobile);
        contentValues.put(TENANT_MOBILE_2, mobile2);
        contentValues.put(TENANT_ADDRESS, address);
        contentValues.put(TENANT_IS_CURRENT, false);
        contentValues.put(PARENT_TENANT_ID, parentId);


        String query = "select * from " + TENANT_TABLE_NAME + " where TENANT_ID = " + highestTenantId;
        Cursor checkRecord = db.rawQuery(query, null);

        if (checkRecord.getCount() == 0) {
            long result = db.insert(TENANT_TABLE_NAME, null, contentValues);
            if (result != -1) {
                opSuccess = true;
            } else {
                Log.i(TAG, "Cannot insert tenant");
            }

        } else {
            Log.i(TAG, "tenant already exists");
        }

        checkRecord.close();
        return opSuccess ? highestTenantId.toString() : null;
    }

    public boolean updateTenant(String id, String name, String mobile, String mobile2, String email, String address, Boolean isCurrent, @NotNull String parentId) {
        Boolean opSuccess = false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(!name.isEmpty()) contentValues.put(TENANT_NAME, name);
        if(!email.isEmpty()) contentValues.put(TENANT_EMAIL, email);
        if(!mobile.isEmpty()) contentValues.put(TENANT_MOBILE, mobile);
        if(!mobile2.isEmpty()) contentValues.put(TENANT_MOBILE_2, mobile2);
        if(!address.isEmpty()) contentValues.put(TENANT_ADDRESS, address);
        contentValues.put(TENANT_IS_CURRENT, isCurrent != null ? isCurrent : false);
        //if(!parentId.isEmpty()) contentValues.put(PARENT_TENANT_ID, parentId);


        String query = "select * from " + TENANT_TABLE_NAME + " where TENANT_ID = " + id;
        Cursor checkRecord = db.rawQuery(query, null);

        if (checkRecord.getCount() != 0) {
            /* Keep Original parent id */
            checkRecord.moveToNext();
            contentValues.put(PARENT_TENANT_ID, checkRecord.getString(checkRecord.getColumnIndex(PARENT_TENANT_ID)));
            db.update(TENANT_TABLE_NAME, contentValues, "TENANT_ID = ?", new String[]{id.toString()});
            opSuccess = true;
        }

        checkRecord.close();

        return opSuccess;
    }

    //TODO: Validation Needed urgently, Duplicate bookings can be made right now, messing the data for rest of the activities
    public String createNewBooking(String bedNumber, String tenantId, String rent, String deposit, String admissionDate) {
        Integer bookingId = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor checkRecord;

        if (!bedNumber.isEmpty()) contentValues.put(BED_NUMBER, bedNumber);
        if (!tenantId.isEmpty()) contentValues.put(TENANT_ID, tenantId);
        if (!rent.isEmpty()) contentValues.put(BOOKING_RENT_AMT, rent);
        if (!deposit.isEmpty()) contentValues.put(BOOKING_DEPOSIT_AMT, deposit);
        if (!admissionDate.isEmpty())
            contentValues.put(BOOKING_DATE, admissionDate);
        else
            contentValues.put(BOOKING_DATE, admissionDate); // current date

        contentValues.put(BOOKING_IS_WHOLE_ROOM, false);

        // Check If the Bed is already booked
        checkRecord = db.rawQuery("select * from " + BEDS_TABLE_NAME + " where BED_IS_OCCUPIED = ? AND BED_NUMBER = ?", new String[]{"1", bedNumber});

        if (checkRecord.getCount() != 0) {
            Log.i(TAG, "CreateNewBooking: Can not book, the room is already booked");
            checkRecord.close();
            return bookingId.toString();
        }

        // Check if there is already a Booking for this TENANT_ID
        checkRecord.close();
        checkRecord = db.rawQuery("select BOOKING_ID from " + BOOKING_TABLE_NAME + " where TENANT_ID = ? AND BOOKING_CLOSE_DATE is null", new String[]{tenantId});

        if (checkRecord.getCount() == 0) {
            bookingId = createNewBookingId();
            contentValues.put(BOOKING_ID, bookingId);
            long result = db.insert(BOOKING_TABLE_NAME, null, contentValues);

            if(result != -1) {
                contentValues.clear();
                contentValues.put(BED_IS_OCCUPIED, true);
                contentValues.put(BOOKING_ID, bookingId);
                int numRows = db.update(BEDS_TABLE_NAME, contentValues, "BED_NUMBER = ?", new String[]{bedNumber});
                Log.v(TAG, "CreateNewBooking: Updated BEDS table, rows affected - " + String.valueOf(numRows));

                contentValues.clear();
                contentValues.put(TENANT_IS_CURRENT, true);
                numRows = db.update(TENANT_TABLE_NAME, contentValues, "TENANT_ID = ?", new String[]{tenantId});
                Log.v(TAG, "CreateNewBooking: Updated TENANT table, rows affected - " + String.valueOf(numRows));

            } else {
                Log.i(TAG, "CreateNewBooking: Insert failed");
                bookingId = -1;
            }

        }

        checkRecord.close();

        return bookingId.toString();
    }

    public boolean updateBooking(String id, String newRent, String newDeposit) {
        Boolean opSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(BOOKING_ID, id);
        if (!newRent.isEmpty()) contentValues.put(BOOKING_RENT_AMT, newRent);
        if (!newDeposit.isEmpty()) contentValues.put(BOOKING_DEPOSIT_AMT, newDeposit);

        db.update(BOOKING_TABLE_NAME, contentValues, BOOKING_ID + " = ?", new String[]{id});

        return opSuccess;
    }

    public boolean closeBooking(String id, String closeingDate) {
        Boolean opSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(BOOKING_CLOSE_DATE, closeingDate);
        Cursor checkRecord = db.rawQuery("select TENANT_ID from " + BOOKING_TABLE_NAME + " where " + BOOKING_ID + " = " + id, null);
        if (checkRecord.getCount() != 0) {
            db.update(BOOKING_TABLE_NAME, contentValues, "BOOKING_ID = ?", new String[]{id});

            checkRecord.moveToNext();
            String tenantId = checkRecord.getString(checkRecord.getColumnIndex(TENANT_ID));
            contentValues.clear();
            contentValues.put("TENANT_IS_CURRENT", false);
            db.update(TENANT_TABLE_NAME, contentValues, "TENANT_ID = ?", new String[]{tenantId});

            //If there are any dependents on this Tenant Id, Remove their Parent Id
            contentValues.put(PARENT_TENANT_ID, "0");
            db.update(TENANT_TABLE_NAME, contentValues, "PARENT_TENANT_ID = ?", new String[]{tenantId});

            contentValues.clear();
            contentValues.putNull(BOOKING_ID);
            contentValues.put(BED_IS_OCCUPIED, false);

            checkRecord = db.rawQuery("select BED_NUMBER from " + BEDS_TABLE_NAME + " where BOOKING_ID = ?", new String[]{id});
            if(checkRecord.moveToNext()) {
                String bedNumber = checkRecord.getString(checkRecord.getColumnIndex(BED_NUMBER));
                db.update(BEDS_TABLE_NAME, contentValues, "BED_NUMBER = ?", new String[]{bedNumber});
                opSuccess = true;
            }
        }

        checkRecord.close();

        return opSuccess;
    }

    private Integer createNewTenantId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Integer highestTenantId = 1;

        Cursor highestIdCursor = db.rawQuery("select * from " + TENANT_TABLE_NAME + " ORDER BY TENANT_ID DESC LIMIT 1", null);
        if(highestIdCursor.getCount() != 0) {
            highestIdCursor.moveToNext();
            highestTenantId = highestIdCursor.getInt(highestIdCursor.getColumnIndex(TENANT_ID));
            highestTenantId += 1;
        }

        highestIdCursor.close();

        return highestTenantId;
    }

    private Integer createNewBookingId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Integer highestTenantId = 1;

        Cursor highestIdCursor = db.rawQuery("select * from " + BOOKING_TABLE_NAME + " ORDER BY BOOKING_ID DESC LIMIT 1", null);
        if(highestIdCursor.getCount() != 0) {
            highestIdCursor.moveToNext();
            highestTenantId = highestIdCursor.getInt(highestIdCursor.getColumnIndex(BOOKING_ID));
            highestTenantId += 1;
        }

        highestIdCursor.close();

        return highestTenantId;
    }


    public ArrayList<DataModel.Bed> getBedsList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataModel.Bed> beds = new ArrayList<DataModel.Bed>();
        Cursor cursor = db.rawQuery("select * from " + BEDS_TABLE_NAME + " ORDER BY BED_NUMBER ASC", null);

        if(cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                beds.add(new DataModel.Bed(
                        cursor.getString(cursor.getColumnIndex(BED_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                        cursor.getString(cursor.getColumnIndex(BED_RENT)),
                        cursor.getString(cursor.getColumnIndex(BED_DEPOSIT)),
                        cursor.getInt(cursor.getColumnIndex(BED_IS_OCCUPIED)) > 0
                ));
            }
        }

        cursor.close();
        return beds;
    }

    public ArrayList<DataModel.Booking> getCurrentBookings() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataModel.Booking> bookings = new ArrayList<DataModel.Booking>();
        Cursor cursor = db.rawQuery("select * from " + BOOKING_TABLE_NAME + " where " + BOOKING_CLOSE_DATE + " is null", null);
        while (cursor.moveToNext()) {
            bookings.add(new DataModel.Booking(
                            cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                            cursor.getString(cursor.getColumnIndex(BED_NUMBER)),
                            cursor.getString(cursor.getColumnIndex(BOOKING_RENT_AMT)),
                            cursor.getString(cursor.getColumnIndex(BOOKING_DEPOSIT_AMT)),
                            cursor.getString(cursor.getColumnIndex(BOOKING_DATE)),
                            cursor.getInt(cursor.getColumnIndex(BOOKING_IS_WHOLE_ROOM)) > 0,
                            cursor.getString(cursor.getColumnIndex(TENANT_ID))
                    ));
        }

        cursor.close();
        return bookings;
    }

    //TODO: Validation
    public DataModel.Bed getBedInfo(String forBedNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        DataModel.Bed bed = null;
        Cursor cursor = db.rawQuery("select * from " + BEDS_TABLE_NAME + " where BED_NUMBER = ?", new String[]{forBedNumber});

        if (cursor.moveToNext()) {
            bed = new DataModel.Bed(
                    cursor.getString(cursor.getColumnIndex(BED_NUMBER)),
                    cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndex(BED_RENT)),
                    cursor.getString(cursor.getColumnIndex(BED_DEPOSIT)),
                    cursor.getInt(cursor.getColumnIndex(BED_IS_OCCUPIED )) > 0
            );
        }

        return bed;
    }

    public DataModel.Booking getBookingInfo(String forId) {
        SQLiteDatabase db = this.getReadableDatabase();
        DataModel.Booking booking = null;
        Cursor cursor = db.rawQuery("select * from " + BOOKING_TABLE_NAME + " where BOOKING_ID = ?", new String[]{forId});
        if (cursor.moveToNext()) {
            booking = new DataModel.Booking(
                    cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndex(BED_NUMBER)),
                    cursor.getString(cursor.getColumnIndex(BOOKING_RENT_AMT)),
                    cursor.getString(cursor.getColumnIndex(BOOKING_DEPOSIT_AMT)),
                    cursor.getString(cursor.getColumnIndex(BOOKING_DATE)),
                    cursor.getInt(cursor.getColumnIndex(BOOKING_IS_WHOLE_ROOM)) > 0,
                    cursor.getString(cursor.getColumnIndex(TENANT_ID))
            );
        }

        cursor.close();
        return booking;
    }

    public ArrayList<DataModel.Booking> getAllBookingInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataModel.Booking> bookingList = new ArrayList<>();
        Cursor bookingCursor = db.rawQuery("select * from " + BOOKING_TABLE_NAME + " ORDER BY BED_NUMBER ASC", null);
        while (bookingCursor.moveToNext()) {
            bookingList.add(new DataModel.Booking(
                    bookingCursor.getString(bookingCursor.getColumnIndex(BOOKING_ID)),
                    bookingCursor.getString(bookingCursor.getColumnIndex(BED_NUMBER)),
                    bookingCursor.getString(bookingCursor.getColumnIndex(BOOKING_RENT_AMT)),
                    bookingCursor.getString(bookingCursor.getColumnIndex(BOOKING_DEPOSIT_AMT)),
                    bookingCursor.getString(bookingCursor.getColumnIndex(BOOKING_DATE)),
                    bookingCursor.getInt(bookingCursor.getColumnIndex(BOOKING_IS_WHOLE_ROOM)) > 0,
                    bookingCursor.getString(bookingCursor.getColumnIndex(TENANT_ID)))
            );
        }

        bookingCursor.close();
        return bookingList;
    }


    public DataModel.Tenant getTenantInfoForBooking(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        DataModel.Tenant t = null;
        Cursor cursor = db.rawQuery("select * from " + TENANT_TABLE_NAME +
                        " LEFT JOIN " + BOOKING_TABLE_NAME + " ON " + BOOKING_TABLE_NAME + ".TENANT_ID = " + TENANT_TABLE_NAME + ".TENANT_ID" +
                        " where BOOKING_ID = ?", new String[]{id});

        if(cursor.moveToNext()) {
            t = new DataModel.Tenant(
                    cursor.getString(cursor.getColumnIndex(TENANT_ID)),
                    cursor.getString(cursor.getColumnIndex(TENANT_NAME)),
                    cursor.getString(cursor.getColumnIndex(TENANT_MOBILE)),
                    cursor.getString(cursor.getColumnIndex(TENANT_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(TENANT_ADDRESS)),
                    cursor.getInt(cursor.getColumnIndex(TENANT_IS_CURRENT)) > 0,
                    cursor.getString(cursor.getColumnIndex(PARENT_TENANT_ID))
                    );
        }

        cursor.close();
        return t;
    }

    public DataModel.Booking getBookingInfoForTenant(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
//        ArrayList<Booking> bookingList = new ArrayList<>();
        DataModel.Booking booking = null;
        Cursor bookingCursor = db.rawQuery("select * from " + BOOKING_TABLE_NAME + " where TENANT_ID = ?" + " ORDER BY " + BOOKING_ID + " ASC", new String[]{id});
        if (bookingCursor.moveToNext()) {
            booking = new DataModel.Booking(
                    bookingCursor.getString(bookingCursor.getColumnIndex(BOOKING_ID)),
                    bookingCursor.getString(bookingCursor.getColumnIndex(BED_NUMBER)),
                    bookingCursor.getString(bookingCursor.getColumnIndex(BOOKING_RENT_AMT)),
                    bookingCursor.getString(bookingCursor.getColumnIndex(BOOKING_DEPOSIT_AMT)),
                    bookingCursor.getString(bookingCursor.getColumnIndex(BOOKING_DATE)),
                    bookingCursor.getInt(bookingCursor.getColumnIndex(BOOKING_IS_WHOLE_ROOM)) > 0,
                    bookingCursor.getString(bookingCursor.getColumnIndex(TENANT_ID)));
        }

        bookingCursor.close();
        return booking;
    }

    public DataModel.Tenant getTenantInfo(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        DataModel.Tenant t = null;
        Cursor cursor = db.rawQuery("select * from " + TENANT_TABLE_NAME + " where TENANT_ID = ?", new String[]{id});
        if(cursor.moveToNext()) {
            t = new DataModel.Tenant(
                    cursor.getString(cursor.getColumnIndex(TENANT_ID)),
                    cursor.getString(cursor.getColumnIndex(TENANT_NAME)),
                    cursor.getString(cursor.getColumnIndex(TENANT_MOBILE)),
                    cursor.getString(cursor.getColumnIndex(TENANT_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(TENANT_ADDRESS)),
                    cursor.getInt(cursor.getColumnIndex(TENANT_IS_CURRENT)) > 0,
                    cursor.getString(cursor.getColumnIndex(PARENT_TENANT_ID))
            );
        }

        cursor.close();
        return t;
    }

    public ArrayList<DataModel.Tenant> getAllTenants(boolean onlyCurrent) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataModel.Tenant> tenantList = new ArrayList<DataModel.Tenant>();

        String query = "select * from " + TENANT_TABLE_NAME + (onlyCurrent? " WHERE " + TENANT_IS_CURRENT + " = 1" : "") + " ORDER BY TENANT_ID Desc";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            tenantList.add(new DataModel.Tenant(
                    cursor.getString(cursor.getColumnIndex(TENANT_ID)),
                    cursor.getString(cursor.getColumnIndex(TENANT_NAME)),
                    cursor.getString(cursor.getColumnIndex(TENANT_MOBILE)),
                    cursor.getString(cursor.getColumnIndex(TENANT_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(TENANT_ADDRESS)),
                    cursor.getInt(cursor.getColumnIndex(TENANT_IS_CURRENT)) > 0,
                    cursor.getString(cursor.getColumnIndex(PARENT_TENANT_ID))
            ));
        }

        cursor.close();
        return tenantList;
    }

    public ArrayList<DataModel.Tenant> getDependents(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataModel.Tenant> tenantList = new ArrayList<DataModel.Tenant>();

        String query = "select * from " + TENANT_TABLE_NAME + " WHERE " + PARENT_TENANT_ID + " = ?" + " ORDER BY TENANT_ID Desc";
        Cursor cursor = db.rawQuery(query, new String[]{id});
        while (cursor.moveToNext()) {
            tenantList.add(new DataModel.Tenant(
                    cursor.getString(cursor.getColumnIndex(TENANT_ID)),
                    cursor.getString(cursor.getColumnIndex(TENANT_NAME)),
                    cursor.getString(cursor.getColumnIndex(TENANT_MOBILE)),
                    cursor.getString(cursor.getColumnIndex(TENANT_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(TENANT_ADDRESS)),
                    cursor.getInt(cursor.getColumnIndex(TENANT_IS_CURRENT)) > 0,
                    cursor.getString(cursor.getColumnIndex(PARENT_TENANT_ID))
            ));
        }

        cursor.close();
        return tenantList;
    }

    /* Total Expected rent for current month */
    // XXX : Do we need to calclulate this expected rent from all the rooms available i.e. Rent for all Beds?
    public String getTotalExpectedRent() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ BOOKING_TABLE_NAME + " where BOOKING_CLOSE_DATE is null or date(BOOKING_CLOSE_DATE) >= date('now', 'start of month')" , null);

        int totalRent = 0;
        while(cursor.moveToNext()) {
            Log.i(TAG, "getTotalExpectedRent(): BOOKING_ID - " + cursor.getString(cursor.getColumnIndex(BOOKING_ID)));
            Log.i(TAG, "getTotalExpectedRent(): TENANT_ID - " + cursor.getString(cursor.getColumnIndex(TENANT_ID)));
            Log.i(TAG, "getTotalExpectedRent(): BED_NUMBER - " + cursor.getString(cursor.getColumnIndex(BED_NUMBER)));
            totalRent = totalRent + cursor.getInt(cursor.getColumnIndex(BOOKING_RENT_AMT));
        }

        cursor.close();
        return String.valueOf(totalRent);
    }

    //TODO: We may not need the following function. Review and remove.
    public String getOutstandingRentForBooking(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Integer outstandingRent = 0;

        //First check if there is any receipt for the booking_id in current month
        Cursor cursor = db.rawQuery("select * from " + RECEIPTS_TABLE_NAME + " WHERE BOOKING_ID = ? AND date(RECEIPT_DATE) >= date('now', 'start of month')", new String[]{id});
        if (cursor.getCount() == 0) {
            // No receipts means the rent is due.
            cursor = db.rawQuery("select BOOKING_RENT_AMT from " + BOOKING_TABLE_NAME + " WHERE BOOKING_ID = ?", new String[]{id});
            if (cursor.moveToNext())
                outstandingRent += cursor.getInt(cursor.getColumnIndex(BOOKING_RENT_AMT));

            //Now check if there are any outstandings from previous month
            cursor = db.rawQuery("select PENDING_AMT from " + PENDING_AMOUNT_TABLE_NAME + " WHERE BOOKING_ID = ?", new String[]{id});
            while (cursor.moveToNext()) {
                outstandingRent += cursor.getInt(cursor.getColumnIndex(PENDING_AMOUNT));
            }
        }


        return String.valueOf(outstandingRent);
    }

    public String  getTotalReceivedAmountForMonth(int month, DataModel.ReceiptType type) {
//        ReceiptType type = ReceiptType.RENT;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select RECEIPT_CASH_AMOUNT + RECEIPT_ONLINE_AMOUNT from " + RECEIPTS_TABLE_NAME + " WHERE strftime('%m', RECEIPT_DATE) = " +
                "'0" + String.valueOf(month) + "' and RECEIPT_TYPE = " + type.getIntValue();
        Cursor cursor = db.rawQuery(query, null);

        Integer receivedAmount = 0;

        while(cursor.moveToNext()) {
//            if(cursor.getInt(1) == ReceiptType.RENT.getIntValue()) {
                receivedAmount += cursor.getInt(0);
//            } else if()
        }
//            receivedAmount += cursor.getInt(0);


        cursor.close();
        return String.valueOf(receivedAmount);
    }


    public int getTotalPendingAmount(DataModel.PendingType type) {
        SQLiteDatabase db = this.getReadableDatabase();
        int pendingAmount = 0;
        Cursor cursor = db.rawQuery("select PENDING_AMOUNT from " + PENDING_AMOUNT_TABLE_NAME + " where PENDING_TYPE = " + type.getIntValue(), null);
        while (cursor.moveToNext()) {
            pendingAmount += cursor.getInt(0);
        }

        return pendingAmount;
    }

    public int getPendingAmountForBooking(String id) {
        int totalPendingAmount = 0;
        ArrayList<DataModel.Pending> pendings = getPendingEntriesForBooking(id);
        for (DataModel.Pending pendingEntry : pendings) {
            totalPendingAmount += pendingEntry.pendingAmt;
        }

        return totalPendingAmount;
    }

    public ArrayList<DataModel.Pending> getPendingEntriesForBooking(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<DataModel.Pending> pendingEntries = new ArrayList<DataModel.Pending>();

        Cursor cursor = db.rawQuery("select * from " + PENDING_AMOUNT_TABLE_NAME + " WHERE BOOKING_ID = ?", new String[]{id});

        while (cursor.moveToNext()) {
            pendingEntries.add(new DataModel.Pending(
                    cursor.getInt(cursor.getColumnIndex(PENDING_AMOUNT)),
                    cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndex(TENANT_ID)),
                    pendingTypeValues[cursor.getInt(cursor.getColumnIndex(PENDING_TYPE))]
            ));
        }

        cursor.close();
        return pendingEntries;
    }

    public ArrayList<DataModel.Pending> getPendingEntriesForTenant(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<DataModel.Pending> pendingEntries = new ArrayList<DataModel.Pending>();
        Cursor cursor = db.rawQuery("select * from " + PENDING_AMOUNT_TABLE_NAME + " WHERE TENANT_ID = ?", new String[]{id});

        while (cursor.moveToNext()) {
            pendingEntries.add(new DataModel.Pending(
                    cursor.getInt(cursor.getColumnIndex(PENDING_AMOUNT)),
                    cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndex(TENANT_ID)),
                    pendingTypeValues[cursor.getInt(cursor.getColumnIndex(PENDING_TYPE))]
            ));
        }

        cursor.close();
        return pendingEntries;
    }

    public ArrayList<DataModel.Receipt> getReceiptsForTenant(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<DataModel.Receipt> receiptEntries = new ArrayList<DataModel.Receipt>();
        String query = "select * from " + RECEIPTS_TABLE_NAME +
                " LEFT JOIN " + BOOKING_TABLE_NAME + " ON " +
                BOOKING_TABLE_NAME + ".BOOKING_ID = " +  RECEIPTS_TABLE_NAME + ".BOOKING_ID" +
                " WHERE " + BOOKING_TABLE_NAME + "." + TENANT_ID + " = ? ORDER BY RECEIPT_ID DESC";

        Cursor cursor = db.rawQuery(query, new String[]{id});

        while (cursor.moveToNext()) {
            receiptEntries.add(new DataModel.Receipt(
                    cursor.getString(cursor.getColumnIndex(RECEIPT_ID)),
                    cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndex(RECEIPT_ONLINE_AMOUNT)),
                    cursor.getString(cursor.getColumnIndex(RECEIPT_CASH_AMOUNT)),
                    cursor.getInt(cursor.getColumnIndex(RECEIPT_PENALTY_WAIVE_OFF)) > 0,
                    cursor.getString(cursor.getColumnIndex(RECEIPT_DATE)),
                    receiptTypeValues[cursor.getInt(cursor.getColumnIndex(RECEIPT_TYPE))])
            );
        }
            return receiptEntries;
    }

    private int getNewReceiptId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Integer highestReceiptId = 1;

        Cursor highestIdCursor = db.rawQuery("select * from " + RECEIPTS_TABLE_NAME + " ORDER BY RECEIPT_ID DESC LIMIT 1", null);
        if(highestIdCursor.getCount() != 0) {
            highestIdCursor.moveToNext();
            highestReceiptId = highestIdCursor.getInt(highestIdCursor.getColumnIndex(RECEIPT_ID));
            highestReceiptId += 1;
        }

        highestIdCursor.close();

        return highestReceiptId;
    }

    //TODO: Validation
    public void createReceipt(DataModel.ReceiptType type, String bookingId, String onlineAmount, String cashAmount, boolean penaltyWaiveOff) {
        SQLiteDatabase db = this.getReadableDatabase();
        Integer highestReceiptId = getNewReceiptId();

        Log.i(TAG, "highestReceiptId: ?, current date: "+ new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString());

        ContentValues contentValues = new ContentValues();
        Integer totalPayment = Integer.valueOf(onlineAmount) + Integer.valueOf(cashAmount);

        contentValues.put(RECEIPT_ID, highestReceiptId);
        contentValues.put(BOOKING_ID, bookingId);
        contentValues.put(RECEIPT_CASH_AMOUNT, cashAmount);
        contentValues.put(RECEIPT_ONLINE_AMOUNT, onlineAmount);
        contentValues.put(RECEIPT_DATE, new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString());
        contentValues.put(RECEIPT_TYPE, type.getIntValue());
        contentValues.put(RECEIPT_PENALTY_WAIVE_OFF, penaltyWaiveOff);

        db.insert(RECEIPTS_TABLE_NAME, null, contentValues);
    }

    public ArrayList<DataModel.Receipt> getAllReceipts(int month){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<DataModel.Receipt> receipts = new ArrayList<DataModel.Receipt>();
        String query = "select * from " + RECEIPTS_TABLE_NAME + " WHERE strftime('%m', RECEIPT_DATE) = " +
                "'0" + String.valueOf(month) + "' ORDER BY RECEIPT_ID DESC"; //sorting on Booking Id to get sorted list of room nos.
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                //SAHIFRE : HACK for now - fix in future release to support ReceiptType.WAIVEOFF
                DataModel.ReceiptType type = receiptTypeValues[cursor.getInt(cursor.getColumnIndex(RECEIPT_TYPE))];
                if(cursor.getInt(cursor.getColumnIndex(RECEIPT_TYPE)) == DataModel.ReceiptType.PENALTY.getIntValue() && cursor.getInt(cursor.getColumnIndex(RECEIPT_PENALTY_WAIVE_OFF)) > 0){
                    type = DataModel.ReceiptType.WAIVEOFF;
                }

                receipts.add(new DataModel.Receipt(
                        cursor.getString(cursor.getColumnIndex(RECEIPT_ID)),
                        cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                        cursor.getString(cursor.getColumnIndex(RECEIPT_ONLINE_AMOUNT)),
                        cursor.getString(cursor.getColumnIndex(RECEIPT_CASH_AMOUNT)),
                        cursor.getInt(cursor.getColumnIndex(RECEIPT_PENALTY_WAIVE_OFF)) > 0,
                        cursor.getString(cursor.getColumnIndex(RECEIPT_DATE)),
                        type)
                );
            }
        }
        cursor.close();
        return receipts;
    }


    public String getTotalCashReceipts(int month, DataModel.ReceiptType receiptType){
        SQLiteDatabase db = this.getReadableDatabase();


        String query = "select * from " + RECEIPTS_TABLE_NAME + " WHERE strftime('%m', RECEIPT_DATE) = " +
                "'0" + String.valueOf(month) + "' and RECEIPT_TYPE = " + String.valueOf(receiptType.getIntValue());
        Cursor cursor = db.rawQuery(query, null);
        int totalCash = 0;

        while (cursor.moveToNext()) {
            //SAHIFRE : HACK for now - fix in future release to support ReceiptType.WAIVEOFF
            DataModel.ReceiptType type = receiptTypeValues[cursor.getInt(cursor.getColumnIndex(RECEIPT_TYPE))];
            if(cursor.getInt(cursor.getColumnIndex(RECEIPT_TYPE)) == DataModel.ReceiptType.PENALTY.getIntValue() && cursor.getInt(cursor.getColumnIndex(RECEIPT_PENALTY_WAIVE_OFF)) > 0){
                type = DataModel.ReceiptType.WAIVEOFF;
                // Do not consider this CASH in calculation - Never received this payment in any mode
                continue;
            }

            totalCash += cursor.getInt(cursor.getColumnIndex(RECEIPT_CASH_AMOUNT));
        }

        cursor.close();
        return String.valueOf(totalCash);
    }



    public void splitRoom(String roomNumber, int numRooms, String rent, String deposit) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();


        roomNumber = roomNumber.split("\\.")[0];
        for (int i = 1; i< numRooms; i++) {
            addNewBed(roomNumber + "." + String.valueOf(i), rent, deposit);
        }
    }

    public void addPenaltyToOutstandingPayments() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataModel.Tenant> tenantList = new ArrayList<DataModel.Tenant>();
        Cursor cursor = db.rawQuery("select * from " + PENDING_AMOUNT_TABLE_NAME + " where PENDING_TYPE = ?", new String[]{String.valueOf(DataModel.PendingType.RENT.getIntValue())});
        while (cursor.moveToNext()) {
            boolean status = createPendingEntryForBooking(cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                                                        DataModel.PendingType.PENALTY, "200", Calendar.getInstance().get(Calendar.MONTH) + 1);

            DataModel.Tenant tenant = DataModule.getInstance().getTenantInfoForBooking(cursor.getString(cursor.getColumnIndex(BOOKING_ID)));
            if(tenant.mobile.isEmpty() == false) {
                SMSManagement smsManagement = SMSManagement.getInstance();

                smsManagement.sendSMS(tenant.mobile,
                        DataModule.getInstance().getSMSMessage(cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                                tenant,
                                0,
                                SMSManagement.SMS_TYPE.PENALTY_GENERATED)
                );
            }
        }
        cursor.close();
    }

    //SAHIRE: Need to re-write this function
    public ArrayList<DataModel.Pending> getAllPendingPayments(int month){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DataModel.Pending> pendingEntries = new ArrayList<DataModel.Pending>();
        String query = "select * from " + PENDING_AMOUNT_TABLE_NAME + " where PENDING_MONTH = " + month + "";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            pendingEntries.add(new DataModel.Pending(
                    cursor.getInt(cursor.getColumnIndex(PENDING_AMOUNT)),
                    cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndex(TENANT_ID)),
                    pendingTypeValues[cursor.getInt(cursor.getColumnIndex(PENDING_TYPE))]
            ));
        }
        cursor.close();
        return pendingEntries;
    }

    public String getSMSMessage(String bookingID, DataModel.Tenant tenant, int amount/* only for receipts*/, SMSManagement.SMS_TYPE smsType){
        String msg = "";
        int deposit = 0, rent = 0, penalty = 0, outstanding = 0;
//        HashMap<PendingType, String> pendingsHash;
        ArrayList<DataModel.Pending> pendingList = getPendingEntriesForBooking(bookingID);
        for (DataModel.Pending pendingEntry : pendingList) {
            if(pendingEntry.type == DataModel.PendingType.DEPOSIT){
                deposit = pendingEntry.pendingAmt;
            } else if(pendingEntry.type == DataModel.PendingType.RENT){
                rent = pendingEntry.pendingAmt;
            } else if(pendingEntry.type == DataModel.PendingType.PENALTY){
                penalty = pendingEntry.pendingAmt;
            }
        }
        outstanding += rent + deposit + penalty;


        if(smsType == SMSManagement.SMS_TYPE.BOOKING) {
            /* From bed View screen from Floating Button */
            //XXX : For Booking add Tenant Name and Booking Month
            DataModel.Booking booking = getBookingInfo(bookingID);
            msg = "Dear "+ tenant.name + ", Your Booking is confirmed for Room#" + getBookingInfo(bookingID).bedNumber + " from Date: " + booking.bookingDate + "(yyyy-mm-dd).\r\n" +
                    "Your Monthly rent amount is: Rs." + booking.rentAmount + " And Deposit Amount is: Rs." + booking.depositAmount + ".\r\n"
                    + "Please make sure that you pay your monthly rent before 5th of every month to avoid Rs.200 penalty.\r\n"
//                    + "Please pay your rent before 5th of " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " to avoid Rs.200 penalty.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else if(smsType == SMSManagement.SMS_TYPE.MONTHLY_RENT) {
            /* will be sent automatically once at the start of every month*/
            msg = "Dear Room#" + getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", Your monthly rent is generated for Month: "
                    + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + "\r\n"
                    + "Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "Please pay your rent before 5th of " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " to avoid Rs.200 penalty.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else if(smsType == SMSManagement.SMS_TYPE.RECEIPT) {
            /* From Receipt Screen when payment is received */
            msg = "Dear Room#" + getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", Payment received of Rs."+ amount + ".\r\n"
                    + "Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "In case there is outstanding, please pay immediately.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else if(smsType == SMSManagement.SMS_TYPE.PENALTY_GENERATED){
            /* Send SMS Automatically when penalty is generated */
            msg = "Dear Room#" + getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", Penalty of Rs.200 is applicable to you due to non payment of Rent on time.\r\n"
                    + "Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "Please pay your outstanding immediately.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else if(smsType == SMSManagement.SMS_TYPE.DUE_REMINDER) {
            /* From Recepit screen using Floating Button*/
            msg = "Dear Room#" + getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", This is a reminder to pay your monthly rent immediately.\r\n"
                    + "Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "Please ignore if already paid.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else {
            /* From Main screen */
            /* SMS_TYPE == DEFAULT
                send general sms even if there is no pending, penalty or month change.
                this can be done from main "send SMS"
            */
            msg = "Dear Room#" + getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "Please pay your outstanding immediately.\r\n"
                    + "Please ignore if already paid.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        }
        return msg;
    }

}

