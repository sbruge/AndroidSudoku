package com.sudoku.android.activity;

import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.xmlpull.v1.XmlPullParserException;

import com.sudoku.database.Database;
import com.sudoku.imgprocess.GridPicture;
import com.sudoku.objects.SudokuGrid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class TestResult extends Activity{
	
	private Bitmap picture;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("loading opCV", "OpenCV loaded successfully");
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
                    
                    // View Img
                    GridPicture gp = new GridPicture("/mnt/sdcard/perspective.jpg",db);
                    SudokuGrid grid = gp.buildGame();
                    /*Mat m=gp.getPicture();
                    picture = Bitmap.createBitmap(m.cols(), m.rows(), Config.ARGB_8888); 
                    Utils.matToBitmap(m, picture);
                    ImageView view = (ImageView) findViewById(R.id.img_view);
            		view.setImageBitmap(picture);*/
            		
            		//Numerise
                    Intent intent = new Intent(TestResult.this,GameActivity.class);	
     			   intent.putExtra("sudokuGrid", grid);
     			   Log.i("start ac","start activity");
     			   startActivity(intent);
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
	}

	@Override
	protected void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	}
	
	
	
	
}
