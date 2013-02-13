package com.sudoku.androidview;

import java.io.IOException;




import com.sudoku.imgprocess.GridPicture;


import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class ImportGridActivity extends Activity implements SurfaceHolder.Callback{
	private static final String TAG = "ImportGrid";
	
	private GridPicture picture;
	private Bitmap pictureView;
	private Camera camera;
	private SurfaceView surfaceCamera;
	private Boolean isPreview;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
        isPreview = false;
        setContentView(R.layout.import_grid);
        surfaceCamera = (SurfaceView) findViewById(R.id.surfaceViewCamera);       
        initializeCamera();
    }
    
    public void initializeCamera(){
    	surfaceCamera.getHolder().addCallback(this);
    	surfaceCamera.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        camera = Camera.open();
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
    

	@Override
	protected void onPause() {
		super.onPause();
		if(camera!=null){
			camera.stopPreview();
			camera.release();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if(isPreview){
			camera.stopPreview();

			Camera.Parameters parameters = camera.getParameters();
			parameters.setPreviewSize(width, height);
			camera.setParameters(parameters);
			try {
				camera.setPreviewDisplay(surfaceCamera.getHolder());
			} 
			catch (IOException e) {
			}

			camera.startPreview();
			isPreview = true;
		}

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(camera==null){
			camera= Camera.open();
		}
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(camera!=null){
			camera.stopPreview();
			camera.release();
			isPreview = false;
		}
		
	}
    
    

}
