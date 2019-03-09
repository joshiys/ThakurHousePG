package com.example.thakurhousepg;

public class TableClass {
    private String roomNumber;
    private String tenantPenalty;
    private String tenantOutDeposit;
    private String roomRent;


    public TableClass(String rNum, String penalty, String outstandingDeposit, String outstanding){
        roomNumber = rNum;
        tenantPenalty = penalty;
        tenantOutDeposit = outstandingDeposit;
        roomRent = outstanding;//outStandingRent
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getTenantName() {
        return tenantPenalty;
    }

    public String getTenantMobile() {
        return tenantOutDeposit;
    }

    public String getRoomRent() {
        return roomRent;
    }
}
