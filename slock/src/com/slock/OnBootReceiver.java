package com.slock;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver
{
  static void sendSMS(String paramString1, String paramString2, Context paramContext)
  {
    PendingIntent localPendingIntent = PendingIntent.getActivity(paramContext, 0, new Intent(paramContext, OnBootReceiver.class), 0);
    SmsManager.getDefault().sendTextMessage(paramString1, null, paramString2, localPendingIntent, null);
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ("android.intent.action.BOOT_COMPLETED".equals(paramIntent.getAction()))
    {
      Log.d("VogueTools", "Got the Boot Event>>>");
      TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService("phone");
      SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("file", 0);
      String str1 = localSharedPreferences.getString("pmob", "");
      String str2 = localSharedPreferences.getString("smob", "");
      localSharedPreferences.getString("deact", "");
      String str3 = "*sLOCK* Android Mobile of IMEI NO: " + localTelephonyManager.getDeviceId() + " is currently using the SIM card service provider:" + localTelephonyManager.getSimOperatorName();
      String str4 = localSharedPreferences.getString("simno", "");
      String str5 = localSharedPreferences.getString("deact", "");
      String str6 = ((TelephonyManager)paramContext.getSystemService("phone")).getSimSerialNumber();
      if ((str1.length() == 0) || (str5.equalsIgnoreCase("YES")))
        System.exit(0);
      if ((!str4.equals(str6)) && (str5.equalsIgnoreCase("NO")))
      {
        sendSMS(str1, str3, paramContext);
        sendSMS(str2, str3, paramContext);
      }
    }
    else
    {
      return;
    }
    System.exit(0);
  }
}
