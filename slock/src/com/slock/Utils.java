package com.slock;

import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class Utils {
    private static final String TAG = "SLOCK";
    
    private static DevicePolicyManager devicePolicyManager;

    static void sendSMS(Context paramContext, String address, String content)
    {
      PendingIntent localPendingIntent = PendingIntent.getActivity(paramContext, 0, new Intent(paramContext, Utils.class), 0);
      SmsManager.getDefault().sendTextMessage(address, null, content, localPendingIntent, null);
    }
    
    protected static void lockDeviceDefault(Context context) {
        devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        final String password = "sLOCK";
        if (password.length() > 0) {
            devicePolicyManager.resetPassword(password, 0);
        }
        Log.i(TAG, "Locking device");
        devicePolicyManager.lockNow();
        Utils.sendSMS(context, SMSReceiver.address, "[SLOCK] Locked device with password: " + password);
        
        System.exit(0);
    }

}
