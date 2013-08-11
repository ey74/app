/**
 * 
 * File : Chart.java
 * Created : July 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * Creates plot fragment, updated from bufferListener
 */


package com.example.tyndall;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

@SuppressLint("NewApi")
public class ChartHistory extends Fragment {
	
		double gValue = 8;
		private XYPlot dynamicPlotHistoryX,dynamicPlotHistoryY,dynamicPlotHistoryZ;     
		private long oldTime = 0; 
		static double xFmax= 0,yFmax= 0,zFmax = 0;
		static double xMaxFFT = 0, yMaxFFT = 0, zMaxFFT = 0;
		
		

		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			final View rootView = inflater.inflate(R.layout.fragment_chart_history, container, false);
			
			Handler handler = new Handler();
			handler.post(new Runnable() {
	            @Override
	            public void run() { 
			
			
	  	    // initialize our XYPlot reference:
	            	dynamicPlotHistoryX = (XYPlot) rootView.findViewById(R.id.dynamicPlotHistoryX);
	            	dynamicPlotHistoryY = (XYPlot) rootView.findViewById(R.id.dynamicPlotHistoryY);
	            	dynamicPlotHistoryZ = (XYPlot) rootView.findViewById(R.id.dynamicPlotHistoryZ);
	      
	        // Create formatters to use for drawing a series using LineAndPointRenderer:
	        final LineAndPointFormatter redFormat = new LineAndPointFormatter(
	                Color.rgb(200, 0, 0),                   // line color
	                null,//Color.rgb(100, 0, 0),              // point color
	                null, null);  								// fill color (none)            
	        final LineAndPointFormatter greenFormat = new LineAndPointFormatter(
	                Color.rgb(0, 200, 0),                  
	                null,//Color.rgb(0, 100, 0),                  
	                null, null);                                  
	        final LineAndPointFormatter blueFormat = new LineAndPointFormatter(
            		Color.rgb(0, 0, 200), 
            		null,//Color.rgb(0, 0, 100), 
            		null, null);
	     
	       
	         //Color
	       // dynamicPlotHistoryX.setBorderStyle(Plot.BorderStyle.NONE, null, null);
	        dynamicPlotHistoryX.setPlotMargins(0, 0, 0, 0);
	        dynamicPlotHistoryX.setPlotPadding(0, 0, 0, 0);
	        dynamicPlotHistoryX.setGridPadding(0, 10, 5, 0);
	        
	        dynamicPlotHistoryY.setPlotMargins(0, 0, 0, 0);
	        dynamicPlotHistoryY.setPlotPadding(0, 0, 0, 0);
	        dynamicPlotHistoryY.setGridPadding(0, 10, 5, 0);
	        
	        dynamicPlotHistoryZ.setPlotMargins(0, 0, 0, 0);
	        dynamicPlotHistoryZ.setPlotPadding(0, 0, 0, 0);
	        dynamicPlotHistoryZ.setGridPadding(0, 10, 5, 0);
	        
	        /** dynamicPlot.position(dynamicPlot.getGraphWidget(),
	        		0, XLayoutStyle.ABSOLUTE_FROM_LEFT,
	        		0, YLayoutStyle.RELATIVE_TO_CENTER,
	        		AnchorPosition.LEFT_MIDDLE);*/
	        dynamicPlotHistoryX.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlotHistoryX.getBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlotHistoryX.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlotHistoryY.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlotHistoryY.getBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlotHistoryY.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlotHistoryZ.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlotHistoryZ.getBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlotHistoryZ.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
	       
	        
	        //Change label
	        dynamicPlotHistoryX.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
	        dynamicPlotHistoryY.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
	        dynamicPlotHistoryZ.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
	        
	        dynamicPlotHistoryX.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
	        dynamicPlotHistoryY.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
	        dynamicPlotHistoryZ.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
	        	        
	        dynamicPlotHistoryX.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
	        dynamicPlotHistoryY.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
	        dynamicPlotHistoryZ.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
	        dynamicPlotHistoryX.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
	        dynamicPlotHistoryY.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
	        dynamicPlotHistoryZ.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
	        
	        dynamicPlotHistoryX.getGraphWidget().getRangeOriginLabelPaint().setColor(Color.BLACK);
	        dynamicPlotHistoryY.getGraphWidget().getRangeOriginLabelPaint().setColor(Color.BLACK);
	        dynamicPlotHistoryZ.getGraphWidget().getRangeOriginLabelPaint().setColor(Color.BLACK);
	        dynamicPlotHistoryX.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
	        dynamicPlotHistoryY.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
	        dynamicPlotHistoryZ.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
	        
	        /**changed
	       // dynamicPlot.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
	      //  dynamicPlot.getGraphWidget().getGridDomainLinePaint().setColor(Color.BLACK);
	      //  dynamicPlot.getGraphWidget().getGridRangeLinePaint().setColor(Color.BLACK);*/
	        
	        dynamicPlotHistoryX.setRangeLabel(" PSD X");
	        dynamicPlotHistoryY.setRangeLabel(" PSD Y");
	        dynamicPlotHistoryZ.setRangeLabel(" PSD Z");
	        dynamicPlotHistoryX.setDomainLabel(" Frequency ");
	        dynamicPlotHistoryY.setDomainLabel(" Frequency ");
	        dynamicPlotHistoryZ.setDomainLabel(" Frequency ");
	        
	        
	        
	        //Domain : abscissa
	       // DELETE VERTICAL GRID
	        
	      //  dynamicPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL,5);
	      //  dynamicPlotHistoryX.setDomainValueFormat(new DecimalFormat("0"));
	      //  dynamicPlotHistoryX.setDomainStepValue(1);
	     //   dynamicPlotHistoryXb.setDomainValueFormat(new DecimalFormat("0"));
	     //   dynamicPlotHistoryXb.setDomainStepValue(1);
	        
	        //Range
	        //Remove Legend
	        dynamicPlotHistoryX.getLayoutManager().remove(dynamicPlotHistoryX.getLegendWidget());
	        dynamicPlotHistoryY.getLayoutManager().remove(dynamicPlotHistoryY.getLegendWidget());
	        dynamicPlotHistoryZ.getLayoutManager().remove(dynamicPlotHistoryZ.getLegendWidget());
	       // dynamicPlotHistoryX.getLayoutManager().remove(dynamicPlotHistoryX.getDomainLabelWidget());
	       // dynamicPlotHistoryX.getLayoutManager().remove(dynamicPlotHistoryX.getRangeLabelWidget());
	        //dynamicPlotHistoryX.getLayoutManager().remove(dynamicPlotHistoryX.getTitleWidget());
	        
	        
	        
	        //Setup a line fill paint to be a slightly transparent gradient
	        Paint lineFillGreen = new Paint();
	        lineFillGreen.setAlpha(200);
	        lineFillGreen.setShader(new LinearGradient(0,0,0,250,Color.WHITE,Color.GREEN,Shader.TileMode.MIRROR));
	        greenFormat.setFillPaint(lineFillGreen);
	        Paint lineFillRed = new Paint();
	        lineFillRed.setAlpha(200);
	        lineFillRed.setShader(new LinearGradient(0,0,0,250,Color.WHITE,Color.RED,Shader.TileMode.MIRROR));
	        redFormat.setFillPaint(lineFillRed);
	        Paint lineFillBlue = new Paint();
	        lineFillBlue.setAlpha(200);
	        lineFillBlue.setShader(new LinearGradient(0,0,0,250,Color.WHITE,Color.BLUE,Shader.TileMode.MIRROR));
	        blueFormat.setFillPaint(lineFillBlue);
	        
	     
	        // reduce the number of range labels
	        dynamicPlotHistoryX.setTicksPerRangeLabel(3);
	        dynamicPlotHistoryY.setTicksPerRangeLabel(3);
	        dynamicPlotHistoryZ.setTicksPerRangeLabel(3);
	        dynamicPlotHistoryX.setRangeBoundaries(0, 3000, BoundaryMode.AUTO); 
	        dynamicPlotHistoryY.setRangeBoundaries(0, 3000, BoundaryMode.AUTO); 
	        dynamicPlotHistoryZ.setRangeBoundaries(0, 3000, BoundaryMode.AUTO); 
	       
	   
	 
	        
	        //Buffer Listener
	        
	        
	        for(int i=0;i<Const.NB_SOCKET_MAX;i++){
				final int k = i;
				BufferListenerThread blt = new BufferListenerThread(MainActivity.getConnectionThr(), k,
						new BufferListener(){

					//called by setNewBuffer in HandlerThread
					public void bufferHasChanged(byte[] newBuffer) {
				     
						
						HandlerThread thr = MainActivity.getConnectionThr().getThreadArray()[k]; 
						boolean[] sId = thr.getSensorPresentInPacket();
						long newTime = System.currentTimeMillis();
						
						int idSensor = Const.ACC_MPU_ID; //Accel
						
						
						
					   /** Plot Fourier for MAX subpackets */
						if((sId[idSensor])&&((newTime - oldTime )> Const.TIME_REFRESH_GRAPH )){//the sensor id has datas
							
							
							
							//**** xAccel Datas
							if((thr.getXIsPresent()[idSensor])&&(thr.getTabDatasFull()[idSensor])){
								//FFT
								double[] xfftRaw = Calc.getFft(thr.getXPacketForFFT()[idSensor]);
								double[] xfft = Calc.getMilliG(xfftRaw, gValue);
								double[] xpsd = Calc.getPsd(xfft);
								
								if(Calc.round(Calc.max(xfft))>xMaxFFT){
									xFmax = Calc.getFreqFFTMax(xfft, 
											thr.getPacketSamplingRates()[idSensor]);
									xMaxFFT = Calc.round(Calc.max(xfft));
									dynamicPlotHistoryX.clear();
									XYSeries seriesXDSP = new DynamicDatas(xpsd,"Accel DSP"); 
									dynamicPlotHistoryX.addSeries(seriesXDSP, redFormat);
									dynamicPlotHistoryX.redraw();
	
								}

							}
							
	
							//**** yAccel Datas
							if((thr.getYIsPresent()[idSensor])&&(thr.getTabDatasFull()[idSensor])){
								
								double[] yfftRaw = Calc.getFft(thr.getYPacketForFFT()[idSensor]);
								double[] yfft = Calc.getMilliG(yfftRaw, gValue);
								double[] ydsp = Calc.getPsd(yfft);
								
								if(Calc.round(Calc.max(yfft))>yMaxFFT){
									yFmax = Calc.getFreqFFTMax(yfft, 
											thr.getPacketSamplingRates()[idSensor]);
									yMaxFFT = Calc.round(Calc.max(yfft));
									dynamicPlotHistoryY.clear();
									XYSeries seriesYDSP = new DynamicDatas(ydsp,"Accel DSP"); 
									dynamicPlotHistoryY.addSeries(seriesYDSP, greenFormat);
									dynamicPlotHistoryY.redraw();
	
								}
	
							}
					
							
							//**** zAccel Datas
							if((thr.getZIsPresent()[idSensor])&&(thr.getTabDatasFull()[idSensor])){
								
								double[] zfftRaw = Calc.getFft(thr.getZPacketForFFT()[idSensor]);
								double[] zfft = Calc.getMilliG(zfftRaw, gValue);
								double[] zdsp = Calc.getPsd(zfft);
								
								if(Calc.round(Calc.max(zfft))>zMaxFFT){
									zFmax = Calc.getFreqFFTMax(zfft, 
											thr.getPacketSamplingRates()[idSensor]);
									zMaxFFT = Calc.round(Calc.max(zfft));
									dynamicPlotHistoryZ.clear();
									XYSeries seriesZDSP = new DynamicDatas(zdsp,"Accel DSP"); 
									dynamicPlotHistoryZ.addSeries(seriesZDSP, blueFormat);
									dynamicPlotHistoryZ.redraw();
	
								}
								
							}
							
							
							
							
							
							
							oldTime = newTime;	
						   
						} 
	
					}
					public void bufferHasStopped(){
						dynamicPlotHistoryX.clear();
						dynamicPlotHistoryX.redraw();
						dynamicPlotHistoryY.clear();
						dynamicPlotHistoryY.redraw();
						dynamicPlotHistoryZ.clear();
						dynamicPlotHistoryZ.redraw();
						ChartHistory.xMaxFFT = 0;
		            	ChartHistory.yMaxFFT = 0;
		            	ChartHistory.zMaxFFT = 0;
		            	ChartHistory.xFmax = 0;
		            	ChartHistory.yFmax = 0;
		            	ChartHistory.zFmax = 0;
					
					}
				});
				blt.start();	
			}    
	        
	     
	            }
	        });
	        
	        
	        
			return rootView;
		}
			


}
