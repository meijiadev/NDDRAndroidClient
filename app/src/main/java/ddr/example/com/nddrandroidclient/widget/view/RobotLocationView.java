package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time :2019/12/25
 * desc :绘制机器人当前位置和雷达射线
 */
public class RobotLocationView extends SurfaceView implements SurfaceHolder.Callback {
    private int bitmapWidth,bitmapHeight;           //图片的大小
    private int measureWidth,measureHeight;         //最初布局的大小
    private int mBackColor=Color.TRANSPARENT;       //背景色透明
    private Bitmap directionBitmap;
    private Paint paint;
    private SurfaceHolder holder;
    public RobotLocationView(Context context) {
        super(context);
    }

    public RobotLocationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        directionBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        paint=new Paint();
        holder=getHolder();
        holder.addCallback(this);
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
    }

    /**
     * 设置显示大小
     * @param width
     * @param height
     */
    public void setBitmapSize(int width,int height){
        this.bitmapWidth=width;
        this.bitmapHeight=height;
        holder.setFixedSize(bitmapWidth,bitmapHeight);

    }


    private void doDraw(){
        Canvas canvas=holder.lockCanvas();
        canvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(directionBitmap,bitmapWidth/2-30,bitmapHeight/2-30,paint);
        holder.unlockCanvasAndPost(canvas);
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (bitmapWidth==0&&bitmapHeight==0){
            if (widthMode == View.MeasureSpec.EXACTLY) {

                // 具体的值和match_parent
                measureWidth = widthSize;
            } else {
                // wrap_content
                measureWidth = 1000;
            }

            if (heightMode == View.MeasureSpec.EXACTLY) {
                measureHeight = heightSize;
            } else {
                measureHeight = 1000;
            }
            setMeasuredDimension(measureWidth, measureHeight);
        }else {
            setMeasuredDimension(bitmapWidth, bitmapHeight);
            Logger.e("重新设置画布大小："+bitmapWidth+";"+bitmapHeight);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        DrawThread drawThread=new DrawThread();
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.e("--------"+width+";"+height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public class DrawThread extends Thread{
        public DrawThread() {
            super();
        }

        @Override
        public void run() {
            super.run();
            while (true){
                doDraw();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
