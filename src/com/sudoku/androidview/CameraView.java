package com.sudoku.androidview;


import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.PictureDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{
	
	private Camera mCamera;
	private SurfaceHolder holder;
	private byte[] dataStored;
	
	public CameraView(Context context) {
		super(context);
		// Installe le SurfaceHolder.Callback pour prevenir quand la surface est créée ou détruite
        holder = getHolder();
        holder.addCallback(this);
       //type de la surface 
       holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	public Camera getCamera(){
		return mCamera;
	}
	
	byte[] getDataStored(){
		return dataStored;
	}
	
	public void stopCamera(){
		if(mCamera!=null){
			mCamera.stopPreview();
			mCamera.release();
			mCamera=null;
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
          mCamera = Camera.open();
          mCamera.getParameters().setPictureFormat(PixelFormat.JPEG);
        try {
          mCamera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;

        }
    }
 
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    	Camera.Parameters parameters = mCamera.getParameters();
    	if(w>h){
    		parameters.setPreviewSize(w, h);
    	}
    	else{
    		parameters.setPreviewSize(h, w);
    		mCamera.setDisplayOrientation(270);
    	}
    	mCamera.setParameters(parameters);
    	mCamera.startPreview();
    }
    
    public boolean takePicture(){
    	Log.i("getPicture", "try to get data");
    	PictureCallback camCallback = new PictureCallback() {
    		@Override
    		public void onPictureTaken(byte[] data, Camera camera) {
    			Log.i("takepic","take picture");
    			// TODO Auto-generated method stub
    			if (data != null) {
    				Log.i("arg", "bmp ok!!");
    				dataStored = data;
    			}
    			else{
    				Log.i("arg", "bmp empty!!");
    			}
    		}
    	};
    	mCamera.takePicture(null,camCallback,camCallback);
    	return dataStored!=null;
    }
}

