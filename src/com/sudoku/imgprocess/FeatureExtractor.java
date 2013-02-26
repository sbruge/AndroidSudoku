package com.sudoku.imgprocess;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.sudoku.database.NumberFeatures;

import android.util.Log;

public class FeatureExtractor {
	private Mat number;
	private NumberFeatures features;
	
	public FeatureExtractor(Sample sample){
		features = new NumberFeatures();
		number=sample.getArea();
		//Imgproc.cvtColor(number,number, Imgproc.COLOR_RGB2GRAY);
		Imgproc.resize(number,number, new Size(50,50));
		//Imgproc.threshold(number, number, 0, 255, Imgproc.THRESH_OTSU);
		resizeSample(0);
		Imgproc.resize(number,number, new Size(50,50));
		buildDensity(5);
		buildAlign(10);
	}
	
	// Pre-Treating sample

	ArrayList<Integer> horizontalHisto(Mat img){
		ArrayList<Integer>  histo = new ArrayList<Integer>();
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

	ArrayList<Integer> verticalHisto(Mat img){
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
	
	void inverse(Mat img){
		for(int i=0;i<img.rows();i++){
	        for(int j=0; j<img.cols(); j++){
	            if(img.get(i,j)[0]==0){
	                img.put(i,j,255);
	            }
	            else{
	            	img.put(i,j,0);
	            }
	        }
	    }
	}
	
	void resizeSample(int blank){
	    ArrayList<Integer> rows = horizontalHisto(number);
	    ArrayList<Integer> columns = verticalHisto(number);
	    int r1=0;
	    int r2=rows.size()-1;
	    int c1=0;
	    int c2=columns.size()-1;
	    while(rows.get(r1)==0 && r1<rows.size()-1){
	        r1++;
	    }
	    while(rows.get(r2)==0&& r2>0){
	        r2--;
	    }
	    while(columns.get(c1)==0 && c1<columns.size()-1){
	        c1++;
	    }
	    while(columns.get(c2)==0 && c2>0){
	        c2--;
	    }
	    number = number.submat(r1-blank, r2+blank, c1-blank, c2+blank);
	}
	
	// Compute curls array
	boolean closeArea(int i1, int i2){
		int j=number.cols()/2;
	    for(int r=i1; r<=i2;r++){
	        int j1=j;
	        int j2=j+1;
	        while(number.get(r,j1)[0] != 0 && j1>0){
	            j1--;
	        }
	        while(number.get(r,j2)[0]!=0 && j2<number.cols()-1){
	            j2++;
	        }
	        if((j1==0 || j2==number.cols())){
	            return false;
	        }
	    }
	    return true;
	}
	
	ArrayList<Point> detectCurls(){
		ArrayList<Point> curls = new ArrayList<Point>();
		int c=number.cols()/2;
	    boolean end=false;
	    int r1=0;
	    int r2=0;
	    while(!end){
	        end=true;
	        while(number.get(r1,c)[0]!=0 && r1<number.rows()-1){
	            r1++;
	        }
	        while(number.get(r1,c)[0]==0 && r1<number.rows()-1 ){
	            r1++;
	        }
	        while(number.get(r1,c)[0]!=0 && r1<number.rows()-1){
	            r1++;
	        }
	        if(r1<number.rows()-3){
	            r2=r1+2;
	            while(number.get(r2,c)[0]!=0 && r2<number.rows()-1){
	                r2++;
	            }
	            if(closeArea(r1,r2)==true){
	                Point p= new Point(c,r1+(r2-r1)/2.);
	                curls.add(p);
	                end=false;
	            }
	        }
	        if(r2<number.rows()-3){
	        	r1=r2+2;
	        }
	        else{
	        	end=true;
	        }
	    }
	    return curls;
	}
	
	// Compute Junctions array with Harris corners
	
	ArrayList<Point> detectJunctions(){
		ArrayList<Point> junctions = new ArrayList<Point>();
		Mat dst= Mat.zeros( number.size(), CvType.CV_32FC1 );
		Mat dst_norm = new Mat();
		Mat dst_norm_scaled = new Mat();

		/// Detector parameters
		int blockSize = 2;
		int apertureSize = 3;
		double alpha = 0.06;
		int T=180;

		/// Detecting corners
		Imgproc.cornerHarris( number, dst, blockSize, apertureSize, alpha, Imgproc.BORDER_DEFAULT );

		/// Normalizing
		Core.normalize( dst, dst_norm, 0, 255, Core.NORM_MINMAX, CvType.CV_32FC1, new Mat() );
		Core.convertScaleAbs( dst_norm, dst_norm_scaled );

		/// Thresholding
		for( int j = 0; j < dst_norm.rows() ; j++ ){
			for( int i = 0; i < dst_norm.cols(); i++ ){
				if( (int) dst_norm.get(j,i)[0] > T ){
					junctions.add(new Point(i,j));
				}
			}
		}
		return junctions;
	}
	

	
	// Extract Mat Features from ArrayLists
	
	int localize(Point p){
	    double c=p.x;
	    double r=p.y;
	    int k=7;
	    if(c<number.cols()/3){
	        if(r<number.rows()/3 +k){
	            return 1;
	        }
	        else if(r<2*number.rows()/3-k){
	            return 4;
	        }
	        else{
	            return 7;
	        }
	    }
	    else if(c<2*number.cols()/3){
	        if(r<number.rows()/3+k){
	            return 2;
	        }
	        else if(r<2*number.rows()/3-k){
	            return 5;
	        }
	        else{
	            return 8;
	        }
	    }
	    else{
	        if(r<number.rows()/3+k){
	            return 3;
	        }
	        else if(r<2*number.rows()/3-k){
	            return 6;
	        }
	        else{
	            return 9;
	        }
	    }
	}

	void buildCurls(){
		ArrayList<Point> curls= detectCurls();
		int[] c = new int[4];
	    if(curls.size()==2){
	        c[3]=1;
	    }
	    else if(curls.size()>0){
	    	int zone = localize(curls.get(0));
	        switch(zone){
	        case 2: c[0]=1;
	            break;
	        case 5: c[1]=1;
	            break;
	        case 8: c[2]=1;
	            break;
	        }      
	    }
	   
	}
	
	// Add area density in feature vector
	
	int countPx(Mat m){
	    int k=0;
	    for(int r=0;r<m.rows();r++){
	        for(int c=0; c<m.cols(); c++){
	            if(m.get(r,c)[0]==0){
	                k++;
	            }
	        }
	    }
	    return k;
	}
	
	void buildDensity(int nb_zone){
		ArrayList<Integer> density = new ArrayList<Integer>();
	    for(int i=0; i<nb_zone; i++){
	        for(int j=0; j<nb_zone;j++){
	            Rect r = new Rect(new Point(i*number.cols()/nb_zone,j*number.rows()/nb_zone),new Point((i+1)*number.cols()/nb_zone,(j+1)*number.rows()/nb_zone));
	            Mat submatrix = number.submat(r);
	            int f = countPx(submatrix);
	            density.add(f);
	        }
	    }
	    features.setDensity(density);
	}
	
	// Compute space between borders and numbers
	
	void buildAlign(int n){
		int space = number.rows()/n;
		ArrayList<Integer> align = new ArrayList<Integer>();
		for(int i=0; i<number.rows(); i+=space){
			int j=0;
			while(number.get(i,j)[0]!=0 && j<number.cols()-1){
				j++;
			}
			align.add(j);
		}
		for(int i=0; i<number.rows(); i+=space){
			int j=number.cols()-1;
			//Log.i("indx",String.valueOf(i)+";"+String.valueOf(j));
			while(number.get(i,j)[0]!=0 && j>0){
				j--;
			}
			align.add(j);
		}
		features.setAlignement(align);
	}
	
	// Accessors
	public Mat getNumber(){
		return number;
	}
	
	public NumberFeatures getFeatures(){
		return features;
	}
	
}
