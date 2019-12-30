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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

public class CollectingView4 extends SurfaceView implements SurfaceHolder.Callback {
    private List<NotifyLidarPtsEntity> ptsEntityList=new ArrayList<>();  //存储雷达扫到的点云
    private int measureWidth, measureHeight;
    public boolean isRunning=false;
    private DrawMapThread drawThread;          //绘制线程
    private SurfaceHolder holder;
    private Paint paint,pointPaint,pathPaint;                       //绘制画笔
    private float ratio=1;         //地图比例
    private List<XyEntity>poiPoints=new ArrayList<>();
    private Bitmap poiBitmap;
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
        paint.setStrokeWidth(3);
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
    public void setData(List<NotifyLidarPtsEntity> ptsEntityList,List<XyEntity>poiPoints,float ratio){
        this.ptsEntityList=ptsEntityList;
        this.poiPoints=poiPoints;
        this.ratio=ratio;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.e("-------surfaceChanged:"+width+";"+height);
        measureWidth=width;
        measureHeight=height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

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
                Canvas canvas=null;
                try {
                    canvas=holder.lockCanvas();
                    if (canvas!=null){
                       drawMap(canvas);
                       drawPath(canvas);
                       drawPoint(canvas);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (canvas!=null){
                        holder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    private void drawMap(Canvas canvas){
        int ptsSize=ptsEntityList.size();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawColor(Color.parseColor("#101112"));
        for (int i=0;i<ptsSize;i++){
            float y=((-ptsEntityList.get(i).getPosX())*ratio+measureHeight/2);
            float x=((-ptsEntityList.get(i).getPosY())*ratio+measureWidth/2);
            List<BaseCmd.notifyLidarPts.Position> positions=ptsEntityList.get(i).getPositionList();
            int pSize=positions.size();
            for (int j=0;j<pSize;j++){
                float ptX=(-positions.get(j).getPtY()*ratio+measureWidth/2);
                float ptY=(-positions.get(j).getPtX()*ratio+measureHeight/2);
                canvas.drawLine(x,y,ptX,ptY,paint);
                canvas.drawPoint(ptX,ptY,pointPaint);
            }
        }
    }

    /**
     * 绘制行走路线
     */
    private void drawPath(Canvas canvas){
        int ptsSize=ptsEntityList.size();
        if (ptsSize>1){
            for (int i=0;i<ptsSize;i++){
                float y=(float)((-ptsEntityList.get(i).getPosX())*ratio+measureHeight/2);
                float x=(float)((-ptsEntityList.get(i).getPosY())*ratio+measureWidth/2);
                float y1=(float)((-ptsEntityList.get(i+1).getPosX())*ratio+measureHeight/2);
                float x1=(float)((-ptsEntityList.get(i+1).getPosY())*ratio+measureWidth/2);
                canvas.drawLine(x,y,x1,y1,pathPaint);
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
            float y=(float)((-poiPoints.get(i).getX())*ratio+measureHeight/2);
            float x=(float)((-poiPoints.get(i).getY())*ratio+measureWidth/2);
            canvas.drawBitmap(poiBitmap,(int) x-10,(int) y-10,pathPaint);
        }
    }

}
