package com.example.thakurhousepg;

public class TenantTable {
    private String roomNumber;
    private String tenantName;
    private String tenantMobile;
    private String tenantAdmDate;
    private String tenantOutstanding;


    public TenantTable(String rNum, String name, String mobile, String admDate, String outstanding){
        roomNumber = rNum;
        tenantName = name;
        tenantMobile = mobile;
        tenantAdmDate = admDate;
        tenantOutstanding = outstanding;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getTenantMobile() {
        return tenantMobile;
    }

    public String getTenantAdmDate() {
        return tenantAdmDate;
    }

    public String getTenantOutstanding() {
        return tenantOutstanding;
    }
}
