package com.sudoku.imgprocess;

import java.util.ArrayList;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvANN_MLP;

import com.sudoku.objects.SudokuData.Input;
import com.sudoku.objects.SudokuGrid;

import android.util.Log;

public class GridPicture {

	private Mat picture;
	private Mat mgray;
	private ArrayList<Integer> hlines;
	private ArrayList<Integer> vlines;

	public GridPicture(String filename) {
		picture = Highgui.imread(filename);
		mgray = new Mat();
		hlines = new ArrayList<Integer>();
		vlines = new ArrayList<Integer>();
		Imgproc.cvtColor(picture, mgray, Imgproc.COLOR_RGB2GRAY);
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

	public SudokuGrid buildGame() {
		SudokuGrid grid = new SudokuGrid();
		int merge=5;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				Mat sample = picture.submat(hlines.get(i)+merge,
						hlines.get(i + 1)-merge, vlines.get(j)+merge, vlines.get(j + 1)-merge);
				Sample s = new Sample(sample);
				if(s.isNumber()){
					Decision decision = new Decision(s);
					grid.insertValue(i, j,decision.getDecision(), Input.ORIGINAL);
					Log.i("insertval",String.valueOf(i));
				}
			}
		}
		return grid;
	}
	
	public Mat viewSample(int i, int j){
		int merge=5;
		return picture.submat(hlines.get(i)+merge,
				hlines.get(i + 1)-merge, vlines.get(j)+merge, vlines.get(j + 1)-merge);
	}

	public Mat extractAreas() {
		Mat im = picture.clone();
		Mat extracted = new Mat();
		Imgproc.Canny(mgray, extracted, 100, 175);
		Mat accu = houghAccumulation(extracted, im.rows(), im.cols());
		Mat lines = drawLines(im, 75, accu);
		return lines;
	}

	Mat houghAccumulation(Mat contour, int r, int c) {
		double p = Math.floor(Math.sqrt(r * r + c * c));
		int q = 5;
		Mat accu = new Mat(q, (int) p, CvType.CV_32F, new Scalar(0));
		for (int i = 0; i < contour.cols(); i++) {
			for (int j = 0; j < contour.rows(); j++) {
				if (contour.get(i, j)[0] == 255) {
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

	Mat drawLines(Mat img, int seuil, Mat accu) {
		// Mat lines = new Mat(400, 400, CvType.CV_8U, new Scalar(255));
		Mat lines = picture.clone();
		for (int rho = 0; rho < accu.cols(); rho++) {
			for (int j = 0; j < accu.rows(); j++) {
				if (accu.get(j, rho)[0] > seuil) {
					double theta = j * Math.PI / 8;
					Point p1, p2;
					if (j == 0) { // horizontal line
						p1 = new Point(rho, 0);
						p2 = new Point(rho, img.rows());
						addLine(rho, hlines);
						Core.line(lines, p1, p2, new Scalar(0, 255, 0), 1, 8, 0);
					} else if (j == 4) { // vertical line
						p1 = new Point(0, rho);
						p2 = new Point(img.cols(), rho);
						addLine(rho, vlines);
						Core.line(lines, p1, p2, new Scalar(0, 255, 0), 1, 8, 0);
					}
					/*
					 * else { p1 = new Point(0, rho / Math.sin(theta)); p2 = new
					 * Point(rho / Math.cos(theta), 0); //
					 * line(lines,p1,p2,CV_RGB(255,0,0),1,8,0); }
					 */

				}
			}
		}
		Log.i("vsize", String.valueOf(vlines.size()));
		Log.i("hsize", String.valueOf(hlines.size()));
		return lines;
	}
}
