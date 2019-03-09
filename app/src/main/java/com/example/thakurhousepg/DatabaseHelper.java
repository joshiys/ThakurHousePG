package com.example.thakurhousepg;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ThakurHouse.db";

    public static final String TABLE_NAME = "master_table";
    public static final String T_TABLE_NAME = "tenant_table";
    public static final String M_TABLE_NAME = "misc_table";

    public static final String COL_0 = "ROOM_NUMBER";
    public static final String COL_1 = "EXPECTED_RENT";


    public static final String T_COL_0 = "ROOM_NUMBER";
    public static final String T_COL_1 = "NAME";
    public static final String T_COL_2 = "MOBILE";
    public static final String T_COL_3 = "ADMISSION_DATE";

    public static final String T_COL_4 = "RENT";
    public static final String T_COL_5 = "ONLINE_RENT";
    public static final String T_COL_6 = "CASH_RENT";

    public static final String T_COL_7 = "PENALTY";
    public static final String T_COL_8 = "LAST_PENALTY_MONTH";//Month number 1..12

    public static final String T_COL_9 = "DECIDED_DEPOSIT";
    public static final String T_COL_10 = "ONLINE_DEPOSIT";
    public static final String T_COL_11 = "CASH_DEPOSIT";

    public static final String M_COL_0 = "RESET_FOR_MONTH";
    public static final String M_COL_1 = "RESET_RENT";
    public static final String M_COL_2 = "EMAIL_ID_1";
    public static final String M_COL_3 = "EMAIL_ID_2";
    public static final String M_COL_4 = "MOBILE_1";
    public static final String M_COL_5 = "MOBILE_2";
    public static final String M_COL_6 = "MOBILE_3";

    public static final String KEY_ALL = "KEY_ALL";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + T_TABLE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;

        query = "create table " + M_TABLE_NAME + " (RESET_FOR_MONTH INTEGER PRIMARY KEY, RESET_RENT BOOLEAN, EMAIL_ID_1 EMAIL," +
                "EMAIL_ID_2 EMAIL, MOBILE_1 MOBILE, MOBILE_2 MOBILE, MOBILE_3 MOBILE)";

        db.execSQL(query);

        db.execSQL("create table " + TABLE_NAME + " (ROOM_NUMBER TEXT PRIMARY KEY, EXPECTED_RENT TEXT)");

        query = "create table " + T_TABLE_NAME + " (ROOM_NUMBER INTEGER PRIMARY KEY, NAME TEXT, MOBILE INTEGER," +
                "ADMISSION_DATE TEXT, RENT INTEGER, ONLINE_RENT INTEGER, CASH_RENT INTEGER, PENALTY INTEGER," +
                "LAST_PENALTY_MONTH INTEGER, DECIDED_DEPOSIT INTEGER, ONLINE_DEPOSIT INTEGER, CASH_DEPOSIT INTEGER)";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + T_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + M_TABLE_NAME);
        onCreate(db);

    }

    public boolean insertInMisc(String resetMonth, String resetFlag, String emailID1, String emailID2,
                                String mobile1, String mobile2, String mobile3) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(M_COL_0, resetMonth);
        contentValues.put(M_COL_1, resetFlag);
        contentValues.put(M_COL_2, emailID1);
        contentValues.put(M_COL_3, emailID2);
        contentValues.put(M_COL_4, mobile1);
        contentValues.put(M_COL_5, mobile2);
        contentValues.put(M_COL_6, mobile3);

        String query = "select * from " + M_TABLE_NAME + " where RESET_FOR_MONTH = " + resetMonth;
        Cursor checkRecord = db.rawQuery(query, null);

        if (checkRecord.getCount() != 0) {
            db.update(M_TABLE_NAME, contentValues, "RESET_FOR_MONTH = ?", new String[]{resetMonth});
            return true;
        } else {

            long result = db.insert(M_TABLE_NAME, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }
    }

    public boolean insertInMaster(String roomNumber, String rent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_0, roomNumber);
        Log.d("TAGG", COL_1 + ":" + roomNumber);
        contentValues.put(COL_1, rent);
        Log.d("TAGG", COL_1 + ":" + rent);


        String query = "select * from " + TABLE_NAME + " where ROOM_NUMBER = " + roomNumber;
        Cursor checkRecord = db.rawQuery(query, null);
        contentValues.put(COL_0, roomNumber);
        contentValues.put(COL_1, rent);

        if (checkRecord.getCount() != 0) {

//            db.update(TABLE_NAME, contentValues, "ROOM_NUMBER = ?", new String[]{roomNumber});
            return true;
        } else {

            long result = db.insert(TABLE_NAME, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                boolean addresult = insertInTenant(roomNumber, null, null, null, rent,
                        null, null, "0", null,
                        rent, null, null);
                if(addresult == false){
                    deleteMasterRecord(roomNumber);
                }
                return addresult;
            }
        }
    }

    public boolean insertInTenant(String roomNumber, String name, String mobile, String admDate, String outstanding,
                                  String onlineRent, String cashRent, String penalty, String penaltyMonth,
                                  String decidedDeposit, String onlineDeposit, String cashDeposit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        String query = "select * from " + T_TABLE_NAME + " where ROOM_NUMBER = " + roomNumber;
        Cursor checkRecord = db.rawQuery(query, null);
        contentValues.put(T_COL_0, roomNumber);
        contentValues.put(T_COL_1, name);
        contentValues.put(T_COL_2, mobile);
        contentValues.put(T_COL_3, admDate);
        contentValues.put(T_COL_4, outstanding);

        contentValues.put(T_COL_5, onlineRent);
        contentValues.put(T_COL_6, cashRent);
        contentValues.put(T_COL_7, penalty);
        contentValues.put(T_COL_8, penaltyMonth);

        contentValues.put(T_COL_9, decidedDeposit);
        contentValues.put(T_COL_10, onlineDeposit);
        contentValues.put(T_COL_11, cashDeposit);

        if (checkRecord.getCount() != 0) {

//            db.update(T_TABLE_NAME, contentValues, "ROOM_NUMBER = ?", new String[]{roomNumber});
            return true;
        } else {
            long result = db.insert(T_TABLE_NAME, null, contentValues);
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }
    }

    public Cursor getMiscTableData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result;
        String query;

        query = "select * from " + M_TABLE_NAME;
        result = db.rawQuery(query, null);

        return result;
    }

    //by key or all
    public Cursor getMasterTableData(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result;
        String query;
        if (key == KEY_ALL) {
            query = "select * from " + TABLE_NAME +" order by " + ""+ COL_0 + " ASC";
            result = db.rawQuery(query, null);
        } else {
            key = Integer.valueOf(key).toString();
            query = "select * from " + TABLE_NAME +" where ROOM_NUMBER = " +
                    "'"+ key + "'";
//            result = db.rawQuery(query, new String[]{key});
            result = db.rawQuery(query, null);
        }
        return result;
    }

    public Cursor getTenantTableData(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result;
        String query;


        if (key == KEY_ALL) {
            query = "select * from " + T_TABLE_NAME +" order by " + ""+ T_COL_0 + " ASC";
            Log.d("TAGG", "QUERY: " + query);
//            result = db.rawQuery("select * from " + T_TABLE_NAME +" orderby '" + T_COL_0 + "'", null);
            result = db.rawQuery(query, null);
        } else {
            key = Integer.valueOf(key).toString();
            query = "select * from " + T_TABLE_NAME +" where ROOM_NUMBER = " +
                    "'"+ key + "'";
            Log.d("TAGG", "QUERY: " + query);
//            result = db.rawQuery("select * from " + T_TABLE_NAME, null);
            result = db.rawQuery(query, null);
        }
        return result;
    }

    public int updateMasterRecord(String roomNumber, String rent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_0, roomNumber);
        contentValues.put(COL_1, rent);


        return db.update(TABLE_NAME, contentValues, "ROOM_NUMBER = ?", new String[]{roomNumber});
    }

    public int updateTenantRecord(String roomNumber, String name, String mobile, String admDate, String outstanding,
                                  String onlineRent, String cashRent, String penalty, String penaltyMonth,
                                  String decidedDeposit, String onlineDeposit, String cashDeposit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(T_COL_0, roomNumber);
        contentValues.put(T_COL_1, name);
        contentValues.put(T_COL_2, mobile);
        contentValues.put(T_COL_3, admDate);
        contentValues.put(T_COL_4, outstanding);

        contentValues.put(T_COL_5, onlineRent);
        contentValues.put(T_COL_6, cashRent);
        contentValues.put(T_COL_7, penalty);
        contentValues.put(T_COL_8, penaltyMonth);

        contentValues.put(T_COL_9, decidedDeposit);
        contentValues.put(T_COL_10, onlineDeposit);
        contentValues.put(T_COL_11, cashDeposit);


        return db.update(T_TABLE_NAME, contentValues, "ROOM_NUMBER = ?", new String[]{roomNumber});
    }

    public int deleteMiscRecord(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        return db.delete(M_TABLE_NAME, "RESET_FOR_MONTH = ?", new String[]{id});
    }

    public int deleteMasterRecord(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        db.delete(TABLE_NAME, "ROOM_NUMBER = ?", new String[]{id});
        return deleteTenantRecord(id);
    }

    public int deleteTenantRecord(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(T_TABLE_NAME, "ROOM_NUMBER = ?", new String[]{id});
    }

    //delete row - first, last by key
    public boolean deleteRow() {
        return true;
    }

    public int deleteAllRow() {
        int count = 0;
        return count;
    }
}


