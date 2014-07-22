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
      TelephonyManager localTelephonyManager = (TelephonyManager)paramContext.getSystemService("phone");
      
      //TelephonyManager local2TelephonyManager = (TelephonyManager)paramContext.getSystemService(Context.TELEPHONY_SERVICE)
      //String num1=localTelephonyManager.getLine1Number();
      
      SharedPreferences localSharedPreferences = paramContext.getSharedPreferences("file", 0);
      String primmob = localSharedPreferences.getString("pmob", "");
      String secmob = localSharedPreferences.getString("smob", "");
      String msgcontent = "*sLOCK* Android Mobile of IMEI NO: " + localTelephonyManager.getDeviceId() + " is currently using the SIM card service provider:" + localTelephonyManager.getSimOperatorName();
      String simnum = localSharedPreferences.getString("simno", "");
      String deactstatus = localSharedPreferences.getString("deact", "");
      String crtsimnum = ((TelephonyManager)paramContext.getSystemService("phone")).getSimSerialNumber();
      //String crtsimnum = localTelephonyManager.getSimSerialNumber();
      
      if ((primmob.length() == 0) || (deactstatus.equalsIgnoreCase("YES")))
        System.exit(0);
      if ((!simnum.equals(crtsimnum)) && (deactstatus.equalsIgnoreCase("NO")))
      {
        //sendSMS(primmob, msgcontent, paramContext);
        sendSMS(secmob, msgcontent, paramContext);
      }
    }
    else
    {
      return;
    }
    System.exit(0);
  }
}