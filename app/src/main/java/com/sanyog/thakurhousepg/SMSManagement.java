package com.sanyog.thakurhousepg;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SMSManagement {
    private static SMSManagement _instance = null;
    private static Context _context = null;
    private NetworkDataModule dataModule = NetworkDataModule.getInstance();

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

    public String getSMSMessage(String bookingID, DataModel.Tenant tenant, int amount/* only for receipts*/, SMSManagement.SMS_TYPE smsType){
        String msg = "";
        int deposit = 0, rent = 0, penalty = 0, outstanding = 0;
//        HashMap<PendingType, String> pendingsHash;
        ArrayList<DataModel.Pending> pendingList = dataModule.getPendingEntriesForBooking(bookingID);
        for (DataModel.Pending pendingEntry : pendingList) {
            if(pendingEntry.type == DataModel.PendingType.DEPOSIT){
                deposit = pendingEntry.pendingAmt;
            } else if(pendingEntry.type == DataModel.PendingType.RENT){
                rent = pendingEntry.pendingAmt;
            } else if(pendingEntry.type == DataModel.PendingType.PENALTY){
                penalty = pendingEntry.pendingAmt;
            }
        }
        outstanding += rent + deposit + penalty;


        if(smsType == SMSManagement.SMS_TYPE.BOOKING) {
            /* From bed View screen from Floating Button */
            //XXX : For Booking add Tenant Name and Booking Month
            DataModel.Booking booking = dataModule.getBookingInfo(bookingID);
            msg = "Dear "+ tenant.name + ", Your Booking is confirmed for Room#" + dataModule.getBookingInfo(bookingID).bedNumber + " from Date: " + booking.bookingDate + "(yyyy-mm-dd).\r\n" +
                    "Your Monthly rent amount is: Rs." + booking.rentAmount + " And Deposit Amount is: Rs." + booking.depositAmount + ".\r\n"
                    + "Please make sure that you pay your monthly rent before 5th of every month to avoid Rs.200 penalty.\r\n"
//                    + "Please pay your rent before 5th of " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " to avoid Rs.200 penalty.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else if(smsType == SMSManagement.SMS_TYPE.MONTHLY_RENT) {
            /* will be sent automatically once at the start of every month*/
            msg = "Dear Room#" + dataModule.getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", Your monthly rent is generated for Month: "
                    + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + "\r\n"
                    + "Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "Please pay your rent before 5th of " + Calendar.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " to avoid Rs.200 penalty.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else if(smsType == SMSManagement.SMS_TYPE.RECEIPT) {
            /* From Receipt Screen when payment is received */
            msg = "Dear Room#" + dataModule.getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", Payment received of Rs."+ amount + ".\r\n"
                    + "Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "In case there is outstanding, please pay immediately.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else if(smsType == SMSManagement.SMS_TYPE.PENALTY_GENERATED){
            /* Send SMS Automatically when penalty is generated */
            msg = "Dear Room#" + dataModule.getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", Penalty of Rs.200 is applicable to you due to non payment of Rent on time.\r\n"
                    + "Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "Please pay your outstanding immediately.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else if(smsType == SMSManagement.SMS_TYPE.DUE_REMINDER) {
            /* From Recepit screen using Floating Button*/
            msg = "Dear Room#" + dataModule.getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", This is a reminder to pay your monthly rent immediately.\r\n"
                    + "Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "Please ignore if already paid.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        } else {
            /* From Main screen */
            /* SMS_TYPE == DEFAULT
                send general sms even if there is no pending, penalty or month change.
                this can be done from main "send SMS"
            */
            msg = "Dear Room#" + dataModule.getBookingInfo(bookingID).bedNumber + "/" + tenant.name + ", Your total outstanding Amount is: Rs." + outstanding + ".\r\n"
                    + "Rent: Rs." + rent + ", Deposit: Rs." + deposit + ", Penalty: Rs." + penalty + ".\r\n"
                    + "Please pay your outstanding immediately.\r\n"
                    + "Please ignore if already paid.\r\n"
                    + "Thanks - THAKUR HOUSE PG";
            Log.d("SMS", msg);
        }
        return msg;
    }

}
