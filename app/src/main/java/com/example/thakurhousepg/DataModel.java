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
        public String bedNumber;
        public String bookingId;
        @SerializedName("defaultRent")
        public String rentAmount;
        @SerializedName("defaultDeposit")
        public String depositAmount;
        public Boolean isOccupied;

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
        public String bedNumber;
        public String rentAmount;
        public String depositAmount;
        public String bookingDate;
        public Boolean isWholeRoom;
        public String tenantId;
        public String closingDate;

        public Booking(String bookingId, String bedNumber, String rentAmount, String depositAmount, String bookingDate, Boolean isWholeRoom, String tenantId, String closingDate) {
            this.bedNumber = bedNumber;
            this.id = bookingId;
            this.rentAmount = rentAmount;
            this.depositAmount = depositAmount;
            this.bookingDate = bookingDate;
            this.isWholeRoom = isWholeRoom;
            this.tenantId = tenantId;
            this.closingDate = closingDate;
        }
    }

    public static class Tenant implements java.io.Serializable {
        @SerializedName("tenantId")
        public String id;
        public String name;
        public String mobile;
        public String email;
        public String address;
        public Boolean isCurrent;
        public String parentId;

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
        public int pendingMonth;

        public Pending(int pendingAmt, String bookingId, String tenantId, PendingType type, int pendingMonth) {
            this.pendingAmt = pendingAmt;
            this.bookingId = bookingId;
            this.tenantId = tenantId;
            this.type = type;
            this.pendingMonth = pendingMonth;
        }
    }
}
