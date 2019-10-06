package com.hnu.anew;



import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class VideoActivity extends AppCompatActivity {

    /**
     * @author Wang YingAo
     * @projectname DoraemonOfHNU
     * @param MSG_AUTOFUCS 1001
     * @param autoFocusCallback  自动对焦回调
     * @param mCamera 相机
     * @param videoview 底层相机预览
     * @param handler 自动对焦处理机制
     */
    private static final int MSG_AUTOFUCS = 1001;
    private static final String TAG = "wang";
    AutoFocusCallbackImpl autoFocusCallback;

    private Camera mCamera;
    private SurfaceView videoview;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.v("zzw",""+msg.what);
                switch (msg.what){
                    case MSG_AUTOFUCS:
                        mCamera.autoFocus(autoFocusCallback);
                        break;
                }
            }
        };
        videoview = findViewById(R.id.videoview);
        Button takebutton = findViewById(R.id.takebutton);
        initButtonlistener(takebutton);
        initAutoFocusCallback();
        initSurfaceView(videoview);
    }

    /**
     * @method 初始化OnTouchListener
     * @param button1
     */
    public void initButtonlistener(Button button1){
        button1.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mCamera != null){
                    if(true){
                        takePhotoThenStorage();
                    }
                }
                return true;
            }
        });
    }

    /**
     * @method 初始化AutoFocusCallbackImpl
     */
    public void initAutoFocusCallback(){
        autoFocusCallback = new AutoFocusCallbackImpl();
        autoFocusCallback.setHandler(handler,MSG_AUTOFUCS);
    }
    /**
     * @method 初始化SurfaceView，开启Camera
     * @param videoview
     */
    public void initSurfaceView(final SurfaceView videoview){
        videoview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                openCamera();
                mCamera.autoFocus(autoFocusCallback);
            }
            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if(mCamera!=null){
                    mCamera.stopPreview();
                    mCamera.release();
                }

            }
        });
    }
    /**
     * @method 弹出提示框
     */
    public void popTips(){
        new  AlertDialog.Builder(getApplicationContext())
                .setTitle("确认" )
                .setMessage("确定吗？" )
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent it = getIntent();
//                        Bundle bun = it.getExtras();
//                        bun.putByteArray("picture",bs);
//                        setResult(RESULT_OK,it);
//                        finish();
                    }
                })
                .setNegativeButton("否" , null)
                .show();
    }
    /**
     * @method 照相
     */
    public void takePhotoThenStorage(){
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {


                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                final String path = "/mnt/sdcard/DCIM/camera/VideoApp"+df.format(new Date())+".png";
                final byte[] bs = data;
                new Thread(){
                    public void run(){
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bs,0,bs.length);
                        //输出流保存数据
                        try {
                            FileOutputStream fileOutputStream=new FileOutputStream(path);
                            bitmap.compress(Bitmap.CompressFormat.PNG,20,fileOutputStream);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                Intent it = getIntent();
                it.putExtra("path",path);
                setResult(RESULT_OK,it);
                finish();
            }
        });
    }
    /**
     * @method 打开摄像头
     */
    private void openCamera() {
        if (mCamera != null) {
            throw new RuntimeException("相机已经被开启，无法同时开启多个相机实例！");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (hasBackFacingCamera()) {
                // 优先开启后置摄像头
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                try {
                    changeParam(mCamera);
                    mCamera.setPreviewDisplay(videoview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            } else if (hasBackFacingCamera()) {
                // 没有前置，就尝试开启前置摄像头
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                try {
                    changeParam(mCamera);
                    mCamera.setPreviewDisplay(videoview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            } else {
                throw new RuntimeException("没有任何相机可以开启！");
            }
        }
    };
    /**
     * @method 关闭摄像头
     */
    private void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
    /**
     * @method 添加摄像头回调方法
     * @param mCamera
     */
    public void addCallBack(Camera mCamera){
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                ByteArrayOutputStream bas = new ByteArrayOutputStream();
                YuvImage yuv = new YuvImage(bytes, ImageFormat.NV21,videoview.getWidth(),videoview.getHeight(),null);
                yuv.compressToJpeg(new Rect(),1,bas);
                byte[] byts = bas.toByteArray();
            }
        });
    }
    /**
     * @method 设置摄像头的参数
     * @param  mCamera
     */
    public void changeParam(Camera mCamera){
        Camera.Parameters param = mCamera.getParameters();
        mCamera.setParameters(param);
        mCamera.setDisplayOrientation(90);
    }

    /**
     * @method 获取摄像头个数并判断是否有无前置或后置摄像头
     * @param facing CAMERA_FACING_BACK || CAMERA_FACING_FRONT
     * @return true
     */
    private static boolean checkCameraFacing(final int facing) {
        if (getSdkVersion() < Build.VERSION_CODES.GINGERBREAD) {
            return false;
        }
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }
    /**
     * @method 判断有无后置摄像头
     * @return true
     */
    public static boolean hasBackFacingCamera() {
        final int CAMERA_FACING_BACK = 0;
        return checkCameraFacing(CAMERA_FACING_BACK);
    }
    /**
     * @method 判断有无前置摄像头
     * @return true
     */
    public static boolean hasFrontFacingCamera() {
        final int CAMERA_FACING_FRONT = 1;
        return checkCameraFacing(CAMERA_FACING_FRONT);
    }
    /**
     * @method 返回版本信息
     * @return int
     */
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }
    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
    }
    @Override
    protected void  onDestroy() {
        super.onDestroy();
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
