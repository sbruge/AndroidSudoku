package com.sudoku.imgprocess;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.util.Log;

public class Squelettisation {

	private Mat lSkel;
	private Mat mSkel;
	private Mat eb;
	private boolean induceChange;
	
	public Squelettisation(){
		lSkel= Mat.zeros(3,3,CvType.CV_32F);
	    lSkel.put(2,0,1);
	    lSkel.put(2,1,1);
	    lSkel.put(2,2,1);
	    lSkel.put(1,0,-1);
	    lSkel.put(1,2,-1);
	    lSkel.put(1,1,1);

	    mSkel= Mat.zeros(3,3,CvType.CV_32F);
	    mSkel.put(0,0,1);
	    mSkel.put(0,1,1);
	    mSkel.put(1,0,-1);
	    mSkel.put(0,2,-1);
	    mSkel.put(1,1,1);

	    eb= Mat.zeros(3,3,CvType.CV_32F);
	    eb.put(0,0,-1);
	    eb.put(0,1,1);
	    eb.put(0,2,-1);
	    eb.put(1,1,1);
	    
	    induceChange = true;
	}
	
	// Usefull operations with filters
	
	Mat rotate(Mat filtre){
	    Mat r_f= Mat.zeros(3,3,CvType.CV_32F);
	    r_f.put(0,0, filtre.get(0,1)[0]);
	    r_f.put(0,1, filtre.get(0,2)[0]);
	    r_f.put(0,2, filtre.get(1,2)[0]);
	    r_f.put(1,0, filtre.get(0,0)[0]);
	    r_f.put(1,1, filtre.get(1,1)[0]);
	    r_f.put(1,2, filtre.get(2,2)[0]);
	    r_f.put(2,0, filtre.get(1,0)[0]);
	    r_f.put(2,1, filtre.get(2,0)[0]);
	    r_f.put(2,2, filtre.get(2,1)[0]);
	    return r_f;

	}
	
	int getColor(Mat img, Mat filtre,int i, int j){
	    boolean conform_mask=true;
	    for(int k=0;k<filtre.cols();k++){
	        for(int l=0; l< filtre.rows(); l++){
	            double m_value=filtre.get(l,k)[0];
	            if(m_value!=-1){
	               double im_value = img.get(j+l-1,i+k-1)[0];
	               if(im_value==0){
	                   im_value=1;
	               }
	               else{
	                   im_value=0;
	               }
	               if(im_value != m_value){
	                   conform_mask=false;
	               }
	            }
	        }
	    }
	    if(conform_mask){
	        induceChange=true;
	        return 255;
	    }
	    else{
	        return 0;
	    }
	}

	Mat applyFilter(Mat img, Mat filtre){
	    Mat filtree= new Mat(img.rows(), img.cols(), img.type());
	    for(int i=1; i<img.cols()-1; i++){
	        for(int j=1; j<img.rows()-1; j++){
	            if(img.get(j,i)[0]==0){
	                int c = getColor(img,filtre,i,j);
	               filtree.put(i,j,c);
	            }
	            else{
	            	filtree.put(i,j,255);
	            }
	        }
	    }
	    return filtree;
	}
	
	// Algorithm of squelettisation
	void epluchage(Mat img){
	    for(int i=0; i<8;i++){
	        img = applyFilter(img,lSkel);
	        lSkel= rotate(lSkel);
	    }
	    lSkel= rotate(lSkel);
	    for(int i=0; i<8;i++){
	       img = applyFilter(img,mSkel);
	       mSkel= rotate(mSkel);
	    }
	    mSkel= rotate(mSkel);
	}

	void ebarbage(Mat img){
	    for(int i=0; i<8;i++){
	        img= applyFilter(img,eb);
	        eb= rotate(eb);
	    }
	    eb= rotate(eb);
	}

	Mat squelettisation(Mat img){
		Mat squel= img.clone();
		int k=0;
		while(induceChange==true){
			induceChange=false;
			epluchage(squel);
			k++;
		}
		for(int i=0; i<8;i++){
			squel= applyFilter(squel,eb);
			eb= rotate(eb);
		}
	    eb= rotate(eb);
	    Log.d("squelett", String.valueOf(k));
	    return squel;
	}
}
