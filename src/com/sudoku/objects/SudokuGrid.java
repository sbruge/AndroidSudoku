package com.sudoku.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.sudoku.objects.SudokuData.Input;

public class SudokuGrid implements Parcelable{

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
	
	public SudokuGrid(Parcel in) {
		grid = new SudokuData[9][9];
		Parcelable[] lineOfData = in.readParcelableArray(SudokuData.class.getClassLoader());
		for(int k=0;k<lineOfData.length;k++){
			int i=k/9;
			int j= k-9*i;
			if(lineOfData[k]!=null){
				grid[i][j]= (SudokuData) lineOfData[k];
			}
			else{
				Log.i("parcelable grid","invlaid data");
			}
			
		}
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private SudokuData[] toOneDimension(){
		SudokuData[] lineOfData = new SudokuData[81];
		for(int i=0;i<9;i++){
			for(int j=0; j<9; j++){
				lineOfData[9*i+j%9]=grid[i][j];
			}
		}
		return lineOfData;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeParcelableArray(toOneDimension(), flags);
		
	}
	
public static final Parcelable.Creator<SudokuGrid> CREATOR = new Parcelable.Creator<SudokuGrid>() {  
	    
        public SudokuGrid createFromParcel(Parcel in) {  
            return new SudokuGrid(in);  
        }  
   
        public SudokuGrid[] newArray(int size) {  
            return new SudokuGrid[size];  
        }  
          
    }; 
	
	
}
