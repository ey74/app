/**
 * 
 * File : MainActivity.java
 * Created : June 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * 
 */



/**
 * PagerAdapter will provide fragments for each of the sections. 
 * We use a FragmentPagerAdapter derivative, which will keep every loaded fragment in memory
 * If this becomes too memory intensive, it may be best to switch to a FragmentStatePagerAdapter
 * 
 */
package com.example.tyndall;

import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;



public class MainActivity extends FragmentActivity{
	public final static int NB_PAGE_RETAINED=10;
	CollectionPagerAdapter mCollectionPagerAdapter ;
	ViewPager mViewPager; //host the section contents
	Context context;
	WifiApManager wifiApManager;
	WifiConfiguration wifiConfig;
	TextView prefEditTextLogin;

	static Handler handler = new Handler();
	public static ConnectionThread connectionThr;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final ActionBar actionBar = getActionBar();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		prefEditTextLogin = (TextView)findViewById(R.id.prefLogin);
		//Preference customPref = (Preference) findPreference("prefNodesConnection");


		// Create the adapter that will return a fragment for each of the primary sections of the app
		mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager(),this);
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager)findViewById(R.id.pager);
		mViewPager.setAdapter(mCollectionPagerAdapter);
		
		mViewPager.setOnPageChangeListener(
				new ViewPager.SimpleOnPageChangeListener(){
					@Override
					public void onPageSelected(int position){
						//when swiping between pages, select the corresponding tab
						getActionBar().setSelectedNavigationItem(position);
					}
				});
		mViewPager.setOffscreenPageLimit(NB_PAGE_RETAINED);
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);//tabs should be displayed in the action bar
		
		
		
		//Create a tab listener that is called when the user changes tabs
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}
			
			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}
		};
		
	    //Add 5tabs, specifying the tab's text and TabListener
		actionBar.addTab(actionBar.newTab()
								.setText("Connection")
								.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab()
								.setText("Main")
								.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab()
								.setText("History")
								.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab()
								.setText("Graphs")
								.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab()
								.setText("Other")
								.setTabListener(tabListener));
		//change title bar colors
		//actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blackAndroid)));	
		actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));
	
		
		//TCP server : thread in background
		boolean connected=true;
	    final Handler handler = new Handler();//change View in other thread
	    
	   
	    connectionThr = new ConnectionThread(connected,handler,this);
		connectionThr.start(); 
		
		
		
		/* works
		final LinearLayout ll = (LinearLayout) findViewById(R.id.layout_main3);
		final TextView textView = new TextView(this);
		textView.setText("hi"); 
		textView.setId(5);
        ll.addView(textView);
          */
		
		
		//wifi param
		wifiApManager = new WifiApManager(this);
		wifiConfig = new WifiConfiguration();
		wifiConfig.SSID="hhh";
		//wifiConfig.preSharedKey="0123456789";
		wifiConfig.hiddenSSID= false;
		wifiConfig.status=WifiConfiguration.Status.ENABLED;
		

		//add
		wifiConfig.wepKeys[0]="0123456789111";
		wifiConfig.wepTxKeyIndex = 0;
		wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		//wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		//wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
		/**wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);*/
		wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		
	}
	
	
	/***************
	 **  getters  **
	 ***************/
	public static ConnectionThread getConnectionThr() {
		return connectionThr;
	}


	
	
	/*****************
	 **  Update UI  **
	 *****************/
	public void updateUI(final String txt, final TextView text){
		
		handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
            	text.setText(txt);
            }
        });
	}
	
	public void updateCreateUIMain(final String txt,final int clientId){
		final LinearLayout ll = (LinearLayout) findViewById(R.id.layout_main_activity);
		final TextView textView = new TextView(this);
		handler.post(new Runnable() {
			@Override
            public void run() {
				textView.setText(txt); 
				textView.setId(clientId);
		        ll.addView(textView);
            }
        });
	}
	
	public void updateDeleteDB(){
		handler.post(new Runnable() {
            @Override
            public void run() {
				LinearLayout ll = (LinearLayout) ConnectionThread.fragmentActivity.findViewById(R.id.layoutForDBConnection);
				final TextView textView = (TextView)ll.findViewById(5);
				ll.removeView(textView);
            }
		});
	
	}
	
	
		
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 @Override
	    public boolean onPrepareOptionsMenu(Menu menu) {
	 
	   /*     if (mTcpClient != null) {
	            // if the client is connected, enable the connect button and disable the disconnect one
	            menu.getItem(1).setEnabled(true);
	            menu.getItem(0).setEnabled(false);
	        } else {
	            // if the client is disconnected, enable the disconnect button and disable the connect one
	            menu.getItem(1).setEnabled(false);
	            menu.getItem(0).setEnabled(true);
	        }
	 */
	        return super.onPrepareOptionsMenu(menu);
	    }
	
	
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.connect:
			//send UDP broadcast
			Discover discover = new Discover(getBaseContext(),Const.UDP_PORT);
			UpdateUI.makeToastUI(handler,"Search new nodes ...");
			discover.start();
			break;
			
		case R.id.disconnect:
			this.finish();
			System.exit(0);
			break;
			
		case R.id.resetDatabase:
			SQLiteHelper db = new SQLiteHelper(this);
			db.deleteAllNodes();
			updateDeleteDB();
			break;
		case R.id.Hotspot:
			wifiApManager.setWifiApEnabled(wifiConfig, true);
			UpdateUI.makeToastUI(handler,"wifi enabled");
			break;
		case R.id.Music:
			@SuppressWarnings("deprecation")
			Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
			startActivity(intent);
			break;
		case R.id.action_settings:
			//confirmFragmentSettings();
			//confirmSettings();
			Intent intentS = new Intent();
			intentS.setClass(MainActivity.this,SetPreferenceActivity.class);
			startActivityForResult(intentS,0);
			break;
		default :
			break;
		}
		return true;
	}
	
	
	//preferences setting
	public void onActivityResult(int requestCode,int resultCode,Intent data){
		loadPref();
	}
	public void loadPref(){
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String edittext_login = mySharedPreferences.getString("prefUsername", "");
		UpdateUI.updateUITextView(handler, edittext_login+" session", prefEditTextLogin);
		
	}
	
	
	
	
	
	
	 /**  
     * A  FragmentPagerAdapter that returns a fragment corresponding to  
     * one of the sections/tabs/pages.  
     */  
	public class CollectionPagerAdapter extends FragmentPagerAdapter{
		final MainActivity fragmActivity;
		public CollectionPagerAdapter(FragmentManager fm, MainActivity fragmActivity ) {
			super(fm);
			this.fragmActivity=fragmActivity;	
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
	    	// Return an EssaiFragment with the page number as its lone argument.  
		    //	String mString = (String) getPageTitle(position);

			Fragment fragment = new HistoryFragment();
			
	
			switch (position){
				case 0: 
				    return fragment = new ConnectionFragment();
				case 1: 
					return fragment = new MainFragment();
				case 2: 
					return fragment = new HistoryFragment();
				case 3: 
					return fragment = new GraphFragment();
				case 4: 
					return fragment = new OtherFragment();
				default:
					break;
			}

			return fragment;
		}

		@Override
		public int getCount() {
			// show 4 total pages
			return 5;
		}
		
		
	    @Override  
		public CharSequence getPageTitle(int position){
			Locale l = Locale.getDefault();   
			switch (position) {   
	            case 0:   
	                return getString(R.string.title_main).toUpperCase(l);   
	            case 1:   
	                return getString(R.string.title_history).toUpperCase(l);   
	            case 2:   
	                return getString(R.string.title_music).toUpperCase(l); 
	            case 3:   
	                return getString(R.string.title_other).toUpperCase(l);
	            case 4:   
	                return getString(R.string.title_tcp).toUpperCase(l);
	           }   
			  return null;   
		}
		
		
	}

		
}
