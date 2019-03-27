package com.example.thakurhousepg;

import android.util.ArraySet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class BedsListContent {
    private static final String TAG = "BedsListContent";

    /**
     * An array of Beds.
     */
    public static List<BedsListItem> items = new ArrayList<BedsListItem>();
    private static ArraySet<String> rooms = new ArraySet<String>();

    public static void create(DataModule dataModule) {
        if(items.isEmpty()) {
            Log.i(TAG, "Creating Beds list");
            ArrayList<DataModule.Bed> beds = dataModule.getBedsList();

            for (DataModule.Bed bed : beds) {
                DataModule.Tenant tenant = null;
                DataModule.Booking booking = null;
                String pendingAmount = bed.rentAmount;

                if (bed.bookingId != null) {
                    booking = dataModule.getBookingInfo(bed.bookingId);
                    tenant = dataModule.getTenantInfoForBooking(bed.bookingId);
                    pendingAmount = String.valueOf(dataModule.getPendingAmountForBooking(booking.id));
                }

                if(OccupancyAndBookingActivity.isRoomForSelectedTab(Integer.valueOf(bed.bedNumber.split("\\.")[0]))) {
                    items.add(new BedsListItem(bed.bedNumber, (tenant != null) ? tenant.name : "", (booking != null) ? pendingAmount : bed.rentAmount));
                }
            }
        }
    }

    public static void refresh(DataModule dataModule) {
        items.clear();
        create(dataModule);
    }

    public static void update(String bedNumber, String tenantName, String rent){
        for (BedsListItem item: items) {
            if(item.bedNumber == bedNumber) {
                item.tenantName = tenantName;
                item.rentPayble = rent;
            }
        }
    }

    /**
     * Represents a single Item in the Beds List.
     */
    public static class BedsListItem {
        public String bedNumber;
        public String tenantName;
        public String rentPayble;
        public boolean isPending;

        public BedsListItem(String bedNumber, String tenantName, String rentPayble) {
            this.bedNumber = bedNumber;
            this.tenantName = tenantName;
            this.rentPayble = rentPayble;
            this.isPending = false;
        }

        @Override
        public String toString() {
            return bedNumber + " " + tenantName + " " + rentPayble;
        }
    }
}
