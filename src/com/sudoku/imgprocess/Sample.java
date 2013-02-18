package com.sudoku.imgprocess;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class Sample {
	
	private Mat area;

	public Sample(Mat sample) {
		super();
		area = new Mat();
		Imgproc.cvtColor(sample, area, Imgproc.COLOR_RGB2GRAY);
		Imgproc.GaussianBlur(area, area,new Size(3,3), 1.6);
		Imgproc.Canny(area, area,10,150);
	}

	public Mat getArea() {
		return area;
	}
	
	public int countPx(){
		int nbOfBlackPxl = 0;
		for(int i=5; i<area.cols();i++){
			for(int j=5; j<area.rows();j++){
				if(area.get(j, i)[0]==255){
					nbOfBlackPxl++;
				}
			}
		}
		return nbOfBlackPxl;
	}
	public boolean isNumber(int min, int max){
		int nbOfBlackPxl = countPx();
		//Log.i("BckPxls",String.valueOf(nbOfBlackPxl));
		return nbOfBlackPxl > min+(max-min)/3;
	}

}
