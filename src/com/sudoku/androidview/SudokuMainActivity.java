package com.sudoku.androidview;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class SudokuMainActivity extends Activity implements OnClickListener{
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sudoku_main);
		// Listeners to Buttons
		View about_button = findViewById(R.id.about_button);
		about_button.setOnClickListener(this);
		
		View continue_button = findViewById(R.id.continue_button);
		continue_button.setOnClickListener(this);
		
		View import_button = findViewById(R.id.import_button);
		import_button.setOnClickListener(this);
		
		View exit_button = findViewById(R.id.exit_button);
		exit_button.setOnClickListener(this);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sudoku_main, menu);
		return true;
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.import_button:
				Intent in_import = new Intent(this,ImportGridActivity.class);
				startActivity(in_import);
				break;
			case R.id.continue_button:
				Intent in_continue = new Intent(this,GameActivity.class);
				startActivity(in_continue);
				break;
			case R.id.about_button:
				Intent in_about = new Intent(this,AboutActivity.class);
				startActivity(in_about);
				break;
			case R.id.exit_button:
				finish();
				break;
		}
		
	}

}
