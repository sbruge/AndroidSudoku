package com.sudoku.database;

import java.util.ArrayList;

public class NumberFeatures {
	
	private int value;
	private ArrayList<Integer> density;
	private ArrayList<Integer> alignement;
	
	public NumberFeatures(int value, ArrayList<Integer> density, ArrayList<Integer> alignement) {
		this.value = value;
		this.density = density;
		this.alignement = alignement;
	}
	
	public NumberFeatures(){
		value=0;
		density = new ArrayList<Integer>();
		alignement = new ArrayList<Integer>();
	}

	public int getValue() {
		return value;
	}

	public ArrayList<Integer> getDensity() {
		return density;
	}

	public ArrayList<Integer> getAlignement() {
		return alignement;
	}

	public void setDensity(ArrayList<Integer> density) {
		this.density = density;
	}

	public void setAlignement(ArrayList<Integer> alignement) {
		this.alignement = alignement;
	}
	
	
	
	
	
}
