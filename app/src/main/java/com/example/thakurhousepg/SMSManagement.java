package com.example.thakurhousepg;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.io.IOException;

public class SMSManagement {
    private static SMSManagement _instance = null;
    private static Context _context = null;

    enum SMS_TYPE {
        RENT,
        RECEIPT,
        PENALTY,
        DEFAULT
    };

    public SMSManagement(Context context) {
        _context = context;
    }

    public static SMSManagement getInstance() {
        if(_instance == null && _context != null) {
            _instance = new SMSManagement(_context);
        }
        return _instance;
    }

    public static void setContext(Context context) {
        _context = context;
    }
    public static void sendSMS(String sendToMobile, SMS_TYPE type){
        String scAddr = null;
        PendingIntent sentIntent = null, deliveryIntent = null;
        String msg = "Test Message";
//        if(type == SMS_TYPE.RENT) {
//            msg = "Room#" + roomNo + ", Your total outstanding Amount is: " + outstanding + ".\r\n"
//                    + "Rent: " + rent + ", Deposit: " + Deposit + ", Penalty: " + penalty + ".\r\n"
//                    + "Please pay your rent before 5th of " + month + " to avoid Rs.200 penalty.\r\n"
//                    + "Thanks - THAKUR HOUSE PG";
//        } else {
//            msg = "Room#" + roomNo + ", Your total outstanding Amount is: " + outstanding + ".\r\n"
//                    + "Rent: " + rent + ", Deposit: " + Deposit + ", Penalty: " + penalty + ".\r\n"
//                    + "Please pay your outstanding immediately\r\n"
//                    + "Thanks - THAKUR HOUSE PG";
//        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(sendToMobile, scAddr, msg, sentIntent, deliveryIntent);

        Toast.makeText(_context, "Sending SMS To "+ sendToMobile, Toast.LENGTH_SHORT).show();
    }
}
