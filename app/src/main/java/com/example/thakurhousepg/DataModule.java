package com.example.thakurhousepg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataModule extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ThakurHouse.db";

    private static final String MASTER_TABLE_NAME = "master_table";
    private static final String MISC_TABLE_NAME = "misc_table";

    public static final String TENANT_TABLE_NAME = "tenant_table";
    public static final String PENDING_AMOUNT_TABLE_NAME = "penalty_table";
    public static final String BEDS_TABLE_NAME = "beds_table";
    public static final String RECEIPTS_TABLE_NAME = "RECEIPTs_table";
    public static final String OVERDUE_TABLE_NAME = "overdue_table";
    public static final String BOOKING_TABLE_NAME = "bookings_table";


    private static final String TENANT_ID = "TENANT_ID";
    private static final String TENANT_NAME = "TENANT_NAME";
    private static final String TENANT_EMAIL = "TENANT_EMAIL";
    private static final String TENANT_MOBILE = "TENANT_MOBILE";
    private static final String TENANT_MOBILE_2 = "TENANT_MOBILE_2";
    private static final String TENANT_ADDRESS = "TENANT_ADDRESS";
    private static final String TENANT_IS_CURRENT = "TENANT_IS_CURRENT";

    private static final String BOOKING_ID = "BOOKING_ID";
    private static final String BOOKING_RENT_AMT = "BOOKING_RENT_AMT";
    private static final String BOOKING_DEPOSIT_AMT = "BOOKING_DEPOSIT_AMT";
    private static final String BOOKING_DATE = "BOOKING_DATE";
    private static final String BOOKING_IS_WHOLE_ROOM = "BOOKING_IS_WHOLE_ROOM";
    private static final String BOOKING_CLOSE_DATE = "BOOKING_CLOSE_DATE";

    private static final String PENALTY_ID = "PENALTY_ID";
    private static final String PENALTY_AMOUNT = "PENALTY_AMOUNT";//Month number 1..12

    private static final String BED_NUMBER = "BED_NUMBER";
    private static final String BED_DEPOSIT = "BED_DEPOSIT";
    private static final String BED_RENT = "BED_RENT";

    private static final String PENDING_AMT = "PENDING_AMT";

    private static final String RECEIPT_ID = "RECEIPT_ID";
    private static final String RECEIPT_ONLINE_AMT = "RECEIPT_ONLINE_AMT";
    private static final String RECEIPT_CASH_AMT = "RECEIPT_CASH_AMT";
    private static final String RECEIPT_DATE = "RECEIPT_DATE";
    private static final String RECEIPT_IS_DEPOSIT = "RECEIPT_IS_DEPOSIT";
    private static final String RECEIPT_PENALTY_AMT = "RECEIPT_PENALTY_AMT";
    private static final String RECEIPT_PENALTY_WAIVE_OFF_AMT = "RECEIPT_PENALTY_WAIVE_OFF_AMT";

    private static final String TAG = "DataModule";

    public static final String KEY_ALL = "KEY_ALL";

    private static DataModule _instance = null;
    public static DataModule getInstance() {
        if(_instance!=null) {
//            _instance = new DataModule(MainActivity.class);
        }

        return _instance;
    }

    public DataModule(Context context) {
        super(context, DATABASE_NAME, null, 1);
        createDefaultBeds();
        insertSampleData();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;

        query = "create table IF NOT EXISTS " + BEDS_TABLE_NAME +
                " (BED_NUMBER TEXT PRIMARY KEY," +
                " BED_RENT INTEGER NOT NULL," +
                " BED_DEPOSIT INTEGER NOT NULL," +
                " IS_OCCUPIED BOOLEAN," +
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
                " RECEIPT_ONLINE_AMT INTEGER," +
                " RECEIPT_CASH_AMT INTEGER," +
                " RECEIPT_DATE TEXT," +
                " RECEIPT_IS_DEPOSIT BOOLEAN," +
                " RECEIPT_PENALTY_AMT INTEGER," +
                " RECEIPT_PENALTY_WAIVE_OFF_AMT INTEGER," +
                " BOOKING_ID INTEGER," +
                " FOREIGN KEY (BOOKING_ID) REFERENCES " + BOOKING_TABLE_NAME + "(BOOKING_ID)" +
                ")";
        db.execSQL(query);



        //Create Penalty table
        query = "create table IF NOT EXISTS " + PENDING_AMOUNT_TABLE_NAME +
                "(PENDING_AMOUNT INTEGER," +
                " IS_PENALTY INTEGER," +
                " BOOKING_ID INTEGER," +
                " TENANT_ID INTEGER," +
                " FOREIGN KEY (BOOKING_ID) REFERENCES " + BOOKING_TABLE_NAME + "(BOOKING_ID)," +
                " FOREIGN KEY (TENANT_ID) REFERENCES " + TENANT_TABLE_NAME + "(BOOKING_ID)" +
                ")";
        db.execSQL(query);
    }

    public void insertSampleData() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();

        String newTenantId = addNewTenant("Yogesh Joshi", "123456789", null, null, null);
        if(newTenantId != null)
            createNewBooking("101.0", newTenantId, "8000", "8000", date);

        newTenantId = addNewTenant("Sachin Ahire", "987654321", null, null, null);
        if(newTenantId != null)
            createNewBooking("102.0", newTenantId, "9000", "9000", date);

        newTenantId = addNewTenant("Suyog J", "214365879", null, null, null);
        if(newTenantId != null)
            createNewBooking("103.0", newTenantId, "9000", "9000", date);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TENANT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MISC_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PENDING_AMOUNT_TABLE_NAME);
        onCreate(db);

    }

    /* Should ONLY be called on the first launch of the app every month.
     * This function DOES NOT make date validations. MUST be done by the caller. */
    public void createPendingEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        //TODO: Write the query to get all current bookings and calculate the expected rent for them
        String query = "select * from " + BOOKING_TABLE_NAME + " where BOOKING_CLOSE_DATE is null";
        Cursor checkRecord = db.rawQuery(query, null);

    }

    public boolean addNewBed(String bedNumber, String rent, String deposit) {
        Boolean opSuccess = false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(BED_NUMBER, bedNumber);
        contentValues.put(BED_RENT, rent);
        contentValues.put(BED_DEPOSIT, deposit);
        contentValues.put("IS_OCCUPIED", false);

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

    public String addNewTenant(String name, String mobile, String mobile2, String email, String address) {
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

    public boolean updateTenant(String id, String name, String mobile, String mobile2, String email, String address, Boolean isCurrent) {
        Boolean opSuccess = false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        if(!name.isEmpty()) contentValues.put(TENANT_NAME, name);
        if(!email.isEmpty()) contentValues.put(TENANT_EMAIL, email);
        if(!mobile.isEmpty()) contentValues.put(TENANT_MOBILE, mobile);
        if(!mobile2.isEmpty()) contentValues.put(TENANT_MOBILE_2, mobile2);
        if(!address.isEmpty()) contentValues.put(TENANT_ADDRESS, address);
        if(isCurrent != null) contentValues.put(TENANT_IS_CURRENT, false);


        String query = "select * from " + TENANT_TABLE_NAME + " where TENANT_ID = " + id;
        Cursor checkRecord = db.rawQuery(query, null);

        if (checkRecord.getCount() != 0) {
            db.update(TENANT_TABLE_NAME, contentValues, "TENANT_ID = ?", new String[]{id.toString()});
            opSuccess = true;
        }

        checkRecord.close();

        return opSuccess;
    }

    //TODO: Validation Needed urgently, Duplicate bookings can be made right now, messing the data for rest of the activities
    public boolean createNewBooking(String bedNumber, String tenantId, String rent, String deposit, String admissionDate) {
        Boolean opSuccess = false;
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

        // If the Whole room is to be booked, check if other beds of the room are free as well.
        // OR If the Bed is already booked
        //TODO: This can combined with the WHoleRoom Logic in the inner if
//        if (isWholeRoom) {
//            String roomNo = bedNumber.split("\\.")[0];
//            checkRecord = db.rawQuery("select * from " + BEDS_TABLE_NAME + " where IS_OCCUPIED = ? AND BED_NUMBER LIKE ?", new String[]{"1", roomNo+"%"});
//        } else {
            checkRecord = db.rawQuery("select * from " + BEDS_TABLE_NAME + " where IS_OCCUPIED = ? AND BED_NUMBER = ?", new String[]{"1", bedNumber});
//        }

        if (checkRecord.getCount() != 0) {
            Log.i(TAG, "CreateNewBooking: Can not book the whole room");
            checkRecord.close();
            return opSuccess;
        }
        // Check if there is already a Booking for this TENANT_ID
        checkRecord = db.rawQuery("select BOOKING_ID from " + BOOKING_TABLE_NAME + " where TENANT_ID = ? AND BOOKING_CLOSE_DATE is null", new String[]{tenantId});
//                " INNER JOIN " + TENANT_TABLE_NAME + " ON " + TENANT_TABLE_NAME + ".BOOKING_ID = " + BOOKING_TABLE_NAME + ".BOOKING_ID" +
//                " where BOOKING_CLOSE_DATE = NULL ", null);
//                " UNION select BOOKING_ID from " + TENANT_TABLE_NAME + " where TENANT_ID = ? AND TENANT_IS_CURRENT = ?", new String[]{tenantId, "1"});

        if (checkRecord.getCount() == 0) {
            Integer bookingId = createNewBookingId();
            contentValues.put(BOOKING_ID, bookingId);
            long result = db.insert(BOOKING_TABLE_NAME, null, contentValues);

            if(result!= -1) {
                contentValues.clear();
                contentValues.put("IS_OCCUPIED", true);
                contentValues.put(BOOKING_ID, bookingId);
                db.update(BEDS_TABLE_NAME, contentValues, "BED_NUMBER = ?", new String[]{bedNumber});

                //Also book the rest of the beds in the room, if whole room is to be booked
                //TODO: Validate the Bed Number
                /*
                if (isWholeRoom) {
                    String roomNo = bedNumber.split("\\.")[0];
                    db.update(BEDS_TABLE_NAME, contentValues, "BED_NUMBER LIKE ?", new String[]{roomNo + "%"});
                } else {
                    db.update(BEDS_TABLE_NAME, contentValues, "BED_NUMBER = ?", new String[]{bedNumber});
                }
*/
                opSuccess = true;
            } else {
                Log.i(TAG, "CreateNewBooking: Insert failed");
            }

        }

        checkRecord.close();

        return opSuccess;
    }

    public boolean closeBooking(String id, String closeingDate, boolean isRentPending, boolean isDepositPending) {
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

            contentValues.clear();
            contentValues.putNull(BOOKING_ID);
            contentValues.put("IS_OCCUPIED", false);

            checkRecord = db.rawQuery("select BED_NUMBER from " + BEDS_TABLE_NAME + " where BOOKING_ID = ?", new String[]{id});
            if(checkRecord.moveToNext()) {
                String bedNumber = checkRecord.getString(checkRecord.getColumnIndex(BED_NUMBER));
                db.update(BEDS_TABLE_NAME, contentValues, "BED_NUMBER = ?", new String[]{bedNumber});
            }

            if(isRentPending) {
                //TODO: Add a entry in the Overdue table
            }

            if(isDepositPending) {
                //TODO: Add a entry in the Overdue table
            }
        }

        checkRecord.close();

        return opSuccess;
    }

    /// Helper Functions
    private void createDefaultBeds() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select BED_NUMBER from " + BEDS_TABLE_NAME + " LIMIT 1", null);
        if (c.getCount() == 0) {
            /* For Ground Floor */
            addNewBed("000.1", "4250", "4250");
            addNewBed("000.2", "4250", "4250");
            addNewBed("000.3", "4250", "4250");
            addNewBed("000.4", "4250", "4250");
            addNewBed("000.5", "4250", "4250");
            addNewBed("000.6", "4250", "4250");

            for (int floorNo = 100; floorNo <= 600; floorNo += 100) {
                int numOfRooms = 7;

                if(floorNo >= 200) {
                    numOfRooms = 8;
                }
                for (int roomNo = 1; roomNo <= numOfRooms; roomNo += 1) {

//                    for (Double bedNo = 1.0; bedNo <= 3.0; bedNo++) {
                        Double bedNumber = Double.valueOf(floorNo + roomNo);// + bedNo / 10);

                        addNewBed(bedNumber.toString(), "8000", "8000");
//                        ContentValues contentValues = new ContentValues();
//                        contentValues.put(BED_NUMBER, bedNumber.toString());
//                        contentValues.put(BED_RENT, "3000");
//                        contentValues.put(BED_DEPOSIT, "3000");
//                        db.insert(BEDS_TABLE_NAME, null, contentValues);
//                    }
                }
            }
        }

        c.close();
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


    public ArrayList<Bed> getBedsList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Bed> beds = new ArrayList<Bed>();
        Cursor cursor = db.rawQuery("select * from " + BEDS_TABLE_NAME + " ORDER BY BED_NUMBER ASC", null);

        if(cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                beds.add(new Bed(
                        cursor.getString(cursor.getColumnIndex(BED_NUMBER)),
                        cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                        cursor.getString(cursor.getColumnIndex(BED_RENT)),
                        cursor.getString(cursor.getColumnIndex(BED_DEPOSIT)),
                        cursor.getInt(cursor.getColumnIndex("IS_OCCUPIED")) > 0
                ));
            }
        }

        cursor.close();
        return beds;
    }

    public ArrayList<Booking> getCurrentBookings() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Booking> bookings = new ArrayList<Booking>();
        Cursor cursor = db.rawQuery("select * from " + BOOKING_TABLE_NAME + " where " + BOOKING_CLOSE_DATE + " is null", null);
        while (cursor.moveToNext()) {
            bookings.add(new Booking(
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
    public Bed getBedInfo(String forBedNumber) {
        SQLiteDatabase db = this.getReadableDatabase();
        Bed bed = null;
        Cursor cursor = db.rawQuery("select * from " + BEDS_TABLE_NAME + " where BED_NUMBER = ?", new String[]{forBedNumber});

        if (cursor.moveToNext()) {
            bed = new Bed(
                    cursor.getString(cursor.getColumnIndex(BED_NUMBER)),
                    cursor.getString(cursor.getColumnIndex(BOOKING_ID)),
                    cursor.getString(cursor.getColumnIndex(BED_RENT)),
                    cursor.getString(cursor.getColumnIndex(BED_DEPOSIT)),
                    cursor.getInt(cursor.getColumnIndex("IS_OCCUPIED")) > 0
            );
        }

        return bed;
    }

    public Booking getBookingInfo(String forId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Booking booking = null;
        Cursor cursor = db.rawQuery("select * from " + BOOKING_TABLE_NAME + " where BOOKING_ID = ?", new String[]{forId});
        if (cursor.moveToNext()) {
            booking = new Booking(
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

    public Tenant getTenantInfoForBooking(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Tenant t = null;
        Cursor cursor = db.rawQuery("select * from " + TENANT_TABLE_NAME + // " where TENANT_ID = ?", new String[]{id});
                        " LEFT JOIN " + BOOKING_TABLE_NAME + " ON " + BOOKING_TABLE_NAME + ".TENANT_ID = " + TENANT_TABLE_NAME + ".TENANT_ID" +
                        " where BOOKING_ID = ?", new String[]{id});

        if(cursor.moveToNext()) {
            t = new Tenant(
                    cursor.getString(cursor.getColumnIndex(TENANT_ID)),
                    cursor.getString(cursor.getColumnIndex(TENANT_NAME)),
                    cursor.getString(cursor.getColumnIndex(TENANT_MOBILE)),
                    cursor.getString(cursor.getColumnIndex(TENANT_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(TENANT_ADDRESS)),
                    cursor.getInt(cursor.getColumnIndex(TENANT_IS_CURRENT)) > 0
                    );
        }

        cursor.close();
        return t;
    }

    public String getTotalOutstandingRent() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+ BOOKING_TABLE_NAME + " where BOOKING_CLOSE_DATE is null" , null);

        int totalRent = 0;
        while(cursor.moveToNext()) {
            Log.i(TAG, "getTotalOutstandingRent(): BOOKING_ID - " + cursor.getString(cursor.getColumnIndex(BOOKING_ID)));
            Log.i(TAG, "getTotalOutstandingRent(): TENANT_ID - " + cursor.getString(cursor.getColumnIndex(TENANT_ID)));
            Log.i(TAG, "getTotalOutstandingRent(): BED_NUMBER - " + cursor.getString(cursor.getColumnIndex(BED_NUMBER)));
            totalRent = totalRent + cursor.getInt(cursor.getColumnIndex(BOOKING_RENT_AMT));
        }

        cursor.close();
        return String.valueOf(totalRent);
    }

    //TODO: Finish the Functions
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
                outstandingRent += cursor.getInt(cursor.getColumnIndex(PENDING_AMT));
            }
        }


        return String.valueOf(outstandingRent);
    }

    public String getTotalDuesForBooking(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + RECEIPTS_TABLE_NAME + " WHERE BOOKING_ID = ?", new String[]{id});
        Integer outstandingRent = 0;

        if (cursor.moveToNext()) {
            outstandingRent += cursor.getInt(cursor.getColumnIndex(BOOKING_RENT_AMT));
            //cursor.getS
        }

        return String.valueOf(outstandingRent);
    }

    private int getNewReceiptId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Integer highestReceiptId = 1;

        Cursor highestIdCursor = db.rawQuery("select * from " + RECEIPTS_TABLE_NAME + " ORDER BY RECEIPT_ID DESC LIMIT 1", null);
        if(highestIdCursor.getCount() != 0) {
            highestIdCursor.moveToNext();
            highestReceiptId = highestIdCursor.getInt(highestIdCursor.getColumnIndex(TENANT_ID));
            highestReceiptId += 1;
        }

        highestIdCursor.close();

        return highestReceiptId;
    }
    //TODO: Validation
    public void createReceipt(int type, String bookingId, String onlineAmount, String cashAmount, String penaltyAmount) {
        SQLiteDatabase db = this.getReadableDatabase();
        Integer highestReceiptId = getNewReceiptId();

        Log.i(TAG, "highestReceiptId: ?, current date: "+ new SimpleDateFormat("dd/mm/yyyy").format(new Date()).toString());

        ContentValues contentValues = new ContentValues();
        Integer totalPayment = Integer.valueOf(onlineAmount) + Integer.valueOf(cashAmount) - Integer.valueOf(penaltyAmount);

        contentValues.put(RECEIPT_ID, highestReceiptId);
        contentValues.put(BOOKING_ID, bookingId);
        contentValues.put(RECEIPT_CASH_AMT, cashAmount);
        contentValues.put(RECEIPT_ONLINE_AMT, onlineAmount);
        contentValues.put(RECEIPT_PENALTY_AMT, penaltyAmount);
        contentValues.put(RECEIPT_DATE, new SimpleDateFormat("dd/mm/yyyy").format(new Date()).toString());
        if(type == 2)
            contentValues.put(RECEIPT_IS_DEPOSIT, true);
        else
            contentValues.put(RECEIPT_IS_DEPOSIT, false);

        db.insert(RECEIPTS_TABLE_NAME, null, contentValues);

//        ReceiptActivity receipt = new ReceiptActivity(String.valueOf(highestReceiptId), bookingId, onlineAmount, cashAmount, penaltyAmount, "", new SimpleDateFormat("dd/mm/yyyy").format(new Date()).toString(), (type == 2));

//        return highestReceiptId;
    }


    public void splitRoom(String roomNumber, int numRooms, String rent, String deposit) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();


        roomNumber = roomNumber.split("\\.")[0];
        for (int i = 1; i< numRooms; i++) {
            addNewBed(roomNumber + "." + String.valueOf(i), rent, deposit);
        }
    }

    //TODO: Check and remove this function
    public Cursor getTenantTableData(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result;
        String query;


        if (key == KEY_ALL) {
            query = "select * from " + TENANT_TABLE_NAME +" ORDER BY TENANT_ID ASC";
            Log.d("TAG", "QUERY: " + query);
            result = db.rawQuery(query, null);
        } else {
            key = Integer.valueOf(key).toString();
            query = "select * from " + TENANT_TABLE_NAME +" where ROOM_NUMBER = " + "'"+ key + "'";
            Log.d("TAG", "QUERY: " + query);
            result = db.rawQuery(query, null);
        }

        result.close();
        return result;
    }

//MARK: ---------------------------------- Data Class Definitions ---------------------------------
    public static class Bed {
        public final String bedNumber;
        public final String bookingId;
        public final String rentAmount;
        public final String depositAmount;
        public final Boolean isOccupied;

        public Bed(String bedNumber, String bookingId, String rentAmount, String depositAmount,  Boolean isOccupied) {
            this.bedNumber = bedNumber;
            this.bookingId = bookingId;
            this.rentAmount = rentAmount;
            this.depositAmount = depositAmount;
            this.isOccupied = isOccupied;
        }
    }

    public static class Booking {
        public final String id;
        public final String bedNumber;
        public final String rentAmount;
        public final String depositAmount;
        public final String bookingDate;
        public final Boolean isWholeRoom;
        public final String tenantId;

        public Booking(String bookingId, String bedNumber, String rentAmount, String depositAmount, String bookingDate, Boolean isWholeRoom, String tenantId) {
            this.bedNumber = bedNumber;
            this.id = bookingId;
            this.rentAmount = rentAmount;
            this.depositAmount = depositAmount;
            this.bookingDate = bookingDate;
            this.isWholeRoom = isWholeRoom;
            this.tenantId = tenantId;
        }
    }

    public static class Tenant {
        public final String id;
        public final String name;
        public final String mobile;
        public final String email;
        public final String address;
        public final Boolean isCurrent;

        public Tenant(String id, String name, String mobile, String email, String address, Boolean isCurrent) {
            this.id = id;
            this.name = name;
            this.mobile = mobile;
            this.isCurrent = isCurrent;
            this.email = email;
            this.address = address;
        }
    }

    public static class Receipt {
        public final String id;
        public final String onlineAmount;
        public final String cashAmount;
        public final String date;
        public final String penaltyAmount;
        public final String penaltyWaiveOffAmount;
        public final Boolean isDeposit;
        public final String bookingId;

        public Receipt(String id, String bookingId, String onlineAmount, String cashAmount, String penaltyAmount, String penaltyWaiveOffAmount, String date, Boolean isDeposit) {
            this.id = id;
            this.bookingId = bookingId;
            this.onlineAmount = onlineAmount;
            this.cashAmount = cashAmount;
            this.penaltyAmount = penaltyAmount;
            this.penaltyWaiveOffAmount = penaltyWaiveOffAmount;
            this.date = date;
            this.isDeposit = isDeposit;
        }
    }

    public static class Payment {

    }
}

