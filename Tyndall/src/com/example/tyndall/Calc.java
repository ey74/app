/**
 * 
 * File : Calc.java
 * Created : July 2013 
 * Author : Emilie Michaud 
 * 			Tyndall Institute
 * 
 */


package com.example.tyndall;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class Calc {
	
	   /** Convert byte to int */
	   public static int byteArrayToInt(byte[] b){
		   	int value = 0;
		   	for(int i=0; i<2; i++){
		   		int n = (b[i]<0 ? (int)b[i]+256 : (int)b[i])<<(8*((i+1)%2));
		   		value+=n;
		   	}
		   	return value;	
	   }

	   /** Average for an array of double */
	   public static double average(double[] myArray){
		   double aver = 0,temp=myArray[0];
		   for(int i=1; i<myArray.length;i++){
			   temp += myArray[i];
		   }
		  
		   aver = temp / myArray.length;
		 
		   return aver;
	   }
	   
	   /** Max of array of double */
	   public static double max(double[] myArray){
		   double max = myArray[0];
		   
		   for(int i=0; i<myArray.length;i++){
			   if(max<myArray[i]){
				   max = myArray[i];
			   }
		   }
		   return max;
	   }
	   
	   /** Max's Index of a double array */
	   public static double getIndexOfMax(double[] myArray){
		   double ind = 0, max = myArray[0];
		   
		   for(int i=0; i<myArray.length;i++){
			   if(max<myArray[i]){
				   max = myArray[i];
				   ind = i;
			   }
		   }
		   return ind;
	   }
	   
	   /** Give the freq max for a FFT tab 
	    * fs = sampling frequency = 1/Ts
	    * for real fft -> else, change by myArray.length by noSamples
	    * */
	   public static double getFreqFFTMax(double[] myArray, double fs){
		   return getIndexOfMax(myArray)*fs/myArray.length; 
	   }
	   
	   
	   /** Min of array of double */
	   public static double min(double[] myArray){
		   double min = myArray[0];
		   
		   for(int i=0; i<myArray.length;i++){
			   if(min>myArray[i]){
				   min = myArray[i];
			   }
		   }
		   return min;
	   }

	   
	   /** Change raw datas in milli g values (vary with sensor config) */
	   public static double[] getMilliG(double[] myArray,double gValue){
		   double[] getMilliG = new double[myArray.length];
		   
		   for(int i=0; i<myArray.length;i++){
			   getMilliG[i]= (double) ((gValue*1000.0/32768.0) * myArray[i]);
		   }
		   return getMilliG;
	   }
	   
	   /** Give the average in milli g of one array(raw datas), depends on the sensor config */
	   public static double getAverageMilliG(double[] myArray,double gValue){
		   return ((gValue*1000.0/32768.0) * average(myArray));
		  
	   }

	   
	   /** FFT */
	  public static double[] getFftFull(double[] input){
		  DoubleFFT_1D doubleFFT = new DoubleFFT_1D(input.length);
		  double[] fft = new double[input.length * 2];
		  System.arraycopy(input, 0, fft, 0, input.length);
		  doubleFFT.realForwardFull(fft);
		  return fft;
	  }
	  
	  public static double[] getIFftFull(double[] input){
		  DoubleFFT_1D doubleFFT = new DoubleFFT_1D(input.length);
		  double[] ifft = new double[input.length * 2];
		  System.arraycopy(input, 0, ifft, 0, input.length);
		  doubleFFT.realInverseFull(ifft,false);
		  return ifft;
	  }

	  public static double[] getFft(double[] input){
		  DoubleFFT_1D doubleFFT = new DoubleFFT_1D(input.length);
		  double[] fft = new double[input.length];
		  //ADD
		  double[] inputWithoutDCComponent = new double[input.length];
		  for(int i=0;i<input.length;i++)
			  inputWithoutDCComponent[i] = input[i] - average(input);
		  
		  //System.arraycopy(input, 0, fft, 0, input.length);
		  System.arraycopy(inputWithoutDCComponent, 0, fft, 0, input.length);
		  doubleFFT.realForward(fft);
	
		  return fft;
	  }
	  
	
	  
	  public static double[] getIFft(double[] input){
		  DoubleFFT_1D doubleFFT = new DoubleFFT_1D(input.length);
		  double[] ifft = new double[input.length ];
		  System.arraycopy(input, 0, ifft, 0, input.length);
		  doubleFFT.realInverse(ifft,false);
		  return ifft;
	  }
	  
	  public static double[] getPsd(double[] fft){
		  double[] res = new double[fft.length];
		  for(int i = 0; i< fft.length; i++)
			  res[i] = 2 * (fft[i] * fft[i]) / (fft.length*fft.length);
		  return res;
	  }
	  
	 /* public static double[] getLog(double[] input){
		  double[] log = new double[input.length];
		  for(int i = 0; i< input.length; i++){
			  log[i] = 20*Math.log10(Math.abs(input[i]));	
			  Log.i("log"," ,val: "+ log[i]);
		  }
		  return log;
	  }*/
	  
	  
	  
	  /** Give the RMS */
	  public static double getRMS(double[] input){
		  double res = 0 ,tmp = 0;
		  if(input.length == 1 )
			  res = Math.abs(input[0]);
		  else{
			  for(int i = 0;i<input.length;i++){
				  tmp += input[i] * input[i];
			  }
			  tmp = tmp / input.length;
			  res = Math.sqrt(tmp);
		  }
		  return res;
	  }
	  
	  
	  public static double round(double d){
		  return ((Math.round(d*100.0))/100.0);
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
