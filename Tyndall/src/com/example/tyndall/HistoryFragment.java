/**
 * 
 * File : OtherFragment.java
 * Created : June 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * 
 */
package com.example.tyndall;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HistoryFragment extends Fragment {

	public static final String ARG_OBJECT = "object";
	static Handler handler = new Handler();


	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_history, container, false);
		
		//Works
		/*
		final LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.layoutForScrollHistory);
		final TextView textView = new TextView(getActivity());
		textView.setText("hi"); 
		textView.setId(5);
        ll.addView(textView);
		
		*/

		return rootView;
	}
	
	
	
	
	
	
	
	/*****************
	 **  Update UI  **
	 *****************/
	public void updateUI(final String txt,final TextView text ){
		handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
            	text.setText(txt);
            }
        });
	}
	
	public void updateCreateUI(final String txt,final int clientId){
		final LinearLayout ll = (LinearLayout) getView().findViewById(R.id.layoutForScrollHistory);
		final TextView textView = new TextView(getActivity().getBaseContext());
		handler.post(new Runnable() {
			@Override
            public void run() {
				textView.setText(txt); 
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(9);
				textView.setId(clientId);
		        ll.addView(textView);
            }
        });
	}
	

}
