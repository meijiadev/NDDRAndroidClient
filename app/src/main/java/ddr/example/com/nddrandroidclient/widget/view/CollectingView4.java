package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.widget.zoomview.SurfaceTouchEventHandler;
import ddr.example.com.nddrandroidclient.widget.zoomview.TouchEvenHandler;

/**
 * desc:采集实时绘制地图
 * time：2020/05/12
 */
public class CollectingView4 extends SurfaceView implements SurfaceHolder.Callback {
    private List<NotifyLidarPtsEntity> ptsEntityList=new ArrayList<>();  //存储雷达扫到的点云
    private int measureWidth, measureHeight;
    public boolean isRunning=false;
    private DrawMapThread drawThread;          //绘制线程
    private SurfaceHolder holder;
    private Paint paint,pointPaint,pathPaint;                       //绘制画笔
    private float perMeter =50;         //每米所占的像素
    private List<XyEntity>poiPoints=new ArrayList<>();
    private Bitmap poiBitmap;
    private SurfaceTouchEventHandler surfaceTouchEventHandler;

    public CollectingView4(Context context) {
        super(context);
        init();
    }

    public CollectingView4(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }
    /**
     * 初始化相关参数对象
     */
    private void init(){
        holder=getHolder();
        holder.addCallback(this);
        paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(8);
        pointPaint=new Paint();
        pointPaint.setColor(Color.BLUE);
        pointPaint.setStrokeWidth(3);
        pathPaint=new Paint();
        pathPaint.setColor(Color.BLACK);
        pathPaint.setStrokeWidth(2);
        poiBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.poi_default);
    }

    /**
     * 设置参数
     */
    public void setData(List<NotifyLidarPtsEntity> ptsEntityList,List<XyEntity>poiPoints){
        this.ptsEntityList=ptsEntityList;
        this.poiPoints=poiPoints;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.e("-------surfaceChanged:"+width+";"+height);
        surfaceTouchEventHandler=SurfaceTouchEventHandler.getInstance(width,height);
        measureWidth=width;
        measureHeight=height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (surfaceTouchEventHandler!=null){
            surfaceTouchEventHandler.onDestroy();
        }
    }

    /**
     * 开始绘制
     */
    public void startThread(){
        drawThread=new DrawMapThread();
        drawThread.start();
    }

    /**
     * 停止绘制
     */
    public void onStop(){
        if (drawThread!=null)
        drawThread.stopThread();
    }

    /**
     * 绘制图像的线程
     */
    public class DrawMapThread extends Thread{
        public DrawMapThread(){
            isRunning=true;
        }

        public void stopThread(){
            if (isRunning){
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
                Logger.e("终止线程");
            }

        }

        @Override
        public void run() {
            super.run();
            while (isRunning){
                long startTime=System.currentTimeMillis();
                Canvas canvas=null;
                try {
                    canvas=holder.lockCanvas();
                    if (canvas!=null&&surfaceTouchEventHandler!=null){
                       drawMap(canvas);
                       drawPoint(canvas);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (canvas!=null){
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
                long endTime=System.currentTimeMillis();
                Logger.i("------地图绘制耗时："+(endTime-startTime));
                long time=endTime-startTime;
                if (time<300){
                    try {
                        Thread.sleep(300-time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 绘制激光地图
     * @param canvas
     */
    private void drawMap(Canvas canvas){
        int ptsSize=ptsEntityList.size();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawColor(Color.parseColor("#101112"));
        for (int i=0;i<ptsSize;i++){
            float y=((-ptsEntityList.get(i).getPosX())*perMeter+measureHeight/2);
            float x=((-ptsEntityList.get(i).getPosY())*perMeter+measureWidth/2);
            XyEntity xyEntity=surfaceTouchEventHandler.coordinatesToCanvas(x,y);
            List<BaseCmd.notifyLidarPts.Position> positions=ptsEntityList.get(i).getPositionList();
            int pSize=positions.size();
            for (int j=0;j<pSize;j++){
                float ptX=(-positions.get(j).getPtY()*perMeter+measureWidth/2);
                float ptY=(-positions.get(j).getPtX()*perMeter+measureHeight/2);
                XyEntity xyEntity1=surfaceTouchEventHandler.coordinatesToCanvas(ptX,ptY);
                canvas.drawLine(xyEntity.getX(),xyEntity.getY(),xyEntity1.getX(),xyEntity1.getY(),paint);
                canvas.drawPoint(xyEntity1.getX(),xyEntity1.getY(),pointPaint);
            }
        }
    }



    /**
     * 添加采集过程中的目标点
     * @param canvas
     */
    private void drawPoint(Canvas canvas){
        int pts=poiPoints.size();
        for (int i=0;i<pts;i++){
            float y=((-poiPoints.get(i).getX())*perMeter+measureHeight/2);
            float x=((-poiPoints.get(i).getY())*perMeter+measureWidth/2);
            XyEntity xyEntity=surfaceTouchEventHandler.coordinatesToCanvas(x,y);
            canvas.drawBitmap(poiBitmap,xyEntity.getX()-poiBitmap.getWidth()/2,xyEntity.getY()-poiBitmap.getHeight()/2,pathPaint);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (surfaceTouchEventHandler!=null){
            Logger.e("---触摸");
            surfaceTouchEventHandler.touchEvent(event);
        }
        return true;
    }



}
