package com.slock;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.File;

public class WipeTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private File[] sdcards;
    private String address;
    private DevicePolicyManager devicePolicyManager;

    public WipeTask(Context context, File[] sdcards, String address) {
        this.address = address;
        this.context = context;
        this.sdcards = sdcards;
    }

    @Override
    protected void onPreExecute() {
        devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    protected Void doInBackground(Void... params) {
    	boolean wipesdcards  = true;
        if(wipesdcards) {
            for(int i = 0; i < sdcards.length; ++i){
                wipeSdcard(sdcards[i]);
            }
            Utils.sendSMS(context, address, "[SLOCK] Wiping sdcard(s).");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        //Call after background worker to not interfere with system resources
        if (devicePolicyManager.isAdminActive(SLOCKMainActivity.adminComponent)) {
            try {
                Utils.sendSMS(context, address, "[SLOCK] Wiping device.");
                devicePolicyManager.wipeData(0);
            } catch (Exception e) {
                Utils.sendSMS(context, address, "[SLOCK] Failed to wipe device.");
            }
        }
    }

    public void wipeSdcard(File locations) {
        Log.v("SLOCK", "Wiping " + locations.getName());
        try {
            File[] filenames = locations.listFiles();
            if (filenames != null && filenames.length > 0) {
                for (File tempFile : filenames) {
                    if (tempFile.isDirectory()) {
                        wipeDirectory(tempFile.toString());
                        tempFile.delete();
                    } else {
                        tempFile.delete();
                    }
                }
            } else {
                locations.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wipeDirectory(String name) {
        File directoryFile = new File(name);
        File[] filenames = directoryFile.listFiles();
        if (filenames != null && filenames.length > 0) {
            for (File tempFile : filenames) {
                if (tempFile.isDirectory()) {
                    wipeDirectory(tempFile.toString());
                    tempFile.delete();
                } else {
                    tempFile.delete();
                }
            }
        } else {
            directoryFile.delete();
        }
    }

}
