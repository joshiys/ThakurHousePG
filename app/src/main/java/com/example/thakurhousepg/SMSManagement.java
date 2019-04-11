package com.example.thakurhousepg;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SMSManagement {
    private static SMSManagement _instance = null;
    private static Context _context = null;

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

//        PendingIntent sentIntent = PendingIntent.getBroadcast(_context, 0, new Intent("SMS_SENT"), 0);
//        PendingIntent deliveryIntent = PendingIntent.getBroadcast(_context, 0, new Intent("SMS_DELIVERED"), 0);
//
//        _context.registerReceiver(new BroadcastReceiver()
//        {
//            @Override
//            public void onReceive(Context context, Intent intent)
//            {
//                Toast.makeText(_context, "SMS sent", Toast.LENGTH_SHORT).show();
//                switch (getResultCode())
//                {
////                    case Activity.RESULT_OK:
////                        Toast.makeText(_context, "SMS sent", Toast.LENGTH_SHORT).show();
////                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(_context, "Generic failure", Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(_context, "No service", Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(_context, "Null PDU", Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(_context, "Radio off", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//
//        }, new IntentFilter("SMS_SENT"));
//
//        _context.registerReceiver(new BroadcastReceiver()
//        {
//            @Override
//            public void onReceive(Context arg0, Intent arg1)
//            {
//                Toast.makeText(_context, "SMS not delivered", Toast.LENGTH_SHORT).show();
////                switch (getResultCode())
////                {
////                    case Activity.RESULT_OK:
////                        Toast.makeText(_context, "SMS delivered", Toast.LENGTH_SHORT).show();
////                        break;
////                    case Activity.RESULT_CANCELED:
////                        Toast.makeText(_context, "SMS not delivered", Toast.LENGTH_SHORT).show();
////                        break;
////                }
//            }
//        }, new IntentFilter("SMS_DELIVERED"));

        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(sendToMobile, scAddr, msg, sentIntent, deliveryIntent);
        ArrayList<String> msgParts = smsManager.divideMessage(msg);
        smsManager.sendMultipartTextMessage(sendToMobile, scAddr, msgParts, null, null);

        Toast toast = Toast.makeText(_context, "Sending SMS To "+ sendToMobile, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0); toast.show();
    }
}
