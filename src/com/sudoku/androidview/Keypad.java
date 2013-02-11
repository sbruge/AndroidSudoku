package com.sudoku.androidview;

import com.sudoku.objects.SudokuData.Input;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public class Keypad extends Dialog{
	private final View[] keys = new View[10];
	private View keypad;
	private final GridView gridView;
	
	public Keypad(Context context, GridView gridView){
		super(context);
		this.gridView = gridView;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keypad);
		setTitle(R.string.keypad_title);
		buildViews();
		setListeners();
	}
	
	private void buildViews(){
		keypad = findViewById(R.id.keypad);
		keys[0] = findViewById(R.id.key_clear);
		keys[1] = findViewById(R.id.key_1);
		keys[2] = findViewById(R.id.key_2);
		keys[3] = findViewById(R.id.key_3);
		keys[4] = findViewById(R.id.key_4);
		keys[5] = findViewById(R.id.key_5);
		keys[6] = findViewById(R.id.key_6);
		keys[7] = findViewById(R.id.key_7);
		keys[8] = findViewById(R.id.key_8);
		keys[9] = findViewById(R.id.key_9);
	}
	
	private void setListeners(){
		for(int i=0; i<keys.length; i++){
			final int value=i;
			keys[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setTileValue(value);
					dismiss();
				}
			});
		}
	}
	
	private void setTileValue(int value){
		if(value == 0){
			gridView.getGame().getGrid().insertValue(gridView.getCol(), gridView.getRow(), value, Input.BLANK);
		}
		else{
			gridView.getGame().getGrid().insertValue(gridView.getCol(), gridView.getRow(), value, Input.USER);
		}
		
	}
	
	
}
