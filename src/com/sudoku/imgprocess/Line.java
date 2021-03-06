package com.sudoku.imgprocess;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.util.Log;

public class Line implements Comparable<Line> {
	
	public enum Orientation{
		VERTICAL,
		HORIZONTAL;
	}
	
	private double rho;
	private double theta;
	private Orientation orientation;
	
	public Line(double rho, double theta, Orientation orientation) {
		this.rho = rho;
		this.theta = theta;
		this.orientation = orientation;
	}
	
	public double getRho(){
		return rho;
	}
	
	public double getTheta(){
		return theta;
	}
	
	public Orientation getOrientation(){
		return orientation;
	}
	
	public double getDistance(){
		if(orientation == Orientation.VERTICAL){
			return rho*Math.cos(theta);
		}
		else{
			return rho*Math.sin(theta);
		}
	}
	
	void drawOn(Mat picture){
		Point p1 = new Point(Math.cos(theta)*rho + 1000*(-Math.sin(theta)),Math.sin(theta)*rho + 1000*(Math.cos(theta)));
		Point p2 = new Point(Math.cos(theta)*rho - 1000*(-Math.sin(theta)),Math.sin(theta)*rho - 1000*(Math.cos(theta)));
		if(orientation == Orientation.VERTICAL){
			Core.line(picture, p1, p2, new Scalar(255,255,0));
		}
		else{
			Core.line(picture, p1, p2, new Scalar(0,255,255));
		}
	}
	
	public Point intersection(Line l){
		Point p = new Point();
		if(l.getOrientation() == orientation){
			return p;
		}
		else{
			if(theta!=0){
				double a = -Math.cos(theta)/Math.sin(theta);
				double b = rho/Math.sin(theta);
				if(l.getTheta()!=0){
					double al = -Math.cos(l.getTheta())/Math.sin(l.getTheta());
					double bl = l.getRho()/Math.sin(l.getTheta());
					p.x = (bl-b)/(a-al);
					p.y = a*p.x+b;
				}
				else{
					p.x= l.getRho();
					p.y= a*p.x+b;
				}
			}
			else{
				double al = -Math.cos(l.getTheta())/Math.sin(l.getTheta());
				double bl = l.getRho()/Math.sin(l.getTheta());
				p.x= rho;
				p.y= al*p.x+bl;
			}
			return p;
		}
	}
	

	@Override
	public boolean equals(Object o) {
		if(o instanceof Line){
			Line l = (Line) o;
			if(orientation == Orientation.VERTICAL){
			return rho*Math.cos(theta) == l.getRho()*Math.cos(l.getTheta());
			}
			else{
				return rho*Math.sin(theta) == l.getRho()*Math.sin(l.getTheta());
			}
		}
		return false;
	}

	@Override
	public int compareTo(Line l) {
		if(orientation==Orientation.VERTICAL){
			if(rho*Math.cos(theta)< l.getRho()*Math.cos(l.getTheta())){
				return -1;
			}
			else{
				return 0;
			}
		}
		else{
			if(rho*Math.sin(theta)< l.getRho()*Math.sin(l.getTheta())){
				return -1;
			}
			else{
				return 0;
			}
		}
	}
	
}
