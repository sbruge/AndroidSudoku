package com.sudoku.imgprocess;
import java.util.ArrayList;
import org.opencv.core.Mat;

import android.util.Log;

public class Decision {
	private ArrayList<double[]> means;
	private Mat sampleFeatures;
	
	public Decision(Sample sample){
		FeatureExtractor fe = new FeatureExtractor(sample);
		means = new ArrayList<double[]>();
		sampleFeatures = fe.getFeatures();
		double one[] = {0, 0, 0, 0, 0.80000007, 0, 0.9333334, 0.16666667, 0.13333334, 0.33333334, 0.033333335, 0, 0, 0, 0.23333335, 0.40000004, 0.73333335, 12.433334, 3.7333336, 9.5333338, 24.633335, 26.800001, 26.466667, 13.066668, 14.166667, 23.866669};
		double two[] = {0.06666667, 0, 0.10000001, 0.033333335, 1, 0.23333335, 1, 0, 0.16666667, 0.13333334, 0.033333335, 0, 0.033333335, 0.06666667, 0.23333335, 0.63333338, 0.90000004, 22.333334, 7.2000003, 27.866669, 32.733334, 31.066668, 42.000004, 26.733335, 32.466667, 29.966669};
		double[] three = {0.06666667, 0.033333335, 0, 0, 1, 0.66666669, 1, 0, 0.33333334, 0.20000002, 0.26666668, 0.033333335, 0.13333334, 0.20000002, 0.20000002, 0.13333334, 0.4666667, 20.000002, 7.3666673, 26.266668, 33.76667, 34.76667, 33.333336, 25.433334, 37.666668, 33.066669};
		double[] four = {0.10000001, 0.70000005, 0.13333334, 0.06666667, 0.5, 0.30000001, 1, 0.20000002, 0, 0.56666672, 0.06666667, 0, 0.20000002, 0.033333335, 0.23333335, 0.76666671, 0.90000004, 0.83333337, 25.600002, 11.6, 26.666668, 53.200005, 28.100002, 19.900002, 26.133335, 28.766668};
		double[] five = {0, 0, 0, 0, 1, 0.76666671, 1, 0, 0.033333335, 0.10000001, 0.83333337, 0, 0.10000001, 0, 0.06666667, 0.16666667, 0.16666667, 17.766668, 19.766668, 25.400002, 32.533337, 33.400002, 34.333336, 24.700001, 31.633335, 31.966669};
		double[] six = {0.033333335, 0, 0.10000001, 0, 1, 0.76666671, 1, 0, 0, 0.23333335, 0.70000005, 0, 0.06666667, 0, 0.10000001, 0.26666668, 0.13333334, 12.700001, 28.066668, 25.066668, 37.000004, 39.966667, 39.033337, 23.500002, 34.366669, 36};
		double[] seven = {0, 0, 0, 0, 1, 0.06666667, 0.80000007, 0.23333335, 0.20000002, 0.10000001, 0.13333334, 0, 0, 0.033333335, 0.26666668, 0.70000005, 0.06666667, 24.466667, 1.6666667, 15.866668, 31.266668, 34.100002, 32, 33.633335, 18.166668, 1.6666667};
		double[] eight = {0.90000004, 0, 0.86666673, 0.80000007, 1, 0.76666671, 1, 0, 0.06666667, 0.33333334, 0.16666667, 0.16666667, 0, 0.30000001, 0.06666667, 0.20000002, 0.33333334, 19.366667, 32.733334, 28.100002, 36.5, 39.866669, 37.000004, 29.266668, 40.866669, 36.600002};
		double[] nine = {0.9333334, 0, 0.76666671, 0.76666671, 1, 0.73333335, 1, 0, 0.033333335, 0.033333335, 0.06666667, 0, 0.06666667, 0.13333334, 0.16666667, 0.56666672, 0.40000004, 22.400002, 29.333334, 18.733334, 36.633335, 37.700001, 41.233334, 27.966667, 36.633335, 24.633335};
		means.add(one);
		means.add(two);
		means.add(three);
		means.add(four);
		means.add(five);
		means.add(six);
		means.add(seven);
		means.add(eight);
		means.add(nine);
	}
	
	// Decision on density zone
	double euclidianDistance(double[] m1, Mat m2){
	    double d=0;
	    for(int i=17;i<m2.cols();i++){
	        d+= Math.pow(m2.get(0,i)[0]-m1[i],2);
	    }
	    //Log.d("distance", String.valueOf(d));
	    return Math.sqrt(d);
	}

	int ppv(){
	    int idx =0;
	    double dist = euclidianDistance(means.get(0),sampleFeatures);
	    for(int i=1;i<means.size();i++){
	        double[] mean = means.get(i);
	        double d = euclidianDistance(mean,sampleFeatures);
	        if(d<dist){
	            idx=i;
	            dist=d;
	        }
	    }
	    return idx+1;
	}

	int[] vote(){
		int[] v = {0,0,0,0,0,0,0,0,0};
	    for(int i=0; i<sampleFeatures.cols()-9; i++){
	        double x = sampleFeatures.get(0,i)[0];
	        for(int k=0; k<means.size();k++){
	            double[] probas = means.get(k);
	            double p = probas[i];
	            int power=1;
	            if(i<4){
	                power=2;
	            }
	            if(x==1){
	                power=2*power;
	                if(p>0.7){
	                    v[k]=v[k]+power;
	                }
	            }
	            else if((1-p)>0.3){
	                    v[k]=v[k]+power;
	            }
	        }
	    }
	    return v;
	}


	public int getDecision(){
	    int[] v = vote();
	    int p = ppv();
	    //v[p-1]= v[p-1]+5;
	    int k=0;
	    for(int i=0; i<v.length; i++){
	    	Log.i("vote", String.valueOf(v[i]));
	        if(v[i]>v[k]){
	            k=i;
	        }
	    }
	    return p;
	    //return k+1;
	}
}
