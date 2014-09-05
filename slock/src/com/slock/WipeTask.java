package com.slock;

import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

//import com.slock.fragments.SMSWipeFragment;

import java.io.File;

public class WipeTask extends AsyncTask<Void, Void, Void> {
    private ProgressDialog dialog = null;
    private Context context;
    private File[] sdcards; // wipe internal memory - try factory reset - reset will remove app/app data ?
    private String address;
    private DevicePolicyManager devicePolicyManager;
    private SharedPreferences preferences;

    public WipeTask(Context context, File[] sdcards, String address) {
        this.address = address;
        this.context = context;
        this.sdcards = sdcards;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle(context.getResources().getString(R.string.app_name));
        dialog.setMessage("Wiping sdcard contents...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        boolean wipesdcards  = preferences.getBoolean("wipe_sdcard", false);

        if(wipesdcards) {
            for(int i = 0; i < sdcards.length; ++i){
                wipeSdcard(sdcards[i]);
            }
            Utils.sendSMS(context, address, "SLOCK: Wiping sdcard(s).");
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        dialog.dismiss();

        //Call after background worker to not interfere with system resources
        if (devicePolicyManager.isAdminActive(SLOCKMainActivity.adminComponent)) {
            try {
                Utils.sendSMS(context, address, "SLOCK: Wiping device.");
                devicePolicyManager.wipeData(0);
            } catch (Exception e) {
                Utils.sendSMS(context, address, "SLOCK: Failed to wipe device. Error:");
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
    
    /*
    // delete all contacts - both sim and mobile contacts ?
     * not necessary to delete sim contacts, deleting mobile contacts is enough bcos he wont use sim
     *   
    ContentResolver cr = getContentResolver();
    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null);
    while (cur.moveToNext()) {
        try{
            String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
            System.out.println("The uri is " + uri.toString());
            cr.delete(uri, null, null);
        }
        catch(Exception e)
        {
            System.out.println(e.getStackTrace());
        }
    }
    */
    
    /*
     * delete all sms
     * 
    Uri inboxUri = Uri.parse("content://sms/conversations");
    int count = 0;
    Cursor c = context.getContentResolver().query(inboxUri , null, null, null, null);
    while (c.moveToNext()) {
        try {
            // Delete the SMS
            String pid = c.getString(0); // Get id;
            String uri = "content://sms/" + pid;
            count = context.getContentResolver().delete(Uri.parse(uri),
                    null, null);
        } catch (Exception e) {
        }
    }
    return count;
    */
    
    /*
     * for deleting forgot pin sms sent from primary mobile
     * 
     * delete sms by phone number and content
     * so, phone number should be primary mobile, body/content should be "__"
     * 
     public void deleteSMS(Context context, String message, String number) {
    try {
        mLogger.logInfo("Deleting SMS from inbox");
        Uri uriSms = Uri.parse("content://sms/inbox");
        Cursor c = context.getContentResolver().query(uriSms,
            new String[] { "_id", "thread_id", "address",
                "person", "date", "body" }, null, null, null);

        if (c != null && c.moveToFirst()) {
            do {
                long id = c.getLong(0);
                long threadId = c.getLong(1);
                String address = c.getString(2);
                String body = c.getString(5);

                if (message.equals(body) && address.equals(number)) {
                    mLogger.logInfo("Deleting SMS with id: " + threadId);
                    context.getContentResolver().delete(
                        Uri.parse("content://sms/" + id), null, null);
                }
            } while (c.moveToNext());
        }
    } catch (Exception e) {
        mLogger.logError("Could not delete SMS from inbox: " + e.getMessage());
    }
	}
	*/

    /*
	* You can delete all SMS of a particular number by using,
	
	private void removeMessage(Context context, String fromAddress) {

    Uri uriSMS = Uri.parse("content://sms/inbox");
    Cursor cursor = context.getContentResolver().query(uriSMS, null, 
                                                               null, null, null);
    cursor.moveToFirst();
      if(cursor.getCount() > 0){
        int ThreadId = cursor.getInt(1);
        Log.d("Thread Id", ThreadId+" id - "+cursor.getInt(0));
        Log.d("contact number", cursor.getString(2));
        Log.d("column name", cursor.getColumnName(2));

        context.getContentResolver().delete(Uri.
                parse("content://sms/conversations/"+ThreadId), "address=?", 
                                                      new String[]{fromAddress});
        Log.d("Message Thread Deleted", fromAddress);
      }
      cursor.close();
	}
	*/


}
