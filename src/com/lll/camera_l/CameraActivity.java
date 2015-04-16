package com.lll.camera_l;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.lll.camera_l.R.id;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity 
		implements Callback{

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	private int mNumberOfCameras;
    private int mCameraCurrentlyLocked;

    // The first rear facing camera
    private int mDefaultCameraId;

    private int mScreenWidth, mScreenHeight;
    public static final String TAG = "mmmm";
	private Camera mCamera;
	private SurfaceHolder mHolder;
	
	Size mPreviewSize;
	List<Size> mSupportedPreviewSizes;
    private MediaRecorder mMediaRecorder;
    long waitTime = 2000;    
	long touchTime = 0;  
	private int checkItem;
	Button vedio;
	Button captureButton;
	Button light;
	private boolean isRecording = false;
	SurfaceView preview;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		 // �õ���Ļ�Ĵ�С
        WindowManager wManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wManager.getDefaultDisplay();
        mScreenHeight = display.getHeight();
        mScreenWidth = display.getWidth();
        MyClickListener myListener = new MyClickListener();
        
//        mCamera = getCameraInstance(mCameraCurrentlyLocked);
        // Create our Preview view and set it as the content of our activity.
        preview = (SurfaceView) findViewById(R.id.camera_preview);
        
        preview.setOnClickListener(myListener);
        captureButton = (Button) findViewById(R.id.button_capture);
        vedio = (Button)findViewById(R.id.vedio);
        light = (Button)findViewById(R.id.light);
       
        captureButton.setOnClickListener(myListener);

        vedio.setOnClickListener(myListener);//vedio
        light.setOnClickListener(myListener);
        mHolder = preview.getHolder(); 
        
        mHolder.addCallback(this); 
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
//      
        mDefaultCameraId = getDefaultCameraId();
        mCameraCurrentlyLocked = mDefaultCameraId;
//        System.out.println("mCameraCurrentlyLocked "+mCameraCurrentlyLocked);
        
        
	}
	 class MyClickListener implements OnClickListener, AutoFocusCallback{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 switch (v.getId()) {
			 case R.id.button_capture:
				  mCamera.takePicture(null, null, mPicture);
	            Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_SHORT).show();
	            break;
			 case R.id.camera_preview:
				 mCamera.autoFocus(this);//自动聚焦
				 break;
			 case R.id.vedio:
				Intent intent=new Intent();
				intent.setClass(CameraActivity.this, CameraVedio.class);
				startActivity(intent);
					break;
			 case R.id.light:
				 turnLightOn(mCamera);
				 if(light.getText()=="On"){
					 light.setText("Off");
				 }else{ 
					turnLightOff(mCamera);
						 light.setText("On");
				 }
				 break;
				 default:
					 break;
			 }
		}

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if(success)  
            {  
         	    params = camera.getParameters();
                params.setRotation(90);  
                camera.setParameters(params);//���������õ��ҵ�camera
                camera.setDisplayOrientation(90);  
                }
			
		}

		
		 
	 }
	
	
	 //˫���˳�
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if(event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {    
//	        long currentTime = System.currentTimeMillis();    
//	        if((currentTime-touchTime)>=waitTime) {    
////	        	mPreview.turnLightOff(mCamera);
//	            Toast.makeText(this, "再按一次退出", (int)waitTime).show();    
//	            touchTime = currentTime;    
//	        }else { 
	        	mCamera.setPreviewCallback(null) ;
	        	mCamera.stopPreview();
	        
	        	mCamera.release();
	        	mCamera = null;
	        	System.out.println("camera release!");
	        	System.exit(0);
//	        }    
        return true;    
	    }    
	    return super.onKeyDown(keyCode, event);    
	}
	
	
	
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		 super.onCreateOptionsMenu(menu);
		 getMenuInflater().inflate(R.menu.main,menu);
		 return true;
		
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
	    {
	    case R.id.setScreenbright:
	    	selectScreenBright();
	    	//Toast.makeText(ColorLightActivity.this, "���ڲ˵�", Toast.LENGTH_LONG).show();
	    	return true;
	    }
	    return false;
	    
	}

	private void selectScreenBright() {
		final String[] items = {"100%", "75%", "50%","25%"}; 
    	new AlertDialog.Builder(this) 
    	.setTitle("屏幕亮度") 
    	.setSingleChoiceItems(items, checkItem, new DialogInterface.OnClickListener() { //�˴�����Ϊѡ����±꣬��0��ʼ�� ��ʾĬ�����ѡ�� 
    	public void onClick(DialogInterface dialog, int item) { 
    	Toast.makeText(getApplicationContext(), items[item],Toast.LENGTH_SHORT).show(); 
    	checkItem=item;
    	switch (item) {
		case 0:
			SetScreenBright(1.0F);
			break;
		case 1:
			SetScreenBright(0.75F);
			break;
		case 2:
			SetScreenBright(0.5F);
			break;
		case 3:
			SetScreenBright(0.25F);
			break;
		default:
			SetScreenBright(1.0F);
			break;
		}	
    	dialog.cancel(); 
    	}

    	private void SetScreenBright(float l) {
			WindowManager.LayoutParams lp=getWindow().getAttributes();
	    	lp.screenBrightness = l;
	    	getWindow().setAttributes(lp);
			
		} 
    	}).show();//��ʾ�Ի��� 
    
		
	}

	private int getDefaultCameraId()
	    {
	        int defaultId = -1;

	        // Find the total number of cameras available
	        mNumberOfCameras = Camera.getNumberOfCameras();

	        // Find the ID of the default camera
	        CameraInfo cameraInfo = new CameraInfo();
	        for (int i = 0; i < mNumberOfCameras; i++)
	        {
	            Camera.getCameraInfo(i, cameraInfo);
	            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)
	            {
	                defaultId = i;
	                System.out.println("defaultId " +defaultId);
	               
	            }
	        }
	        if (-1 == defaultId)
	        {
	            if (mNumberOfCameras > 0)
	            {
	                // ���û�к�������ͷ
	                defaultId = 0;
	            }
	            else
	            {
	                // û������ͷ
	                Toast.makeText(getApplicationContext(), R.string.no_camera,
	                        Toast.LENGTH_LONG).show();
	            }
	        }
	        return defaultId;
	    }
	 
	 
	/** Check if this device has a camera */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        // this device has a camera
	        return true;
	    } else {
	        // no camera on this device
	        return false;
	    }
	}

	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(int cameraId){
	    Camera c = null;
	    try {
	        c = Camera.open(cameraId); // attempt to get a Camera instance
	        System.out.println("camera opened!");
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	
	private PictureCallback mPicture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {

	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
//	            Log.d(TAG, "Error creating media file, check storage permissions: " +
//	                e.getMessage());
	            return;
	        }

	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	        } catch (FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }

		    // ���պ����¿�ʼԤ��
	        mCamera.stopPreview();
	        mCamera.startPreview();
	    }
	    
	};
	
	
	private boolean prepareVideoRecorder() {
		
//		mCamera.startPreview();
	    System.out.println("mCamera" +mCamera);
	    mMediaRecorder = new MediaRecorder();
	    System.out.println("prepareVideoRecorder");
	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    System.out.println("camera unlock");
	    
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
	    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); 
	    
	    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
	    mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
	    
	    // Step 4: Set output file
	    mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}
	
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	
	  @Override
	    protected void onPause() {
	        super.onPause();
	        System.out.println("onPause");
	        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
	        releaseCamera();              // release the camera immediately on pause event
	    }

	    private void releaseMediaRecorder(){
	        if (mMediaRecorder != null) {
	            mMediaRecorder.reset();   // clear recorder configuration
	            mMediaRecorder.release();// release the recorder object
	            System.out.println("realese the mediarecorder");
	            mMediaRecorder = null;
	            mCamera.lock();           // lock camera for later use
	        }
	    }

	    private void releaseCamera(){
	        if (mCamera != null){
	            mCamera.release();        // release the camera for other applications
	           System.out.println("camera release");
	            mCamera = null;
	        }
	    }
	    
	    
	    Parameters params ;

		private Camera.Size cs;
		private float psizeheight;
		private float psizewidth;
		private float sizeheight;
		private float sizewidth;
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
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
	             params = mCamera.getParameters();
//	             params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
	//
//	             requestLayout();
	             List<Camera.Size> psizes = params.getSupportedPictureSizes(); 
		            Camera.Size pcs = (Camera.Size) psizes.get(0); 
		            psizeheight = pcs.height; 
		            psizewidth = pcs.width;
		            params.setPictureSize((int)psizewidth, (int)psizeheight);
		            float n = psizeheight/psizewidth;
		          List<Camera.Size> sizes = params.getSupportedPreviewSizes(); 
		         for (int i = 0; i < sizes.size(); i++) { 
		            Camera.Size cs = (Camera.Size) sizes.get(i); 
		             sizeheight = cs.height; 
		             sizewidth = cs.width; 
		             if(n == (sizeheight/ sizewidth)){
		            	params.setPreviewSize((int)sizewidth, (int)sizeheight);
		            	break;
		             }
		         }
	             params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	             params.setRotation(90);
	             mCamera.setParameters(params);
	             mCamera.setDisplayOrientation(90);
	             Log.d(TAG, "camera set params successfully!: "
	                     + params);

	         }
	         // ���������4���óߴ�

	         // start preview with new settings
	         try
	         {
	             if (null != mCamera)
	             {

	                 mCamera.setPreviewDisplay(mHolder);
	                 mCamera.startPreview();
//	                 turnLightOn(mCamera);
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



		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			System.out.println("surfaceCreated");
	        // The Surface has been created, now tell the camera where to draw the preview.
	    	 if(mCamera == null)  
	         {  
	              mCamera = Camera.open(); 
	    	      params = mCamera.getParameters();
	    	      List<Camera.Size> psizes = params.getSupportedPictureSizes(); 
		            Camera.Size pcs = (Camera.Size) psizes.get(0); 
		            psizeheight = pcs.height; 
		            psizewidth = pcs.width;
		            params.setPictureSize((int)psizewidth, (int)psizeheight);
		            float n = psizeheight/psizewidth;
		          List<Camera.Size> sizes = params.getSupportedPreviewSizes(); 
		         for (int i = 0; i < sizes.size(); i++) { 
		            Camera.Size cs = (Camera.Size) sizes.get(i); 
		             sizeheight = cs.height; 
		             sizewidth = cs.width; 
		             if(n == (sizeheight/ sizewidth)){
		            	params.setPreviewSize((int)sizewidth, (int)sizeheight);
		            	break;
		             }
		         }
//	    	      params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
	              mCamera.setParameters(params);
	    	   }
	    	try {
	        	
	            mCamera.setPreviewDisplay(holder);
	            mCamera.startPreview();
//	            turnLightOn(mCamera);

	        } catch (IOException e) {
	            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
	        }
			
		}



		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			 System.out.println("surfaceDestroyed"); 
			
		}
	   
		public static void turnLightOn(Camera mCamera) {
		  	  if (mCamera == null) {
		  	   return;
		  	  }
		  	  Parameters params = mCamera.getParameters();
		  
		  	  if (params == null) {
		  	   return;
		  	  }
		  	List<String> flashModes = params.getSupportedFlashModes();
		  	  // Check if camera flash exists
		  	  if (flashModes == null) {
		  	   // Use the screen as a flashlight (next best thing)
		  	   return;
		  	  }
		  	  String flashMode = params.getFlashMode();
		  	  if (!params.FLASH_MODE_TORCH.equals(flashMode)) {
		  	   // Turn on the flash
		  	   if (flashModes.contains(params.FLASH_MODE_TORCH)) {
		  	    params.setFlashMode(params.FLASH_MODE_TORCH);
		  	    mCamera.setParameters(params);
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
		  	Parameters params = mCamera.getParameters();
		  	  if (params == null) {
		  	   return;
		  	  }
		  	  List<String> flashModes = params.getSupportedFlashModes();
		  	  String flashMode = params.getFlashMode();
		  	  // Check if camera flash exists
		  	  if (flashModes == null) {
		  	   return;
		  	  }
		  	  if (!params.FLASH_MODE_OFF.equals(flashMode)) {
		  	   // Turn off the flash
		  	   if (flashModes.contains(params.FLASH_MODE_OFF)) {
		  	    params.setFlashMode(params.FLASH_MODE_OFF);
		  	    mCamera.setParameters(params);
		  	   } else {
		  	    Log.e(TAG, "FLASH_MODE_OFF not supported");
		  	   }
		  	  }
		  	}

}
