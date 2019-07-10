package com.sanyog.thakurhousepg;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */
public class BedsListContent {
    private static final String TAG = "BedsListContent";

    /**
     * An array of Beds.
     */
    static HashMap<Integer, List<BedsListItem>> itemMap = new HashMap<>();

    private static void create() {
        NetworkDataModule restService = NetworkDataModule.getInstance();

        for (int i = 0; i < 7; i++) {
            List<BedsListItem> items = itemMap.get(i);
            if(items == null){
                items = new ArrayList<>();
                itemMap.put(i, items);
            }
            if (items.isEmpty()) {
                Log.i(TAG, "Creating Beds list");
                ArrayList<DataModel.Bed> beds = restService.getRoomsList();
    
                for (DataModel.Bed bed : beds) {
                    DataModel.Tenant tenant = null;
                    DataModel.Booking booking = null;
                    String pendingAmount = bed.rentAmount;
    
                    if (bed.bookingId != null) {
                        booking = restService.getBookingInfo(bed.bookingId);
                        tenant = restService.getTenantInfoForBooking(bed.bookingId);
                        pendingAmount = String.valueOf(restService.getPendingAmountForBooking(booking.id));
                    }
                    String tenantName = "";
                    if (tenant != null) {
                        tenantName = tenant.name;
                        if(restService.getDependents(tenant.id).size() > 0) {
                            tenantName += " +";
                        }
                    }
    
                    if (OccupancyAndBookingActivity.isRoomForSelectedTab(Integer.valueOf(bed.bedNumber.split("\\.")[0]), i)) {
                        items.add(new BedsListItem(bed.bedNumber, tenantName, (booking != null) ? pendingAmount : bed.rentAmount));
                    }
                }
            }
        }
    }


    static void refresh() {
        for (int i = 0; i < itemMap.size(); i++) {
            Objects.requireNonNull(itemMap.get(i)).clear();
        }
        create();
    }

    /* SAHIRE Optimise this function if used*/
    public static void update(String bedNumber, String tenantName, String rent){
        for (int i = 0; i < itemMap.size(); i++) {
            List<BedsListItem> items = itemMap.get(i);
            assert items != null;
            for (BedsListItem item : items) {
                if (item.bedNumber.equals(bedNumber)) {
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
        String tenantName;
        String rentPayble;
        boolean isPending;

        BedsListItem(String bedNumber, String tenantName, String rentPayble) {
            this.bedNumber = bedNumber;
            this.tenantName = tenantName;
            this.rentPayble = rentPayble;
            this.isPending = false;
        }

        @NotNull
        @Override
        public String toString() {
            return bedNumber + " " + tenantName + " " + rentPayble;
        }
    }
}
