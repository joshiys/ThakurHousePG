package com.example.thakurhousepg;

public class TableViewColumns {
    private String roomNumber;
    private String outstandingRent;
    private String OutstandingDeposit;
    private String outstandingPenalty;
    private DataModel.Pending pendingEntry = null;


    public TableViewColumns(String rNum, String rent, String deposit, String penalty){
        roomNumber = rNum;
        outstandingRent = rent;
        OutstandingDeposit = deposit;
        outstandingPenalty = penalty;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getOutstandingRent() {
        return outstandingRent;
    }

    public String getOutstandingDeposit() {
        return OutstandingDeposit;
    }

    public String getOutstandingPenalty() {
        return outstandingPenalty;
    }

    public DataModel.Pending getPendingEntry() { return pendingEntry; }


    public static TableViewColumns fromPendingEntry(DataModel.Pending pendingEntry) {
        String rent = "0", deposit = "0", penalty = "0";

        if (pendingEntry.type == DataModel.PendingType.RENT) {
            rent = String.valueOf(pendingEntry.pendingAmt);
        } else if (pendingEntry.type == DataModel.PendingType.DEPOSIT) {
            deposit = String.valueOf(pendingEntry.pendingAmt);
        } else {
            penalty = String.valueOf(pendingEntry.pendingAmt);
        }

        DataModel.Booking booking = NetworkDataModule.getInstance().getBookingInfo(pendingEntry.bookingId);
        TableViewColumns newEntry = new TableViewColumns(booking.bedNumber, rent, deposit, penalty);
        newEntry.pendingEntry = pendingEntry;

        return newEntry;
    }
}
