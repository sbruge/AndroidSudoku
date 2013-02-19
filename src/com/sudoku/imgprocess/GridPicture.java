package com.sudoku.imgprocess;

import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.sudoku.database.Database;
import com.sudoku.objects.SudokuData.Input;
import com.sudoku.objects.SudokuGrid;

import android.graphics.Bitmap;
import android.util.Log;

public class GridPicture {

	private Mat picture;
	private Database database;
	private Mat mgray;
	private int maxPx;
	private int minPx;
	private ArrayList<Integer> hlines;
	private ArrayList<Integer> vlines;
	private ArrayList<Rect> areas;
	private ArrayList<Sample> samples;

	public GridPicture(String filename, Database db) {
		picture = Highgui.imread(filename);
		database =db;
		Imgproc.resize(picture, picture, new Size(400,400));
		mgray = new Mat();
		hlines = new ArrayList<Integer>();
		vlines = new ArrayList<Integer>();
		areas = new ArrayList<Rect>();
		samples = new ArrayList<Sample>();
		maxPx=0;
		minPx=0;
		Imgproc.cvtColor(picture, mgray, Imgproc.COLOR_BGR2GRAY);
		//Imgproc.blur(mgray, mgray, new Size(3,3), new Point(-1,-1));
		//Imgproc.blur(mgray, mgray, new Size(3,3), new Point(-1,-1));
		//Imgproc.equalizeHist(mgray,mgray);
		Imgproc.Canny(mgray, mgray, 25,150);
		extractAreas();
		Log.e("sudogrid","error in grid:"+String.valueOf(hlines.size())+";"+String.valueOf(vlines.size()));
		buildRects();
	}
	
	public GridPicture(Bitmap bmp, Database db) {
		picture = new Mat();
		database= db;
		Utils.bitmapToMat(bmp, picture);
		Imgproc.resize(picture, picture, new Size(400,400));
		mgray = new Mat();
		hlines = new ArrayList<Integer>();
		vlines = new ArrayList<Integer>();
		areas = new ArrayList<Rect>();
		samples = new ArrayList<Sample>();
		maxPx=0;
		minPx=0;
		Imgproc.cvtColor(picture, mgray, Imgproc.COLOR_BGR2GRAY);
		Log.i("sudoGrid","start extract");
		Imgproc.cvtColor(picture, mgray, Imgproc.COLOR_BGR2GRAY);
		/*Imgproc.blur(mgray, mgray, new Size(3,3), new Point(-1,-1));
		Imgproc.blur(mgray, mgray, new Size(3,3), new Point(-1,-1));
		Imgproc.equalizeHist(mgray,mgray);*/
		Imgproc.Canny(mgray, mgray, 10,150);
		extractAreas();
		if(isValidGrid()){
			Log.i("sudogrid", "valid grid!");
			buildRects();
		}
		else{
			Log.i("sudogrid","error in grid:"+String.valueOf(hlines.size())+";"+String.valueOf(vlines.size()));
		}
	}

	public Mat getPicture() {
		return picture;
	}

	public Mat getGrayPicture() {
		return mgray;
	}

	public boolean isValidGrid() {
		return hlines.size() == 10 && vlines.size() == 10;
	}
	
	int countPx(Mat area){
		int k=0;
		for(int r=0;r<area.rows();r++){
			for(int c=0; c<area.cols();c++){
				if(area.get(r,c)[0]==0){
					k++;
				}
			}
		}
		return k;
	}
	
	void buildRects(){
		int blank = 5;
		for(int c=0; c<vlines.size()-1;c++){
			for(int r=0; r<hlines.size()-1;r++){
				Point p1 = new Point(hlines.get(r)+blank, vlines.get(c)+blank);
				Point p2= new Point(hlines.get(r+1)-blank+2, vlines.get(c+1)-blank+2);
				Rect rec = new Rect(p1,p2);
				Mat m = picture.submat(rec);
				Sample s = new Sample(m);
				int count = s.countPx();
				if(count>maxPx){
					maxPx=count;
				}
				else if(minPx>count){
					minPx=count;
				}
				areas.add(rec);
				samples.add(s);
			}
		}
	}

	public SudokuGrid buildGame() {
		SudokuGrid grid = new SudokuGrid();
		for(int r=0; r<samples.size();r++){
			Sample s = samples.get(r);
			if(s.isNumber(minPx,maxPx)){
				int j = r/9;
				int i = r%9;
				FeatureExtractor extract = new FeatureExtractor(s);
				grid.insertValue(i,j, database.findValue(extract.getFeatures(),5), Input.ORIGINAL);
				Core.rectangle(picture, areas.get(r).tl(),areas.get(r).br(), new Scalar(0,255,0),2,8,0);
			}
		}
		return grid;
	}
	
	
	public Mat viewSample(int i, int j){
		return picture.submat(areas.get(9*i+j%9));
	}

	void extractAreas() {
		Mat extracted = new Mat();
		//Imgproc.Canny(mgray, extracted, 30, 75);
		Mat accu = houghAccumulation(mgray, picture.rows(), picture.cols());
		buildLines(picture, 105, accu);
	}

	Mat houghAccumulation(Mat contour, int r, int c) {
		double p = Math.floor(Math.sqrt(r * r + c * c));
		int q = 5;
		Mat accu = new Mat(q, (int) p, CvType.CV_32F, new Scalar(0));
		for (int i = 0; i < contour.cols(); i++) {
			for (int j = 0; j < contour.rows(); j++) {
				if (contour.get(j,i)[0] == 255) {
					for (int k = 0; k < q; k++) {
						double theta = k * Math.PI / 8;
						double rho = i * Math.cos(theta) + j * Math.sin(theta);
						accu.put(k, (int) rho, accu.get(k, (int) rho)[0] + 1);
					}
				}
			}
		}
		return accu;
	}

	void addLine(int rho, ArrayList<Integer> lines) {
		int threshold = 10; //10
		if (lines.size() == 0) {
			lines.add(rho);
		} else {
			int previousIndex = 0;
			while (lines.get(previousIndex) < rho) {
				if (previousIndex < lines.size() - 1) {
					previousIndex++;
				} else {
					break;
				}
			}
			if (rho - lines.get(previousIndex) > threshold) {
				lines.add(previousIndex + 1, rho);
				//Log.i("indx",String.valueOf(rho));
			}
		}
	}

	void buildLines(Mat img, int seuil, Mat accu) {
		for (int rho = 0; rho < accu.cols(); rho++) {
			for (int j = 0; j < accu.rows(); j++) {
				if (accu.get(j, rho)[0] > seuil) {
					double theta = j * Math.PI / 8;
					Point p1, p2;
					if (j == 0) { // horizontal line
						p1 = new Point(rho, 0);
						p2 = new Point(rho, img.rows());
						//Core.line(picture,p1,p2,new Scalar(0,255,0));
						addLine(rho, hlines);
					} else if (j == 4) { // vertical line
						p1 = new Point(0, rho);
						p2 = new Point(img.cols(), rho);
						//Core.line(picture,p1,p2,new Scalar(0,255,0));
						addLine(rho, vlines);
					}
					
					 /*else { 
						 p1 = new Point(0, rho / Math.sin(theta)); 
						 p2 = new Point(rho / Math.cos(theta), 0);
						 Core.line(picture,p1,p2,new Scalar(255,255,0));
					 }*/
					 

				}
			}
		}
		//Log.i("vsize", String.valueOf(vlines.size()));
		//Log.i("hsize", String.valueOf(hlines.size()));
	}
	
	public Mat showAreas(){
		Mat img = picture.clone();
		for(int r=0;r<areas.size();r++){
			Core.rectangle(img, areas.get(r).tl(),areas.get(r).br(), new Scalar(0,0,255),2,8,0);
		}
		return img;
	}
	
	public Mat viewSample(int i){
		return samples.get(i).getArea();
	}
}
