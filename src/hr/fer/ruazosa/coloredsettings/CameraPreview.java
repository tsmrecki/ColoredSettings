package hr.fer.ruazosa.coloredsettings;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * CameraPreview class, defines SurfaceHolder for camera view.
 * Instantiates DrawOnTop class.
 * @author Tomislav
 *
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    Camera mCamera;
    DrawOnTop mDrawOnTop;
    boolean mFinished;

    CameraPreview(Context context, DrawOnTop drawOnTop) {
        super(context);
        
        mDrawOnTop = drawOnTop;
        mFinished = false;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    /**
     * Initialize camera resource and values in DrawOnTop class
     */
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        try {
        	mCamera.setDisplayOrientation(90);
           mCamera.setPreviewDisplay(holder);
           
           // Preview callback used whenever new viewfinder frame is available
           mCamera.setPreviewCallback(new PreviewCallback() {
        	  public void onPreviewFrame(byte[] data, Camera camera)
        	  {
        		  if ( (mDrawOnTop == null) || mFinished )
        			  return;
        		  
        		  if (mDrawOnTop.mBitmap == null)
        		  {
        			  // Initialize the draw-on-top companion
        			  Camera.Parameters params = camera.getParameters();
        			  mDrawOnTop.mImageWidth = params.getPreviewSize().width;
        			  mDrawOnTop.mImageHeight = params.getPreviewSize().height;
        			  mDrawOnTop.mBitmap = Bitmap.createBitmap(mDrawOnTop.mImageWidth, 
        					  mDrawOnTop.mImageHeight, Bitmap.Config.RGB_565);
        			  mDrawOnTop.mRGBData = new int[mDrawOnTop.mImageWidth * mDrawOnTop.mImageHeight]; 
        			  mDrawOnTop.mYUVData = new byte[data.length];        			  
        		  }
        		  
        		  // Pass YUV data to draw-on-top companion
        		  System.arraycopy(data, 0, mDrawOnTop.mYUVData, 0, data.length);
    			  mDrawOnTop.invalidate();
        	  }
           });
        } 
        catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
    }
    
    /** Stops the camera preview by releasing it
     * 
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
 
    	mFinished = true;
    	mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        
    }
    
    /**
     * Sets parameters and starts the preview
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
     
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(320, 240);
        parameters.setPreviewFrameRate(15);
        parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
         
 
    }
    
   
    
}