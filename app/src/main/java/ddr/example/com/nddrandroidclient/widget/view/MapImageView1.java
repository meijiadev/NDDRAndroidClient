package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.SpaceItem;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * desc: 基于SurfaceView的实时绘制当前机器人位置和路线的图片
 * time: 2020/3/16
 */
public class MapImageView1 extends SurfaceView implements SurfaceHolder.Callback {



    public MapImageView1(Context context) {
        super(context);

    }

    public MapImageView1(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    private SurfaceHolder holder;

    private List<BaseCmd.notifyLidarPts.Position> positionList=new ArrayList<>();    //雷达当前扫到的点云

    private NotifyLidarPtsEntity notifyLidarPtsEntity;

    private Bitmap mapBitmap;
    private Bitmap targetBitmap,targetBitmap1; //目标点
    private Bitmap directionBitmap,directionBitmap1;
    private Paint paint,radarPaint,linePaint1;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private double r00=0;
    private double r01=-61.5959;
    private double t0=375.501;
    private double r10=-61.6269;
    private double r11=0;
    private double t1=410.973;
    private float radian,angle;                /**经过矩阵变换后的坐标（相对于图片，单位是像素)**/
    private int posX,posY;

    private float scale=1;
    private boolean waitData=false;           //是否需要等待数据
    private String taskName;
    private boolean isStartRadar=false;       //是否雷达开始绘制
    private String mapName;

    private TargetPoint targetPoint;         //目标点



    private Matrix mapMatrix;






    /**
     * 绘制雷达扫到的区域
     * @param canvasGL
     */
/*
    private void drawRadarLine(Canvas canvasGL){
        if (isStartRadar){
            positionList=notifyLidarPtsEntity.getPositionList();
            if (positionList!=null){
                int size =positionList.size();
                for (int i=0;i<size;i++){
                    float x= (float) (r00*positionList.get(i).getPtX()+r01*positionList.get(i).getPtY()+t0)/scale+mRectDst.left;
                    float y=(float)(r10*positionList.get(i).getPtX()+r11*positionList.get(i).getPtY()+t1)/scale+mRectDst.top;
                    canvasGL.drawLine(posX+mRectDst.left,posY+mRectDst.top,x,y,radarPaint);
                }
                mapMatrix.setRotate(-angle);
                directionBitmap1=Bitmap.createBitmap(directionBitmap,0,0,60,60,mapMatrix,true);
                if (mapBitmap!=null){
                    canvasGL.drawBitmap(directionBitmap1,mRectDst.left+posX-30,mRectDst.top+posY-30,paint);
                }
            }
        }
    }
*/

    /**
     * 绘制
     */
    private void doDraw(){
        long startTime=System.currentTimeMillis();
        Canvas canvas=null;
        try {
            canvas=holder.lockCanvas();
            if (canvas!=null){
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
                //drawRadarLine(canvas);

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
        if (time<400){
            try {
                Thread.sleep(400-time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
     * 世界坐标——>像素坐标
     * @param x
     * @param y
     * @return
     */
    public XyEntity toXorY(float x, float y){
        float x1=(float)( r00*x+r01*y+t0)/scale;
        float y1=(float) (r10*x+r11*y+t1)/scale;
        return new XyEntity(x1,y1);
    }



    /**
     * 实时绘制（将世界坐标经过矩阵变换成图片上的像素坐标)
     */
    private void realTimeDraw(){
        float x=notifyBaseStatusEx.getPosX();
        float y=notifyBaseStatusEx.getPosY();
        radian=notifyBaseStatusEx.getPosDirection();
        posX=(int) ((r00*x+r01*y+t0)/scale);
        posY=(int) ((r10*x+r11*y+t1)/scale);
        angle=radianToangle(radian);
    }

    public void clearDraw(){
        targetPoint=null;
    }
    /**
     * 开始绘制
     */
    public void startThread(){
        drawThread=new DrawThread();
        drawThread.start();
    }
    /**
     * 停止绘制
     */
    public void onStop(){
        if (drawThread!=null){
            drawThread.stopThread();
        }
    }
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void upDate(MessageEvent mainUpDate){
        switch (mainUpDate.getType()){
            case updateBaseStatus:
                realTimeDraw();
                mapName=NotifyBaseStatusEx.getInstance().getCurroute();
                break;
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
        startThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        EventBus.getDefault().unregister(this);
    }





}
