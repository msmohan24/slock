package com.slock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.Toast;
import android.telephony.SmsMessage;
 
public class SmsEngine extends BroadcastReceiver {
 
    /* package */ static final String ACTION =
            "android.provider.Telephony.SMS_RECEIVED";
 
    public void onReceiveIntent(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            StringBuilder buf = new StringBuilder();
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                for (int i = 0; i < messages.length; i++) {
                    SmsMessage message = messages[i];
                    buf.append("Received SMS from  ");
                    buf.append(message.getDisplayOriginatingAddress());
                    buf.append(" - ");
                    buf.append(message.getDisplayMessageBody());
                }
            }
 
            Toast.makeText(context, buf.toString(), Toast.LENGTH_LONG).show();
 
        }
    }
 
    /*private void appendData(StringBuilder buf, String key, String value) {
        buf.append(", ");
        buf.append(key);
        buf.append('=');
        buf.append(value);
    }*/

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
	}
}