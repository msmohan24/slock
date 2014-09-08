package com.slock;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class OnBootReceiver extends BroadcastReceiver
{
  static void sendSMS(String address, String content, Context paramContext)
  {
    PendingIntent localPendingIntent = PendingIntent.getActivity(paramContext, 0, new Intent(paramContext, OnBootReceiver.class), 0);
    SmsManager.getDefault().sendTextMessage(address, null, content, localPendingIntent, null);
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ("android.intent.action.BOOT_COMPLETED".equals(paramIntent.getAction()))
    {
      TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService("phone");
      		//(TelephonyManager)paramContext.getSystemService(Context.TELEPHONY_SERVICE)
      
      SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("file", 0);
      //String primmob = localSharedPreferences.getString("pmob", "");
      String secmob = localSharedPreferences.getString("smob", "");
      String msgcontent = "[SLOCK] Android Mobile of IMEI NO: " + localTelephonyManager.getDeviceId() + " is currently using the SIM card service provider: " + localTelephonyManager.getSimOperatorName();
      String msgcontent2 = "Reply this SMS with below content.\n[SLOCK] [BACKUP:YES][WIPE:YES][LOCK:YES]";
      String simnum = localSharedPreferences.getString("simno", "");
      String activstatus = localSharedPreferences.getString("activ", "");
      String crtsimnum = ((TelephonyManager)paramContext.getSystemService("phone")).getSimSerialNumber();
      	//String num1=localTelephonyManager.getLine1Number();
      
      if ((secmob.length() == 0) || (activstatus.equalsIgnoreCase("NO")))
        System.exit(0);
      if ((!simnum.equals(crtsimnum)) && (activstatus.equalsIgnoreCase("YES")))
      {
        sendSMS(secmob, msgcontent, paramContext);
        sendSMS(secmob, msgcontent2, paramContext);
      }
    }
    else
    {
      return;
    }
    System.exit(0);
  }
}
