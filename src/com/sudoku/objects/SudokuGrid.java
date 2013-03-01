package com.sudoku.objects;

import java.util.ArrayList;

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
	
	public SudokuGrid(SudokuGrid sudoku){
		grid = new SudokuData[9][9];
		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				SudokuData data = sudoku.getGrid()[i][j];
				grid[i][j] = new SudokuData(data.getDataType(),data.getValue());
			}
		}
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
	
	// Solve Grid
	
	// Déplacement
    private int ligneSuivante(int i,int j){
        // si on est sur la dernière colonne, on passe à la ligne suivante
    	if (j==8) 
           return i+1;
        // sinon on reste sur la même ligne
    	else 
           return i;
    }

    private int colonneSuivante(int i,int j){
        // si on est sur la dernière colonne, on passe à la colonne 0
    	if (j==8) 
           return 0;
        // sinon on se déplace d'une colonne vers la droite
    	else 
           return j+1;
    }

    // Tests de remplissage

     private boolean lignePossible(int l,int v) {
    	 boolean bool=true;
    	 for (int j=0;j<9;j++){
    		 if (grid[l][j].getValue()==v){
    			 return false;
    		 }
    	 }
    	 return bool;
    }

    
    private boolean colonnePossible(int c,int v) {
        boolean bool=true;
        for(int i=0; i<9; i++){
        	if (grid[i][c].getValue()==v){
        		return false;
        	}
        }
        return bool;
     }

    
    private boolean carrePossible(int i,int j,int v) {
    	int coinI = (i/3) * 3;
    	int coinJ = (j/3) * 3;
    	for (int i1=0;i1<3;i1++)
    		for (int j1=0;j1<3;j1++)
    			if (grid[coinI+i1][coinJ+j1].getValue()==v){
    				return false;
    			}
    	return true;
    }

    //Remplissage
    
    public boolean remplir() {
        // on commence par placer le nombre dans la case en haut à gauche
    	return placer(0,0);
    }
           
    private boolean placer(int i, int j) {

        if (i==9){
        	Log.i("rempli","Finish with sucess!!");
           return true;
        }

        if (grid[i][j].getValue()!=0){
        	return placer(ligneSuivante(i,j),colonneSuivante(i,j));
        }
       
        int k=1;

        boolean possible = false;
        while (possible==false && k<=9) {
        	//
        	if(lignePossible(i,k)==true && colonnePossible(j,k)==true && carrePossible(i,j,k)==true){
        		grid[i][j].setValue(k);
        		if(placer(ligneSuivante(i,j),colonneSuivante(i,j))==true){
        			return true;
        		}
        		else{
        			grid[i][j].setValue(0);
	        		k++;
        		}
        	}
        	else{
        		k++;
        	}
	}
	return false;
  }
    
    // Help tools for user
    
    ArrayList<Integer> getPossibilities(int i, int j){
    	ArrayList<Integer> p = new ArrayList<Integer>();
    	for(int v=1;v<=9;v++){
    		if(lignePossible(i, v) && colonnePossible(j, v) && carrePossible(i, j, v)){
    			p.add(v);
    		}
    	}
    	return p;
    }
	
	
	// Parcelable Override methods
	
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
