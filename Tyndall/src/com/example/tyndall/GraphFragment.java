/**
 * 
 * File : GraphFragment.java
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
import android.widget.TextView;

public class GraphFragment extends Fragment {
	/**  
     * The fragment argument representing the section number for this  
	 * fragment.  
	 */  
	
	public static final String ARG_OBJECT = "object";
	static Handler handler = new Handler();
	//UI
	private TextView accelXSamples,accelYSamples,accelZSamples,
	textViewSamplesFftX,textViewSamplesFftY,textViewSamplesFftZ, 
	textViewSampling, xMaxFreq,yMaxFreq,zMaxFreq,textViewRMS;
	
	//Var
	double gValue = 8;
	private long oldTime = 0; //All the values are changed in the same time
	double xFmax= 0,yFmax= 0,zFmax = 0;
	double oldTimeStamp = 0;
	double xMaxFFT = 0,yMaxFFT = 0,zMaxFFT = 0;
	
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

		//Initializations

		accelXSamples = (TextView)rootView.findViewById(R.id.accelXSamplesGraph);
		accelYSamples = (TextView)rootView.findViewById(R.id.accelYSamplesGraph);
		accelZSamples = (TextView)rootView.findViewById(R.id.accelZSamplesGraph);
		textViewSamplesFftX = (TextView) rootView.findViewById(R.id.textViewFftSamplesGraphX);
		textViewSamplesFftY = (TextView) rootView.findViewById(R.id.textViewFftSamplesGraphY);
		textViewSamplesFftZ = (TextView) rootView.findViewById(R.id.textViewFftSamplesGraphZ);
		textViewSampling = (TextView) rootView.findViewById(R.id.textViewSamplingGraph);
		xMaxFreq = (TextView) rootView.findViewById(R.id.xMaxFreq);
		yMaxFreq = (TextView) rootView.findViewById(R.id.yMaxFreq);
		zMaxFreq = (TextView) rootView.findViewById(R.id.zMaxFreq);
		textViewRMS = (TextView) rootView.findViewById(R.id.textViewRMS);
		
		//Buffer Listener
		for (int i = 0 ; i<Const.NB_SOCKET_MAX ; i++){
			final int clientI = i;
			final double[] RMStot = {0,0,0};
			
			
			BufferListenerThread blt = new BufferListenerThread(MainActivity.getConnectionThr(), clientI,
					new BufferListener(){
				
				//called by setNewBuffer in HandlerThread
				public void bufferHasChanged(byte[] newBuffer) {
					
					HandlerThread thr = MainActivity.getConnectionThr().getThreadArray()[clientI];		
					boolean[] sId = thr.getSensorPresentInPacket();
					int idSensor = Const.ACC_MPU_ID; //Accel
					long newTime = System.currentTimeMillis();
				
					
				
					if((sId[idSensor])){//&&((newTime - oldTime )> Const.TIME_REFRESH )){//the sensor id has datas
						
						
						/** X axis */
						if(thr.getXIsPresent()[idSensor]){
							double[] x = thr.getXPacketData()[idSensor];
							double[] xG = Calc.getMilliG(x, gValue);
							RMStot[0] = Calc.average(xG);
						
							//for each subpacket
							if((newTime - oldTime )> Const.TIME_REFRESH )
								updateUI("X : rate = " + thr.getPacketSamplingRates()[idSensor]+" Hz,       Sample:"+
										Calc.getIndexOfMax(Calc.getFft(xG)),
										textViewSampling);
							
							
								
							//for each N subpackets
							if(thr.getTabDatasFull()[idSensor]){
								double[] xN = thr.getXPacketForFFT()[idSensor]; //Xvalues for N samples
								double[] xGN = Calc.getMilliG(xN, gValue);
								
								
								double[] xfftRaw = Calc.getFft(thr.getXPacketForFFT()[idSensor]);
								double[] xfft = Calc.getMilliG(xfftRaw, gValue);
								
									
								//update fmax for fft
								if(Calc.round(Calc.max(xfft))>xMaxFFT){
									xFmax = Calc.getFreqFFTMax(xfft, 
											thr.getPacketSamplingRates()[idSensor]);
									xMaxFFT = Calc.round(Calc.max(xfft));
								}
								
								
								//update UI
								if((newTime - oldTime )> Const.TIME_REFRESH ){
			
									updateUI("Accel X = " + Calc.round(Calc.average(xGN))+" mg, for  "+
											 xGN.length+" datas ",accelXSamples);
									updateUI("xfft max realtime = " + Calc.round(Calc.max(xfft))
											+", for freqmax = "+ Calc.getFreqFFTMax(xfft, 
													thr.getPacketSamplingRates()[idSensor])+" Hz",textViewSamplesFftX);
									
									//double newTimeStamp = thr.getTimePacketEpoch();
									//double timeStamp = newTimeStamp-oldTimeStamp;
									//Log.i("Graph","Packet sampling Rate ="+timeStamp);
								    // oldTimeStamp = newTimeStamp;
									updateUI("xfft max = " + xMaxFFT +", for freq = "+ xFmax +" Hz",xMaxFreq);
									
								}
							}
							
						}else
							updateUI("no axe X", accelXSamples);
						
						
						
						/** Y axis */
						if(thr.getYIsPresent()[idSensor]){
							
							//for each subpacket Y
							double[] y = thr.getYPacketData()[idSensor];
							double[] yG = Calc.getMilliG(y, gValue);
							RMStot[1] = Calc.average(yG);
							
							
							//for each N subpackets Y
							if(thr.getTabDatasFull()[idSensor]){
								double[] yN = thr.getYPacketForFFT()[idSensor]; //Yvalues for N samples
								double[] yGN = Calc.getMilliG(yN, gValue);
								
							
								double[] yfftRaw = Calc.getFft(thr.getYPacketForFFT()[idSensor]);
								double[] yfft = Calc.getMilliG(yfftRaw, gValue);
								
									
								//update fmax for fft
								if(Calc.round(Calc.max(yfft))>yMaxFFT){
									yFmax = Calc.getFreqFFTMax(yfft, 
											thr.getPacketSamplingRates()[idSensor]);
									yMaxFFT = Calc.round(Calc.max(yfft));
									
								}
								
								
								//update UI
								if((newTime - oldTime )> Const.TIME_REFRESH ){
			
									updateUI("Accel Y = " + Calc.round(Calc.average(yGN))+" mg, for  "+
											 yGN.length+" datas ",accelYSamples);
									updateUI("yfft max realtime = " + Calc.round(Calc.max(yfft))
											+", for freqmax = "+ Calc.getFreqFFTMax(yfft, 
													thr.getPacketSamplingRates()[idSensor]) +" Hz",textViewSamplesFftY);
									updateUI("yfft max = " + yMaxFFT +", for freq = "+ yFmax +" Hz",yMaxFreq);

								}
							}
							
							
						}else
							updateUI("no axe Y",accelYSamples);
						
						
						
						
						
						/** Z axis */
						if(thr.getZIsPresent()[idSensor]){
							//for each subpacket Z
							
							double[] z = thr.getZPacketData()[idSensor];
							double[] zG = Calc.getMilliG(z, gValue);
							RMStot[2] =Calc.average(zG);
							
							
							//for each N subpackets Z
							if(thr.getTabDatasFull()[idSensor]){
								double[] zN = thr.getZPacketForFFT()[idSensor]; //Yvalues for N samples
								double[] zGN = Calc.getMilliG(zN, gValue);
								
								double[] zfftRaw =  Calc.getFft(thr.getZPacketForFFT()[idSensor]);
								double[] zfft = Calc.getMilliG(zfftRaw, gValue);
						
									
								//update fmax for fft
								if(Calc.round(Calc.max(zfft))>zMaxFFT){
									zFmax = Calc.getFreqFFTMax(zfft, 
											thr.getPacketSamplingRates()[idSensor]);
									zMaxFFT = Calc.round(Calc.max(zfft));
									
									
								}
							
								
								//update UI
								if((newTime - oldTime )> Const.TIME_REFRESH ){
			
									updateUI("Accel Z = " + Calc.round(Calc.average(zGN))+" mg, for  "+
											 zGN.length+" datas ",accelZSamples);
									updateUI("zfft max realtime  = " + Calc.round(Calc.max(zfft))
											+", for freq = "+ Calc.getFreqFFTMax(zfft, 
													thr.getPacketSamplingRates()[idSensor])+" Hz",textViewSamplesFftZ);
									updateUI("zfft max = " + zMaxFFT +", for freq = "+ zFmax +" Hz",zMaxFreq);
		
										
								}
							}
							
				
						}else
							updateUI("no axe Z",accelZSamples);	
						
						
						
						if((newTime - oldTime )> Const.TIME_REFRESH ){
							updateUI("RMS tot=" + Calc.round(Calc.getRMS(RMStot)), textViewRMS);
						}
						
						
						
						
						oldTime = newTime;	
					}
				}
				public void bufferHasStopped(){
					stopAccelUI();	
				}
			});
			blt.start();	
		}
						
	

		return rootView;
	}
	
	
	/*****************
	 **  Update UI  **
	 *****************/

	public void updateUI(final String txt, final TextView textView){
		
		handler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
            	textView.setText(txt);
            }
        });
	}
	
	/**
	
	public void updateCreateUI(final String txt){//,final int textId){
		final LinearLayout ll = (LinearLayout) getView().findViewById(R.id.layoutForScrollGraph);
		final TextView textView = new TextView(getActivity().getBaseContext());
		handler.post(new Runnable() {
			@Override
            public void run() {
				textView.setText(txt); 
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(9);
				//textView.setId(textId);
		        ll.addView(textView);
            }
        });
	}*/
	
	public void stopAccelUI(){

		updateUI("MaxFFTX = ",textViewSamplesFftX);
		updateUI("MaxFFTY = ",textViewSamplesFftY);
		updateUI("MaxFFTZ = ",textViewSamplesFftZ);
		updateUI("X freq max =", xMaxFreq);
		updateUI("Y freq max =", yMaxFreq);
		updateUI("Z freq max =", zMaxFreq);
		
	}
	
}
