package com.example.thakurhousepg;

public class TableViewColumns {
    private String roomNumber;
    private String outstandingRent;
    private String OutstandingDeposit;
    private String outstandingPenalty;
//    private String roomRent;


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
}
