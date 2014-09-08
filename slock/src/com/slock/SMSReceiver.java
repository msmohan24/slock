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

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SLOCK";
    
    private static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String EXTRA_SMS_PDUS = "pdus";
    protected static String address;
    
    
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
                
                Log.i(TAG, "Received SMS");

                SmsMessage[] messages = getMessagesFromIntent(intent);
                for (SmsMessage sms : messages) {
                    String body = sms.getMessageBody();
                    address = sms.getOriginatingAddress();

                    SharedPreferences localSharedPreferences = context.getSharedPreferences("file", 0);
                    String secmob = localSharedPreferences.getString("smob", "");
                    
                    if (address.equals(secmob)) {
                    	
                    boolean backupEnabled = preferences.getBoolean("backup_enabled", false);
                    boolean wipeEnabled = preferences.getBoolean("wipe_enabled", false);
                    boolean lockEnabled = preferences.getBoolean("lock_enabled", false);

                    if (lockEnabled && body.startsWith("[SLOCK]") && body.contains("[LOCK:YES]")) {
                        Utils.lockDeviceDefault(context);
                            abortBroadcast();
                    }

                    if (backupEnabled && body.startsWith("[SLOCK]") && body.contains("[BACKUP:YES]")) {
                            Intent backupDropboxIntent = new Intent(context, BackupToDropboxActivity.class);
                            backupDropboxIntent.putExtra("fromReceiver", address);
                            backupDropboxIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                            	.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            	.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            context.startActivity(backupDropboxIntent);
                            	abortBroadcast();
                    }

                    if(wipeEnabled && body.startsWith("[SLOCK]") && body.contains("[WIPE:YES]")) {
                        Intent wipeIntent = new Intent(context, WipeDataActivity.class);
                        wipeIntent.putExtra("address", address);
                        wipeIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                    		.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    		.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        context.startActivity(wipeIntent);
                            abortBroadcast();
                    }
                    
                    }
                    else {
                    	// do nothing
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
