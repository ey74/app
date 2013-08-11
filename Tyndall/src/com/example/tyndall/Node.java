package com.example.tyndall;


public class Node {
	//Variables
	private int id;
	private String ipAddress;
	private int nodeID;
	
	
	public Node(){
		
	}
	
	public Node (int id, String ipAddress, int nodeID){
		this.setId(id);
		this.ipAddress = ipAddress;
		this.nodeID = nodeID;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
	
	
	
}
