package com.sudoku.androidview;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import com.sudoku.imgprocess.GridPicture;

import android.app.Activity;
import android.graphics.Bitmap;
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
                    GridPicture gp = new GridPicture("/mnt/sdcard/sudoku.jpg");
                    Mat m=gp.showAreas();
                    picture = Bitmap.createBitmap(m.cols(), m.rows(), Config.ARGB_8888); 
                    Utils.matToBitmap(m, picture);
                    ImageView view = (ImageView) findViewById(R.id.img_view);
            		view.setImageBitmap(picture);
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
