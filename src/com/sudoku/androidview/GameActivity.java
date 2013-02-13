package com.sudoku.androidview;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.sudoku.imgprocess.GridPicture;
import com.sudoku.objects.SudokuData.Input;
import com.sudoku.objects.SudokuGrid;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class GameActivity extends Activity{
	
	private final static String TAG = "Game";
	private SudokuGrid grid;
	private GridView gridView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		//grid = new SudokuGrid();
		/*grid.insertValue(3, 3, 5, Input.ORIGINAL);
		grid.insertValue(1, 6, 2, Input.ORIGINAL);*/
        String path = Environment.getExternalStorageDirectory().getPath()+"/sudoku.png";
		GridPicture picture = new GridPicture(path);
		//picture.extractAreas();
		grid = picture.buildGame();
		
		gridView = new GridView(this);
		setContentView(gridView);
		gridView.requestFocus();
	}
	
	SudokuGrid getGrid(){
		return grid;
	}
	
	
	protected void showKeypad(){
		Dialog v= new Keypad(this,gridView);
		v.show();
	}
	

}
