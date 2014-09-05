package com.slock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.slock.fragments.*;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SLOCK";
    
    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String EXTRA_SMS_PDUS = "pdus";
    protected static String address;
    
    
   	//BroadcastReceiver myBroadcast = new SMSReceiver();
    public void onCreate() {
    	//SMSReceiver myBroadcast = new SMSReceiver();
    	this.registerReceiver(new SMSReceiver(), new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }
    
    private void registerReceiver(BroadcastReceiver myBroadcast,
			IntentFilter intentFilter) {
		// TODO Auto-generated method stub
		
	}
    
    @Override
    public void onReceive(Context context, Intent intent) {
    	
        if (intent.getAction().equals(ACTION_SMS_RECEIVED)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                System.out.println(preferences);
                
                Log.i(TAG, "Received SMS");

                SmsMessage[] messages = getMessagesFromIntent(intent);
                for (SmsMessage sms : messages) {
                    String body = sms.getMessageBody();
                    address = sms.getOriginatingAddress();

                    //SharedPreferences pref = getSharedPreferences("file", 0);
                    //boolean isBackupEnabled = pref.getString("backup_enabled", "").equalsIgnoreCase("YES");
                    
                    boolean backupEnabled = preferences.getBoolean("backup_enabled", false);
                    //boolean backupEnabled = preferences.getString("backup_enabled", "").equalsIgnoreCase("YES"); // backupEnabled=true
                    //boolean wipeEnabled = preferences.getString("wipe_enabled", "").equalsIgnoreCase("YES"); // wipeEnabled=true
                    //boolean lockEnabled = preferences.getString("lock_enabled", "").equalsIgnoreCase("YES"); // lockEnabled=true

                    //boolean backupEnabled = true;
                    boolean wipeEnabled = true;
                    boolean lockEnabled = false;
                    
                    String actionSMS = "[SLOCK][BACKUP:YES][WIPE:YES][LOCK:YES]";
                    String actionBackupSms = "backup";
                    String actionWipeSms = "wipe";
                    String actionLockSms = "lock";
                                    
                    if (lockEnabled && body.startsWith(actionLockSms)) {
                        Utils.lockDevice(context);
                            //abortBroadcast();
                    }

                    if(wipeEnabled && body.startsWith(actionWipeSms)) {
                        Intent wipeIntent = new Intent(context, WipeDataActivity.class);
                        wipeIntent.putExtra("address", address);
                        context.startActivity(wipeIntent);
                            //abortBroadcast();
                    }

                    //if (backupEnabled && body.startsWith(actionBackupSms)) {
                    if (backupEnabled && body.startsWith("[SLOCK]") && body.contains("[BACKUP:YES]")) {
                            Intent backupDropboxIntent = new Intent(context, BackupToDropboxActivity.class);
                            backupDropboxIntent.putExtra("fromReceiver", address);
                            backupDropboxIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                            	.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            	.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            context.startActivity(backupDropboxIntent);
                            //abortBroadcast();
                    }
                }
            }
        }
    }

    private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent
                .getSerializableExtra(EXTRA_SMS_PDUS);
        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }
}
