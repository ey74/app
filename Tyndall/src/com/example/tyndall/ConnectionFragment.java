/**
 * 
 * File : MainFragment.java
 * Created : June 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * 
 */

package com.example.tyndall;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ConnectionFragment extends Fragment implements AdapterView.OnItemSelectedListener{
	
	
	static Handler handler = new Handler();
	//private Button clickButton = null;
	private TextView selection;
	private enum Sport {Hurling, Darts, ExtremeSport, Football, Golf,Tennis, Rugby, Running};


	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_connection, container, false);
	
		
		/** Spinner */
		selection = (TextView) rootView.findViewById(R.id.selection);
	    Spinner spin = (Spinner) rootView.findViewById(R.id.spinner);
	    ArrayAdapter<Sport> sport = new ArrayAdapter<Sport>(this.getActivity(), android.R.layout.simple_spinner_item, Sport.values());
	    sport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
	    spin.setOnItemSelectedListener(this);
	    spin.setAdapter(sport);
	    
		
		//Delete database
	/*	clickButton = (Button) rootView.findViewById(R.id.startSport);
		clickButton.setText("Start");
		clickButton.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				//go to main menu
				ConnectionFragment.this.getActivity().getActionBar().setSelectedNavigationItem(1);
				
			}
		});*/

		return rootView;
	}
	
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View vue, int position, long id) {
		// TODO Auto-generated method stub
		Sport sport = Sport.values()[position];
	    selection.setText("Sport selected: " + sport.name());
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		selection.setText("Select");
		
	}
	
	
	
	
	/***********************
	 **  Update Database  **
	 ***********************/

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

	
}
