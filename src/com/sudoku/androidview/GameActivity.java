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
	private Bitmap picture;
	private SudokuGrid grid;
	private GridView gridView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent!=null){
			byte[] data = intent.getByteArrayExtra("pictureData");
			if(data!=null){
				Log.i(TAG,"build bmp from data");
				picture = BitmapFactory.decodeByteArray(data,0,data.length);
			}
			else{
				Log.e("gridpicture","no data in bmp");
			}
		}
		GridPicture gridPicture = new GridPicture(picture);
		grid = gridPicture.buildGame();
		
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
