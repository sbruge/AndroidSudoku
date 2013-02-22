package com.sudoku.imgprocess;

import java.util.ArrayList;
import java.util.Collections;

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
	private ArrayList<Line> hlines;
	private ArrayList<Line> vlines;
	private ArrayList<Rect> areas;
	private ArrayList<Sample> samples;

	public GridPicture(String filename, Database db) {
		picture = Highgui.imread(filename);
		database =db;
		Imgproc.resize(picture, picture, new Size(400,400));
		mgray = new Mat();
		hlines = new ArrayList<Line>();
		vlines = new ArrayList<Line>();
		areas = new ArrayList<Rect>();
		samples = new ArrayList<Sample>();
		maxPx=0;
		minPx=0;
		Imgproc.cvtColor(picture, mgray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(mgray, mgray, 25,150);
		extractAreas();
		Log.e("sudogrid","error in grid:"+String.valueOf(hlines.size())+";"+String.valueOf(vlines.size()));
		//buildRects();
	}
	
	public GridPicture(Bitmap bmp, Database db) {
		picture = new Mat();
		database= db;
		Utils.bitmapToMat(bmp, picture);
		Imgproc.resize(picture, picture, new Size(400,400));
		mgray = new Mat();
		hlines =new ArrayList<Line>();
		vlines =new ArrayList<Line>();
		areas = new ArrayList<Rect>();
		samples = new ArrayList<Sample>();
		maxPx=0;
		minPx=0;
		Imgproc.cvtColor(picture, mgray, Imgproc.COLOR_BGR2GRAY);
		Log.i("sudoGrid","start extract");
		Imgproc.cvtColor(picture, mgray, Imgproc.COLOR_BGR2GRAY);
		Imgproc.Canny(mgray, mgray, 10,150);
		extractAreas();
		if(isValidGrid()){
			Log.i("sudogrid", "valid grid!");
			//buildRects();
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
	
	/*void buildRects(){
		int blank = 6;
		for(int c=0; c<vlines.size()-1;c++){
			for(int r=0; r<hlines.size()-1;r++){
				Point p1 = new Point(hlines.get(r)+blank, vlines.get(c)+blank);
				Point p2= new Point(hlines.get(r+1)-blank+3, vlines.get(c+1)-blank+3);
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
	}*/

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
		//Mat accu = houghAccumulation(mgray, picture.rows(), picture.cols());
		Mat lines = new Mat();
		Imgproc.HoughLines(mgray, lines, 1, Math.PI/180, 130);
		splitLines(lines);
		//buildLines(picture, 95, accu);
	}
	
	void splitLines(Mat lines){
		// Séparation des verticales et horizontales
		for(int k=0; k<lines.cols();k++){
			double[] data = lines.get(0,k);
			double rho = data[0];
			double theta = data[1];	
			if((theta<= Math.PI/2 && Math.PI/2-theta>=theta) || (theta>Math.PI/2 && Math.PI-theta<theta-Math.PI/2)){ // plus proche d'une verticale
				vlines.add(new Line(rho,theta, Line.Orientation.VERTICAL));
			}
			else{
				hlines.add(new Line(rho,theta, Line.Orientation.HORIZONTAL));
			}
		}
		Collections.sort(vlines);
		removeLines(vlines);
		Collections.sort(hlines);
		removeLines(hlines);
		for(int i=0; i<vlines.size();i++){
			vlines.get(i).drawOn(picture);
		}
		for(int i=0; i<hlines.size();i++){
			hlines.get(i).drawOn(picture);
		}
	}
	
	void removeLines(ArrayList<Line> lines){
		double dprec=lines.get(0).getDistance();
		Log.i("size",String.valueOf(lines.size()));
		for(int i=1;i<lines.size();i++){
			Line l = lines.get(i);
			double d = l.getDistance();
			if(l.getDistance()-dprec<=90){
				Log.e("remove",String.valueOf(d));
				lines.remove(i);
				
			}
			else{
				Log.i("keep",String.valueOf(d));
				dprec = l.getDistance();
			}
				
		}
		Log.i("size",String.valueOf(lines.size()));
	}

	Mat houghAccumulation(Mat contour, int r, int c) {
		double p = Math.floor(Math.sqrt(r * r + c * c));
		int delta = 10;
		double max=0;
		Mat accu = new Mat(delta, (int) p, CvType.CV_32F, new Scalar(0));
		for (int i = 0; i < contour.cols(); i++) {
			for (int j = 0; j < contour.rows(); j++) {
				if (contour.get(j,i)[0] == 255) {
					for (int k = 0; k < delta; k++) {
						double theta = k * Math.PI /delta;
						double rho = i * Math.cos(theta) + j * Math.sin(theta);
						accu.put(k, (int) rho, accu.get(k, (int) rho)[0] + 1);
						if(accu.get(k, (int) rho)[0]>max){
							max = accu.get(k, (int) rho)[0];
						}
					}
				}
			}
		}
		buildLines(picture, 0.4*max, accu);
		return accu;
	}

	boolean addLine(double rho, ArrayList<Double> lines) {
		int threshold = 10; //10
		if (lines.size() == 0) {
			lines.add(rho);
			return true;
		} 
		else if(rho<lines.get(0)){
			Log.i("indx",String.valueOf(rho));
			if (lines.get(0) -rho > threshold) {
				lines.add(0, rho);
				//Log.i("indx",String.valueOf(rho));
				return true;
			}
		}
		else{	
			int previousIndex = 0;
			while (lines.get(previousIndex) < rho && previousIndex < lines.size()-1) {
					previousIndex++;

			}
			if (rho - lines.get(previousIndex-1) > threshold) {
				lines.add(previousIndex, rho);
				//Log.i("indx",String.valueOf(rho));
				return true;
			}
		}
		return false;
	}

	void buildLines(Mat img, double seuil, Mat accu) {
		for (int rho = 0; rho < accu.cols(); rho++) {
			for (int j = 0; j < accu.rows(); j++) {
				if (accu.get(j, rho)[0] > seuil) {
					double theta = j * Math.PI / 8;
					Point p1, p2;
					if (j == 0) { // horizontal line
						p1 = new Point(rho, 0);
						p2 = new Point(rho, img.rows());
						//if(addLine(rho, hlines)){
							Core.line(picture,p1,p2,new Scalar(0,255,0));
						//}
					} else if (j == 4) { // vertical line
						p1 = new Point(0, rho);
						p2 = new Point(img.cols(), rho);
						//if(addLine(rho, vlines)){
							Core.line(picture,p1,p2,new Scalar(0,255,0));
						//}
					}
					
					 else { 
						 p1 = new Point(0, rho / Math.sin(theta)); 
						 p2 = new Point(rho / Math.cos(theta), 0);
						 Core.line(picture,p1,p2,new Scalar(255,255,0));
					 }
					 

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
		return samples.get(i).getcontours();
	}
}
