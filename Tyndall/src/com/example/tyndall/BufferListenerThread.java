/**
 * 
 * File : BufferListenerThread.java
 * Created : June 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * 
 */

package com.example.tyndall;
import java.util.concurrent.Semaphore;

import android.util.Log;
/**
 *  Add listener from a fragment
 */


public class BufferListenerThread extends Thread{
	public ConnectionThread mConnectionThread;
	public BufferListener mBufferListener;
	public boolean sockIsConnected;
	public HandlerThread thr;
	public int idThread;
	int num=0;
	
	public BufferListenerThread(ConnectionThread mConnectionThread, int idThread, BufferListener mBufferListener){
		super();
		this.mConnectionThread=mConnectionThread;
		this.mBufferListener=mBufferListener;
		this.idThread = idThread;
	}
	
	@Override
	public void run() {
		boolean listenerAlreadyAdded = false;
		Semaphore mutex = new Semaphore(1);//protect listenerAlreadyAdded
		
		
		while(ConnectionThread.connected){
			
			while((!(mConnectionThread.getIsClient()[idThread]))||(listenerAlreadyAdded)
					||(!(mConnectionThread.getThreadIsReady()[idThread]))){
				if (!(mConnectionThread.getIsClient()[idThread])){
					listenerAlreadyAdded = false;
					this.sockIsConnected=false;//used in other fragm
				}else{
					this.sockIsConnected=true;
				}
				try {
					sleep(10);
				} catch (InterruptedException e) {
					Log.i("BufferListenerThread","error");
				}
			}
			
			//Add the listener for the thread idThread
			mConnectionThread.getThreadArray()[idThread].addBufferListener(mBufferListener);
			try {
				mutex.acquire();
				listenerAlreadyAdded=true;
				//mutex : end of section
		 		mutex.release();
			} catch (InterruptedException e1) {
				Log.i("BufferListenerThread","Caught exception");
			}

		}
		
	}

}
