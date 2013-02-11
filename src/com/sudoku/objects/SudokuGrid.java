package com.sudoku.objects;

import com.sudoku.objects.SudokuData.Input;

public class SudokuGrid {

	private SudokuData[][] grid;
	
	public SudokuGrid(){
		grid = new SudokuData[9][9];
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				grid[i][j] = new SudokuData();
			}
		}
	}
	
	public SudokuGrid(SudokuData[][] grid){
		this.grid=grid;
	}
	
	public SudokuData[][] getGrid(){
		return grid;
	}
	
	public void insertValue(int i, int j, int value, Input type){
		if(value<0 || value>9){
			System.out.println("Please insert a valid number : 0-9");
		}
		else{
			grid[i][j]=new SudokuData(type,value);
		}
	}
	
	public int getValue(int i, int j){
		return grid[i][j].getValue();
	}
	
	public Input getType(int i, int j){
		return grid[i][j].getDataType();
	}
	
	
}
