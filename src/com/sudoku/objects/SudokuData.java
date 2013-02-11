package com.sudoku.objects;

public class SudokuData {
	
	public enum Input{
		ORIGINAL,
		USER,
		BLANK
	};
	
	private Input dataType;
	private int value;
	
	public SudokuData(Input dataType, int value) {
		this.dataType = dataType;
		this.value = value;
	}

	public SudokuData() {
		dataType = Input.BLANK;
		value=0;
	}

	public Input getDataType() {
		return dataType;
	}

	public int getValue() {
		return value;
	}

	public void setData(int value, Input type) {
		this.value = value;
		this.dataType = type;
	}
	
	
	
}
