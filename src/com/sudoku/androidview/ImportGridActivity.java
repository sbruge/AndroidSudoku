package com.sudoku.androidview;

import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.xmlpull.v1.XmlPullParserException;


import com.sudoku.database.Database;
import com.sudoku.imgprocess.GridPicture;
import com.sudoku.objects.SudokuGrid;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;

import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


public class ImportGridActivity extends Activity {
	private static final String TAG = "ImportGrid";
	
	//private GridPicture grid;
	private CameraView cameraView;
	private FrameLayout mainLayout;
	private RelativeLayout buttonLayout;
	private Bitmap picture;
	private boolean ready; 
	
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
        
        ready = false;
        cameraView = new CameraView(this);
        mainLayout = new FrameLayout(this);
        buttonLayout = new RelativeLayout(this);
        
        Button takePicture = new Button(this);
        takePicture.setText("Take Picture");
        Button importGrid = new Button(this);
        importGrid.setText("Import");
        
       RelativeLayout.LayoutParams paramsButton = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
       RelativeLayout.LayoutParams mainParams = new LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT);
       buttonLayout.setLayoutParams(mainParams);
       
       buttonLayout.addView(takePicture);
       buttonLayout.addView(importGrid);
       paramsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
       paramsButton.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
       takePicture.setLayoutParams(paramsButton);
       //importGrid.setLayoutParams(paramsButton);
       
       mainLayout.addView(cameraView);
       mainLayout.addView(buttonLayout);
       setContentView(mainLayout);
       
       takePicture.setOnTouchListener(new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				Log.i("event","push");
				AutoFocusCallback autofocusCb = new AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						if(success==true){
							Log.i("ontouch","touch!");
								ready = cameraView.takePicture();

						}
					}
				};
				cameraView.getCamera().autoFocus(autofocusCb);
				ready = true;
			}
			return false;
		}
       });
       
       importGrid.setOnTouchListener(new OnTouchListener() {

    	   @Override
    	   public boolean onTouch(View v, MotionEvent event) {
    		   Log.i("event",String.valueOf(ready));
    		   if(ready==true){
    			   Database db = new Database();
    			   try {
    				   InputStream is = getAssets().open("database.xml");
    				   try {
    					   Log.i("db","loading database...");
    					   db.loadXmlDb(is);
    				   } catch (XmlPullParserException e) {
    					   Log.e("db","error in loading");
    					   e.printStackTrace();
    				   }
    			   } catch (IOException e1) {
    				   // TODO Auto-generated catch block
    				   e1.printStackTrace();
    			   }

    			   Intent intent = new Intent(ImportGridActivity.this,GameActivity.class);	
    			   byte[] data = cameraView.getDataStored();
    			   picture = BitmapFactory.decodeByteArray(data,0,data.length);
    			   GridPicture gridPicture = new GridPicture(picture,db);
    			   SudokuGrid grid = gridPicture.buildGame();
    			   intent.putExtra("sudokuGrid", grid);
    			   Log.i("start ac","start activity");
    			   startActivity(intent);
    			   ready=false;
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
