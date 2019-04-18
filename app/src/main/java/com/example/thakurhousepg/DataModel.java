package com.example.thakurhousepg;

import com.google.gson.annotations.SerializedName;

public class DataModel {
    public enum PendingType {
        RENT,
        DEPOSIT,
        PENALTY;

        public int getIntValue() {
            switch(this) {
                case RENT:
                    return 0;
                case DEPOSIT:
                    return 1;
                case PENALTY:
                    return 2;
                default:
                    return 0;
            }
        }
    }

    public enum ReceiptType {
        RENT,
        DEPOSIT,
        PENALTY,
        ADVANCE,
        WAIVEOFF;

        public int getIntValue() {
            switch(this) {
                case RENT:
                    return 0;
                case DEPOSIT:
                    return 1;
                case PENALTY:
                    return 2;
                case ADVANCE:
                    return 3;
                default:
                    return 0;
            }
        }
    }

    //MARK: ---------------------------------- Data Class Definitions ---------------------------------
    public static class Bed {
        @SerializedName("roomNumber")
        public final String bedNumber;
        public final String bookingId;
        @SerializedName("defaultRent")
        public final String rentAmount;
        @SerializedName("defaultDeposit")
        public final String depositAmount;
        public final Boolean isOccupied;

        public Bed(String bedNumber, String bookingId, String rentAmount, String depositAmount, Boolean isOccupied) {
            this.bedNumber = bedNumber;
            this.bookingId = bookingId;
            this.rentAmount = rentAmount;
            this.depositAmount = depositAmount;
            this.isOccupied = isOccupied;
        }
        @Override
        public String toString() {
            return bedNumber + " , " + bookingId + " , " + rentAmount + " , " + depositAmount + " , " + isOccupied;

        }
    }

    public static class Booking {
        @SerializedName("bookingId")
        public final String id;
        @SerializedName("roomNumber")
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

    public static class Tenant implements java.io.Serializable {
        @SerializedName("tenantId")
        public final String id;
        public final String name;
        public final String mobile;
        public final String email;
        public final String address;
        public final Boolean isCurrent;
        public final String parentId;

        public Tenant(String id, String name, String mobile, String email, String address, Boolean isCurrent, String parentId) {
            this.id = id;
            this.name = name;
            this.mobile = mobile;
            this.isCurrent = isCurrent;
            this.email = email;
            this.address = address;
            this.parentId = parentId;
        }
    }

    public static class Receipt {
        public final String id;
        public final String onlineAmount;
        public final String cashAmount;
        public final String date;
        public final boolean ispPenaltyWaiveOff;
        public final ReceiptType type;
        public final String bookingId;


        public Receipt(String id, String bookingId, String onlineAmount, String cashAmount, boolean ispPenaltyWaiveOff, String date, ReceiptType type) {
            this.id = id;
            this.bookingId = bookingId;
            this.onlineAmount = onlineAmount;
            this.cashAmount = cashAmount;
            this.ispPenaltyWaiveOff = ispPenaltyWaiveOff;
            this.date = date;
            this.type = type;
        }
    }

    public static class Payment {

    }

    public static class Pending {
        @SerializedName("pendingAmount")
        public final int pendingAmt;
        public final String bookingId;
        public final String tenantId;
        @SerializedName("pendingType")
        public final PendingType type;

        public Pending(int pendingAmt, String bookingId, String tenantId, PendingType type) {
            this.pendingAmt = pendingAmt;
            this.bookingId = bookingId;
            this.tenantId = tenantId;
            this.type = type;
        }
    }
}
