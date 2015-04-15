package com.lll.camera_l;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CameraVedio extends Activity {
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	private static final String TAG = "CameraVedio";  
    private SurfaceView surfaceView;  
    private MediaRecorder mMediaRecorder;  
    private boolean record;  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.vedio);  
          
        mMediaRecorder = new MediaRecorder();  
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);  
        /*下面设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到用户面前*/  
        this.surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
//        this.surfaceView.getHolder().setFixedSize(320, 240);//设置分辨率  
  
        ButtonClickListener listener = new ButtonClickListener();  
        Button stopButton = (Button) this.findViewById(R.id.stop);  
        Button recordButton = (Button) this.findViewById(R.id.record);  
        stopButton.setOnClickListener(listener);  
        recordButton.setOnClickListener(listener);          
    }  
      
    @Override  
    protected void onDestroy() {  
    	mMediaRecorder.release();  
        super.onDestroy();  
    }  
  
    private final class ButtonClickListener implements View.OnClickListener{  
        @Override  
        public void onClick(View v) {  
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  
                Toast.makeText(CameraVedio.this, "sdcarderror", 1).show();  
                return ;  
            }  
            try {  
                switch (v.getId()) {  
                case R.id.record:  
                	mMediaRecorder.reset();  
                	mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //从照相机采集视频  
                	mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);   
                	mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); 
                  
//                    mediaRecorder.setVideoSize(320, 240);  
//                    mediaRecorder.setVideoFrameRate(3); //每秒3帧  
                 
                	mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT); //设置视频编码方式  
                	mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);  
//                    File videoFile = new File(Environment.getExternalStorageDirectory(),
//                    		System.currentTimeMillis()+".3gp");  
//                    mediaRecorder.setOutputFile(videoFile.getAbsolutePath());  
                	mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
                    
                	mMediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());  
                	mMediaRecorder.prepare();//预期准备  
                	mMediaRecorder.start();//开始刻录  
                    Toast.makeText(getApplicationContext(), 
                    		"开始录制", Toast.LENGTH_SHORT).show();
                    record = true;  
                    break;  
  
                case R.id.stop:  
                    if(record){  
                    	mMediaRecorder.stop();  
                        record = false; 
                        Toast.makeText(getApplicationContext(), 
                        		"录制完成，已保存", Toast.LENGTH_SHORT).show();
                    }  
                    break;  
                }  
            } catch (Exception e) {  
                Toast.makeText(CameraVedio.this,"error", 1).show();  
                Log.e(TAG, e.toString());  
            }  
        }  
          
    } 
    
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

}  