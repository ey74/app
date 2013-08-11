package com.example.tyndall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends Activity{
	Button btnSignIn,btnSignUp;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		//For the font title
	    TextView txt = (TextView) findViewById(R.id.textViewTynsport);  
	    Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");  
	    txt.setTypeface(font);  
		
		
		 // Get The Refference Of Buttons
	     btnSignIn=(Button)findViewById(R.id.buttonSignIN);
	     btnSignUp=(Button)findViewById(R.id.buttonSignUP);
	     
	     // Set OnClick Listener on SignUp button 
	    btnSignUp.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			/// Create Intent for SignUpActivity  and Start The Activity
			Intent intentSignUP=new Intent(getApplicationContext(),MainActivity.class);
			startActivity(intentSignUP);
			}
		});
	}
	
	// Methos to handleClick Event of Sign In Button
	public void signIn(View V)
		   {
			/*	final Dialog dialog = new Dialog(HomeActivity.this);
				dialog.setContentView(R.layout.login);
			    dialog.setTitle("Login");
		
			    // get the Refferences of views
			    final  EditText editTextUserName=(EditText)dialog.findViewById(R.id.editTextUserNameToLogin);
			    final  EditText editTextPassword=(EditText)dialog.findViewById(R.id.editTextPasswordToLogin);
			    
				Button btnSignIn=(Button)dialog.findViewById(R.id.buttonSignIn);
					
				// Set On ClickListener
				btnSignIn.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						// get The User name and Password
						String userName=editTextUserName.getText().toString();
						String password=editTextPassword.getText().toString();
						
						// fetch the Password form database for respective user name
						String storedPassword=loginDataBaseAdapter.getSinlgeEntry(userName);
						
						// check if the Stored password matches with  Password entered by user
						if(password.equals(storedPassword))
						{
							Toast.makeText(HomeActivity.this, "Congrats: Login Successfull", Toast.LENGTH_LONG).show();
							dialog.dismiss();
						}
						else
						{
							Toast.makeText(HomeActivity.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
						}
					}
				});
				
				dialog.show();*/
		}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
}
