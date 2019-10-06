package com.hnu.anew;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    /**
     * @author hnu.wangYA
     */
    FaceDetect faceDetect=new FaceDetect();
    Button camerabutton,uploadbutton,urlbutton;
    ImageView myimageView;
    WebView videoview;
    String file_str = Environment.getExternalStorageDirectory().getPath();              //获取sd卡根目录的地址
    File father_file = new File(file_str+"/mycamera");                      //建立父文件和子文件
    File file = new File(file_str+"/mycamera/image.jpg");
    @SuppressLint("WrongViewCast")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        getSupportActionBar().hide();//隐藏标题栏

        camerabutton = findViewById(R.id.takebutton);
        uploadbutton = findViewById(R.id.makebutton);
        urlbutton = findViewById(R.id.urlbutton);
        myimageView = findViewById(R.id.picture);
        videoview = (WebView)findViewById(R.id.videoplay);

        urlbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! urlbutton.getText().equals("url")){
                    System.out.println("***********************");
                    videoview.loadUrl(urlbutton.getText().toString());
                }
            }
        });
        camerabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){   //判断sd卡是否正确安装，如果正确则建立父文件
                    if(!father_file.exists()){
                        father_file.mkdirs();                                                //建立父目录
                    }
                    if(!file.exists()){
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);             //设置跳转的系统拍照页面
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));            //设置拍照为外部存储以及其存储的路径
                    startActivityForResult(intent,0x1);                        //跳转到拍照界面

                }
                else {
                    Toast.makeText(MainActivity.this, "请先安装好sd卡",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        uploadbutton.setOnClickListener(new View.OnClickListener() {                        //实现上传功能
            @Override
            public void onClick(View view) {
                if(file.exists()){
                    Toast.makeText(MainActivity.this,"正在上传4"+file,Toast.LENGTH_LONG).show();
                    System.out.println("文件名字："+file);

                    new Thread(){
                        public void run(){
                            faceDetect.setFaceDetect(urlbutton);
                            faceDetect.path=file.toString();
                            faceDetect.detect();
                        }
                    }.start();

                }
                else{
                    Toast.makeText(MainActivity.this,"请拍照完再上传"+file.length(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        System.out.println("onActivity");
        if(requestCode==0x1&&resultCode==this.RESULT_OK){                     //若请求码和结果码相同，就显示照片
            BitmapFactory.Options options = new BitmapFactory.Options();      //用来处理bitmap
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(),options);          //把长宽高返回给Options对象
            int height = options.outHeight*222/options.outWidth;
            options.outWidth=222;
            options.outHeight=height;
            options.inJustDecodeBounds=false;
            options.inSampleSize=options.outWidth/222;
            options.inPurgeable=true;
            options.inInputShareable=true;
            options.inPreferredConfig = Bitmap.Config.ARGB_4444;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),options);
            myimageView.setImageBitmap(bitmap);

        }
        else{
            Toast.makeText(MainActivity.this,"无法显示图片",Toast.LENGTH_LONG);
        }
        super.onActivityResult(requestCode,resultCode,data);
    }



}
