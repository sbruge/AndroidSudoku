package com.sudoku.imgprocess;

import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.sudoku.objects.SudokuData.Input;
import com.sudoku.objects.SudokuGrid;

import android.graphics.Bitmap;
import android.util.Log;

public class GridPicture {

	private Mat picture;
	private Mat mgray;
	private int maxPx;
	private int minPx;
	private ArrayList<Integer> hlines;
	private ArrayList<Integer> vlines;
	private ArrayList<Rect> areas;

	public GridPicture(String filename) {
		picture = Highgui.imread(filename);
		mgray = new Mat();
		hlines = new ArrayList<Integer>();
		vlines = new ArrayList<Integer>();
		areas = new ArrayList<Rect>();
		maxPx=0;
		minPx=0;
		Imgproc.cvtColor(picture, mgray, Imgproc.COLOR_BGR2GRAY);
		extractAreas();
		buildRects();
	}
	
	public GridPicture(Bitmap bmp) {
		picture = new Mat();
		Utils.bitmapToMat(bmp, picture);
		mgray = new Mat();
		hlines = new ArrayList<Integer>();
		vlines = new ArrayList<Integer>();
		areas = new ArrayList<Rect>();
		maxPx=0;
		minPx=0;
		Imgproc.cvtColor(picture, mgray, Imgproc.COLOR_BGR2GRAY);
		Log.i("sudoGrid","start extract");
		extractAreas();
		if(isValidGrid()){
			buildRects();
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
				Point p2= new Point(hlines.get(r+1), vlines.get(c+1));
				Rect rec = new Rect(p1,p2);
				Mat m = mgray.submat(rec);
				Imgproc.threshold(m, m, 0, 200, Imgproc.THRESH_OTSU);
				int count = countPx(m);
				if(count>maxPx){
					maxPx=count;
				}
				else if(minPx>count){
					minPx=count;
				}
				areas.add(rec);
			}
		}
	}

	public SudokuGrid buildGame() {
		SudokuGrid grid = new SudokuGrid();
		for(int r=0; r<areas.size();r++){
			Sample s = new Sample(picture.submat(areas.get(r)));
			if(s.isNumber(minPx,maxPx)){
				int j = r/9;
				int i = r%9;
				Decision decision = new Decision(s);
				grid.insertValue(i,j, decision.getDecision(), Input.ORIGINAL);
			}
		}
		return grid;
	}
	
	
	public Mat viewSample(int i, int j){
		return picture.submat(areas.get(9*i+j%9));
	}

	void extractAreas() {
		Mat extracted = new Mat();
		Imgproc.Canny(mgray, extracted, 100, 175);
		Mat accu = houghAccumulation(extracted, picture.rows(), picture.cols());
		buildLines(picture, 75, accu);
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
		int threshold = 10;
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
						addLine(rho, hlines);
					} else if (j == 4) { // vertical line
						p1 = new Point(0, rho);
						p2 = new Point(img.cols(), rho);
						addLine(rho, vlines);
					}
					/*
					 * else { p1 = new Point(0, rho / Math.sin(theta)); p2 = new
					 * Point(rho / Math.cos(theta), 0); //
					 * line(lines,p1,p2,CV_RGB(255,0,0),1,8,0); }
					 */

				}
			}
		}
		//Log.i("vsize", String.valueOf(vlines.size()));
		//Log.i("hsize", String.valueOf(hlines.size()));
	}
	
	public Mat showAreas(){
		Mat img = picture.clone();
		for(int r=0;r<areas.size();r++){
			Core.rectangle(img, areas.get(r).br(),areas.get(r).tl(), new Scalar(0,255,0),1,8,0);
		}
		return img;
	}
}
