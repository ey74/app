/**
 * 
 * File : ConnectionThread.java
 * Created : June 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * 
 */

package com.example.tyndall;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Create thread for each client
 */
public class ConnectionThread extends Thread{
	 private static final String TAG="TCP Server";
	 private static  int nbSock=0;
	 private static int clientId=0;
	 private static HandlerThread[] threadArray = new HandlerThread[Const.NB_SOCKET_MAX];//array of thread
	 
	 public static FragmentActivity fragmentActivity;//for UI
	 public static Context context;
	 
	 private static boolean[] isClient = new boolean[Const.NB_SOCKET_MAX];
	 private static boolean[] threadIsReady = new boolean[Const.NB_SOCKET_MAX];
	 private ServerSocket serverSocket;
	 protected static boolean connected = false;
	 static Handler handler;
	 Semaphore mutex = new Semaphore(1);//protect nbSock connected
	 
	 
	 
	 
	 //DATAS TO SEND FOR EACH CLIENT 
	 private byte[] bufferCl1={00,10,20,'c'},
	 			bufferCl2={'b',2,16,'a'},
	 			bufferCl3={'a',3,20,'c'},
	 			bufferCl4={4,'a',1,'c'};
	 private byte[][] bufferCl={bufferCl1,bufferCl2,bufferCl3,bufferCl4};

	
	 ConnectionThread(boolean connected, Handler handler,FragmentActivity fragmentActivity){
			ConnectionThread.fragmentActivity=fragmentActivity;
			ConnectionThread.connected=connected;	 
			ConnectionThread.handler=handler;
			ConnectionThread.context = fragmentActivity.getBaseContext();
	 }
	 
	 
	 /*************************
	  **  setters & getters  **
	  *************************/
	 public static int getNbSock() {
			return nbSock;
	 }

	 public static void setNbSock(int nbSock) {
		ConnectionThread.nbSock = nbSock;
	 }
	
	 public HandlerThread[] getThreadArray() {
		return threadArray;
	 }

	 public void setThreadArray(HandlerThread[] threadArray,HandlerThread thr, int idClient) {
		threadArray[idClient] = thr;
	 }
	 
	 public boolean[] getIsClient() {
			return isClient;
	 }

	public void setIsClient(boolean[] isClient, int clientId, boolean b) {
		ConnectionThread.isClient[clientId] = b;
	}
	
	public boolean[] getThreadIsReady() {
		return threadIsReady;
	}

	public void setThreadIsReady(boolean[] threadIsReady, int clientId, boolean b) {
		ConnectionThread.threadIsReady[clientId] = b;
	}
	
	
	
	
	
	/**********
	 *  Main  *
	 **********/
	 public void run(){
	 		try {
	 			
	 			if(connected){
	 				
	 				
	 				//** SERVER SOCKET CREATION */
			 		Log.i(TAG, "S: Connecting...");
					serverSocket= new ServerSocket(Const.SERVER_PORT);
			 		
					//UDP Broadcast
					
					Discover discover = new Discover(context,Const.UDP_PORT);
					discover.start();
					
				 	while(connected){
				 		//mutex : beginning of section 
				 		try {
							mutex.acquire();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				 		while(nbSock>=Const.NB_SOCKET_MAX){
				 			Log.i(TAG, "NB_SOCKET_MAX, please wait 1000ms nbSock="+nbSock);
				 			try {
								Thread.sleep(1000); //sleep
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
				 		}
				 		
				 		//mutex : end of section
				 		mutex.release();

				 		//** SOCKET CONNECTION */
				 		Socket s = serverSocket.accept();
				 		
				 		//UpdateUI.updateUICreateButtonInMain(handler, "connected");
				
					    //Look for the free clientId
				 	    while(getIsClient()[clientId%Const.NB_SOCKET_MAX]){clientId=(clientId+1)%Const.NB_SOCKET_MAX;}
						nbSock++;
						setIsClient(isClient,clientId,true);
						Log.i(TAG, "S: Client connected: "+clientId+" Nb sock "+nbSock);
						
						// new Thread
						HandlerThread thr = new HandlerThread(s,clientId,bufferCl[clientId]);
						thr.start();
						
						setThreadArray(threadArray,thr,clientId);
						
						//in database
						storeNodeDB(thr,s);
				 	}
				 	serverSocket.close();	
	 			}
				
	 	} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "error ConnectionThread");
		}    
   }

	 
	 
	 /**Store datas in db*/
	 public void storeNodeDB(final HandlerThread thr, final Socket socket){
		 handler.post(new Runnable() {
	            @Override
	            public void run() {
		
					 while(!thr.isIdNodeReceived()){
						 try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							Log.i(TAG, "Thread: Exception");
						} 
					 }
					 int idNode = thr.getIdNode();
					 
					//store Node IP address in DATABASE
					 SQLiteHelper db = new SQLiteHelper(context);
					
					 if( db.getNodeByNodeId(idNode) != null){//this idnode is already present in db
						Node node = db.getNodeByNodeId(idNode);
						
						Log.d("DB","old: "+node.getIpAddress()+", new : "+socket.getInetAddress().toString());
						
						//ip addr different
						if(!node.getIpAddress().equals(socket.getInetAddress().toString())) {
							Log.d("DB","old: "+node.getIpAddress()+", new : "+socket.getInetAddress().toString());
							//check if the new ip addr is not already stored, else delete it
							if( db.getNodeByIPAddress(socket.getInetAddress().toString()) != null){
								db.deleteNode(db.getNodeByIPAddress(socket.getInetAddress().toString()));
								Log.d("DB","exists");
							}
							
							db.updateNode(node);
							Log.d("DB","updated");
						}
					} else{
						db.addNode(new Node(6, socket.getInetAddress().toString(),idNode));  
						Log.d("DB","ADD");
					}
					 UpdateUI.updateUICreateDB(handler);
	            }
	       });

	 }
	 

	 
	 
	 
}