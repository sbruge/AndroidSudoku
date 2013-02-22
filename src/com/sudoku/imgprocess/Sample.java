package com.sudoku.imgprocess;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class Sample {
	
	private Mat contours;
	private Mat area;

	public Sample(Mat sample) {
		area = sample.clone();
		contours = new Mat();
		Imgproc.cvtColor(sample, contours, Imgproc.COLOR_RGB2GRAY);
		Imgproc.GaussianBlur(contours, contours,new Size(3,3), 1.6);
		Imgproc.Canny(contours, contours,10,150);
	}

	public Mat getcontours() {
		return contours;
	}
	
	public Mat getArea(){
		return area;
	}
	
	public int countPx(){
		int nbOfBlackPxl = 0;
		for(int i=5; i<contours.cols();i++){
			for(int j=5; j<contours.rows();j++){
				if(contours.get(j, i)[0]==255){
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
