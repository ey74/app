/**
 * 
 * File : HandlerThread.java
 * Created : June 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * 
 */

package com.example.tyndall;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;
import android.widget.TextView;

/**
 *  Each thread handles,writes and reads in the connection  
 *  - represents 1 connection with 1 client -
 */

public class HandlerThread extends ConnectionThread{
	
	//** Variables */
	
	//Connection
	public static final String TAG="TCP Server";
	InputStream is; //read the message received from client
	OutputStream os; //sends the message to the client
    private Socket socket;
    private int clientId;
    private int noInstruction;
	
	//Data 
    private byte[] bufferR;
	private boolean[] tabDatasFull = new boolean[Const.NO_SENSOR];
	private double[][] xPacketData = new double[Const.NO_SENSOR][],
 			yPacketData= new double[Const.NO_SENSOR][],
 			zPacketData= new double[Const.NO_SENSOR][];//each array is the array of a sensor
 	private double[][] xPacketForFFT = new double[Const.NO_SENSOR][Const.MAX_SAMPLES_FOR_FFT]
 			,yPacketForFFT = new double[Const.NO_SENSOR][Const.MAX_SAMPLES_FOR_FFT]
 			,zPacketForFFT = new double[Const.NO_SENSOR][Const.MAX_SAMPLES_FOR_FFT];
	private static double[][] tmpXFFT = new double[Const.NO_SENSOR][Const.MAX_SAMPLES_FOR_FFT]
			,tmpYFFT = new double[Const.NO_SENSOR][Const.MAX_SAMPLES_FOR_FFT]
			,tmpZFFT = new double[Const.NO_SENSOR][Const.MAX_SAMPLES_FOR_FFT];
	private int[] index = new int[Const.NO_SENSOR];
   
    //Variable for parsing
    private int idNode = 0;
    private int sensorId;
    private boolean[] sensorPresentInPacket = new boolean[Const.NO_SENSOR];
    private int[] noSample = new int[Const.NO_SAMPLE_MAX];
    private int packetLength;
	private int numPacket=0;
	private int[] packetSamplesRelativeTime = new int[Const.NO_SENSOR];
	private int[] packetSamplingRates = new int[Const.NO_SENSOR];
	private int[] previousPacketSamplingRates = new int[Const.NO_SENSOR];	
	private int timePacketEpoch = 0, higherTime=0;
	private boolean dataReady;
	private boolean idNodeReceived = false;
	private boolean[] xIsPresent = new boolean[Const.NO_SENSOR],
 			yIsPresent = new boolean[Const.NO_SENSOR],
 			zIsPresent = new boolean[Const.NO_SENSOR];
	
	//UI 
    private  TextView textSocketConnected;
    private static TextView[] textViewArrC;
 	
    //Send
    @SuppressWarnings("unused")
	private byte[] bufferToSend;
 	
 	//Listeners
 	private List<BufferListener> listeners= new ArrayList<BufferListener>();
 	
 	
 	
 	

 	//** Constructor */
	HandlerThread(Socket s, int clientId,byte[] bufferToSend){
	   	super(connected,handler, fragmentActivity);
		this.socket=s;
	   	this.clientId=clientId;
	   	this.bufferToSend=bufferToSend;
    }
    
	

    /*************************
     **  setters & getters  **
     *************************/
    public int getIdNode() {
  		return idNode;
  	}
     
  	public List<BufferListener> getListeners() {
 		return listeners;
 	}
    
    public int getNumPacket() {
		return numPacket;
	}
	
	public void setTimePacketEpoch(int timeEpoch) {
		this.timePacketEpoch = timeEpoch;
	}
	public int getTimePacketEpoch() {
		return this.timePacketEpoch;
	}
	public int getPacketLength() {
		return packetLength;
	}
	
	/** Values */
	
	public boolean[] getXIsPresent() {
		return xIsPresent;
	}
	public boolean[] getYIsPresent() {
		return yIsPresent;
	}
	public boolean[] getZIsPresent() {
		return zIsPresent;
	}

	/** gives sensors present in the packet */
	public boolean[] getSensorPresentInPacket() {
		return sensorPresentInPacket;
	}

	public void setSensorPresentInPacket(int sensorId, boolean b) {
		this.sensorPresentInPacket [sensorId]= b;
	}
	
	public int getSensorId() {
		return sensorId;
	}

	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}
	   
	public double[][] getXPacketData(){
		return this.xPacketData;
	}
	
	public void setXPacketData(double[] xSensor, int idSensor) {
		this.xPacketData[idSensor]=xSensor;
	}
	
	public double[][] getYPacketData(){
		return this.yPacketData;
	}
	
	public void setYPacketData(double[] ySensor, int idSensor) {
		this.yPacketData[idSensor]=ySensor;
	}
	
	public double[][] getZPacketData(){
		return this.zPacketData;
	}
	
	public void setZPacketData(double[] zSensor, int idSensor) {
		this.zPacketData[idSensor]=zSensor;
	}
	
	
	public int[] getNoSample() {
		return noSample;
	}

	public void setNoSample(int noSample, int sensorId) {
		this.noSample[sensorId] = noSample;
	}
	

	public void setXIsPresent(boolean xIsPresent,int sensorId) {
		this.xIsPresent[sensorId] = xIsPresent;
	}
	public void setYIsPresent(boolean yIsPresent,int sensorId) {
		this.yIsPresent[sensorId] = yIsPresent;
	}
	public void setZIsPresent(boolean zIsPresent,int sensorId) {
		this.zIsPresent[sensorId] = zIsPresent;
	}
	
	public boolean getDataReady(){
		return dataReady;
	}
	
	public int[] getPacketSamplingRates() {
		return packetSamplingRates;
	}
	
	public void setPacketSamplingRates(int rate, int sensorId){
		this.packetSamplingRates[sensorId] = rate;
	}
	
	public int[] getPacketSamplesRelativeTime() {
		return packetSamplesRelativeTime;
	}
	
	public void setPacketSamplesRelativeTime(int relativeTimeStamp, int sensorId){
		this.packetSamplesRelativeTime[sensorId] = relativeTimeStamp;
	}
	
	
	public boolean isIdNodeReceived() {
		return idNodeReceived;
	}

	public boolean[] getTabDatasFull() {
		return tabDatasFull;
	}

	public void setXPacketForFFT(double[] tmp, int sensorId){
		this.xPacketForFFT[sensorId] = tmp;
	}
	public void setYPacketForFFT(double[] tmp, int sensorId){
		this.yPacketForFFT[sensorId] = tmp;
	}
	public void setZPacketForFFT(double[] tmp, int sensorId){
		this.zPacketForFFT[sensorId] = tmp;
	}
	
	public double[][] getXPacketForFFT(){
		return this.xPacketForFFT;
	}
	public double[][] getYPacketForFFT(){
		return this.yPacketForFFT;
	}
	public double[][] getZPacketForFFT(){
		return this.zPacketForFFT;
	}

	public void setTabDatasFull(boolean isFull, int sensorId) {
		this.tabDatasFull[sensorId] = isFull;
	}
	
	
	

	/***************************
     ** Methods for listener  **
     ***************************/
    public byte[] getNewBuffer() {
		return bufferR;
	}
    
    public void setNewBuffer(byte[] newBuffer) {
		this.bufferR=newBuffer;
		reportBufferHasChanged(newBuffer);
	}
    
    public void addBufferListener(BufferListener listener){
    	listeners.add(listener);
    }
    
    //called by setNewBuffer
    public void reportBufferHasChanged(byte[] newBuffer){
    	for (BufferListener listener : listeners){
    		listener.bufferHasChanged(newBuffer);
    	}	
    }
    
    public void reportBufferHasStopped(){
    	for (BufferListener listener : listeners){
    		listener.bufferHasStopped();
    	}	
    }
    
    
    
    
    /***************************
     **   UI update methods   **
     ***************************/
   
    public void updateUISend(final TextView textView, final String message){
   	 handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
                 textView.setText(message);
            }
        });
   }
   
  
   
   public TextView textViewConnection(int clientId){
		textViewArrC = new TextView[4];
		textViewArrC[0] = (TextView) ConnectionThread.fragmentActivity.findViewById(R.id.textStatusCl1Connection);
		textViewArrC[1] = (TextView) ConnectionThread.fragmentActivity.findViewById(R.id.textStatusCl2Connection);
		textViewArrC[2] = (TextView) ConnectionThread.fragmentActivity.findViewById(R.id.textStatusCl3Connection);
		textViewArrC[3] = (TextView) ConnectionThread.fragmentActivity.findViewById(R.id.textStatusCl4Connection);
		return textViewArrC[clientId];
   }
 
  
   
     
   /***********************************
    **  Main : read and write datas  **
    ***********************************/
    
   public void run(){
	   
   	  try {
   		//Update nb sock connected
   		textSocketConnected = (TextView)ConnectionThread.fragmentActivity.findViewById(R.id.textSocketConnected);
   		UpdateUI.updateUITextView(handler,"Sockets connected : "+ConnectionThread.getNbSock(),textSocketConnected);
   		final TextView textViewUpdated = textViewConnection(clientId);

   		  
   		if(socket.isConnected()){
			os = socket.getOutputStream();
			is = socket.getInputStream();
				
				
			// Reading in a new child thread
			new Thread(new Runnable() {
	    		@Override                 		 
	    		public void run() {
	    			//	updateUICreateButton("connected");
	    			setThreadIsReady(getThreadIsReady(),clientId,true);
	    			
	    			//add to listeners
	    			addBufferListener(new BufferListener(){
	    				public void bufferHasChanged(byte[] bufferR){
	    					//contains one data packet 		
	    					updateUISend(textViewUpdated,"Node Id: "+getIdNode()+" received "+getNumPacket()+" packets");
	    				}
	    				
	    				public void bufferHasStopped(){
	    				}
	    				
	    			});
	    		
	    			// change the bufferR when it has received 1 packet
	    			readDataArray();

	    		}
	    	}).start();
				  
				
			updateUISend(textViewUpdated,"datas sent to client "+clientId); 

				 
			 //Welcome Message (ACK)
			 sendDataArray(createHeader(0,(byte)(0x0)),0,
					 createHeader(0,(byte)(0x0)).length);
			 
			 // Wait id Node to send instructions
			 while(!idNodeReceived){
				 try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					Log.i(TAG, "Thread: Exception");
				} 
			 }
	
			 
			 // Send first instruction 
			 noInstruction = 7;
			 byte[] firstInstructionArray = {
					 (byte)Const.INST_MPU_SAMPLING_FREQ,(byte)Const.MPU_SAM_FRE_NO_FILTER[4],
					 (byte)Const.INST_ACC_MPU_ENABLE,(byte)Const.INST_DATA_ONE,
					 (byte)Const.INST_GYR_MPU_ENABLE,(byte)Const.INST_DATA_ONE,
					 (byte)Const.INST_MAG_MPU_ENABLE,(byte)Const.INST_DATA_ZERO,
					 (byte)Const.INST_ACC_FXO_ENABLE,(byte)Const.INST_DATA_ZERO,
					 (byte)Const.INST_MAG_FXO_ENABLE,(byte)Const.INST_DATA_ZERO,
					 (byte)Const.INST_ACC_LIS_ENABLE,(byte)Const.INST_DATA_ZERO
			};
			 sendInstructionWithHeader(noInstruction,firstInstructionArray,(byte)0x0);
			 Log.i(TAG, "Send instructions");
			
			 
			 //Send other datas
			 //	 sendDataArray(bufferToSend,0,bufferToSend.length);
				 
   		}else
   			 stopSocket();	        			  
   		  
	  } catch (IOException e) {
			//e.printStackTrace();
			stopSocket();
		    Log.i(TAG, "Thread: IOException");
	  }	  
   	
   }
   

   
   
   
   /*********************************************************************
  	***********                   RECEPTION                   ***********
  	*********************************************************************/
  
   
   /** Read data from client */
   public void readDataArray(){
	bufferR = new byte[4096];
	int read=0;
	packetLength = 0;
	
	try{
		//read length packet (2bytes)
		read = is.read(bufferR, 0, Const.HEADER_DATA_LENGTH);
		
		while((socket.isConnected())&&(read != -1)){  
			packetLength = Calc.byteArrayToInt(bufferR);

    		
			//following = data
	    	read = is.read(bufferR, 0, packetLength); //This is blocking
	    	numPacket = getNumPacket()+1 ;

	    	
	    	//call listener to report bufferR changed, one packet has been read
	    	if((socket.isConnected())&&(read!=-1)){
	    		parser(bufferR);
	    		setNewBuffer(bufferR);
	    	}
	    	read = is.read(bufferR, 0, Const.HEADER_DATA_LENGTH);	

    	}  	
		
		stopSocket();
            
		
	}catch(IOException e) {
		    Log.i(TAG, "readDataArray: IOException");
	}
   }
   
   
   
   /**************
	**  parser  **
	**************/
   private void parser(byte[] bufferR) {
	   int current = 0;
	   boolean batteryLow=false, batteryFull=false, sdFull = false;
	   int usbState=0;
	   int powerSaveMode = 0;
	   
 
	   //step 1. Parse header
	   //step 1a. 
	   boolean BASIC_HEADER=true;
	   if((bufferR[0]&(1<<7))==1)
		   BASIC_HEADER=false;
	   
	   //Low battery alert
	    if( ((bufferR[0]>>6 & (0x01) ) == 1) && (!batteryLow) ) 
	    	UpdateUI.makeToastUI(handler,"low battery");
	    batteryLow = (((bufferR[0]>>6 & (0x01))==0) ? false : true);
	    
	    //Battery full alert
	    if( ((bufferR[0]>>5 & (0x01) ) == 1) && (!batteryFull) ) 
	    	UpdateUI.makeToastUI(handler,"full battery");
	    batteryFull  = (((bufferR[0]>>5 & (0x01))==0) ? false : true);

	    //USB State Change
	    if( (bufferR[0]>>4 & (0x01) ) != usbState ) {
	        usbState = bufferR[0]>>4 & (0x01);
	        if(usbState == 1)
	        	UpdateUI.makeToastUI(handler,"USB connected");
	        else
	        	UpdateUI.makeToastUI(handler,"USB disconnected");
	    }

	    //SD Card Full Alert
	    if( ((bufferR[0]>>3 & (0x01) ) == 1) && (!sdFull) )
	    	UpdateUI.makeToastUI(handler,"SD Card full");
	    sdFull = ((( bufferR[0]>>3 & (0x01))==0) ? false : true);

	    //Power Save Mode
	    if( (bufferR[0]>>2 & (0x01) ) != powerSaveMode ) {
	        powerSaveMode = bufferR[0]>>2 & (0x01);
	        if(powerSaveMode == 1)
	        	UpdateUI.makeToastUI(handler,"Power Save Enabled");
	        else
	        	UpdateUI.makeToastUI(handler,"Power Save Disabled");
	    }

	    //Internal Error TODO
	    /*  if( ((bufferR[0]>>1 & (0x01) ) == 1) && (!interError) )
	        //makeToastUI("Internal Error");
	     */
	    
	    
	    //Step1b: Save timeStamp & NodeId
	  //  int timePacketEpoch = bufferR[1]<<56 + bufferR[2]<<48+
	    int timePacketEpoch = 	bufferR[0]<<40 + bufferR[1]<<32+
	    		bufferR[2]<<24 + bufferR[3]<<16+
	    		bufferR[4]<<8 + bufferR[5];
	    timePacketEpoch *= 2000;
	    higherTime = timePacketEpoch;
	    timePacketEpoch += ((bufferR[6]*256)+bufferR[7])/((int)Math.pow(2, 16))*2000.0;
	    setTimePacketEpoch(timePacketEpoch);
	    


	    
	    
	    
	    
	    
	    
	    byte[] idNodeA = Arrays.copyOfRange(bufferR,9,11);
	    idNode=(Calc.byteArrayToInt(idNodeA));
	    idNodeReceived = true;

	    
	    //Step1c: Wifi info basic
	    // byte[] wifiInfo = Arrays.copyOfRange(bufferR, 11, 15);
	    
	    //Step1d: extend
	    if(BASIC_HEADER==false) 
	       // byte batteryPercent = bufferR[16];
	        current = 36; //assuming 20 extra bytes in the extended header
	    else 
	        current = 16;
	    

	    byte extendByte = bufferR[15];
	    while(extendByte!=0) {
	        //Step 1e: check for extend byte
	        switch (extendByte) {
		        case Const.EXTEND_SENSOR_DATA:
		            //call sensor data parser
		            current=sensorDataParser(bufferR,current,higherTime);
		            extendByte=bufferR[current];
		            current++;
		            break;
	
		        case Const.EXTEND_ALGORITHM:
		            //call algorithm parser
		            current=AlgorithmParser(bufferR,current,higherTime);
		            extendByte=bufferR[current];
		            current++;
		            break;
	
		        default:
		            extendByte=0;
		            break;
		   }
	    }
   }
   
   
   
   public int sensorDataParser(byte[] sensorPacket,int sensorIndex,int timePacketEpoch){
	   int sensorPacketAxesEnabled,sensorPacketSampleRate;
	  //, sensorPacketRange,sensorPacketResolution;
	   
	   
	   //Step 1: get sensorID : ACC_MPU_ID, GYR_MPU_ID, ...
	   sensorId = sensorPacket[sensorIndex+0];
	   setSensorPresentInPacket(sensorId,true);
	
	   //Step 2,3,4,5: sampling rate, resolution, axes enabled, range
	    switch (sensorId) {
	    case Const.ACC_MPU_ID:
	    	/*
	        sensorPacketResolution = accMpuResolution;
	        sensorPacketAxesEnabled = accMpuAxes;
	        sensorPacketRange = accMpuRange;
	        sensorPacketSampleRate = accMpuSampleRate;
	        */
	        break;
	    case Const.GYR_MPU_ID :
	    	/*
	        sensorPacketResolution = gyrMpuResolution;
	        sensorPacketAxesEnabled = gyrMpuAxes;
	        sensorPacketRange = gyrMpuRange;
	        sensorPacketSampleRate = gyrMpuSampleRate;
	        */
	        break;
	    case Const.MAG_MPU_ID:
	    	/*
	        sensorPacketResolution = magMpuResolution;
	        sensorPacketAxesEnabled = magMpuAxes;
	        sensorPacketRange = magMpuRange;
	        sensorPacketSampleRate = magMpuSampleRate;
	        */
	        break;
	    case Const.ACC_FXO_ID:
	    	/*
	        sensorPacketResolution = accFxoResolution;
	        sensorPacketAxesEnabled = accFxoAxes;
	        sensorPacketRange = accFxoRange;
	        sensorPacketSampleRate = accFxoSampleRate;
	        */
	        break;
	    case Const.MAG_FXO_ID:
	    	/*
	        sensorPacketResolution = magFxoResolution;
	        sensorPacketAxesEnabled = magFxoAxes;
	        sensorPacketRange = magFxoRange;
	        sensorPacketSampleRate = magFxoSampleRate;
	        */
	        break;
	    case Const.ACC_LIS_ID:
	    	/*
	        sensorPacketResolution = accLisResolution;
	        sensorPacketAxesEnabled = accLisAxes;
	        sensorPacketRange = accLisRange;
	        sensorPacketSampleRate = accLisSampleRate;
	        */
	        break;
	        
	    default:
	        break;
	    }
	   

	    //Step 6: Hz -- improve the code for the 1st (because previoussampling always != newsampling)
	    previousPacketSamplingRates[sensorId]= getPacketSamplingRates()[sensorId];
	    int samplingRate = sensorPacket[sensorIndex+1]*256 + sensorPacket[sensorIndex+2];
	  //  Log.i("HANDLER","samplingrate "+samplingRate);
	    
	    //FOR TESTS:
	    samplingRate = 1000;
	    
	    
	    setPacketSamplingRates(samplingRate,sensorId);
	    //Log.i("Handler","sampling rate = "+packetSamplingRates[sensorId]);
	   
	    
	    //Step 7: Store timeStamp : ms
	   	int relativeTimeStamp=sensorPacket[sensorIndex+3]*256 + sensorPacket[sensorIndex+4];
	    setPacketSamplesRelativeTime(relativeTimeStamp,sensorId);

	    //Step 8: No. of samples & No. of bytes to read
	    int noSamples = (sensorPacket[sensorIndex+5]<0 ? (int)sensorPacket[sensorIndex+5]+256 
	    		: (int)sensorPacket[sensorIndex+5]);
	    setNoSample(noSamples,sensorId);//array else can't be used in fragment (?)
	    
	    //Step 9: Read data
	    sensorIndex = sensorIndex+6;
	    
	    //Test
	    sensorPacketAxesEnabled=0x07;//00000111
	    //sensorPacketAxesEnabled=0x05;//00000101
	    sensorPacketSampleRate=1000000;
	    
	    dataReady=false;	   
	    double[] xSensorData = new double[noSamples],
	    		 ySensorData = new double[noSamples],
	    		 zSensorData = new double[noSamples];
	    boolean  xAxeEnable=false,
	    		 yAxeEnable=false,
	    		 zAxeEnable=false; 
	    double[] timeStampsArray = new double[noSamples];
	    int ihigh,ilow,tempData;
	   
	    
	    
	    for(int loopI=0;loopI<noSamples;loopI++) {

	        if ((sensorPacketAxesEnabled & 0x01) != 0){ //Xaxis
	        
	        	//in Java byte are signed, in -128:127
	        	ihigh = ( (sensorPacket[sensorIndex]>=0) ? 
	        			sensorPacket[sensorIndex] : 256 + sensorPacket[sensorIndex]);
	        	ilow = ( (sensorPacket[sensorIndex+1]>=0) ? 
	        			sensorPacket[sensorIndex+1] : 256 + sensorPacket[sensorIndex+1]);
	            tempData=ihigh*256+ilow;
	            sensorIndex+=2;
	      
	            xAxeEnable=true;
	            //datas 2's complement
	            xSensorData[loopI] = ( (tempData >=32768) ? 
	            		tempData-65536 : tempData);
	            
	        }

	        
	        if ((sensorPacketAxesEnabled & 0x02) != 0) //Yaxis
	        {
	        	ihigh = ( (sensorPacket[sensorIndex]>0) ? 
	        			sensorPacket[sensorIndex] : 256 + sensorPacket[sensorIndex]);
	        	ilow = ( (sensorPacket[sensorIndex+1]>0) ? 
	        			sensorPacket[sensorIndex+1] : 256 + sensorPacket[sensorIndex+1]);
	            tempData=ihigh*256+ilow;
	            sensorIndex+=2;
	            
	            yAxeEnable=true;
	            //datas 2's complement
	            ySensorData[loopI] = ( (tempData >=32768) ? 
	            		tempData-65536 : tempData);
         
	        }

	        if ((sensorPacketAxesEnabled & 0x04) != 0) //Zaxis
	        {
	        	ihigh = ( (sensorPacket[sensorIndex]>0) ? 
	        			sensorPacket[sensorIndex] : 256 + sensorPacket[sensorIndex]);
	        	ilow = ( (sensorPacket[sensorIndex+1]>0) ? 
	        			sensorPacket[sensorIndex+1] : 256 + sensorPacket[sensorIndex+1]);
	            tempData=ihigh*256+ilow;
	            sensorIndex+=2;
	       
	            zAxeEnable=true;
	            //datas 2's complement
	            zSensorData[loopI] = ( (tempData >=32768) ? tempData-65536 : tempData);
	      
	        }

	        timeStampsArray[loopI]=getTimePacketEpoch() - relativeTimeStamp;
	        relativeTimeStamp+=(1/sensorPacketSampleRate)*1000000;
	        
	 
	    }
	    
	    setXPacketData(xSensorData,sensorId);setXIsPresent(xAxeEnable,sensorId);
	    setYPacketData(ySensorData,sensorId);setYIsPresent(yAxeEnable,sensorId);
	    setZPacketData(zSensorData,sensorId);setZIsPresent(zAxeEnable,sensorId);
	 
	    
	    
	    
	//    Log.i("CHART","1- new subpacket isFull normally false : "+getTabForFFTIsFull()[sensorId]);
	    
	    //if sample rate hasn't changed && if index<MAX_FOR_FFT
	    // continue to fill the tab for fft,
	    //else the tab can be sent and a new tab can be filled
	    // System.arraycopy(xSensorData, 0, xSamplesForFFT, index, getNoSample()[sensorId]);
	   //if samplesRate has changed, fft is calculated from previous datas
	    
	    setTabDatasFull(false,sensorId);
	    if(previousPacketSamplingRates[sensorId] != getPacketSamplingRates()[sensorId]){
	    	setXPacketForFFT(tmpXFFT[sensorId],sensorId);
	    	setYPacketForFFT(tmpYFFT[sensorId],sensorId);
	    	setZPacketForFFT(tmpZFFT[sensorId],sensorId);
	    	setTabDatasFull(true, sensorId);
    		index[sensorId] = 0;
    		Log.i("CHART","2- sensorId:"+sensorId+"isFull  : Samplings are differents: "+getTabDatasFull()[sensorId]);
    		
    	}
	//    Log.i("CHART","3- if 2 then true else false : "+getTabForFFTIsFull()[sensorId]);

	    int datacopied = 0;
	    while(datacopied != getNoSample()[sensorId]){
	         
	    	int len = Math.min(Const.MAX_SAMPLES_FOR_FFT-index[sensorId], getNoSample()[sensorId]);	
	    	System.arraycopy(xSensorData, 0, tmpXFFT[sensorId], index[sensorId], len);
	    	System.arraycopy(ySensorData, 0, tmpYFFT[sensorId], index[sensorId], len);
	    	System.arraycopy(zSensorData, 0, tmpZFFT[sensorId], index[sensorId], len);
	    	index[sensorId]+=len;
	    	datacopied+=len;
	    	
	    	if(index[sensorId] == Const.MAX_SAMPLES_FOR_FFT){
	    		//new tab with the appropriate length
	    		double[] xPacket = new double[index[sensorId]],
	    				 yPacket = new double[index[sensorId]],
	    				 zPacket = new double[index[sensorId]];
	    		System.arraycopy(tmpXFFT[sensorId], 0, xPacket, 0, index[sensorId]);
	    		System.arraycopy(tmpYFFT[sensorId], 0, yPacket, 0, index[sensorId]);
	    		System.arraycopy(tmpZFFT[sensorId], 0, zPacket, 0, index[sensorId]);
	    		
	    		setXPacketForFFT(xPacket,sensorId);
	    		setYPacketForFFT(yPacket,sensorId);
	    		setZPacketForFFT(zPacket,sensorId);
	    		index[sensorId] = 0;
	    		setTabDatasFull(true,sensorId);
	    		
	    		//Log.i("CHART","sensorId! 4- New tab, Max has been reached : "+sensorId+"   "+getTabXForFFTIsFull()[sensorId]);
	    	}	
	    }
	   // Log.i("CHART","5- true if 2 or 4 true  : "+getTabXForFFTIsFull()[sensorId]);

	    
	    dataReady = true; //Now, access to all previous variables 

	    return sensorIndex;
	}
   

private int AlgorithmParser(byte[] bufferR2, int sensorIndex, int nodeTimeStamp2) {
	// TODO Auto-generated method stub
	return 0;
 }




/*********************************************************************
 *************                   SENDING                **************
 *********************************************************************/

/** Send the byte array to the client id*/
public void sendDataArray(byte[] cmd, int offset, int len){
	   try {
             if (socket.isConnected()) {
                 Log.i(TAG, "SendData to client");
                 os.write(cmd,offset,len);

             } else 
                 Log.i(TAG, "SendData: Cannot send message. Socket is closed");
             
    } catch (Exception e) {
             Log.i(TAG, "SendData: Message send failed. Caught an exception");
    }     	
}




/**
 * createHeader
 */
	public byte[] createHeader(int packetLengthFollowing, byte extByteHeader) throws IOException{
		int len = packetLengthFollowing+13;
		byte[] packLen = new byte[2];
		packLen[0] = (byte)(len>>>8);
		packLen[1] = (byte)(len);
		
	
		byte[] header = new byte[15];
		
		int index = 0;
		System.arraycopy(packLen, 0, header, index, 2);
		index += 2;
		
		System.arraycopy(Const.PASSWORD, 0, header, index, 4);
		index += 4;
		
		long timeStamp = System.currentTimeMillis();
		System.arraycopy(Calc.longToBytes(timeStamp),0,header,index,8);  //Current time
		index += 8;
		
		header[index] = extByteHeader;
		return header;
	}
	

	
	
	public byte[] createInstruction(int noInstruction, byte[] instructionArray, byte extendByteInstruction){
		int len = instructionArray.length + 2 + 1;
		byte[] instruction =new byte[len];
		int current = 0;
		
		//no instruction
       // byte[] noInstrArray = new byte[1];
        //noInstrArray[0] = (byte)(noInstruction>>>8);
       // noInstrArray[1] = (byte)(noInstruction);
		instruction[current] = (byte)noInstruction;
       // System.arraycopy(noInstrArray,0,instruction,current,2);
        current += 1;
        
        // instructions 
		int index = 0;
		for(int loopI = 0; loopI < noInstruction ; loopI++){
			 System.arraycopy(instructionArray,index,instruction,current,2);
			 current += 2;
			 index+=2;
		}
		//extend byte	
		instruction[current] = extendByteInstruction;
		
		return instruction;
	}
	
   
   
	public void sendInstructionWithHeader(int noInstruction, byte[] instructionArray, byte extendByteInstruction ){
		try {
	           if (socket.isConnected()) {
	        	   byte[] instruct = createInstruction( noInstruction, instructionArray, extendByteInstruction );
	        	   int packetLengthFollowing = instruct.length;
	        	   byte[] head = createHeader(packetLengthFollowing, (byte)0x02);
	        	   int lenTot =  packetLengthFollowing + head.length;
	        	   
	        	   byte[] res = new byte[lenTot];
	        	   System.arraycopy(head, 0, res, 0, head.length);
	        	   System.arraycopy(instruct, 0, res, head.length, instruct.length);
	        	   os.write(res);
	           } else 
	               Log.i(TAG, "SendData: Cannot send message. Socket is closed");
		  } catch (Exception e) {
		           Log.i(TAG, "SendData: Message send failed. Caught an exception");
		  }   
		   
	   }
	
   
   


	/*******************
     **  Stop socket  **
     *******************/
  
   public void stopSocket(){
	   try {
	   	   Log.i(TAG, "--- StopSocket --- ");
	   	   is.close();
	       os.close();
	       socket.close();
	
	       if(ConnectionThread.getNbSock()>0){ConnectionThread.setNbSock(ConnectionThread.getNbSock()-1);}
	       
	       Log.i(TAG, "--- StopSocket --- "+ConnectionThread.getNbSock()); 
	       textSocketConnected = (TextView)ConnectionThread.fragmentActivity.findViewById(R.id.textSocketConnected);
	   	   UpdateUI.updateUITextView(handler,"Sockets connected : "+ConnectionThread.getNbSock(),textSocketConnected);
	       
	   	   setIsClient(getIsClient(),clientId,false);
	       setThreadIsReady(getThreadIsReady(),clientId,false);

	       final TextView textViewUpdated = textViewConnection(clientId);
	       updateUISend(textViewUpdated,"Socket stopped");
	       reportBufferHasStopped();//All the listeners will know the connection is over
	          
	   } catch (Exception e) {
                Log.i(TAG, "Close. Caught an exception");
       }
   }
   
   
   
 
   /**************
    **  Others  **
    **************/
   
  public int[] getTimeSamplesArray(int sensorId){
	  //if(SensorIsPresent()[sensorId]){
	  int noSamples = this.getNoSample()[sensorId];
	  int[] timeSamplesArray = new int[noSamples];
	  for(int loopI = 0; loopI<noSamples; loopI++){
		  timeSamplesArray[loopI] = this.getTimePacketEpoch() - this.getPacketSamplesRelativeTime()[sensorId]
				  + loopI * 1000 / this.getPacketSamplingRates()[sensorId];
	  }
	  //}
	  return timeSamplesArray; 
  }
  
	  
 }



