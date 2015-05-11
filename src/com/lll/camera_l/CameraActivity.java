package com.lll.camera_l;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lll.camera_l.R.id;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

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
	ImageButton vedio;
	ImageButton captureButton;
	ImageButton light;
	ImageButton upload;
	ImageButton menu;
	private boolean isRecording = false;
	private boolean lighton = false;
	SurfaceView preview;
    LinearLayout layout_myview;
//  private static UserManager mUserManager;
    
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
        captureButton = (ImageButton) findViewById(R.id.button_capture);
//        vedio = (ImageButton)findViewById(R.id.vedio);
        upload = (ImageButton)findViewById(R.id.Upload);
        light = (ImageButton)findViewById(R.id.light);
        menu = (ImageButton)findViewById(R.id.menu);
        layout_myview = (LinearLayout)findViewById(R.id.layout_myview);
        
        captureButton.setOnClickListener(myListener);
        menu.setOnClickListener(myListener);
//        vedio.setOnClickListener(myListener);//vedio
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
//			 case R.id.vedio:
//				Intent intent=new Intent();
//				intent.setClass(CameraActivity.this, CameraVedio.class);
//				startActivity(intent);
//				break;
				 
			 case R.id.Upload:
				 System.out.println("一键上传至##邮箱。。。未完成");
				 break;
			 case R.id.light:
				 
				 if(!lighton){
					 turnLightOn(mCamera);
					 lighton = true;
					 light.setBackgroundResource(R.drawable.c32);
					 
				 }else{
					 lighton = false;
					 turnLightOff(mCamera);
					 light.setBackgroundResource(R.drawable.c31); 
				 }
				 break;
			 case R.id.menu:
				 initListView();
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
	
	
	 	LayoutInflater inflater = null;
	 	 View view = null;
		List<Map<String, String>> list = null;
		 ListView listViewMenu;
		 View fileView;
		 ListView fileListView;
		 TextView file_item;
		AlertDialog menuDialog;
		AlertDialog filesNameDialog;
		 TextView menu_item;
		private EditText name;  
		private EditText age; 
		private EditText sex; 
		private static String namestring;
		private static String agestring;
		private static String sexstring;
		
		
		private void initListView() {
			
			inflater = LayoutInflater.from(this);
			view = inflater.inflate(R.layout.customlistview, null);
			listViewMenu = (ListView) view.findViewById(R.id.mylistview);
			
			list = new ArrayList<Map<String, String>>();
			Map<String, String> map = new HashMap<String,String>();
			
			map.put("item", this.getString(R.string.create));
			list.add(map);
			
			map = new HashMap<String, String>();
			map.put("item", this.getString(R.string.delete));
			list.add(map);
			
			map = new HashMap<String, String>();
			map.put("item", this.getString(R.string.help));
			list.add(map);
			
			SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.item, new String[] { "item"}, new int[] { R.id.menu_item });
			listViewMenu.setAdapter(adapter);
			listViewMenu.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					switch(position){
					case 0:
						createNewUser();
						break;
					case 1:
						deleteUsers();
						break;
					case 2:
						Toast.makeText(getApplicationContext(), "help~~", Toast.LENGTH_SHORT).show();
						break;
						default:
							break;
					}
					
				}
				
			});
			
			// 创建AlertDialog
						menuDialog = new AlertDialog.Builder(this).create();
						menuDialog.setView(view);
						//设置对话框的显示位置
						Window window = menuDialog.getWindow();     
						window.setGravity(Gravity.TOP);   //window.setGravity(Gravity.BOTTOM);  
						    
						menuDialog.show();
						menuDialog.setOnKeyListener(new OnKeyListener() {
							public boolean onKey(DialogInterface dialog, int keyCode,
									KeyEvent event) {
								if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
									dialog.dismiss();
								return false;
							}
						});
						
		}

		public void createNewUser(){
			
			 TableLayout info_user = (TableLayout)getLayoutInflater().inflate(R.layout.user, null);
			 name = (EditText)info_user.findViewById(R.id.name);
			 age = (EditText)info_user.findViewById(R.id.age);
			 sex = (EditText)info_user.findViewById(R.id.sex);
			 
			 new AlertDialog.Builder(this).setView(info_user)
			 .setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog,int which) {
					namestring = name.getText().toString().trim();
					agestring = age.getText().toString();
					sexstring = sex.getText().toString();
					newUsers = true;
				}
				 
			 })
			 .setNegativeButton("取消", new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int which) {
					//取消新建用户，不做任何事情。
					
				}
				 
			 }).create().show();
				 
			
		 }

		private static File[] currentFiles;
		private static int dposition;
		private SimpleAdapter sa;
		public void deleteUsers(){
			
			//制定目录下所有新建用户文件名
			//首先是要得到music文件的路径
			File file= new File(Environment.getExternalStoragePublicDirectory(
		              Environment.DIRECTORY_DCIM).getPath());
			 if (!file.exists()){
			        if (!file.mkdirs()){
			            Log.d("file", "failed to create directory");
			        }
			    }
			List<Map<String,Object>> list= new ArrayList<Map<String,Object>>();
			inflater = LayoutInflater.from(this);
			fileView = inflater.inflate(R.layout.filelistview, null);
			fileListView = (ListView) fileView.findViewById(R.id.filelistview);
			
			currentFiles = file.listFiles(new customfilter());
			//将所有的文件加入到一个list文件中
			if(file.list(new customfilter()).length>0){
				
				for (int i = 0; i < currentFiles.length; i++){
					Map<String,Object> map=new HashMap<String, Object>();
					String s = currentFiles[i].getName();
				   map.put("filename",s);
				   list.add(map);
				}
			}
			 sa= new SimpleAdapter(this, list, 
					R.layout.fileitem, new String[]{"filename"}, new int[]{R.id.file_item} );
			fileListView.setAdapter(sa);
			filesNameDialog = new AlertDialog.Builder(this).create();
			filesNameDialog.setView(fileView);
			//设置对话框的显示位置
			Window window = filesNameDialog.getWindow();     
			window.setGravity(Gravity.TOP);   //window.setGravity(Gravity.BOTTOM);  
			    
			filesNameDialog.show();
			filesNameDialog.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
						dialog.dismiss();
					return false;
				}
			});
			
			fileListView.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//判断是不是文件夹
					boolean a = currentFiles[position].isDirectory();
					if(a){
						//弹出对话框，问是否删除这个文件？
						dposition = position;
						deleteuserDialog();
						//添加下面这句之后，就成功通知listview删除文件夹并且图库中文件缩略图也没有了
						sa.notifyDataSetChanged();
					}
				}
				
			});
		
			
		}
		
		public void deleteuserDialog(){
			LinearLayout delete_user = (LinearLayout)getLayoutInflater().inflate(R.layout.isdelete, null);
			 
			 new AlertDialog.Builder(this)
			.setView(delete_user)
			.setPositiveButton("Yes", new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					//没能完全删除是因为这里是文件夹，要删除所有的文件才能删除文件夹，删除所有内容就可以了
						String[] childs = currentFiles[dposition].list();  
						if (childs == null || childs.length == 0) {  
						         currentFiles[dposition].delete();  
						          return;  
						 }  
						for (int i = 0; i < childs.length; i++) {  
							new File(currentFiles[dposition], childs[i]).delete(); 
//							getContentResolver.delete(Media.EXTERNAL_CONTENT_URI, Media.DATA + "=?",Environment.getExternalStoragePublicDirectory(
//						              Environment.DIRECTORY_DCIM).getPath());
						}  
						currentFiles[dposition].delete();  
				}
				 
			 })
			 .setNegativeButton("No", new android.content.DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int which) {
				}
				 
			 }).create().show();
		}
		
		// 用来去筛选出特定的文件夹
		class customfilter implements FilenameFilter {
			/*
			 * accept方法的两个参数的意义： dir：文件夹对像，也就是你原来调用list方法的File文件夹对像 name：当前判断的文件名，
			 * 这个文件名就是文件夹下面的文件
			 * 返回：这个文件名是否符合条件，当为true时，list和listFiles方法会把这个文件加入到返回的数组里，false时则不会加入
			 */
			public boolean accept(File dir, String filename) {
				// TODO Auto-generated method stub
				return (filename.endsWith("files"));
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
	
	
	
//	 @Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		 super.onCreateOptionsMenu(menu);
//		 getMenuInflater().inflate(R.menu.main,menu);
//		 return true;
//		
//	}
//
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		switch(item.getItemId())
//	    {
//	    case R.id.setScreenbright:
//	    	selectScreenBright();
//	    	//Toast.makeText(ColorLightActivity.this, "���ڲ˵�", Toast.LENGTH_LONG).show();
//	    	return true;
//	    }
//	    return false;
//	    
//	}
	
	//设置屏幕亮度，实际上并没有用到该方法
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
    	}).show();
    
		
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

	      //刷新图库
	        Intent intent = new Intent(
	                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	        Uri uri = Uri.fromFile(mediaStorageDir);
	        intent.setData(uri);
	        sendBroadcast(intent);
	        
		    // 重新预览
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

	private static File mediaStorageDir;
	private static boolean newUsers = false;
	//存储目录在系统目录中dcim/MyCameraApp
	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.
		if(!newUsers){
	     mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_DCIM), "MyCameraApp"+"_"+"files");
	     }else{
	    	 mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
		              Environment.DIRECTORY_DCIM), namestring+"_"+sexstring+"_"+agestring+"_"+"files");
	     }
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("mediaStorageDir", "failed to create directory");
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
