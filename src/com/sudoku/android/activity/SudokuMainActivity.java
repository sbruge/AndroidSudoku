package com.sudoku.android.activity;

import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.xmlpull.v1.XmlPullParserException;

import com.sudoku.database.Database;
import com.sudoku.imgprocess.GridPicture;
import com.sudoku.objects.SudokuGrid;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class SudokuMainActivity extends Activity implements OnClickListener{
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("loading opCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
	

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
	protected void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.import_button:
				Intent in_import = new Intent(this,ImportGridActivity.class);
				startActivity(in_import);
				break;
			case R.id.continue_button:
				Database db = new Database();
                try {
					InputStream is = getAssets().open("database.xml");
					try {
						db.loadXmlDb(is);
					} catch (XmlPullParserException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 GridPicture gp = new GridPicture("/mnt/sdcard/perspective.jpg",db);
                 SudokuGrid grid = gp.buildGame();
				Intent in_continue = new Intent(this,GameActivity.class);
				in_continue.putExtra("sudokuGrid", grid);
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
