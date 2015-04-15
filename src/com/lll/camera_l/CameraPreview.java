package com.lll.camera_l;


import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "PePe";
	private SurfaceHolder mHolder;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;


    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    
    public void setCamera(Camera camera)
    {

        mCamera = camera;
        if (mCamera != null)
        {
        	try {
                 mCamera.setPreviewDisplay(mHolder);
             } catch (IOException e) {
                 e.printStackTrace();
             }

            mSupportedPreviewSizes = mCamera.getParameters()
                    .getSupportedPreviewSizes();
            requestLayout();
            System.out.println("setCamera successs");
        }
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
    	System.out.println("surfaceCreated");
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            turnLightOn(mCamera);

        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    
    	
	public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
		 System.out.println("surfaceDestroyed"); 

//	        if (null != mCamera)
//	        {
////	        	mCamera.setPreviewCallbackWithBuffer(null);
//	        	mCamera.setPreviewCallback(null) ;
//	        	mCamera.stopPreview();
//	        	
//	        	mCamera.release();
//	        	 mCamera = null;
//	        }
//	        
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

    	 System.out.println("surface changed");
         // If your preview can change or rotate, take care of those events here.
         // Make sure to stop the preview before resizing or reformatting it.
    
         if (null == mHolder.getSurface())
         {
             // preview surface does not exist
             return;
         }

         // stop preview before making changes
         try
         {
             if (null != mCamera)
             {
                 mCamera.stopPreview();
             }
         }
         catch (Exception e)
         {
             // ignore: tried to stop a non-existent preview
         }

         // set preview size and make any resize, rotate or
         // reformatting changes here

         if (null != mCamera)
         {
             Camera.Parameters parameters = mCamera.getParameters();
             parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);

             requestLayout();

             mCamera.setParameters(parameters);
             mCamera.setDisplayOrientation(0);
             Log.d(TAG, "camera set parameters successfully!: "
                     + parameters);

         }
         // ���������4���óߴ�

         // start preview with new settings
         try
         {
             if (null != mCamera)
             {

                 mCamera.setPreviewDisplay(mHolder);
                 mCamera.startPreview();
                 turnLightOn(mCamera);
                 mCamera.lock();
                 System.out.println("camera locked!");
             }

         }
         catch (Exception e)
         {
             Log.d(TAG,
                     "Error starting camera preview: " + e.getMessage());
         }
     
    }
    
    public static void turnLightOn(Camera mCamera) {
  	  if (mCamera == null) {
  	   return;
  	  }
  	  Parameters parameters = mCamera.getParameters();
  
  	  if (parameters == null) {
  	   return;
  	  }
  	List<String> flashModes = parameters.getSupportedFlashModes();
  	  // Check if camera flash exists
  	  if (flashModes == null) {
  	   // Use the screen as a flashlight (next best thing)
  	   return;
  	  }
  	  String flashMode = parameters.getFlashMode();
  	  if (!Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
  	   // Turn on the flash
  	   if (flashModes.contains(Parameters.FLASH_MODE_TORCH)) {
  	    parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
  	    mCamera.setParameters(parameters);
  	   } else {
  	   }
  	  }
  	}
  	/**
  	  * ͨ关闭闪光灯
  	  */
  	public static void turnLightOff(Camera mCamera) {
  	  if (mCamera == null) {
  	   return;
  	  }
  	  Parameters parameters = mCamera.getParameters();
  	  if (parameters == null) {
  	   return;
  	  }
  	  List<String> flashModes = parameters.getSupportedFlashModes();
  	  String flashMode = parameters.getFlashMode();
  	  // Check if camera flash exists
  	  if (flashModes == null) {
  	   return;
  	  }
  	  if (!Parameters.FLASH_MODE_OFF.equals(flashMode)) {
  	   // Turn off the flash
  	   if (flashModes.contains(Parameters.FLASH_MODE_OFF)) {
  	    parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
  	    mCamera.setParameters(parameters);
  	   } else {
  	    Log.e(TAG, "FLASH_MODE_OFF not supported");
  	   }
  	  }
  	}

  	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null)
        {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width,
                    height);
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h)
    {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes)
        {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff)
            {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null)
        {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes)
            {
                if (Math.abs(size.height - targetHeight) < minDiff)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
  	
}
