package com.example.thakurhousepg.NewFiles.OccupancyPackage;

import android.database.sqlite.SQLiteDatabase;

import com.example.thakurhousepg.NewFiles.DataModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class BedsListContent {

    /**
     * An array of Beds.
     */
    public static List<BedsListItem> items = new ArrayList<BedsListItem>();

    public static void create(DataModule dataModule) {
        SQLiteDatabase db = dataModule.getReadableDatabase();
        ArrayList<DataModule.Bed> beds = dataModule.getBedsList();

//        Cursor cursor = db.rawQuery("select TENANT_NAME, " + DataModule.TENANT_TABLE_NAME + ".TENANT_ID from " + DataModule.TENANT_TABLE_NAME +
//                " LEFT JOIN " + DataModule.BOOKING_TABLE_NAME + " ON " + DataModule.BOOKING_TABLE_NAME + ".BOOKING_ID = " + DataModule.TENANT_TABLE_NAME + ".BOOKING_ID" +
//                " where TENANT_IS_CURRENT = 1" ,
//                null);

        for (DataModule.Bed bed:beds) {
            DataModule.Tenant tenant = null;
            DataModule.Booking booking = null;
            if(bed.bookingId != null) {
                booking = dataModule.getBooking(bed.bookingId);
                tenant = dataModule.getTenantInfoForBooking(bed.bookingId);
            }

            items.add(new BedsListItem(bed.bedNumber, (tenant != null)?tenant.name:"", (booking!=null)?booking.rentAmount:bed.rentAmount));
        }
    }

    /**
     * Represents a single Item in the Beds List.
     */
    public static class BedsListItem {
        public final String bedNumber;
        public final String tenantName;
        public final String rentPending;

        public BedsListItem(String bedNumber, String tenantName, String rentPending) {
            this.bedNumber = bedNumber;
            this.tenantName = tenantName;
            this.rentPending = rentPending;
        }

        @Override
        public String toString() {
            return bedNumber + " " + tenantName + " " +rentPending;
        }
    }
}
