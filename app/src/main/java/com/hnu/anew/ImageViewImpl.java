package com.hnu.anew;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;


@SuppressLint("AppCompatCustomView")
public class ImageViewImpl extends ImageView  {
    public ImageViewImpl(Context context){
        this(context,null);
    }

    public ImageViewImpl(Context context, AttributeSet attrs) {
        this(context,attrs,1);
     }

    public ImageViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Paint paint = getPaint();
                int action = motionEvent.getAction();
                switch(action){
                    case MotionEvent.ACTION_UP:autoImage(motionEvent.getX(),motionEvent.getY(),paint);break;
                    default:break;
                }
                //Invalidate()     UI线程
                //postInvalidate();  非UI线程
                return true;
            }
        });
    }
    public Paint getPaint(){
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        return paint;
    }
    public void autoImage(final float x,final float y,final Paint paint) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.v("zzw", "" + msg.what);
                int radius = msg.arg1;
                Bitmap bit = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bit);
                canvas.drawCircle(x, y, radius, paint);
                canvas.drawLine(x,y-radius,x,y-radius+30,paint);
                canvas.drawLine(x,y+radius,x,y+radius-30,paint);
                canvas.drawLine(x-radius,y,x-radius+30,y,paint);
                canvas.drawLine(x+radius,y,x+radius-30,y,paint);
                setImageBitmap(bit);
            }
        };
        new Thread(){
            public void run(){
                for(int i=120;i>=80;i--){
                    Message msg = new Message();
                    msg.arg1 = i;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                interrupt();
            }
        }.start();
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

}
