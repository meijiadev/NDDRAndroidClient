package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.view.View;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.GLPaint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time ：2019/12/13
 * desc : 利用SurfaceView绘制地图(闲置)
 */
public class CollectingView2 extends SurfaceView implements SurfaceHolder.Callback{
    private NotifyLidarPtsEntity notifyLidarPtsEntity;
    private NotifyLidarPtsEntity notifyLidarPtsEntity1;
    private List<NotifyLidarPtsEntity> ptsEntityList=new ArrayList<>();  //存储雷达扫到的点云
    //private List<NotifyLidarPtsEntity> ptsSwitchs=new ArrayList<>(); //经过坐标转化之后的点云列表（可以直接绘制到画布上的）
    private float posXSwitch,posYSwitch;                             //坐标转换之后的坐标
    private List<XyEntity>poiPoints=new ArrayList<>();
    private float posX,posY;
    private float radian;
    private float angle;
    private float minX=0,minY=0,maxX=0,maxY=0;  //雷达扫到的最大坐标和最小坐标
    private float ratio=1;         //地图比例
    private float oldRatio=1;
    private int measureWidth, measureHeight;
    private Bitmap directionBitmap,directionBitmap1;
    private Bitmap poiBitmap;
    private Bitmap bgBitmap;
    private Matrix matrix;
    private SurfaceHolder holder;
    public boolean isRunning=false;
    private DrawMapThread drawThread;          //绘制线程

    private Paint paint,lastFrame,pathPaint,pointPaint,bitmapPaint;


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void upDate(MessageEvent mainUpDate) {
        switch (mainUpDate.getType()) {
            case receivePointCloud:
                if (NotifyBaseStatusEx.getInstance().getSonMode()==6){
                    posX=notifyLidarPtsEntity.getPosX();
                    posY=notifyLidarPtsEntity.getPosY();
                    radian=notifyLidarPtsEntity.getPosdirection();
                    angle=radianToangle(radian);
                    notifyLidarPtsEntity1=new NotifyLidarPtsEntity();
                    notifyLidarPtsEntity1.setPosX(notifyLidarPtsEntity.getPosX());
                    notifyLidarPtsEntity1.setPosY(notifyLidarPtsEntity.getPosY());
                    notifyLidarPtsEntity1.setPositionList(notifyLidarPtsEntity.getPositionList());
                    ptsEntityList.add(notifyLidarPtsEntity1);
                    maxOrmin(notifyLidarPtsEntity.getPositionList());
                }
                break;
            case addPoiPoint:
                poiPoints.add(new XyEntity(posX,posY));
                break;
        }
    }

    public CollectingView2(Context context) {
        super(context);
    }

    public CollectingView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        Logger.e("--------实例化");
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        holder=getHolder();
        holder.addCallback(this);
        paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        lastFrame=new Paint();
        lastFrame.setStrokeWidth(1);
        lastFrame.setStyle(Paint.Style.FILL);
        lastFrame.setColor(Color.parseColor("#00CED1"));
        pathPaint=new Paint();
        pathPaint.setColor(Color.BLACK);
        pathPaint.setStrokeWidth(2);
        pointPaint=new Paint();
        pointPaint.setColor(Color.BLUE);
        pointPaint.setStrokeWidth(3);
        bitmapPaint=new Paint();
        matrix=new Matrix();
        poiBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.poi_default);
        directionBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        bgBitmap=Bitmap.createBitmap(1000,1000,Bitmap.Config.ARGB_8888);
        bgBitmap.eraseColor(Color.parseColor("#646464"));
        EventBus.getDefault().register(this);       //注册监听

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawThread=new DrawMapThread();
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.e("-------surfaceChanged:"+width+";"+height);
        measureWidth=width;
        measureHeight=height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        ptsEntityList.clear();
        notifyLidarPtsEntity.setNull();
        Logger.e("-------取消注册，清空内存");
    }

    /**
     * 停止绘制
     */
    public void onStop(){
        EventBus.getDefault().unregister(this);
        drawThread.stopThread();
    }

    /**
     * 计算缩放比例
     * @param list
     */
    private void maxOrmin(List<BaseCmd.notifyLidarPts.Position> list){
        long startTime=System.currentTimeMillis();
        if (list!=null){
            int listSize=list.size();
            for (int i=0;i<listSize;i++){
                if (maxX<list.get(i).getPtX()) maxX=list.get(i).getPtX();
                if (maxY<list.get(i).getPtY()) maxY=list.get(i).getPtY();
                if (minX>list.get(i).getPtX()) minX=list.get(i).getPtX();
                if (minY>list.get(i).getPtY()) minY=list.get(i).getPtY();
            }
            if (maxX<posX) maxX=posX;
            if (maxY<posY) maxY=posY;
            if (minX>posX) minX=posX;
            if (minY>posY) minY=posY;
            float xy=Math.max(Math.max(Math.abs(maxX),Math.abs(minX)),Math.max(Math.abs(maxY),Math.abs(minY)));
            if (xy<=0){
                ratio=1;
            }else {
                if (measureWidth>measureHeight){
                    ratio=measureWidth/(xy)/2*1;
                }else {
                    ratio=measureHeight/(xy)/2*1;
                }
            }
        }
        long endTime=System.currentTimeMillis();
    }

    /**
     * 弧度转角度
     */
    public float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
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
                doDraw();
            }
        }
    }



    /**
     * 绘制
     */
    private void doDraw(){
        long startTime1=System.currentTimeMillis();
        Canvas canvas=null;
        try {
            canvas=holder.lockCanvas();
            if (canvas!=null){
                drawMap(canvas);
                drawPoint(canvas);
                drawPath(canvas);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (canvas!=null)
                holder.unlockCanvasAndPost(canvas);
        }
        long endTime1=System.currentTimeMillis();
        long time=endTime1-startTime1;
        if (time<300){
            try {
                Thread.sleep(300-time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Logger.e("------绘制耗时："+time);
    }



    /**
     * 实时绘制地图
     * @param canvas
     */
    private void drawMap(Canvas canvas){
        int ptsSize=ptsEntityList.size();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawColor(Color.parseColor("#101112"));
        for (int i=0;i<ptsSize;i++){
            float y=(float)((-ptsEntityList.get(i).getPosX())*ratio+measureHeight/2);
            float x=(float)((-ptsEntityList.get(i).getPosY())*ratio+measureWidth/2);
            List<BaseCmd.notifyLidarPts.Position> positions=ptsEntityList.get(i).getPositionList();
            int pSize=positions.size();
            for (int j=0;j<pSize;j++){
                float ptX=(float)(-positions.get(j).getPtY()*ratio+measureWidth/2);
                float ptY=(float)(-positions.get(j).getPtX()*ratio+measureHeight/2);
                canvas.drawLine(x,y,ptX,ptY,paint);
                canvas.drawPoint(ptX,ptY,pointPaint);
                if (i==ptsSize-1){
                    canvas.drawLine(x,y,ptX,ptY,lastFrame);
                }
            }
        }
        posXSwitch=(-posY*ratio+measureWidth/2);
        posYSwitch=(-posX*ratio+measureHeight/2);
        matrix.setRotate(-angle);
        directionBitmap1=Bitmap.createBitmap(directionBitmap,0,0,60,60,matrix,true);
        canvas.drawBitmap(directionBitmap1,posXSwitch-30,posYSwitch-30,paint);

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
