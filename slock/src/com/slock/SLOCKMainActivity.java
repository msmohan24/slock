package com.slock;

import com.slock.OnBootReceiver;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class SLOCKMainActivity extends Activity implements OnCheckedChangeListener {

	EditText pin,rePin,loginPin,primMobile,secMobile,oldPin,newPin,confirmPin,deactPin,otp;
	Button btnSetPin,btnLogin,btnForgotPin,btnSave,btnExit,btnChangePin,btnCancelPin,btnDeact,btnCancelDeact,btnOTP;
	Switch cspinSwitch,deactSwitch;
	CheckBox chkBackup,chkWipe,chkLock;
	private ComponentName adminComponent;
	private DevicePolicyManager devicePolicyManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/*
		pin = (EditText)findViewById(2131296278);
		rePin = (EditText)findViewById(R.id.rePin);
		btnSetPin = (Button)findViewById(R.id.btnSetPin);
		
		loginPin = (EditText)findViewById(R.id.loginPin);
		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnForgotPin = (Button)findViewById(R.id.btnForgotPin);
		
		primMobile = (EditText)findViewById(R.id.primMobile);
		secMobile = (EditText)findViewById(R.id.secMobile);
		oldPin = (EditText)findViewById(R.id.oldPin);
		newPin = (EditText)findViewById(R.id.newPin);
		confirmPin = (EditText)findViewById(R.id.confirmPin);
		deactPin = (EditText)findViewById(R.id.deactPin);
		btnSave = (Button)findViewById(R.id.btnSave);
		btnExit = (Button)findViewById(R.id.btnExit);
		btnChangePin = (Button)findViewById(R.id.btnChangePin);
		btnDeact = (Button)findViewById(R.id.btnDeact);
		btnCancelDeact = (Button)findViewById(R.id.btnCancelDeact);
		btnCancelPin = (Button)findViewById(R.id.btnCancelPin);
		cspinSwitch = (Switch)findViewById(R.id.changeSecPinSwitch);
		deactSwitch = (Switch)findViewById(R.id.deactSwitch);
		
		otp = (EditText)findViewById(R.id.otp);
		btnOTP = (Button)findViewById(R.id.btnOTP);
		*/
		
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
		setContentView(R.layout.setpin);
		//setContentView(2130903045);
		pin = (EditText)findViewById(R.id.pin);
		rePin = (EditText)findViewById(R.id.rePin);
		btnSetPin = (Button)findViewById(R.id.btnSetPin);
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
		          SLOCKMainActivity.this.options();
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
		setContentView(R.layout.login);
		loginPin = (EditText)findViewById(R.id.loginPin);
		btnLogin = (Button)findViewById(R.id.btnLogin);
		btnForgotPin = (Button)findViewById(R.id.btnForgotPin);
	    if (getSharedPreferences("file", 0).getString("deact", "").equalsIgnoreCase("YES"))
	    	// TODO -- this should be a alert text. makeText only works on button click.
	    	Toast.makeText(SLOCKMainActivity.this, "Choose options and activate the App", 1).show();
	    
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
	          SLOCKMainActivity.this.options();
	        }
	        else
	        {
	          loginPin.setText("");
	          Toast.makeText(SLOCKMainActivity.this, "Incorrect PIN", 1).show();
	          return;
	        }
	      }
	    });
	    btnForgotPin.setOnClickListener(new View.OnClickListener()
	    {
	      public void onClick(View view)
	      {
	    	  	String prim = getSharedPreferences("file", 0).getString("pmob", "");
			    if (prim.equals(""))
			    	Toast.makeText(SLOCKMainActivity.this, "Mobile numbers not stored inside the App.\nCannot send PIN", 1).show();
			    else
			    	{
			          SharedPreferences localSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
			          String spin = localSharedPreferences.getString("pin", "");
			          String prim1 = localSharedPreferences.getString("pmob", "");
			          if (prim1.equalsIgnoreCase(""))
			          {
			        	  Toast.makeText(SLOCKMainActivity.this, "Mobile numbers not stored inside the App.\nCannot send PIN", 1).show();
			        	  return;
			          }
			          
			          // really need OTP ? -- generate random number and sent OTP via SMS
			          // this sends stored pin
			          SLOCKMainActivity.sendSMS(prim1, "*sLOCK* Your PIN is: " + spin, SLOCKMainActivity.this.getBaseContext());
			          Toast.makeText(SLOCKMainActivity.this.getApplicationContext(), "PIN sent to your Primary Mobile.\nYou will receive SMS shortly.", 0).show();
			        }
			    loginPin.setText("");
			    return;
			    //SLOCKMainActivity.this.recoverpin();
	      }
	    });
	}

	protected void recoverpin() {
		    setContentView(R.layout.verifyotp);
		    otp = (EditText)findViewById(R.id.otp);
			btnOTP = (Button)findViewById(R.id.btnOTP);
//		    String prim = getSharedPreferences("file", 0).getString("pmob", "");
//		    
//		    if (prim.equals(""))
//		    	Toast.makeText(SLOCKMainActivity.this, "Mobile numbers not stored inside the App.\n Cannot send OTP", 1).show();
//		    else
//		    	{
//		          SharedPreferences localSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
//		          String spin = localSharedPreferences.getString("pin", "");
//		          String prim1 = localSharedPreferences.getString("pmob", "");
//		          if (prim1.equalsIgnoreCase(""))
//		          {
//		        	  Toast.makeText(SLOCKMainActivity.this, "Mobile numbers not stored inside the App.\n Cannot send OTP", 1).show();
//		        	  return;
//		          }
//		          
//		          // really need OTP ? -- generate random number and sent OTP via SMS
//		          // this sends stored pin
//		          SLOCKMainActivity.sendSMS(prim1, "*sLOCK* Your PIN is: " + spin, SLOCKMainActivity.this.getBaseContext());
//		          Toast.makeText(SLOCKMainActivity.this.getApplicationContext(), "PIN Sent to your Primary Mobile.\n You will receive SMS in few minutes.", 0).show();
//		        }

		    btnOTP.setOnClickListener(new View.OnClickListener()
		      {
		        public void onClick(View view)
		        {
		        	// get generated random number, may need to generate random number in Global scope
		        	// verify the entered otp with random number
		        	// if fails, goto to login page -- if user enter otp wrongly, going to login page and in-case requesting new otp again, one otp code is always active to use -- loop hole !?
		        	// if success, goto options page -- and request user to set new pin -- in this case change sec pin will ask for old pin (the one use forgot) !? 
		        		//SLOCKMainActivity.this.options();
		        }
		      });
	}

	protected void options() {
		setContentView(R.layout.options);
		primMobile = (EditText)findViewById(R.id.primMobile);
		secMobile = (EditText)findViewById(R.id.secMobile);
		oldPin = (EditText)findViewById(R.id.oldPin);
		newPin = (EditText)findViewById(R.id.newPin);
		confirmPin = (EditText)findViewById(R.id.confirmPin);
		deactPin = (EditText)findViewById(R.id.deactPin);
		btnSave = (Button)findViewById(R.id.btnSave);
		btnExit = (Button)findViewById(R.id.btnExit);
		btnChangePin = (Button)findViewById(R.id.btnChangePin);
		btnDeact = (Button)findViewById(R.id.btnDeact);
		btnCancelDeact = (Button)findViewById(R.id.btnCancelDeact);
		btnCancelPin = (Button)findViewById(R.id.btnCancelPin);
		cspinSwitch = (Switch)findViewById(R.id.changeSecPinSwitch);
		deactSwitch = (Switch)findViewById(R.id.deactSwitch);
		
	    SharedPreferences localSharedPreferences = getSharedPreferences("file", 0);
	    primMobile.setText(localSharedPreferences.getString("pmob", ""));
	    secMobile.setText(localSharedPreferences.getString("smob", ""));
	    /*
	     * may add contact icon here
	     
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
	     
	    btnSave.setOnClickListener(new View.OnClickListener()
    	{
      		public void onClick(View view)
      		{
      			TelephonyManager localTelephonyManager = (TelephonyManager)SLOCKMainActivity.this.getSystemService("phone");
		        String simSN = localTelephonyManager.getSimSerialNumber();
		        String pmobile = primMobile.getText().toString();
		        String smobile = secMobile.getText().toString();
		        if ((pmobile.trim().length() == 0) || (smobile.trim().length() == 0))
		        {
		          Toast.makeText(SLOCKMainActivity.this, "Please enter primary and secondary mobile", 1).show();
		          return;
		        }
		        SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
		        localEditor.putString("pmob", pmobile);
		        localEditor.putString("smob",smobile);
		        localEditor.putString("simno", simSN);
		        localEditor.commit();
		        AlertDialog.Builder localBuilder = new AlertDialog.Builder(SLOCKMainActivity.this);
		        localBuilder.setMessage("Saved.\n\nChoose from options for more security or Exit.");
		        localBuilder.setTitle("Choose Options");
		        localBuilder.setNeutralButton("Ok", null);
		        localBuilder.show();
		      }
		    });

	    cspinSwitch.setOnCheckedChangeListener(this);
	    deactSwitch.setOnCheckedChangeListener(this);
	    
	    //chkBackup.setOnClickListener((OnClickListener) this);
	    //chkWipe.setOnClickListener((OnClickListener) this);
	    //chkLock.setOnClickListener((OnClickListener) this);
	    
	    btnExit.setOnClickListener(new View.OnClickListener()
    	{
      		public void onClick(View view)
      		{
      			System.exit(0);
		        }
	    });
	}
	
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
		
	  
	  /*
				if (enterPin.isEmpty() || reenterPin.isEmpty()) {
					Toast.makeText(SLOCKMainActivity.this, "Blank input(s) encountered",Toast.LENGTH_LONG).show();
					pin.setText("");
					rePin.setText("");
					pin.requestFocus();
				}
				else if (enterPin.length() <= 3) {
					Toast.makeText(SLOCKMainActivity.this, "PIN length violation",Toast.LENGTH_LONG).show();
					pin.setText("");
					rePin.setText("");
					pin.requestFocus();
				}
				else if (!enterPin.equals(reenterPin))  {
					Toast.makeText(SLOCKMainActivity.this, "Entered PINs are mismatched",Toast.LENGTH_LONG).show();
					pin.setText("");
					rePin.setText("");
					pin.requestFocus();
				}
				else
				{
					Toast.makeText(SLOCKMainActivity.this, "Security PIN set!",Toast.LENGTH_SHORT).show();
					pin.setText("");
					rePin.setText("");

					Intent configActivity = new Intent(SLOCKMainActivity.this,ConfigureOptionsActivity.class);
					startActivity(configActivity);
					SLOCKMainActivity.this.finish();
				}
			}
		});
		*/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.slockmain, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCheckedChanged(CompoundButton chkView, boolean isChecked) {
		switch(chkView.getId()){
			case R.id.changeSecPinSwitch :
				if(isChecked)
					onChangeSecPinClicked();
				break;
			case R.id.deactSwitch :
				if (isChecked)
					onDeactivateClicked();
			case R.id.chkBackup :
				if (isChecked)
					onChkBackupClicked();
				break;
		}
	}

	private void onChkBackupClicked() {
		// TODO Auto-generated method stub
		
	}

	// switch back to No (activate) should not open another page, and activate the app in bg
	// how to identify the switch on and off state
	// switch should be in same state
	private void onDeactivateClicked() {
		setContentView(R.layout.deactivate);
		btnDeact.setOnClickListener(new View.OnClickListener()
		{
		    public void onClick(View view)
		     {
		    		String deactpin = deactPin.getText().toString();
		    		SharedPreferences localSharedPreferences = SLOCKMainActivity.this.getSharedPreferences("file", 0);
		    		String storedpin = localSharedPreferences.getString("pin", "");
		    		if ((deactpin.equals(storedpin)))
		    		{
		    			// deactivate the app
		    			SharedPreferences.Editor localEditor = SLOCKMainActivity.this.getSharedPreferences("file", 0).edit();
		    			localEditor.putString("deact", "YES");
		    			localEditor.commit();
		    			return;
		    		}
		    		deactPin.setText("");
		    		Toast.makeText(SLOCKMainActivity.this, "Incorrect PIN", 1).show();
		    	}
		 });

        btnCancelDeact.setOnClickListener(new View.OnClickListener()
		{
		    public void onClick(View view)
		     {
		        SLOCKMainActivity.this.options();
		      }
		});
	}

	// switch back to No should not open another page, and do nothing
	// how to identify the switch on and off state
	// switch should be back to No
	private void onChangeSecPinClicked() {
		setContentView(R.layout.changepin);
		btnChangePin.setOnClickListener(new View.OnClickListener()
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
		    btnCancelPin.setOnClickListener(new View.OnClickListener()
		    {
		      public void onClick(View view)
		      {
		    	  SLOCKMainActivity.this.options();
		      }
		    });
		  }

}
