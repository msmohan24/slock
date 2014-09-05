package com.slock.fragments;

import com.slock.SLOCKMainActivity;

import android.app.DialogFragment;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceClickListener;

public class AdvancedSettingsFragment extends PreferenceFragment {
    private static final String TAG = "SLOCK";
    private static boolean dropboxAccess;
    public static final String PREFERENCES_DROPBOX_BACKUP_CHECKED = "dropbox_account_chosen";
    public static final String PREFERENCES_CONFIRMATION_SMS = "advanced_enable_confirmation_sms";
    public static final String PREFERENCES_ABORT_BROADCAST = "advanced_enable_abort_broadcast";
    public static final String PREFERENCES_BACKUP_PHONE_NUMBER = "advanced_backup_phone_number";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.advanced_preferences);

        final DevicePolicyManager mDPM = (DevicePolicyManager) getActivity()
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        
        if (mDPM.getActiveAdmins() == null || !mDPM.isAdminActive(SLOCKMainActivity.adminComponent)) {
            PreferenceCategory mCategory = (PreferenceCategory) findPreference("advanced_category");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences prefs1 = getActivity().getSharedPreferences("dropbox_prefs", 0);
        String key = prefs1.getString("dropbox_access_key", null);
        dropboxAccess = getKeyHonesty(key);

        final CheckBoxPreference dropboxAccount = (CheckBoxPreference) findPreference("dropbox_account_chosen");

        if (getDropboxAccess()) {
            dropboxAccount.setSummary("Active");
            dropboxAccount.setChecked(true);
        } else {
            dropboxAccount.setSummary("Inactive");
            dropboxAccount.setChecked(false);
        }
    }


    private void disableDependents() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SMSLockFragment.PREFERENCES_LOCK_ENABLED, false);
        editor.putBoolean(SMSWipeFragment.PREFERENCES_WIPE_ENABLED, false);
        editor.commit();
    }

    private boolean getKeyHonesty(String key) {
        if(key != null) {
            return true;
        }
        return false;
    }

    private static boolean getDropboxAccess() {
        return dropboxAccess;
    }
}
