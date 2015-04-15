package com.lll.camera_l;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity {

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	private int mNumberOfCameras;
    private int mCameraCurrentlyLocked;

    // The first rear facing camera
    private int mDefaultCameraId;

    private int mScreenWidth, mScreenHeight;
    public static final String TAG = "mmmm";
	private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
    long waitTime = 2000;    
	long touchTime = 0;  
	private int checkItem;
	Button start;
	Button captureButton;
	private boolean isRecording = false;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		 // �õ���Ļ�Ĵ�С
        WindowManager wManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wManager.getDefaultDisplay();
        mScreenHeight = display.getHeight();
        mScreenWidth = display.getWidth();

//        mCamera = getCameraInstance(mCameraCurrentlyLocked);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        
        captureButton = (Button) findViewById(id.button_capture);
        start = (Button)findViewById(R.id.start);
        captureButton.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    mCamera.takePicture(null, null, mPicture);
                    Toast.makeText(getApplicationContext(), "Yes", Toast.LENGTH_SHORT).show();
                }
            }
        );
        start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(CameraActivity.this, CameraVedio.class);
				startActivity(intent);
			}
		});
	 
        
//        start.setOnClickListener(
//        	    new View.OnClickListener() {
//        	        @Override
//        	        public void onClick(View v) {
//        	            if (isRecording) {
//        	            	//��ӵ�stopPreview
//        	            	mCamera.stopPreview();
//        	                // stop recording and release camera
//        	                mMediaRecorder.stop();  // stop the recording
//        	                releaseMediaRecorder(); // release the MediaRecorder object
//        	                mCamera.lock();         // take camera access back from MediaRecorder
//
//        	                // inform the user that recording has stopped
//        	                setStartButtonText("Start");
//        	                isRecording = false;
//        	            } else {
//        	                // initialize video camera
//        	                if (prepareVideoRecorder()) {
//        	                	try {
//									mMediaRecorder.prepare();
//								} catch (IllegalStateException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} catch (IOException e) {
//						
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//        	                    // Camera is available and unlocked, MediaRecorder is prepared,
//        	                    // now you can start recording
//        	                    mMediaRecorder.start();
//        	                    Toast.makeText(getApplicationContext(), "��ʼ¼��", Toast.LENGTH_SHORT).show();
//        	                    System.out.println("��ʼ¼��");
//        	                    // inform the user that recording has started
//        	                    setStartButtonText("Stop");
//        	                    isRecording = true;
//        	                } else {
//        	                    // prepare didn't work, release the camera
//        	                    releaseMediaRecorder();
//        	                    // inform user
//        	                }
//        	            }
//        	        }
//
//					private void setStartButtonText(String string) {
//						// TODO Auto-generated method stub
//						start.setText(string);
//					}
//        	    }
//        	);
        // �õ�Ĭ�ϵ����ID
        mDefaultCameraId = getDefaultCameraId();
        mCameraCurrentlyLocked = mDefaultCameraId;
//        System.out.println("mCameraCurrentlyLocked "+mCameraCurrentlyLocked);
        
	}

	 //˫���˳�
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if(event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {    
	        long currentTime = System.currentTimeMillis();    
	        if((currentTime-touchTime)>=waitTime) {    
	          //  Toast����ʾʱ��͵ȴ�ʱ����ͬ  
	            Toast.makeText(this, "再按一次退出", (int)waitTime).show();    
	            touchTime = currentTime;    
	        }else { 
	        	mCamera.setPreviewCallback(null) ;
	        	mCamera.stopPreview();
	        
	        	mCamera.release();
	        	mCamera = null;
	        	System.out.println("camera release!");
	        	System.exit(0);
	        }    
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
	    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

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
	
	//ԭ4API��û����д����
	 @Override
	    protected void onResume()
	    {
	        
	        super.onResume();
	        System.out.println("onResume");

	        // Open the default i.e. the first rear facing camera.
	        mCamera = getCameraInstance(mCameraCurrentlyLocked);
	        
	        mPreview.setCamera(mCamera);
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
	   

}
