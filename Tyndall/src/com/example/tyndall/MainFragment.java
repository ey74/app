/**
 * 
 * File : MainFragment.java
 * Created : June 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * 
 */

package com.example.tyndall;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.androidplot.xy.XYPlot;


public class MainFragment extends Fragment {
	
	public static final String ARG_OBJECT = "object";
	
	static Handler handler = new Handler();
	//UI
	//private Button clickButton = null;
	private TextView accelX,accelY,accelZ,
				textViewRMSMain, textViewScore,textViewScore2;//textViewFreq,
	private Button buttonAccelX,buttonAccelY,buttonAccelZ
	,buttonSaveMain,buttonResetMain;
	String textPacketLength = "Packet length : ";
	private Chronometer2 chronometer,chronometerMain;
	private boolean stopped = true;
	double[] RMSmax = new double[Const.NB_SOCKET_MAX]
			, score = new double[Const.NB_SOCKET_MAX];

	double scoreMax = 0;//For now, for one client
	
	
	//Var
	double gValue = 8;
	private long oldTime = 0; //All the values are changed in the same time
	double xFmax= 0,yFmax= 0,zFmax = 0;
	double xMaxFFT = 0,yMaxFFT = 0,zMaxFFT = 0;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		
		
		//Initializations
		buttonAccelX = (Button)rootView.findViewById(R.id.buttonAccelX);
		buttonAccelY = (Button)rootView.findViewById(R.id.buttonAccelY);  
		buttonAccelZ = (Button)rootView.findViewById(R.id.buttonAccelZ); 
		buttonSaveMain = (Button)rootView.findViewById(R.id.buttonSaveMain);
		buttonResetMain = (Button)rootView.findViewById(R.id.buttonResetMain);
		accelX = (TextView)rootView.findViewById(R.id.accelX);
		accelY = (TextView)rootView.findViewById(R.id.accelY);
		accelZ = (TextView)rootView.findViewById(R.id.accelZ);
		//textViewFreq = (TextView) rootView.findViewById(R.id.textViewFreqMax);
		textViewRMSMain = (TextView) rootView.findViewById(R.id.textViewRMSMain);
		textViewScore = (TextView) rootView.findViewById(R.id.textViewScore);
		textViewScore2 = (TextView) rootView.findViewById(R.id.textViewScore2);
		
		//chronometer
		chronometer = (Chronometer2) rootView.findViewById(R.id.chronometer);
		chronometerMain = (Chronometer2) this.getActivity().findViewById(R.id.chronometerMain);
		
		 final Button startButton = (Button)rootView.findViewById(R.id.buttonStartChrono);
		 startButton.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				if(stopped){
					chronometer.setBase(SystemClock.elapsedRealtime());
					chronometer.start();
					
					chronometerMain.setBase(SystemClock.elapsedRealtime());
					chronometerMain.start();
					startButton.setText("stop");
					stopped = false;
				}else{
					chronometer.stop();
					chronometerMain.stop();
					stopped = true;
					startButton.setText("start");
				}
				
			}	
		});
	   
		 
		
		 //RMS reset Button
		 buttonResetMain.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				for(int i = 0 ; i<RMSmax.length;i++){
					RMSmax[i] = 0;
				}
				UpdateUI.updateUITextView(handler,"0.0 %",textViewScore);
				chronometer.setBase(SystemClock.elapsedRealtime());
				chronometerMain.setBase(SystemClock.elapsedRealtime());
				
				XYPlot dynamicPlotHistoryX = (XYPlot)  getActivity().findViewById(R.id.dynamicPlotHistoryX);
				XYPlot dynamicPlotHistoryY = (XYPlot)  getActivity().findViewById(R.id.dynamicPlotHistoryY);
				XYPlot dynamicPlotHistoryZ = (XYPlot)  getActivity().findViewById(R.id.dynamicPlotHistoryZ);
				clearGraphHistory(dynamicPlotHistoryX);
				clearGraphHistory(dynamicPlotHistoryY);
				clearGraphHistory(dynamicPlotHistoryZ);
				
			}	
		});
		 
		 //RMS save Button
		 buttonSaveMain.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				double tmp = 0;
				tmp = Calc.max(score);
				if (tmp > scoreMax)
					scoreMax = tmp;
				//for now, for 1 client only
				UpdateUI.updateUITextView(handler,""+scoreMax+" %",textViewScore2);
			}	
		});
		 
		 
		 			
		
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
					
					
					
				
					if(sId[idSensor]){//&&((newTime - oldTime )> Const.TIME_REFRESH )){//the sensor id has datas
						
						/** X */
						if(thr.getXIsPresent()[idSensor]){
							double[] x = thr.getXPacketData()[idSensor];
							RMStot[0] = Calc.getAverageMilliG(x, gValue);
							
						
							//for each subpacket
							if((newTime - oldTime )> Const.TIME_REFRESH ){
								UpdateUI.updateUITextView(handler,"AccelX = " + Calc.round(Calc.getAverageMilliG(x, gValue))+" mg",accelX);
								checkFast(Calc.getAverageMilliG(x, gValue),Const.MAX_ACCEL,buttonAccelX);
							}
							
							//for each N subpackets
							//for FFT 
							if(thr.getTabDatasFull()[idSensor]){
						
								double[] xfftRaw = Calc.getFft(thr.getXPacketForFFT()[idSensor]);
								double[] xfft = Calc.getMilliG(xfftRaw, gValue);

								//update fmax for fft
								if(Calc.round(Calc.max(xfft))>xMaxFFT){
									xFmax = Calc.getFreqFFTMax(xfft, 
											thr.getPacketSamplingRates()[idSensor]);
									xMaxFFT = Calc.round(Calc.max(xfft));
								}
								
								
								
							}
						}else
							UpdateUI.updateUITextView(handler,"no axe X", accelX);
							
						
						
						/** Y */
						if(thr.getYIsPresent()[idSensor]){
							double[] y = thr.getYPacketData()[idSensor];
							RMStot[1] = Calc.getAverageMilliG(y, gValue);
							if((newTime - oldTime )> Const.TIME_REFRESH ){
								UpdateUI.updateUITextView(handler,"AccelY = " + Calc.round(Calc.getAverageMilliG(y, gValue))+" mg",accelY);
								checkFast(Calc.getAverageMilliG(y, gValue),Const.MAX_ACCEL,buttonAccelY);
							}
						}else
							UpdateUI.updateUITextView(handler,"no axe Y",accelY);
						
						
						
						/** Z */
						if(thr.getZIsPresent()[idSensor]){
							double[] z = thr.getZPacketData()[idSensor];
							RMStot[2] = Calc.getAverageMilliG(z, gValue);
							
							if((newTime - oldTime )> Const.TIME_REFRESH ){
								UpdateUI.updateUITextView(handler,"AccelZ = " + Calc.round(Calc.getAverageMilliG(z, gValue))+" mg",accelZ);	
								checkFast(Calc.getAverageMilliG(z, gValue),Const.MAX_ACCEL,buttonAccelZ);
							}
								
						}else
							UpdateUI.updateUITextView(handler,"no axe Z",accelZ);	
						
						
						//HITING
						
						
						
						
						
						
						
						
						//RMS
						//check if better or not, if yes -> graph again TODO
						if(Calc.round(Calc.getRMS(RMStot))>RMSmax[clientI]){
							
							RMSmax[clientI] = Calc.round(Calc.getRMS(RMStot));
							score[clientI] = Calc.round(RMSmax[clientI]/7000*100);
							UpdateUI.updateUITextView(handler,""+score[clientI]+"  %",textViewScore);
							//updateScore(""+score[clientI]+"  %");
							//updateRMS();
						}
						
						if((newTime - oldTime )> Const.TIME_REFRESH ){
							UpdateUI.updateUITextView(handler,"RMS max=" + RMSmax[clientI]+" mg"+"   Score : "+score[clientI], textViewRMSMain);
							/*Log.i("MAIN","RMSTOT =  "+RMStot[0]+" et = "+ RMStot[1]+ " et =" + RMStot[2]
									+ "  tot = "+ Calc.round(Calc.getRMS(RMStot)));*/
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
	
	public void clearGraphHistory(final XYPlot dynamicPlotHistory){
		handler.post(new Runnable() {
            @Override
            public void run() {
            	dynamicPlotHistory.clear();
            	dynamicPlotHistory.redraw();
            	
            	// Score = 0
            	ChartHistory.xMaxFFT = 0;
            	ChartHistory.yMaxFFT = 0;
            	ChartHistory.zMaxFFT = 0;
            	ChartHistory.xFmax = 0;
            	ChartHistory.yFmax = 0;
            	ChartHistory.zFmax = 0;
            }
        });
	}
	
	
	
	public void checkFast(final double valueToCompare, final double valueMax, final Button b){
		handler.post(new Runnable() {
            @Override
	            public void run() {
					if(valueToCompare >= valueMax)
						b.setVisibility(View.VISIBLE);
					else
						b.setVisibility(View.INVISIBLE);

            }
		 });
	}
	
	
	
	
	
	public void stopAccelUI(){
		UpdateUI.updateUITextView(handler,"AccelX = " ,accelX);
		UpdateUI.updateUITextView(handler,"AccelY = " ,accelY);
		UpdateUI.updateUITextView(handler,"AccelZ = " ,accelZ);
		
		handler.post(new Runnable() {
            @Override
            public void run() {
            	buttonAccelX.setVisibility(View.INVISIBLE);
				buttonAccelY.setVisibility(View.INVISIBLE);
				buttonAccelZ.setVisibility(View.INVISIBLE);
            }
        });
	}
	
	
	
}
