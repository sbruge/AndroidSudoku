package com.sudoku.androidview;

import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;


import com.sudoku.imgprocess.GridPicture;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class ImportGridActivity extends Activity {
	private static final String TAG = "ImportGrid";
	
	//private GridPicture grid;
	private CameraView cameraView;
	private Bitmap picture;
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
	
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
        cameraView = new CameraView(this);
        setContentView(cameraView);	 
        cameraView.setOnTouchListener(new OnTouchListener() {

        	@Override
        	public boolean onTouch(View v, MotionEvent event) {
        		Log.i("ontouch","touch!");
        		if(cameraView.getCamera()!=null){
        			boolean aquisitionSuccess = cameraView.takePicture();
        			if(aquisitionSuccess==true){
        				Intent intent = new Intent(ImportGridActivity.this,GameActivity.class);	
        				byte[] data = cameraView.getDataStored();
        				picture = BitmapFactory.decodeByteArray(data,0,data.length);
        				//picture.compress(format, quality, stream);
        				intent.putExtra("pictureData", picture);
        				Log.i("start ac","start activity");
        				startActivity(intent);
        				//cameraView.stopCamera();
        			}
        		}
        		else{
        			Log.i("ontouch","camera null");
        		}
        		return false;
        	}
		});
    }
     

    @Override
    public void onResume()
    {
    	Log.i(TAG,"resume");
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
    

	@Override
	protected void onPause() {
		super.onPause();
	}

}
