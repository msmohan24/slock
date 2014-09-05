package com.slock;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class Utils {
    private static final String TAG = "SLOCK";
    
    /*protected static void sendSMS(Context context, String address, String content)
    {        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);
 
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(address, null, content, sentPI, deliveredPI);
    }*/

    static void sendSMS(Context paramContext, String address, String content)
    {
      PendingIntent localPendingIntent = PendingIntent.getActivity(paramContext, 0, new Intent(paramContext, Utils.class), 0);
      SmsManager.getDefault().sendTextMessage(address, null, content, localPendingIntent, null);
    }
    
    protected static void lockDeviceDefault(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        final String password = "sLOCK";
        if (password.length() > 0) {
            devicePolicyManager.resetPassword(password, 0);
        }
        devicePolicyManager.lockNow();
    }
    
    protected static void lockDevice(Context context) {
        
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        final String password = "sLOCK";
        
        if (devicePolicyManager.isAdminActive(SLOCKMainActivity.adminComponent)) {
            if (password.length() > 0) {
                devicePolicyManager.resetPassword(password, 0);
            }
            
            try {
                Log.i(TAG, "Locking device");
                devicePolicyManager.lockNow();
                Utils.sendSMS(context, SMSReceiver.address, "SLOCK: Locked device with password: " + password);
            } catch (Exception e) {
                Log.wtf(TAG, "Failed to lock device");
                Log.wtf(TAG, e.toString());
                Utils.sendSMS(context, SMSReceiver.address, "SLOCK: Failed to lock device. Error: " + e.toString());
            }
        }
    }

}
