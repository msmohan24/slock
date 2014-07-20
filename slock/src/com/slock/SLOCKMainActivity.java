package com.slock;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.slock.OnBootReceiver;

import android.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.app.AlertDialog;

public class SLOCKMainActivity extends Activity {

	EditText pin,rePin,loginPin,primMobile,secMobile,oldPin,newPin,confirmPin,dbxEmail,dbxPassword;
	TextView forgotPin,mobileNos,changeSecPin,chooseSecOpts,actDeactApp,uninstallProt,about,logoff,lblBackup,lblWipe,lblLock,lblActivate,lblDAEnable;
	Button btnSetPin,btnLogin,btnSavePin,btnCancelPin,btnSaveMobile,btnCancelMobile,btnSaveOpts,btnCancelOpts,btnActivate,btnCancelActivate,btnEnableDA,btnCancelEditDA,btnSaveDbx,btnCancelDbx;
	private ComponentName adminComponent;
	private DevicePolicyManager devicePolicyManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		this.adminComponent = new ComponentName(this, DAR.class);
	    this.devicePolicyManager = ((DevicePolicyManager)getSystemService("device_policy"));
	    if (getSharedPreferences("file", 0).getString("pin", "").length() == 0)
	    {
	      setpin();
	      return;
	    }
	    login();
	}

	private void setpin() {
		//setContentView(R.layout.setpin_main);
		setContentView(0x7f030007);
		pin = (EditText)findViewById(0x7f09002e);
		rePin = (EditText)findViewById(0x7f09002f);
		btnSetPin = (Button)findViewById(0x7f090030);
		
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
		          //SLOCKMainActivity.this.options();
		          options();
		          return;
		        }
		        Toast.makeText(SLOCKMainActivity.this, "Enter same PIN in both fields of length 4-6", 1).show();
		        pin.setText("");
		        rePin.setText("");
		        pin.requestFocus();
		      }
		    });
		}
	
	private void login() {
		setContentView(0x7f030004);
		//setContentView(R.layout.login_main);
		loginPin = (EditText)findViewById(0x7f090020);
		btnLogin = (Button)findViewById(0x7f090023);
		forgotPin = (TextView)findViewById(0x7f090022);
		
	    if (getSharedPreferences("file", 0).getString("deact", "").equalsIgnoreCase("YES"))
	    	// TODO -- this should be a alert text. makeText only works on button click.
	    	Toast.makeText(SLOCKMainActivity.this, "Choose options and activate the Application", 1).show();
	    
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
	    		  //Toast.makeText(SLOCKMainActivity.this, "Login Success", 1).show();
	    		  //SLOCKMainActivity.this.options();
	    		  options();
	    	  }
	    	  else
	    	  {
	    		  loginPin.setText("");
	    		  Toast.makeText(SLOCKMainActivity.this, "Incorrect PIN", 1).show();
	    		  return;
	    	  }
	      	}
	    	});

	    	forgotPin.setOnClickListener(new View.OnClickListener()
	    	{
	    		public void onClick(View view)
	    		{
	    			String prim = getSharedPreferences("file", 0).getString("pmob", "");
	    			String secon = getSharedPreferences("file", 0).getString("smob", "");
	    			if (prim.equals("") || secon.equals(""))
			    	Toast.makeText(SLOCKMainActivity.this, "Mobile numbers not stored inside the App.\nCannot send PIN", 1).show();
	    			else
			    		{
	    				SharedPreferences localSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
	    				String spin = localSharedPreferences.getString("pin", "");
			          
	    				/*if (prim1.equalsIgnoreCase(""))
			          	{
			        	  Toast.makeText(SLOCKMainActivity.this, "Mobile numbers not stored inside the App.\nCannot send PIN", 1).show();
			        	  return;
			          	}*/
			          
	    				// really need OTP ? -- generate random number and sent OTP via SMS
	    				// this sends stored pin
	    				SLOCKMainActivity.sendSMS(secon, "*sLOCK* Your PIN is: " + spin, SLOCKMainActivity.this.getBaseContext());
	    				Toast.makeText(SLOCKMainActivity.this.getApplicationContext(), "PIN sent to your Secondary Mobile.\nYou will receive SMS shortly.", 0).show();
			    		}
	    			loginPin.setText("");
	    			return;
	    			//SLOCKMainActivity.this.recoverpin();
	    		}
	    	});
	}
	
	private void options() {
		setContentView(0x7f030008);
		//setContentView(R.layout.settings_main);
		mobileNos = (TextView)findViewById(0x7f090033);
		changeSecPin = (TextView)findViewById(0x7f090034);
		chooseSecOpts = (TextView)findViewById(0x7f090035);
		actDeactApp = (TextView)findViewById(0x7f090036);
		uninstallProt = (TextView)findViewById(0x7f090037);
		about = (TextView)findViewById(0x7f090038);
		logoff = (TextView)findViewById(0x7f090039);
		
		mobileNos.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View view)
	      {
	    	  setContentView(0x7f030005);
	    	  primMobile = (EditText)findViewById(0x7f090025);
	    	  secMobile = (EditText)findViewById(0x7f090026);
	    	  btnSaveMobile = (Button)findViewById(0x7f090028);
	    	  btnCancelMobile = (Button)findViewById(0x7f090027);
	    	  
	    	  SharedPreferences localSharedPreferences = getSharedPreferences("file", 0);
	    	  primMobile.setText(localSharedPreferences.getString("pmob", ""));
	    	  secMobile.setText(localSharedPreferences.getString("smob", ""));
	    	  /*
	    	   *may add contact icon here
	     
	     		primary.setOnClickListener(new View.OnClickListener()
	    		{
	      			public void onClick(View view)
	      			{
	      				Intent conIntent = new Intent("android.intent.action.GET_CONTENT");
	      				conIntent.setType("vnd.android.cursor.item/phone_v2");
	      				SLOCKMainActivity.this.startActivityForResult(conIntent, 1);
		      		}
		    	});
	     
	    	   */

	    	  btnSaveMobile.setOnClickListener(new View.OnClickListener()
	    	  {
	    		  public void onClick(View view)
	    		  {
	    			  TelephonyManager localTelephonyManager = (TelephonyManager)SLOCKMainActivity.this.getSystemService("phone");
	    			  String simSN = localTelephonyManager.getSimSerialNumber();
	    			  String pmobile = primMobile.getText().toString();
	    			  String smobile = secMobile.getText().toString();
	    			  if ((pmobile.trim().length() == 0) || (smobile.trim().length() == 0) || (pmobile.equals(smobile)))
	    			  {
	    				  Toast.makeText(SLOCKMainActivity.this, "Please enter primary and secondary mobile.\nSecondary mobile should be different from Primary mobile.", 1).show();
	    				  return;
	    			  }
	    			  SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
	    			  localEditor.putString("pmob", pmobile);
	    			  localEditor.putString("smob",smobile);
	    			  localEditor.putString("simno", simSN);
	    			  localEditor.commit();
	    			  
	    			  /*AlertDialog.Builder localBuilder = new AlertDialog.Builder(SLOCKMainActivity.this);
	    			  localBuilder.setMessage("Saved.\n\nChoose from options for more security or Exit.");
	    			  localBuilder.setTitle("Choose Options");
	    			  localBuilder.setNeutralButton("Ok", null);
	    			  localBuilder.show();*/
	    		  }
	    	  });

	    	  btnCancelMobile.setOnClickListener(new View.OnClickListener()
	    	  {
	    		  public void onClick(View view)
	    		  {
	  	        	//SLOCKMainActivity.this.options();
	  	        	options();	    		  }
	    	  });
	      }
	    });
		
		changeSecPin.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	  setContentView(0x7f030002);
		    	  oldPin = (EditText)findViewById(0x7f090011);
		    	  newPin = (EditText)findViewById(0x7f090012);
		    	  confirmPin = (EditText)findViewById(0x7f090013);
		    	  btnSavePin = (Button)findViewById(0x7f090015);
		    	  btnCancelPin = (Button)findViewById(0x7f090014);
		    	  
		    	  btnSavePin.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  String oldpin = oldPin.getText().toString();
				    	  String newpin = newPin.getText().toString();
				    	  String confirmpin = confirmPin.getText().toString();
				    	  if ((oldpin.trim().length() == 0) || (newpin.trim().length() == 0) || (confirmpin.trim().length() == 0))
				    	  {
				    		  Toast.makeText(SLOCKMainActivity.this, "Please enter the fields to change PIN", 1).show();
				    		  return;
				    	  }
				    	  SharedPreferences localSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
				    	  String storedpin = localSharedPreferences.getString("pin", "");
				    	  SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
				    	  if ((oldpin.equals(storedpin)) && (newpin.equals(confirmpin)))
				    	  {
				    		  localEditor.putString("pin", newpin);
				    		  localEditor.commit();
				    		  oldPin.setText("");
				    		  newPin.setText("");
				    		  confirmPin.setText("");
				    		  return;
				    	  }
				    	  oldPin.setText("");
				    	  newPin.setText("");
				    	  confirmPin.setText("");
				    	  Toast.makeText(SLOCKMainActivity.this, "PIN is not changed", 1).show();
				      	}
				    	});
		    	  	// enter input and click cancel, again open same page - input not cleared ? -- setText("")
				    btnCancelPin.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  //SLOCKMainActivity.this.options();
				    	  options();
				      }
				    });
		      }
	    });
		
		chooseSecOpts.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	  setContentView(0x7f030003);
		    	  lblBackup = (TextView)findViewById(0x7f090017);
		    	  lblWipe = (TextView)findViewById(0x7f090019);
		    	  lblLock = (TextView)findViewById(0x7f09001b);
		    	  btnSaveOpts = (Button)findViewById(0x7f09001e);
		    	  btnCancelOpts = (Button)findViewById(0x7f09001d);
		  		
		    	  btnSaveOpts.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    
				      }
				    });

		    	  btnCancelOpts.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  //SLOCKMainActivity.this.options();
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
		    	  btnActivate = (Button)findViewById(0x7f09000d);
		    	  btnCancelActivate = (Button)findViewById(0x7f09000c);
		  		
		    	  btnActivate.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    
				      }
				    });

		    	  btnCancelActivate.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  //SLOCKMainActivity.this.options();
				    	  options();
				      }
				    });
		      }
	    });
		
		uninstallProt.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	  setContentView(0x7f030009);
		    	  lblDAEnable = (TextView)findViewById(0x7f09003b);
		    	  btnEnableDA = (Button)findViewById(0x7f09003d);
		    	  btnCancelEditDA = (Button)findViewById(0x7f09003c);
		  		
		    	  btnEnableDA.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    
				      }
				    });

		    	  btnCancelEditDA.setOnClickListener(new View.OnClickListener()
				    {
				      public void onClick(View view)
				      {
				    	  //SLOCKMainActivity.this.options();
				    	  options();
				      }
				    });  
		      }
	    });
		
		about.setOnClickListener(new View.OnClickListener()
	    {
		      public void onClick(View view)
		      {
		    	  setContentView(0x7f030000);
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

	@SuppressWarnings("unused")
	private void disable_device_admin()
	  {
	    this.devicePolicyManager.removeActiveAdmin(this.adminComponent);
	  }

	@SuppressWarnings("unused")
	private void enable_device_admin()
	 {
	    Intent daIntent = new Intent("android.app.action.ADD_DEVICE_ADMIN");
	    daIntent.putExtra("android.app.extra.DEVICE_ADMIN", this.adminComponent);
	    startActivityForResult(daIntent, 0);
	  }

	@SuppressWarnings("unused")
	private boolean get_device_admin_status()
	  {
	    return this.devicePolicyManager.isAdminActive(this.adminComponent);
	  }
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.slockmain, menu);
		getMenuInflater().inflate(0x7f080000, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		//if (id == R.id.action_settings) {
		if (id == 0x7f09001d) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
