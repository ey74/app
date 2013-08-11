/**
 * 
 * File : Chart.java
 * Created : July 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * Creates plot fragment, updated from bufferListener
 */


package com.example.tyndall;
import java.text.DecimalFormat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

@SuppressLint("NewApi")
public class Chart extends Fragment {
	
		double gValue = 8;
		private XYPlot dynamicPlot, dynamicPlotFourier, dynamicPlotSamples;     
		private long oldTime = 0; 
		private XYSeries seriesX,seriesY,seriesZ;
		double[] timestamps = {60,120,130,150,160,200,300,450};
		

	
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
			final View rootView = inflater.inflate(R.layout.fragment_chart, container, false);
			
			Handler handler = new Handler();
			handler.post(new Runnable() {
	            @Override
	            public void run() { 
			
			
	  	    // initialize our XYPlot reference:
			dynamicPlot = (XYPlot) rootView.findViewById(R.id.dynamicPlot);
			dynamicPlotFourier = (XYPlot) rootView.findViewById(R.id.dynamicPlotFourier);
			
	      
	        // Create a formatter to use for drawing a series using LineAndPointRenderer:
	        final LineAndPointFormatter redFormat = new LineAndPointFormatter(
	                Color.rgb(200, 0, 0),                   // line color
	                null,//Color.rgb(100, 0, 0),                   // point color
	                null, null);  								// fill color (none)
	        
	     /*   final BarFormatter redFormatBar = new BarFormatter(
	                Color.rgb(200, 0, 0),                   // line color
	                Color.rgb(100, 0, 0));                 // point color
	       */        
	        
	        
	        
	        final LineAndPointFormatter greenFormat = new LineAndPointFormatter(
	                Color.rgb(0, 200, 0),                  
	                null,//Color.rgb(0, 100, 0),                  
	                null, null);                                  
	        final LineAndPointFormatter blueFormat = new LineAndPointFormatter(
            		Color.rgb(0, 0, 200), 
            		null,//Color.rgb(0, 0, 100), 
            		null, null);
	     
	       
	      
	        /** ADD */
	        //Color

	        dynamicPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
	        dynamicPlot.setPlotMargins(0, 0, 0, 0);
	        dynamicPlot.setPlotPadding(0, 0, 0, 0);
	        dynamicPlot.setGridPadding(0, 10, 5, 0);
	/**        dynamicPlot.position(dynamicPlot.getGraphWidget(),
	        		0, XLayoutStyle.ABSOLUTE_FROM_LEFT,
	        		0, YLayoutStyle.RELATIVE_TO_CENTER,
	        		AnchorPosition.LEFT_MIDDLE);*/
	        dynamicPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
	        dynamicPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlot.getBackgroundPaint().setColor(Color.TRANSPARENT);
	        dynamicPlot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
	        
	        //Change label
	       
	        dynamicPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
	        dynamicPlot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);
	        	        
	        dynamicPlot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
	        dynamicPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
	        
	        dynamicPlot.getGraphWidget().getRangeOriginLabelPaint().setColor(Color.BLACK);
	        dynamicPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
//changed
	       // dynamicPlot.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
	      //  dynamicPlot.getGraphWidget().getGridDomainLinePaint().setColor(Color.BLACK);
	      //  dynamicPlot.getGraphWidget().getGridRangeLinePaint().setColor(Color.BLACK);
	        dynamicPlot.setRangeLabel(" Acc : mg");
	        dynamicPlot.setDomainLabel(" Samples ");
	        
	        
	        
	        //Domain : abscissa
	       // XYStepMode.
	      //  dynamicPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL,5);
	        dynamicPlot.setDomainValueFormat(new DecimalFormat("0"));
	        dynamicPlot.setDomainStepValue(1);
	     //   dynamicPlotFourier.setTicksPerRangeLabel(3);
	       // dynamicPlotFourier.setDomainStep(XYStepMode.INCREMENT_BY_VAL,1);
	        
	        
	        //Range
	        //Remove Legend
	        //dynamicPlot.getLayoutManager().remove(dynamicPlot.getLegendWidget());
	       // dynamicPlot.getLayoutManager().remove(dynamicPlot.getDomainLabelWidget());
	       // dynamicPlot.getLayoutManager().remove(dynamicPlot.getRangeLabelWidget());
	        //dynamicPlot.getLayoutManager().remove(dynamicPlot.getTitleWidget());
	        
	     
	        
	        /** End */
	        
	        
	        
	        // reduce the number of range labels
	        dynamicPlot.setTicksPerRangeLabel(2);
	        dynamicPlot.setRangeBoundaries(-600, 600, BoundaryMode.FIXED); 
	        //dynamicPlot.setRangeBoundaries(-600, 600, BoundaryMode.AUTO); 
	      //  dynamicPlot.setRangeBoundaries(-10, 10, BoundaryMode.AUTO); 
	        dynamicPlotFourier.setRangeBoundaries(-20, 20, BoundaryMode.AUTO); 
	        //dynamicPlotFourier.setRangeBoundaries(-200, 2000, BoundaryMode.AUTO); 
	       
	        
	        //sin
		/*	double[] sin = new double[100];
			double d=0;
			double f = 7812.15;//Hz
			for (int i = 0; i< 100; i++){
				//d +=0.2;
				sin[i] = Math.sin(2*Math.PI*i*f);
			}
			double[] sinFFT = Calc.getFft(sin);
			double[] sinPSD = Calc.getPsd(sinFFT);
			XYSeries seriesSin = new DynamicDatas(sin,"sin"); 
			dynamicPlot.addSeries(seriesSin, blueFormat);
			XYSeries seriesSinFFT = new DynamicDatas(sinFFT,"AccelsinFFT"); 
			
			XYSeries seriesSinPSD = new DynamicDatas(sinPSD,"AccelsinPSD"); 
			dynamicPlotFourier.addSeries(seriesSinFFT, blueFormat);
		//	dynamicPlotFourier.addSeries(seriesSinPSD, greenFormat);
			
			
			double[] sinIFFT = Calc.getIFft(sinFFT);
			XYSeries seriesSinIFFT = new DynamicDatas(sinIFFT,"sinIFFT"); 
			//dynamicPlot.addSeries(seriesSinIFFT, redFormat);
			dynamicPlot.redraw();
			dynamicPlotFourier.redraw();
	    */
	      
	        
	        //porte
	    /*	double[] porte = new double[300];
	    	double[] porte1 = new double[300];
			double d=0;
			for (int i = 0; i< 300; i++){
				//d +=0.2;
				porte[i] = 0;
				porte1[i] = 4;
			}
			for (int i = 100; i< 200; i++){
				//d +=0.2;
				porte[i] = 1;
			}
			double[] porteFFT = Calc.getFft(porte);
			double[] porte1FFT = Calc.getFft(porte1);
			double[] porte1PSD = Calc.getPsd(porte1FFT);
			
			double[] portePSD = Calc.getPsd(porteFFT);
			XYSeries seriesPorte = new DynamicDatas(porte,"porte"); 
			XYSeries seriesPorte1 = new DynamicDatas(porte1,"cst"); 
			//dynamicPlot.addSeries(seriesPorte, blueFormat);
			dynamicPlot.addSeries(seriesPorte1, redFormat);
			
			XYSeries seriesPorteFFT = new DynamicDatas(porteFFT,"AccelporteFFT"); 	
			XYSeries seriesPorte1FFT = new DynamicDatas(porte1FFT,"AccelporteFFT");
			
			 //dynamicPlotFourier.addSeries(seriesPorteFFT, blueFormat);
			dynamicPlotFourier.addSeries(seriesPorte1FFT, redFormat);
			
			
			double[] porteIFFT = Calc.getIFft(porteFFT);
			
			XYSeries seriesPorteIFFT = new DynamicDatas(porteIFFT,"sinIFFT"); 
		//	dynamicPlot.addSeries(seriesPorteIFFT, redFormat);
			dynamicPlot.redraw();
			dynamicPlotFourier.redraw();
	     */   
	        
	        
	        
			
			
	        double[] xIni= {0};
	        seriesX = new DynamicDatas(xIni,"Accel X"); 
	        seriesY = new DynamicDatas(xIni,"Accel X");
	        seriesZ = new DynamicDatas(xIni,"Accel X");
	        
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
							
							
							//Graph
							if( ((thr.getXIsPresent()[idSensor])||(thr.getYIsPresent()[idSensor])||(thr.getZIsPresent()[idSensor]))&&(thr.getTabDatasFull()[idSensor])){
								dynamicPlot.clear();
								//Log.i("Chart","here1");
							} 
							
							//**** xAccel Datas
							if((thr.getXIsPresent()[idSensor])&&(thr.getTabDatasFull()[idSensor])){
								
								double[] xRaw = thr.getXPacketForFFT()[idSensor];
								double[] x = Calc.getMilliG(xRaw, gValue );
								
						
								seriesX = new DynamicDatas(x,"Accel X"); 
								dynamicPlot.addSeries(seriesX, redFormat);
							
								
								
								//FFT
								double[] fftRaw = Calc.getFft(thr.getXPacketForFFT()[idSensor]);
								double[] fft = Calc.getMilliG(fftRaw, gValue);
								double[] fftCut = new double[Const.FFT_CHART_LENGTH];
								System.arraycopy(fft, 0, fftCut, 0, Const.FFT_CHART_LENGTH);
								
								double[] dsp = Calc.getPsd(fft);
								double[] dspCut = new double[Const.FFT_CHART_LENGTH];
								System.arraycopy(dsp, 0, dspCut, 0, Const.FFT_CHART_LENGTH);
								dynamicPlotFourier.clear();
								XYSeries seriesXFFT = new DynamicDatas(fftCut,"Accel XFFT"); 
								XYSeries seriesXDSP = new DynamicDatas(dspCut,"Accel DSP"); 
								dynamicPlotFourier.addSeries(seriesXFFT, redFormat);
								dynamicPlotFourier.addSeries(seriesXDSP, blueFormat);
								dynamicPlotFourier.redraw();
							}

							//**** yAccel Datas
							if((thr.getYIsPresent()[idSensor])&&(thr.getTabDatasFull()[idSensor])){
								
								double[] yRaw = thr.getYPacketForFFT()[idSensor];
								double[] y = Calc.getMilliG(yRaw, gValue );
								
								seriesY = new DynamicDatas(y,"Accel Y"); 
								dynamicPlot.addSeries(seriesY, greenFormat);
								
							}
					
							
							//**** zAccel Datas
							if((thr.getZIsPresent()[idSensor])&&(thr.getTabDatasFull()[idSensor])){
								
								double[] zRaw = thr.getZPacketForFFT()[idSensor];
								double[] z = Calc.getMilliG(zRaw, gValue );

								seriesZ = new DynamicDatas(z,"Accel Z"); 
								dynamicPlot.addSeries(seriesZ, blueFormat);
								
							}
							
							
							//Graph
							if( ((thr.getXIsPresent()[idSensor])||(thr.getYIsPresent()[idSensor])||(thr.getZIsPresent()[idSensor]))&&(thr.getTabDatasFull()[idSensor])){
								dynamicPlot.redraw();	
							}
							
							oldTime = newTime;	
						   
						} 
	
					}
					public void bufferHasStopped(){
						dynamicPlot.clear();
						dynamicPlotFourier.clear();
						dynamicPlotSamples.clear();
						
						dynamicPlot.redraw();
						dynamicPlotFourier.redraw();
						dynamicPlotSamples.redraw();
					}
				});
				blt.start();	
			}    
	        
	     
	            }
	        });
	        
	        
	        
			return rootView;
		}
			


}
