package com.slock;

import com.slock.OnBootReceiver;

import android.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.*;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.app.AlertDialog;

public class SLOCKMainActivity extends Activity {

	EditText pin,rePin,loginPin,primMobile,secMobile,oldPin,newPin,confirmPin;
	TextView forgotPin,mobileNos,changeSecPin,chooseSecOpts,actDeactApp,uninstallProt,about,logoff,lblActivate,lblDAEnable;
	Button btnSetPin,btnLogin,btnSavePin,btnCancelPin,btnSaveMobile,btnCancelMobile,btnSaveOpts,btnCancelOpts,btnActivate,btnCancelActivate,btnEnableDA,btnCancelEditDA;
	CheckBox chkBackup, chkWipe, chkLock;
	//private ComponentName adminComponent;
	//private DevicePolicyManager devicePolicyManager;
	public static ComponentName adminComponent;
	public static DevicePolicyManager devicePolicyManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setAdminComponent(new ComponentName(this, DAR.class));
	    SLOCKMainActivity.devicePolicyManager = ((DevicePolicyManager)getSystemService("device_policy"));
	    
	    //this.devicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE); 
	    //this.adminComponent = new ComponentName(this, DAR.class); 
 
	    if (getSharedPreferences("file", 0).getString("pin", "").length() == 0)
	    {
	      setpin();
	      return;
	    }
	    login();
	}

	private void setpin() {
		//setContentView(R.layout.setpin_main);
		setContentView(0x7f030006);
		pin = (EditText)findViewById(0x7f090024);
		rePin = (EditText)findViewById(0x7f090025);
		btnSetPin = (Button)findViewById(0x7f090026);
		
		btnSetPin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view)
		      {
				String enterPin = pin.getText().toString();
				String reenterPin = rePin.getText().toString();
		        if ( (enterPin.equals(reenterPin)) && !(enterPin.length() < 4) && (enterPin.length() != 0) )
		        {
		          SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
		          localEditor.putString("pin", enterPin);
		          localEditor.commit();
		          Toast.makeText(SLOCKMainActivity.this, "Security PIN set", 1).show();
		          pin.setText("");
		          rePin.setText("");
		          
		          options();
		          
	    		  SLOCKMainActivity.this.enable_device_admin();
	    		  
	    		  SharedPreferences.Editor localEditor1 = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
	    		  localEditor1.putString("activ", "YES");
	    		  localEditor1.commit();
	    		  
		        }
		        else
		        {		        
		        	Toast.makeText(SLOCKMainActivity.this, "Enter same PIN in both fields of length 4-6", 1).show();
		        	pin.setText("");
		        	rePin.setText("");
		        	pin.requestFocus();
		        } 
		      }
		    });
		}

	int invalidLoginCount = 0;
	private void login() {
		setContentView(0x7f030004);
		//setContentView(R.layout.login_main);
		loginPin = (EditText)findViewById(0x7f09001b);
		btnLogin = (Button)findViewById(0x7f09001d);
		forgotPin = (TextView)findViewById(0x7f09001c);

	    btnLogin.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View view)
	      {
	    	  String logPin = loginPin.getText().toString();
	    	  if (logPin.trim().length() == 0)
	    	  {
	    		  Toast.makeText(SLOCKMainActivity.this, "Please enter the PIN", 1).show();
	    		  return;
	    	  }
	    	  else if (SLOCKMainActivity.this.getSharedPreferences("file", 0).getString("pin", "").equals(logPin))
	    	  {
	    		  options();
	    	  }
	    	  else
	    	  {
	    		  loginPin.setText("");
	    		  Toast.makeText(SLOCKMainActivity.this, "Incorrect PIN", 1).show();
	    		  
	    		  invalidLoginCount += 1;
	    		  final String password = "sLOCK";
	    		  if (invalidLoginCount == 4 ) {
	    			  
	    			  /*Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
	    			  boolean isAdmin = devicePolicyManager.isAdminActive(adminComponent);
	    			  if (isAdmin) 
	    				  devicePolicyManager.lockNow();*/
	    			    
	    			  devicePolicyManager.lockNow();
	    			  
	    			  SLOCKMainActivity.sendSMS(SLOCKMainActivity.this.getSharedPreferences("file", 0).getString("smob", ""), 
	    					  "[SLOCK] Locked device with password: " + password, SLOCKMainActivity.this.getBaseContext());
	    			  
	    			  System.exit(0);
	    		  }
	    	  }
	      	}
	    	});

	    forgotPin.setOnClickListener(new View.OnClickListener()
	    {
	    	public void onClick(View view)
	    	{
	    		final String prim = getSharedPreferences("file", 0).getString("pmob", "");
	    		String secon = getSharedPreferences("file", 0).getString("smob", "");
	    		if (secon.equals(""))
		    	Toast.makeText(SLOCKMainActivity.this, "Secondary Mobile number is not stored inside the App. Cannot send PIN.", 1).show();
	    		else
		    		{
	    			AlertDialog.Builder localBuilder = new AlertDialog.Builder(SLOCKMainActivity.this);
	    			localBuilder.setMessage("Send PIN via SMS?");
	    			localBuilder.setTitle("Alert");
	    			
	    			localBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog, int which) {
	    					String secon = getSharedPreferences("file", 0).getString("smob", "");
	    					String spin = getSharedPreferences("file", 0).getString("pin", "");
	    					
	    					SLOCKMainActivity.sendSMS(secon, "[SLOCK] Your PIN is: " + spin, 
	    							SLOCKMainActivity.this.getBaseContext());

	    					Toast.makeText(SLOCKMainActivity.this.getApplicationContext(), 
	    							"PIN sent to your Secondary Mobile.\nYou will receive SMS shortly.", 0).show();
		    		}
	    			});
	    			
	    			localBuilder.setNegativeButton("No", null);
	    			localBuilder.show();
	    			
	    		loginPin.setText("");
	    		return;
		    	}
	    	}
	    });
	}
	
	private void options() {
		setContentView(0x7f030007);
		//setContentView(R.layout.settings_main);
		mobileNos = (TextView)findViewById(0x7f090029);
		changeSecPin = (TextView)findViewById(0x7f09002a);
		chooseSecOpts = (TextView)findViewById(0x7f09002b);
		actDeactApp = (TextView)findViewById(0x7f09002c);
		uninstallProt = (TextView)findViewById(0x7f09002d);
		about = (TextView)findViewById(0x7f09002e);
		logoff = (TextView)findViewById(0x7f09002f);
		
		mobileNos.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View view)
	      {
	    	  setContentView(0x7f030005);
	    	  primMobile = (EditText)findViewById(0x7f09001f);
	    	  secMobile = (EditText)findViewById(0x7f090020);
	    	  btnSaveMobile = (Button)findViewById(0x7f090022);
	    	  btnCancelMobile = (Button)findViewById(0x7f090021);
	    	  
	    	  SharedPreferences localSharedPreferences = getSharedPreferences("file", 0);
	    	  //primMobile.setText(localSharedPreferences.getString("pmob", ""));
	    	  secMobile.setText(localSharedPreferences.getString("smob", ""));
	    	  
	    	  TelephonyManager localTelephonyManager = (TelephonyManager) SLOCKMainActivity.this.getSystemService(Context.TELEPHONY_SERVICE); 
	    	  		//(TelephonyManager)SLOCKMainActivity.this.getSystemService("phone");

	    	  final String sim1 = localTelephonyManager.getLine1Number();
	    	  primMobile.setText(sim1);
	    	  primMobile.setEnabled(false);

	    	  btnSaveMobile.setOnClickListener(new View.OnClickListener()
	    	  {
	    		  public void onClick(View view)
	    		  {
	    			  TelephonyManager localTelephonyManager = (TelephonyManager)SLOCKMainActivity.this.getSystemService("phone");
	    			  		//getSystemService(Context.TELEPHONY_SERVICE)
	    			  
	    			  String simSN = localTelephonyManager.getSimSerialNumber();
	    			  String pmobile = primMobile.getText().toString();
	    			  String smobile = secMobile.getText().toString();
	    			  //if ((pmobile.trim().length() == 0) || (smobile.trim().length() == 0) || (pmobile.equals(smobile)))
	    			  if ((smobile.trim().length() != 10) || (sim1.equals(smobile)))
	    			  {
	    				  //primMobile.setText("");
	    				  secMobile.setText("");
	    				  //primMobile.requestFocus();
	    				  Toast.makeText(SLOCKMainActivity.this, "Please enter valid secondary mobile.", 1).show();
	    				  return;
	    			  }
	    			  SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
	    			  localEditor.putString("pmob", sim1);
	    			  localEditor.putString("smob", smobile);
	    			  localEditor.putString("simno", simSN);
	    			  
	    			  localEditor.commit();
	    			  Toast.makeText(SLOCKMainActivity.this, "Secondary Mobile number is saved.", 1).show();
	    			  
	    			  //store first time and use it to verify when smob changes
	    			  SharedPreferences localSharedPreferences = getSharedPreferences("file", 0);
	    			  if (localSharedPreferences.getString("smob", "") != smobile) {
	    				  SLOCKMainActivity.sendSMS(localSharedPreferences.getString("smob", ""), "Secondary Mobile is "
	    				  		+ "changed from " + localSharedPreferences.getString("smob", "") + "to " + smobile,
	    				  		SLOCKMainActivity.this.getBaseContext());  
	    			  }


	    			  Handler handler = new Handler(); 
		    		  handler.postDelayed(new Runnable() { 
		    	         public void run() { 
		    	        	 options(); 
		    	         } 
		    		  }, 1000);
		    		  
	    		  }
	    	  });

	    	  btnCancelMobile.setOnClickListener(new View.OnClickListener()
	    	  {
	    		  public void onClick(View view)
	    		  {
	  	        	//SLOCKMainActivity.this.options();
	  	        	options();	    		  
	  	        	}
	    	  });
	      }
	    });
		
		changeSecPin.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	  setContentView(0x7f030002);
		    	  oldPin = (EditText)findViewById(0x7f09000f);
		    	  newPin = (EditText)findViewById(0x7f090010);
		    	  confirmPin = (EditText)findViewById(0x7f090011);
		    	  btnSavePin = (Button)findViewById(0x7f090013);
		    	  btnCancelPin = (Button)findViewById(0x7f090012);
		    	  
		    	  btnSavePin.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  String oldpin = oldPin.getText().toString();
				    	  String newpin = newPin.getText().toString();
				    	  String confirmpin = confirmPin.getText().toString();
				    	  
				    	  SharedPreferences localSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
				    	  String storedpin = localSharedPreferences.getString("pin", "");
				    	  
				    	  if ((oldpin.trim().length() == 0) || (newpin.trim().length() == 0) || (confirmpin.trim().length() == 0))
				    	  {
				    		  Toast.makeText(SLOCKMainActivity.this, "Please enter the fields to change PIN", 1).show();
				    		  oldPin.setText("");
				    		  newPin.setText("");
				    		  confirmPin.setText("");
				    	  }
				    	  else if ((oldpin.equals(storedpin)) && !(newpin.equals(storedpin)) && (newpin.equals(confirmpin)))
				    	  {
				    		  SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
				    		  localEditor.putString("pin", newpin);
				    		  localEditor.commit();
				    		  oldPin.setText("");
				    		  newPin.setText("");
				    		  confirmPin.setText("");
				    		  Toast.makeText(SLOCKMainActivity.this, "New PIN is set", 1).show();

				    		  Handler handler = new Handler(); 
				    		  handler.postDelayed(new Runnable() { 
				    	         public void run() { 
				    	        	 options(); 
				    	         } 
				    		  }, 1000);

				    	  }
				    	  else {
				    		  oldPin.setText("");
				    		  newPin.setText("");
				    		  confirmPin.setText("");
				    		  Toast.makeText(SLOCKMainActivity.this, "PIN is not changed. Please enter correct details.", 1).show();
				    	  }

				      }
				    });

		    	  btnCancelPin.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  oldPin.setText("");
				    	  newPin.setText("");
				    	  confirmPin.setText("");
				    	  options();
				      }
				    });
		      }
	    });

		final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		chooseSecOpts.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	  setContentView(0x7f030003);
		    	  chkBackup = (CheckBox)findViewById(0x7f090015);
		    	  chkWipe = (CheckBox)findViewById(0x7f090016);
		    	  chkLock = (CheckBox)findViewById(0x7f090017);
		    	  btnSaveOpts = (Button)findViewById(0x7f090019);
		    	  btnCancelOpts = (Button)findViewById(0x7f090018);

		    	  //SharedPreferences backupSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
		    	  //boolean isBackupEnabled = backupSharedPreferences.getString("backup_enabled", "").equalsIgnoreCase("YES");
		    	  boolean isBackupEnabled = pref.getBoolean("backup_enabled", false);
		    	  chkBackup.setChecked(isBackupEnabled);
		    	 
		    	  chkBackup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (chkBackup.isChecked()) {
							//SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
							//localEditor.putString("backup_enabled", "YES");
							//localEditor.commit();

							SharedPreferences.Editor localEditor = pref.edit();
							localEditor.putBoolean("backup_enabled", true).commit();
							
							Intent dbxActivity = new Intent(SLOCKMainActivity.this,BackupToDropboxActivity.class);
							dbxActivity.putExtra("fromReceiver", SMSReceiver.address);
							startActivity(dbxActivity);
		                }
		                else {
							SharedPreferences.Editor localEditor = pref.edit();
							localEditor.putBoolean("backup_enabled", false).commit();
							
		                	// clear keys
		                	BackupToDropboxActivity dbx = new BackupToDropboxActivity();
		                	dbx.clearKeys();
		                    finish();
		                }	
					}
		    	  });

		    	  boolean isWipeEnabled = pref.getBoolean("wipe_enabled", false);
		    	  chkWipe.setChecked(isWipeEnabled);
		    	  
		    	  chkWipe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (chkWipe.isChecked()) {
								SharedPreferences.Editor localEditor = pref.edit();
								localEditor.putBoolean("wipe_enabled", true).commit();
								
								AlertDialog.Builder localBuilder = new AlertDialog.Builder(SLOCKMainActivity.this);
								localBuilder.setMessage("Wipe option is enabled for remote wipe action. "
										+ "Remote action will Wipe out Device memory and SD cards.");
								localBuilder.setTitle("Notification");
								localBuilder.setNeutralButton("OK", null);
								localBuilder.show();
			                }
			                else {
			                	SharedPreferences.Editor localEditor = pref.edit();
								localEditor.putBoolean("wipe_enabled", false).commit();
			                }	
						}
			    	  });
		    	  
		    	  boolean isLockEnabled = pref.getBoolean("lock_enabled", false);
		    	  chkLock.setChecked(isLockEnabled);
		    	  
		    	  chkLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (chkLock.isChecked()) {
								SharedPreferences.Editor localEditor = pref.edit();
								localEditor.putBoolean("lock_enabled", true).commit();
								
								AlertDialog.Builder localBuilder = new AlertDialog.Builder(SLOCKMainActivity.this);
								localBuilder.setMessage("Lock option is enabled for remote lock action. "
										+ "Remote action will Lock out the device.");
								localBuilder.setTitle("Notification");
								localBuilder.setNeutralButton("OK", null);
								localBuilder.show();
			                }
			                else {
			                	SharedPreferences.Editor localEditor = pref.edit();
								localEditor.putBoolean("lock_enabled", false).commit();
			                }	
						}
			    	  });
		    	  
		    	  btnSaveOpts.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  options();
				      }
				    });

		    	  btnCancelOpts.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  options();
				      }
				    });
		      }
	    });
		
		actDeactApp.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	  setContentView(0x7f030001);
		    	  lblActivate = (TextView)findViewById(0x7f090009);
		    	  btnActivate = (Button)findViewById(0x7f09000c);
		    	  btnCancelActivate = (Button)findViewById(0x7f09000b);

		    	  SharedPreferences localSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
		    	  boolean activstatus = localSharedPreferences.getString("activ", "").equalsIgnoreCase("NO");
		    	  
		    	  lblActivate.setText(activstatus ? "Deactivated" : "Activated");
		    	  btnActivate.setText(activstatus ? "Activate" : "Dectivate");
		    	  
		    	  btnActivate.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  SharedPreferences localSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
				    	  boolean activstatus = localSharedPreferences.getString("activ", "").equalsIgnoreCase("YES");
				    	  
				    	  if (activstatus)
				  	      {
				    		  SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
				    		  localEditor.putString("activ", "NO");
				    		  localEditor.commit();

				    		  lblActivate.setText("Deactivated");
					    	  btnActivate.setText("Activate");
				  	      }
				    	  else {
				    		  SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
				    		  localEditor.putString("activ", "YES");
				    		  localEditor.commit();
				    	  
				    		  lblActivate.setText("Activated");
				    		  btnActivate.setText("Deactivate");
				    	  }
				    		  Handler handler = new Handler(); 
				    		  handler.postDelayed(new Runnable() { 
				    	         public void run() { 
				    	        	 options(); 
				    	         } 
				    		  }, 1000);
				      }
				    });

		    	  btnCancelActivate.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  options();
				      }
				    });
		      }
	    });
		
		uninstallProt.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	  setContentView(0x7f030008);
		    	  lblDAEnable = (TextView)findViewById(0x7f090031);
		    	  btnEnableDA = (Button)findViewById(0x7f090033);
		    	  btnCancelEditDA = (Button)findViewById(0x7f090032);

		    	  if (!get_device_admin_status())
		  	      {
		    		  lblDAEnable.setText("Disabled");
    				  btnEnableDA.setText("Enable Protection");
		  	      }
		    	  else {
		    		  lblDAEnable.setText("Enabled");
    				  btnEnableDA.setText("Disable Protection");
		    	  }
		    	  
		    	  btnEnableDA.setOnClickListener(new View.OnClickListener()
		    	  {
		    		  public void onClick(View view)
		    		  {
		    			  if (SLOCKMainActivity.this.get_device_admin_status())
		    			  {
		    				  SLOCKMainActivity.this.disable_device_admin();
		    				  lblDAEnable.setText("Disabled");
		    				  btnEnableDA.setText("Enable Protection");
		    			  }
		    			  else
		    			  {
		    				  SLOCKMainActivity.this.enable_device_admin();
		    				  lblDAEnable.setText("Enabled");
		    				  btnEnableDA.setText("Disable Protection");
		    			  }
		    			  
		    			  new Thread(new Runnable() {
				    		    @Override
				    		    public void run() {
				    		        try {
				    		            Thread.sleep(1000);
				    		        } catch (InterruptedException e) {
				    		            e.printStackTrace();
				    		        }
				    		        runOnUiThread(new Runnable() {
				    		            @Override
				    		            public void run() {
				    		            	options();
				    		            }
				    		        });
				    		    }
				    		}).start();
				      }
				    });

		    	  btnCancelEditDA.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  options();
				      }
				    });  
		      }
	    });

		about.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	  //setContentView(0x7f030000);
		      }
	    });
				
		logoff.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	System.exit(0);  
		      }
		      
	    });
		
	} // end of options()
	
	  
	static void sendSMS(String paramString1, String paramString2, Context paramContext)
	  {
	    PendingIntent smsPendingIntent = PendingIntent.getActivity(paramContext, 0, new Intent(paramContext, OnBootReceiver.class), 0);
	    SmsManager.getDefault().sendTextMessage(paramString1, null, paramString2, smsPendingIntent, null);
	  }

	private void disable_device_admin()
	  {
	    SLOCKMainActivity.devicePolicyManager.removeActiveAdmin(SLOCKMainActivity.getAdminComponent());
	  }

	private void enable_device_admin()
	 {
	    Intent daIntent = new Intent("android.app.action.ADD_DEVICE_ADMIN");
	    daIntent.putExtra("android.app.extra.DEVICE_ADMIN", SLOCKMainActivity.getAdminComponent());
	    startActivityForResult(daIntent, 0);
	  }

	private boolean get_device_admin_status()
	  {
	    return SLOCKMainActivity.devicePolicyManager.isAdminActive(SLOCKMainActivity.getAdminComponent());
	  }

	public static ComponentName getAdminComponent() {
		return adminComponent;
	}

	public void setAdminComponent(ComponentName adminComponent) {
		SLOCKMainActivity.adminComponent = adminComponent;
	}

}
