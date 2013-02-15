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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class GameActivity extends Activity{
	
	private final static String TAG = "Game";

	private SudokuGrid grid;
	private GridView gridView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent!=null){
			grid = intent.getParcelableExtra("sudokuGrid");
			if(grid!=null){
				gridView = new GridView(this);
			}
			else{
				Log.e("gridpicture","no data to load");
			}
		}
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
