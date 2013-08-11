/**
 * 
 * File : UpdateUI.java
 * Created : June 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * NOT USED 
 * 
 */
package com.example.tyndall;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateUI extends Activity{
	static FragmentActivity fragmentActivity;
	
	
	public static void updateUITextView(final Handler handler, final String txt, final TextView textView){	
			handler.post(new Runnable() {
	            @Override
	            public void run() {
	                // This gets executed on the UI thread so it can safely modify Views
	            	textView.setText(txt);
	            }
	        });
	}
	
	
	public static void  updateUICreateButtonInMain(final Handler handler, final String message){
		   final LinearLayout ll = (LinearLayout) ConnectionThread.fragmentActivity.findViewById(R.id.layout_main_activity);
		   final Button mButton = new Button(ConnectionThread.context);
		   handler.post(new Runnable() {
	            @Override
	            public void run() {
	                // This gets executed on the UI thread so it can safely modify Views
	            	mButton.setText(message); 
	            	mButton.setTextSize(9); 
	            	mButton.setId(9); 
	            	mButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,55));
	   	            ll.addView(mButton);
		        }
		   });
	    }
	
	
	 public static void makeToastUI(final Handler handler, final String text){
		   	handler.post(new Runnable() {
		            @Override
		            public void run() {
		                Toast.makeText(ConnectionThread.context, 
		                		text, Toast.LENGTH_SHORT).show();
		            }
		        });
		   }
	 
	public static void  updateUICreateTextViewConnection(final Handler handler, final String message, final int id){
		   final LinearLayout ll = (LinearLayout) ConnectionThread.fragmentActivity.findViewById(R.id.layoutForSocketConnectionCreation);
		   final TextView mTextView = new TextView(ConnectionThread.context);
		   handler.post(new Runnable() {
	            @Override
	            public void run() {
	                // This gets executed on the UI thread so it can safely modify Views
	            	ll.removeAllViewsInLayout();
	            	mTextView.setText(message); 
	            	mTextView.setTextSize(9); 
	            	mTextView.setId(id);
	            	mTextView.setTextColor(Color.BLACK);
	            	mTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,55));
	   	            ll.addView(mTextView);
		        }
		   });
	    }
	
	
	
	 public static void updateUICreateDB(final Handler handler){
		   final LinearLayout ll = (LinearLayout) ConnectionThread.fragmentActivity.findViewById(R.id.layoutForDBConnection);
		   final TextView textView = new TextView(ConnectionThread.context);
		   	handler.post(new Runnable() {
		            @Override
		            public void run() {
		                // This gets executed on the UI thread so it can safely modify Views
		            	SQLiteHelper db = new SQLiteHelper(ConnectionThread.context);
		               
		            	// Reading all contacts
		               Log.d("Reading: ", "Reading all contacts..");//+db.getNodesCount());
		               String message = "";
		               List<Node> nodes = db.getAllNodes();    
		                for (Node n : nodes) {
		                    String tmp =" IP : " + n.getIpAddress() + 
		                    		" , ID node : " + n.getNodeID()+
		                    		"<br>";
		                    message += tmp;        
			                // Writing Contacts to log	                   
		                    Log.d("Name: ", tmp);
		                    
		                }
		                ll.removeAllViewsInLayout();
	                    textView.setText(Html.fromHtml(message));
	                    textView.setTextColor(Color.BLACK);
		                textView.setId(5);
		                ll.addView(textView);
		                 
		            }
		        });
		   }
	
	
}
