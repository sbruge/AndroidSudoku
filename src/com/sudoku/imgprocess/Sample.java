package com.sudoku.imgprocess;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class Sample {
	
	private Mat area;

	public Sample(Mat sample) {
		super();
		area = new Mat();
		Imgproc.cvtColor(sample, area, Imgproc.COLOR_RGB2GRAY);
	}

	public Mat getArea() {
		return area;
	}
	
	public boolean isNumber(){
		int nbOfPixels = area.cols()*area.rows();
		int nbOfBlackPxl = 0;
		for(int i=5; i<area.cols()-5;i++){
			for(int j=5; j<area.rows();j++){
				if(area.get(j, i)[0]==0){
					nbOfBlackPxl++;
				}
			}
		}
		Log.i("nbPxls",String.valueOf(nbOfPixels));
		Log.i("10%nbPxls",String.valueOf(nbOfPixels*0.1));
		Log.i("BckPxls",String.valueOf(nbOfBlackPxl));
		
		return nbOfBlackPxl > nbOfPixels*0.06;
	}

}
