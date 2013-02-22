package com.sudoku.androidview;

import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.sudoku.database.Database;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GameActivity extends Activity{
	
	private final static String TAG = "Game";

	private SudokuGrid grid;
	private SudokuGrid gridSolve;
	private GridView gridView;
	
	private FrameLayout mainLayout;
	private LinearLayout buttonLayout;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent!=null){
			grid = intent.getParcelableExtra("sudokuGrid");
			if(grid!=null){
				gridSolve = new SudokuGrid(grid);
				gridSolve.remplir();
				gridView = new GridView(this);
			}
			else{
				Log.e("gridpicture","no data to load");
			}
		}
		gridView.setPadding(0, 50, 0, 0);
		mainLayout = new FrameLayout(this);
        buttonLayout = new LinearLayout(this);
        
        Button check = new Button(this);
        check.setText("check");
        Button solve= new Button(this);
        solve.setText("solve");
        
        buttonLayout.addView(check);
        buttonLayout.addView(solve);
        
        mainLayout.addView(gridView);
        mainLayout.addView(buttonLayout);
        
		setContentView(mainLayout);
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
