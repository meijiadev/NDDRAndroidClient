package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * desc: 基于SurfaceView的实时绘制当前机器人位置和路线的图片
 * time: 2020/3/16
 */
public class MapImageView1 extends SurfaceView implements SurfaceHolder.Callback {
    private MapImageView0 mapImageView0;
    private SurfaceHolder holder;
    private int mBackColor = Color.TRANSPARENT;       //背景色透明
    //承载点云数据的基类，并保存最新一帧的数据
    private NotifyLidarPtsEntity notifyLidarPtsEntity;
    private List<BaseCmd.notifyLidarPts.Position> positionList=new ArrayList<>();    //雷达当前扫到的点云
    private Paint radarPaint,paint;
    private boolean isStartRadar=false;       //是否雷达开始绘制
    private Matrix mapMatrix;
    private Bitmap directionBitmap,directionBitmap1;
    private int measureWidth,measureHeight;

    public MapImageView1(Context context) {
        super(context);
        init();

    }
    public MapImageView1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public void setMapImageView0(MapImageView0 mapImageView0){
        this.mapImageView0=mapImageView0;
    }

    /**
     * 初始化参数
     */
    private void init(){
        holder=getHolder();
        holder.addCallback(this);
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        radarPaint=new Paint();
        radarPaint.setStrokeWidth(1);
        radarPaint.setColor(Color.parseColor("#00CED1"));
        paint=new Paint();
        mapMatrix=new Matrix();
        directionBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        EventBus.getDefault().register(this);


    }







    /**
     * 绘制雷达扫到的区域
     * @param canvas
     */
    private void drawRadarLine(Canvas canvas){
        if (mapImageView0!=null){
            canvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR);
            if (isStartRadar){
                positionList=notifyLidarPtsEntity.getPositionList();
                Logger.e("-------点云数量："+positionList.size());
                if (positionList!=null){
                    int size =positionList.size();
                    XyEntity xyEntity1=mapImageView0.toXorY(notifyLidarPtsEntity.getPosX(),notifyLidarPtsEntity.getPosY());
                    xyEntity1=mapImageView0.coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                    for (int i=0;i<size;i++){
                        XyEntity xyEntity=mapImageView0.toXorY(positionList.get(i).getPtX(),positionList.get(i).getPtY());
                        xyEntity=mapImageView0.coordinate2View(xyEntity.getX(),xyEntity.getY());
                        canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity.getX(),xyEntity.getY(),radarPaint);
                    }
                    float angle=radianToangle(notifyLidarPtsEntity.getPosdirection());
                    mapMatrix.setRotate(-angle);
                    directionBitmap1=Bitmap.createBitmap(directionBitmap,0,0,60,60,mapMatrix,true);
                    canvas.drawBitmap(directionBitmap1,xyEntity1.getX()-30,xyEntity1.getY()-30,paint);
                }
            }
        }
    }

    /**
     * 绘制
     */
    private void doDraw(){
        long startTime=System.currentTimeMillis();
        Canvas canvas=null;
        try {
            canvas=holder.lockCanvas();
            if (canvas!=null){
                drawRadarLine(canvas);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (canvas!=null){
                holder.unlockCanvasAndPost(canvas);
            }
        }
        long endTime=System.currentTimeMillis();
        long time=endTime-startTime;
        if (time<100){
            try {
                Thread.sleep(100-time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Logger.e("------------绘制时间："+time);
    }

    public boolean isRunning=false;
    public DrawThread drawThread;
    public class DrawThread extends Thread{
        public DrawThread() {
            isRunning=true;
        }

        public void stopThread(){
            isRunning=false;
            boolean workIsNotFinish=true;
            while (workIsNotFinish){
                try {
                    drawThread.join();   //保证run方法执行完毕
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                workIsNotFinish=false;
            }
        }
        @Override
        public void run() {
            super.run();
            while (isRunning){
                doDraw();
            }
        }

    }

    /**
     * 开始绘制
     */
    public void startThread(){
        drawThread=new DrawThread();
        drawThread.start();
        Logger.e("开启线程");
    }
    /**
     * 停止绘制
     */
    public void onStop(){
        if (drawThread!=null){
            Logger.e("线程停止");
            drawThread.stopThread();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void upDate(MessageEvent mainUpDate){
        switch (mainUpDate.getType()){
            case receivePointCloud:
                isStartRadar=true;
                break;

        }
    }


    /**
     * 弧度转角度
     */
    public float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }




    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.e("surfaceView的大小："+width+";"+height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        //Logger.e("--------:"+widthSize+";"+heightSize);
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
        int max=Math.max(measureWidth,measureHeight);
        setMeasuredDimension(max, max);
    }



}
