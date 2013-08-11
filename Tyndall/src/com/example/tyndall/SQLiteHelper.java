package com.example.tyndall;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper{
	
	 // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "nodesDatabase.db";
    // Nodes table name
    private static final String TABLE_NODES = "nodes";
 
    // Nodes Table Columns names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IP_ADDRESS = "ip";
    private static final String COLUMN_NODE_ID = "node_id";
    
    private String[] allColumns = { COLUMN_ID, COLUMN_IP_ADDRESS, COLUMN_NODE_ID};
   
    
    
    
    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }
 
    
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NODES_TABLE = "CREATE TABLE " + TABLE_NODES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_IP_ADDRESS + " TEXT,"
                + COLUMN_NODE_ID + " INTEGER)";
        db.execSQL(CREATE_NODES_TABLE);    
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NODES);
 
        // Create tables again
        onCreate(db);
    }
 
    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
 
    // Adding new Node
    void addNode(Node Node) {
    
	        SQLiteDatabase db = this.getWritableDatabase();
	 
	        ContentValues values = new ContentValues();
	        values.put(COLUMN_IP_ADDRESS, Node.getIpAddress()); // Node Name
	        values.put(COLUMN_NODE_ID, Node.getNodeID()); // Node Phone
	 
	        // Inserting Row
	        db.insert(TABLE_NODES, null, values);
	        db.close(); // Closing database connection
   }
    
 
    // Getting single Node
    Node getNodeById(int id) {
    
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query(TABLE_NODES, allColumns, COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if(cursor.getCount() ==0 ) //no element id 
        	return null;
        else {
        	 if (cursor != null)
                 cursor.moveToFirst();
	        Node Node = new Node(Integer.parseInt(cursor.getString(0)),
	                cursor.getString(1), Integer.parseInt(cursor.getString(2)));
	        // return Node
	        return Node;
        }
  
    }
    
    
    // Getting single Node by idNode
    Node getNodeByNodeId(int nodeId) {
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.query(TABLE_NODES, allColumns, COLUMN_NODE_ID + "=?",
	                new String[] { String.valueOf(nodeId) }, 
	                null, null, null, null);
	        
	        if(cursor.getCount() ==0 ) //no element id 
	        	return null;
	        else {
		        if (cursor != null)
		            cursor.moveToFirst();
		        Node Node = new Node(Integer.parseInt(cursor.getString(0)),
		                cursor.getString(1), Integer.parseInt(cursor.getString(2)));
	        
	        // return Node
	        return Node;
	        }
    }
    
    
 // Getting single Node by idNode
    Node getNodeByIPAddress(String ipAddress) {
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.query(TABLE_NODES, allColumns, COLUMN_IP_ADDRESS +
	        		" LIKE \""+ipAddress+"\"", 
	                null, null, null, null);
	        
	        if(cursor.getCount() ==0 ) //no element id 
	        	return null;
	        else {
		        if (cursor != null)
		            cursor.moveToFirst();
		        Node Node = new Node(Integer.parseInt(cursor.getString(0)),
		                cursor.getString(1), Integer.parseInt(cursor.getString(2)));
	        
	        // return Node
	        return Node;
	        }
    }
    
     
    // Getting All Nodes
    public List<Node> getAllNodes() {

	        List<Node> NodeList = new ArrayList<Node>();
	        // Select All Query
	        String selectQuery = "SELECT  * FROM " + TABLE_NODES;
	 
	        SQLiteDatabase db = this.getWritableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	 
	        // looping through all rows and adding to list
	        if (cursor.moveToFirst()) {
	            do {
	                Node Node = new Node();
	                Node.setId(Integer.parseInt(cursor.getString(0)));
	                Node.setIpAddress(cursor.getString(1));
	                Node.setNodeID(Integer.parseInt(cursor.getString(2)));
	                // Adding Node to list
	                NodeList.add(Node);
	            } while (cursor.moveToNext());
	        }
	 
	        // return Node list
	        return NodeList;
    
    }
 
    // Updating single Node
    public int updateNode(Node Node) {
    	
	        SQLiteDatabase db = this.getWritableDatabase();
	 
	        ContentValues values = new ContentValues();
	        values.put(COLUMN_IP_ADDRESS, Node.getIpAddress());
	        values.put(COLUMN_NODE_ID, Node.getNodeID());
	 
	        // updating row
	        return db.update(TABLE_NODES, values, COLUMN_ID + " = ?",
	                new String[] { String.valueOf(Node.getId()) });
    	
    }
 
    
    // Deleting single Node
    public void deleteNode(Node Node) {
    
	        SQLiteDatabase db = this.getWritableDatabase();
	        db.delete(TABLE_NODES, COLUMN_ID + " = ?",
	                new String[] { String.valueOf(Node.getId()) });
	        db.close();
    
    }
    
 // Deleting single Node
    public void deleteAllNodes() {
    	
    		List<Node> nodeList = getAllNodes();
    		for (Node node : nodeList){
    			deleteNode(node);
        	}	
    
    }
    
    
    
 
    // Deleting table
    public void deleteTable() {
    	
	    	SQLiteDatabase db = this.getWritableDatabase();
	        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NODES);
    }
    
 
    // Getting Nodes Count
    public int getNodesCount() {
	        String countQuery = "SELECT  * FROM " + TABLE_NODES;
	        SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.rawQuery(countQuery, null);
	        cursor.close();
	 
	        // return count
	        return cursor.getCount();
    	
    }

}
