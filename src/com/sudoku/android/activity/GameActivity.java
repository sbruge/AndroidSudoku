package com.sudoku.android.activity;


import com.sudoku.android.view.GridView;
import com.sudoku.android.view.Keypad;
import com.sudoku.objects.SudokuGrid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


public class GameActivity extends Activity{
	
	private final static String TAG = "Game";

	private SudokuGrid grid;
	private SudokuGrid gridSolve;
	private GridView gridView;
	private LinearLayout mainLayout;
	
    
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
		
		Button check = new Button(this);
		check.setText("Check");
		Button solve = new Button(this);
		solve.setText("Solve");
		
		mainLayout = new LinearLayout(this);
		mainLayout.setWeightSum(2);
		FrameLayout gameLayout = new FrameLayout(this);
		LinearLayout buttonLayout = new LinearLayout(this);
		LinearLayout.LayoutParams button_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
		LinearLayout.LayoutParams game_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
		gameLayout.setLayoutParams(game_params);
		buttonLayout.setLayoutParams(button_params);
		gameLayout.addView(gridView);		
		
		buttonLayout.setOrientation(LinearLayout.VERTICAL);
		buttonLayout.addView(check);
		buttonLayout.addView(solve);
		
		mainLayout.addView(buttonLayout);
		mainLayout.addView(gameLayout);

		gridView.setPadding(0, 50, 0, 0);
		setContentView(mainLayout);
		gridView.requestFocus();
		

		 check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gridView.activateCheckMode();
				gridView.invalidate();
				
			}
		});
		 
		 solve.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					gridView.activateSolveMode();
					gridView.invalidate();
					
				}
			});
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_activity, menu);
		return true;
	}



	public SudokuGrid getGrid(){
		return grid;
	}
	
	public SudokuGrid getSolution(){
		return gridSolve;
	}
	
	
	public void showKeypad(){
		Dialog v= new Keypad(this,gridView);
		v.show();
	}
	

}
