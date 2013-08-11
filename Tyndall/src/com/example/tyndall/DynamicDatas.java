package com.example.tyndall;

import com.androidplot.xy.XYSeries;

public class DynamicDatas implements XYSeries {     
	private double[] data;
	private String title;
	
	public DynamicDatas( double[] data, String title) {               
		this.title = title;    
		this.data = data;
	}


	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public int size() {
		return data.length;
	}

	@Override
	public Number getX(int index) {
		return index;
	}

	@Override
	public Number getY(int index) {	
		return data[index];
	}
	
	
}
	