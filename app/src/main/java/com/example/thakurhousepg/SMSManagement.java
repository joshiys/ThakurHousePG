package com.example.thakurhousepg;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class SMSManagement {
    private static SMSManagement _instance = null;
    private static Context _context = null;
    private static final String TAG = "SMSManagement";

    enum SMS_TYPE {
        BOOKING,
        CLOSE_BOOKING,
        MONTHLY_RENT,
        RECEIPT,
        PENALTY_GENERATED,
        DUE_REMINDER,
        DEFAULT //Mostly same as REMINDER unless find another purpose
    }

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
    public void sendSMS(String sendToMobile, String msg){
        String scAddr = null;
        PendingIntent sentIntent = null, deliveryIntent = null;
        Log.v(TAG, "Sending SMS to: " + sendToMobile);
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
        smsManager.sendMultipartTextMessage(sendToMobile, null, smsManager.divideMessage(msg), null, null);

        Toast toast = Toast.makeText(_context, "Sending SMS To "+ sendToMobile, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
    }
}
