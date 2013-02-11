package com.sudoku.androidview;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.sudoku.imgprocess.FeatureExtractor;
import com.sudoku.imgprocess.GridPicture;
import com.sudoku.imgprocess.Sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

public class ImportGridActivity extends Activity{
	private static final String TAG = "ImportGrid";
	
	private GridPicture picture;
	private Bitmap pictureView;
	
	public ImportGridActivity(){
		Log.i(TAG,"Instanciate"+this.getClass());
	}
	
	private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("mixed_sample");
                    
                    //Build objects depending on opencv
                    ImageView imgContainer = (ImageView)findViewById(R.id.grid_view);
            		String path = Environment.getExternalStorageDirectory().getPath()+"/sudoku.png";
            		picture = new GridPicture(path);
            		//Mat im = picture.getPicture();
            		//Mat im = picture.extractAreas();
            		picture.extractAreas();
            		Mat im = picture.viewSample(0,4);
            		/*Sample samp = new Sample(im);
            		FeatureExtractor fe = new FeatureExtractor(samp);
            		im = fe.getNumber();*/
            		pictureView = Bitmap.createBitmap(im.cols(), im.rows(), Bitmap.Config.ARGB_8888);
            		Utils.matToBitmap(im, pictureView);
            		imgContainer.setImageBitmap(pictureView);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.import_grid);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

}
