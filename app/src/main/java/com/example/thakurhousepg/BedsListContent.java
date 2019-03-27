package com.example.thakurhousepg;

import android.util.ArraySet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public static HashMap<Integer, List<BedsListItem>> itemMap = new HashMap<>();
//    public static List<BedsListItem> items = new ArrayList<BedsListItem>();
    private static ArraySet<String> rooms = new ArraySet<String>();

    public static void create() {
        DataModule dataModule = DataModule.getInstance();

        for (int i = 0; i < 7; i++) {
            List<BedsListItem> items = itemMap.get(Integer.valueOf(i));
            if(items == null){
                items = new ArrayList<BedsListItem>();
                itemMap.put(Integer.valueOf(i), items);
            }
            if (items.isEmpty()) {
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
    
                    if (OccupancyAndBookingActivity.isRoomForSelectedTab(Integer.valueOf(bed.bedNumber.split("\\.")[0]), i)) {
                        items.add(new BedsListItem(bed.bedNumber, (tenant != null) ? tenant.name : "", (booking != null) ? pendingAmount : bed.rentAmount));
                    }
                }
            }
        }
    }


    public static void refresh() {
        for (int i = 0; i < itemMap.size(); i++) {
            itemMap.get(Integer.valueOf(i)).clear();
        }
        create();
    }

    /* SAHIRE Optimise this function if used*/
    public static void update(String bedNumber, String tenantName, String rent){
        for (int i = 0; i < itemMap.size(); i++) {
            List<BedsListItem> items = itemMap.get(Integer.valueOf(i));
            for (BedsListItem item : items) {
                if (item.bedNumber == bedNumber) {
                    item.tenantName = tenantName;
                    item.rentPayble = rent;
                }
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
