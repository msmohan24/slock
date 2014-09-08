package com.slock;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BackupUtils extends Utils {

    public static Uri getAllContacts(ContentResolver cr, Uri internal, Context context, String timeStamp) {

    	String[] contactsArray = new String[2];
        Uri contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        
        Cursor cur = cr.query(contactsUri, null, null, null, null);
        
        FileOutputStream fOut = null;
        try {
            fOut = context.openFileOutput("contacts_" + timeStamp + ".txt",
                    Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        
        while (cur.moveToNext()) {
        	contactsArray[0] = cur.getString(cur.
        			getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).toString();
        	contactsArray[1] = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
        
            writeToOutputStreamArray(contactsArray, osw);
        }
        
        try {
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return internal;
    }
    
    public static Uri getSMSLogs(ContentResolver cr, Uri internal, Context context, String timeStamp) {
        String[] smsLogArray = new String[2];
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cur= cr.query(uri, null, null ,null,null);
        //Cursor cur= cr.query(uri, smsLogArray, null ,null,null);
        FileOutputStream fOut = null;
        
        try {
            fOut = context.openFileOutput("sms_logs_" + timeStamp + ".txt", Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        OutputStreamWriter osw = new OutputStreamWriter(fOut);
        
        while (cur.moveToNext()) {
            smsLogArray[0] = cur.getString(cur.getColumnIndexOrThrow("address")).toString();
            smsLogArray[1] = cur.getString(cur.getColumnIndexOrThrow("body")).toString();
            
            writeToOutputStreamArray(smsLogArray, osw);
        }
        
        try {
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return internal;
    }
    
    private static void writeToOutputStreamArray(String[] array, OutputStreamWriter oswriter) {
        for (int i = 0; i < array.length; i++) {
            try {
                oswriter.append(array[i] + "  ");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            oswriter.append("\n\n");
            oswriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
