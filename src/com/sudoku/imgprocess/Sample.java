package com.sudoku.imgprocess;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

public class Sample {
	
	private Mat contours;
	private Mat area;
	private Rect rect;

	public Sample(Mat sample) {
		area = new Mat();
		contours = new Mat();
		rect = new Rect();
		Imgproc.cvtColor(sample, area, Imgproc.COLOR_RGB2GRAY);
		Imgproc.GaussianBlur(area, contours,new Size(3,3), 1.6);
		Imgproc.Canny(contours, contours,10,150);
		Imgproc.threshold(area, area, 0, 255, Imgproc.THRESH_OTSU);
	}
	
	// Histogramme de projection

		ArrayList<Integer> horizontal_histo(Mat img){
			ArrayList<Integer> histo= new ArrayList<Integer>();
		    for(int i=0;i<img.rows();i++){
		        int nb_pixels=0;
		        for(int j=0; j<img.cols(); j++){
		            if(img.get(i,j)[0]==0){
		                nb_pixels+=1;
		            }
		        }
		        histo.add(nb_pixels);
		    }
		    return histo;
		}

		ArrayList<Integer> vertical_histo(Mat img){
			ArrayList<Integer> histo = new ArrayList<Integer>();
		    for(int j=0;j<img.cols();j++){
		        int nb_pixels=0;
		        for(int i=0; i<img.rows(); i++){
		            if(img.get(i,j)[0]==0){
		                nb_pixels+=1;
		            }
		        }
		        histo.add(nb_pixels);
		    }
		    return histo;
		}
		
		void adjustRoi(){
		    int deltaX = area.cols()/9;
		    int deltaY = area.rows()/9;

		    ArrayList<Integer> vdensity = horizontal_histo(area);
		    int i1=0, i2=0;
		    for(int i=0;i<vdensity.size();i++){
		        int density = vdensity.get(i);
		        if(density < area.cols()/2 && density > area.cols()/4){
		            i1=i;
		            break;
		        }
		    }

		    for(int i=vdensity.size()-1;i>0;i--){
		        int density = vdensity.get(i);
		        if(density < area.cols()/2 && density > area.cols()/4){
		            i2=i;
		            break;
		        }
		    }

		    int j1=0, j2=0;
		    ArrayList<Integer> hdensity = vertical_histo(area);
		    for(int j=0;j<hdensity.size();j++){
		        int density = hdensity.get(j);
		        if(density < area.rows()/2 && density > area.rows()/4){
		            j1=j;
		            break;
		        }
		    }

		    for(int j=hdensity.size()-1;j>0;j--){
		    	int density = hdensity.get(j);
		    	if(density < area.rows()/2 && density > area.rows()/4){
		    		j2=j;
		    		break;
		    	}
		    }
		    int bk =0;
		    Point p1 = new Point(j1-bk,i1-bk);
		    Point p2 = new Point(j2+bk,i2+bk);
		    rect =new Rect(p1,p2);
		    area = area.submat(rect);
		}

	public Mat getcontours() {
		return contours;
	}
	
	public Mat getArea(){
		return area;
	}
	
	public Rect getRect(){
		return rect;
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
