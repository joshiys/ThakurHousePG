package com.example.thakurhousepg;

public class MasterTable {
    private String roomNumber;
    private String roomRent;


    public MasterTable(String rNum, String rRent){
        roomNumber = rNum;
        roomRent = rRent;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomRent() {
        return roomRent;
    }
}
