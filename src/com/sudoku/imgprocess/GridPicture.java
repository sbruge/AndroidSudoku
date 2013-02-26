package com.sudoku.imgprocess;

import java.util.ArrayList;
import java.util.Collections;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import com.sudoku.database.Database;
import com.sudoku.imgprocess.Line.Orientation;
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
		reshape();
		if(isValidGrid()){
			Log.i("sudogrid", "valid grid!");
			buildRects();
		}
		else{
			Log.e("sudogrid","error in grid:"+String.valueOf(hlines.size())+";"+String.valueOf(vlines.size()));
		}		
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
		Imgproc.Canny(mgray, mgray, 10,130);
		reshape();
		if(isValidGrid()){
			Log.i("sudogrid", "valid grid!");
			buildRects();
		}
		else{
			Log.e("sudogrid","error in grid:"+String.valueOf(hlines.size())+";"+String.valueOf(vlines.size()));
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
		for(int r=0; r<9;r++){
			for(int c=0; c<9;c++){
				int bk =5;
				Rect rec = new Rect(new Point(c*picture.cols()/9+bk+3, r*picture.rows()/9+bk),new Point((c+1)*picture.cols()/9-bk, (r+1)*picture.rows()/9-bk));
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
				//s.adjustRoi();
				int j = r/9;
				int i = r%9;
				FeatureExtractor extract = new FeatureExtractor(s);
				grid.insertValue(i,j, database.findValue(extract.getFeatures(),5), Input.ORIGINAL);
				Core.rectangle(picture, areas.get(r).tl(),areas.get(r).br(), new Scalar(0,255,0),1,8,0);
			}
		}
		return grid;
	}
	
	
	public Mat viewSample(int i, int j){
		return picture.submat(areas.get(9*i+j%9));
	}
	
	void reshape(){
		Mat lines = new Mat();
		Imgproc.HoughLines(mgray, lines, 1, Math.PI/180, 130);
		splitLines(lines);
		adjustPerspective();
	}

	
	void splitLines(Mat lines){
		// S�paration des verticales et horizontales
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
		removeLines(vlines,Orientation.VERTICAL);
		Collections.sort(hlines);
		removeLines(hlines,Orientation.HORIZONTAL);
		for(int i=0; i<vlines.size();i++){
			vlines.get(i).drawOn(picture);
		}
		for(int i=0; i<hlines.size();i++){
			hlines.get(i).drawOn(picture);
		}
	}
	
	void adjustPerspective(){
		Point p1 = vlines.get(0).intersection(hlines.get(0));
		Point p2 = vlines.get(vlines.size()-1).intersection(hlines.get(0));
		Point p3 = vlines.get(vlines.size()-1).intersection(hlines.get(hlines.size()-1));
		Point p4 = vlines.get(0).intersection(hlines.get(hlines.size()-1));
		
		ArrayList<Point> src_pts = new ArrayList<Point>();
		ArrayList<Point> dst_pts = new ArrayList<Point>();
		src_pts.add(p1);
		src_pts.add(p2);
		src_pts.add(p3);
		src_pts.add(p4);
		
		dst_pts.add(new Point(0,0));
		dst_pts.add(new Point(400,0));
		dst_pts.add(new Point(400,400));
		dst_pts.add(new Point(0,400));
		Mat src= new Mat();
		src=Converters.vector_Point2f_to_Mat(src_pts);
		Mat dst=new Mat();
		dst=Converters.vector_Point2f_to_Mat(dst_pts);
		Mat H = Imgproc.getPerspectiveTransform(src, dst);
		Imgproc.warpPerspective(picture, picture, H, picture.size());
	}
	
	void removeLines(ArrayList<Line> lines, Line.Orientation orientation){
		ArrayList<Line> newLines = new ArrayList<Line>();
		newLines.add(lines.get(0));
		if(orientation == Orientation.VERTICAL){
			Line l = new Line(picture.rows()/2,Math.PI/2,Orientation.HORIZONTAL);
			double x = l.intersection(lines.get(0)).x;
			for(int i=1; i<lines.size();i++){
				Point p = l.intersection(lines.get(i));
				if(p.x-x>10){
					newLines.add(lines.get(i));
					x=p.x;
				}
			}
			vlines = newLines;
		}
		else{
			Line l = new Line(picture.cols()/2,0,Orientation.VERTICAL);
			l.drawOn(picture);
			double y = l.intersection(lines.get(0)).y;
			for(int i=1; i<lines.size();i++){
				Point p = l.intersection(lines.get(i));
				if(p.y-y>10){
					newLines.add(lines.get(i));
					y=p.y;
				}
			}
			hlines = newLines;
		}
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
