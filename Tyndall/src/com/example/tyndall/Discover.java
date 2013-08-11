/**
 * 
 * File : Discover.java
 * Created : July 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * UDP Broadcast to send IP Server Address & to discover 
 */


package com.example.tyndall;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;


public class Discover extends Thread{
	private DatagramSocket socketUDP;
	private Context mContext;
	private int mPort;
	
	//private byte[] currentTime = {0,0,0,0,0,0,0,0};
	private byte[] byteDatagram = new byte[16];/* contains password[4Bytes]+ 
	TCP_SERVER_IP Address[as 4 bytes]+ Current time encoded as 8 bytes */
	
	
	
	Discover(Context mContext, int mPort){
		this.mContext = mContext;
		this.mPort = mPort;
	 }

	
	 public void run(){
		 try {
			 InetAddress address = InetAddress.getByName(getMyIPAddress());
			
			 byte[] ip = address.getAddress();
			 System.arraycopy(Const.PASSWORD, 0, byteDatagram, 0, 4); //Password
			 System.arraycopy(ip,0,byteDatagram,4,4); //IP
			 
			 //time
			    long nowSecs = System.currentTimeMillis()/2000;
			    long now32 = System.currentTimeMillis()%2000;
			    now32 *= Math.pow(2, 16)/2000.0;
			   /* byteDatagram[]=(byte)((nowSecs>>40)&0xFF);
			    byteDatagram[]=(byte)((nowSecs>>32)&0xFF);
			    byteDatagram[]=(byte)((nowSecs>>24)&0xFF);
			    byteDatagram[]=(byte)((nowSecs>>16)&0xFF);  
			    byteDatagram[]=(byte)((nowSecs>>8)&0xFF);
			    byteDatagram[]=(byte)((nowSecs)&0xFF);
			    byteDatagram[]=(byte)((now32>>8)&0xFF);
			    byteDatagram[]=(byte)((now32)&0xFF);*/
			    System.arraycopy(longToBytes(nowSecs),0,
						 byteDatagram,8,6); //Current Time 6bytes     
			    System.arraycopy(longToBytes(now32),0,
						 byteDatagram,14,2);
			    
			 
			 
			/* System.arraycopy(longToBytes( System.currentTimeMillis()),0,
					 byteDatagram,8,8); *///Current Time 8bytes
			 sendBroadcast(byteDatagram,mPort);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	
	 
	
	 /**
	   * Calculate the broadcast IP . 
	   * 
	   */
	 @SuppressWarnings("unused")
	private InetAddress getBroadcastAddress2() throws Exception {
	     WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	     DhcpInfo dhcp = wifiManager.getDhcpInfo();
	     int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	     byte[] quads = new byte[4];
	     for (int k = 0; k < 4; k++)
	       quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	     return InetAddress.getByAddress(quads);
	 }
	 
	 
	 /**
	   * Calculate the broadcast IP . 
	   * 
	   */
	 @SuppressLint("NewApi")
	private InetAddress getBroadcastAddress(InetAddress inetAddr) throws Exception {
	    NetworkInterface temp;
	    InetAddress iAddr=null;
	    try{
	    	temp = NetworkInterface.getByInetAddress(inetAddr);
	    	List<InterfaceAddress> addresses = temp.getInterfaceAddresses();
	    	for(InterfaceAddress inetAddress:addresses)
	    		iAddr = inetAddress.getBroadcast();
	    
	    	return iAddr;
	    }catch(SocketException e){
	    	Log.e("Discover","Exception");
	    }
	    return null;
	 }

	 /**
	  * Send the datagram UDP in broadcast : 
	  * containing PASSWORD[‘L’,’U’,’K’,’E’][4Bytes]+
	  * TCP_SERVER IP Address[as 4 bytes]+
	  * Current time encoded as 64 bits
	  *
	  * @param requete
	  * @param port
	  * @throws Exception
	  */
	 @SuppressLint("NewApi")
	private DatagramPacket sendBroadcast(byte[] byteToSend, int port) throws Exception {
		 
	 	DatagramSocket socket = new DatagramSocket(mPort);
	 	socket.setBroadcast(true);
	 	
	 	InetAddress myAddress = InetAddress.getByName(getMyIPAddress());
	 	InetAddress broadcastAdress = getBroadcastAddress(myAddress);
	

	 	DatagramPacket packet = new DatagramPacket(byteToSend, 
	 			byteToSend.length, broadcastAdress, port);

	 
	 	// 10 UDP Packets
	 	 for (int k = 0; k < 10; k++)
	 		 socket.send(packet);
	 	

	 	//byte[] buf = new byte[1024];
	 	//packet = new DatagramPacket(buf, buf.length); 	
	 	//TODO: No, clients don't answer in UDP connection
	 	/*socket.setSoTimeout(Const.RECEIVING_TIMEOUT_SERVER);
	 	String myIP = getMyIPAddress();		
	 	//Wait the answer of each nodes (clients), and store their IP @ 
	 	socket.receive(packet);
	 	// Caution, broadcast -> I send the message to me too...
	 	while (packet.getAddress().getHostAddress().contains(myIP)) {
	 		socket.receive(packet);
	 	}
	 	*/
	 	
	 	//TODO : Store all the ip adress received
	 	//String p = packet.getAddress().getHostAddress();
	 	
	 
	 	
	 	socket.close();
	 
	 	return packet;
	 	
	 }
	 
	 
	 /**
	  * Get my IP @
	  * @return
	  */
	 
	 public String getMyIPAddress(){
	     try 
	     {
	         for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); 
	        		 en.hasMoreElements();){
	             NetworkInterface intf = en.nextElement();
	             for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); 
	            		 enumIpAddr.hasMoreElements();){
	                 InetAddress inetAddress = enumIpAddr.nextElement();
	                 if (!inetAddress.isLoopbackAddress()) 
	                 	return inetAddress.getHostAddress();
	             }
	         }
	     } 
	     catch (SocketException e) {
	         Log.e("DISCOVER","Exception");
	     }
	     return null;
	 }
	 
	 
	 
	/* 
	 public String getClientIp(String message, int port)
		{
			try 
			{
				//DatagramPacket packet = sendBroadcast("Ping",port);
				DatagramPacket packet = sendBroadcast(byteDatagram,port);//client give his @
				return packet.getAddress().getHostAddress();
			} 
			catch (InterruptedIOException ie)
			{
				Log.d("ERROR", "No server found");
				try
				{
					socketUDP.close();
				}
				catch (Exception e2) {}
				ie.printStackTrace();
			}
			catch (Exception e) 
			{
				Log.d("ERROR", "Verify your Wifi connection");
				try
				{
					socketUDP.close();
				}
				catch (Exception e2) {}
				e.printStackTrace();
			}
			
			stopSocket();		
			return null;
		}
	 
	 */
	 
	 public void stopSocket()
		{
		 	try
			{
				socketUDP.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	 
	 public static byte[] longToBytes(long someLong) throws IOException {
		   ByteArrayOutputStream baos = new ByteArrayOutputStream();
		   DataOutputStream dos = new DataOutputStream(baos);
		   dos.writeLong(someLong);
		   dos.close();
		   byte[] longBytes = baos.toByteArray();
		   return longBytes;
	  }
	 
	 
}
