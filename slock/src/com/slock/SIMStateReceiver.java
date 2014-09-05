package com.slock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.slock.fragments.AdvancedSettingsFragment;

import com.slock.SLOCKMainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SIMStateReceiver extends BroadcastReceiver {
    private static final String TAG = "SLOCK";
    
    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private static final String EXTRAS_SIM_STATUS = "ss";
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences localSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
        String address = preferences.getString("smob", "");
        //String address = preferences.getString("advanced_backup_phone_number", "0000000000" ); 
        
        if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
            Log.v(TAG, "SIM STATUS CHANGED");
            Bundle extras = intent.getExtras();
            
            if(extras != null) {
                String ss = extras.getString(EXTRAS_SIM_STATUS);
                
                if(ss.equals("ABSENT")) {
                    Log.v(TAG, "SIM IS ABSENT");
                } else if (ss.equals("IMSI") || ss.equals("LOADED")) {
                    Log.v(TAG, "SIM IS PRIMED");
                    TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String imsi = mTelephonyManager.getSubscriberId();
                    String newSim = mTelephonyManager.getLine1Number();
                    //
                    //////
                    // what to do for inlist ??
                    // retrieve and store primary mobile subscriber id as pmob and compare it with new sim's imsi/sid
                    // else
                    // store pmob sid in list only when the app is active (or) display old one and save new one
                    	//on activating back. app is active and new sim's sid is mismatched send sms
                    ////////
                    
                    if(isInList(imsi)) {
                        Log.v(TAG, "SIM ACCEPTED");
                    } else {
                        Utils.lockDeviceDefault(context);
                        try {
                            Intent locateIntent = new Intent(context,BackupToDropboxActivity.class);
                            locateIntent.putExtra("address", address);
                            context.startActivity(locateIntent);
                            Log.i(TAG, "Locate intent sent");
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to locate device");
                            Log.e(TAG, e.toString());
                            Utils.sendSMS(context, address, "SLOCK: Failed to locate device. Error: " + e.toString());
                        }
                        Log.v(TAG, "SIM NOT IN LIST, LOCK IT AND TRACK IT TO BACKUP NUMBER!");
                    }
                    Log.v(TAG, "SIM IMSI " + imsi);
                } else {
                    Log.v(TAG, "SIM IS " + ss);
                }
            }
        }
        
    }

    /*
     * But really, should make this logic a bit better. Empty arraylist just passes through.
     * If the device doesn't actually give a proper imsi from the new SIM card, then it's wide open
     * to usage.
     */
    private boolean isInList(String imsi) {
        ArrayList<String> identifiers;
        Set<String> set = getList();
        if(set == null) {
            identifiers = new ArrayList<String>();
        } else {
            identifiers = new ArrayList<String>(set);
        }

        //if(identifiers == null)
          //  return true;

        if(identifiers.contains(imsi)) { // || identifiers.isEmpty()) {
            return true;
        }
        return false;
    }

    private Set<String> getList() {
        Set<String> set = new HashSet<String>();
        SharedPreferences prefs = mContext.getSharedPreferences("imsi_list", 0);
        set = prefs.getStringSet("identifiers", null);
        if(set == null)
            return null;
        Log.v(TAG, set.toString());
        return set;
    }
}
