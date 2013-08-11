package com.example.tyndall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

public class SetPreferenceActivity extends Activity{

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getFragmentManager()
		.beginTransaction()
		.replace(android.R.id.content, new SettingsPreferenceFragment())
		.commit();
	}
}
