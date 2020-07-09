package ddr.example.com.nddrandroidclient.widget.zoomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.GridItem;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarCurSubMap;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.helper.EventBusManager;
import ddr.example.com.nddrandroidclient.helper.OpenCVUtility;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 *  desc:生成栅格地图的View
 *  time:2020/06/19
 */
public class GenerateMapView extends SurfaceView implements SurfaceHolder.Callback {
    private int mBackColor = Color.TRANSPARENT;       //背景色透明
    public boolean isRunning=false;
    private DrawMapThread drawThread;          //绘制线程
    private SurfaceHolder holder;
    private Bitmap srcBitmap;
    private Matrix matrix,matrix1,matrix2;
    private Paint paint,lastFrame,pathPaint;
    private NotifyLidarCurSubMap notifyLidarCurSubMap;
    private OpenCVUtility openCVUtility;
    private SurfaceTouchEventHandler surfaceTouchEventHandler;
    private float lidarRange=19.5f;
    private float perMeter;
    private float originX,originY;         //相对于Mat的原点坐标
    private int eachPixelW;               //每块像素的宽高
    private Bitmap directionBitmap,targetBitmap;
    private float angle;
    private List<BaseCmd.notifyLidarPts.Position> positionList=new ArrayList<>();    //雷达当前扫到的点云
    private NotifyLidarPtsEntity notifyLidarPtsEntity;
    private List<XyEntity> robotPath=new ArrayList<>();
    public GenerateMapView(Context context) {
        super(context);
        init();
    }

    public GenerateMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init(){
        holder=getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
        holder.addCallback(this);
        matrix=new Matrix();
        matrix1=new Matrix();
        matrix2=new Matrix();
        paint=new Paint();
        paint.setAntiAlias(true);
        openCVUtility=OpenCVUtility.getInstance();
        notifyLidarCurSubMap=NotifyLidarCurSubMap.getInstance();
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        directionBitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        targetBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.target_point);
        lastFrame=new Paint();
        lastFrame.setStrokeWidth(1);
        lastFrame.setStyle(Paint.Style.FILL);
        lastFrame.setColor(Color.parseColor("#9900CED1"));
        pathPaint=new Paint();
        pathPaint.setColor(Color.BLACK);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(2);
        EventBusManager.register(this);
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
        if (drawThread!=null&&isRunning){
            drawThread.stopThread();
        }
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
                    drawMap(canvas);
                    //drawPath(canvas);
                    drawRobot(canvas);
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
                if (time<100){
                    try {
                        Thread.sleep(100-time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 绘制地图
     * @param canvas
     */
    private void drawMap(Canvas canvas){
        if (srcBitmap!=null){
            canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
            matrix=surfaceTouchEventHandler.getMatrix();
            canvas.drawBitmap(srcBitmap,matrix,paint);
        }
    }

    /**
     * 绘制机器人当前位置
     * @param canvas
     *
     */
    private void drawRobot(Canvas canvas){
        positionList=notifyLidarPtsEntity.getPositionList();
        int size = positionList.size();
        if (positionList != null && size > 0) {
            XyEntity xyEntity1 = coordinatesToMat(notifyLidarPtsEntity.getPosX(), notifyLidarPtsEntity.getPosY());
            xyEntity1=surfaceTouchEventHandler.coordinatesToCanvas(xyEntity1.getX(),xyEntity1.getY());
            for (int i = 0; i < size; i++) {
                XyEntity xyEntity = coordinatesToMat(positionList.get(i).getPtX(), positionList.get(i).getPtY());
                xyEntity=surfaceTouchEventHandler.coordinatesToCanvas(xyEntity.getX(),xyEntity.getY());
                canvas.drawLine(xyEntity1.getX(), xyEntity1.getY(), xyEntity.getX(), xyEntity.getY(), lastFrame);
            }
            angle=radianToangle(notifyLidarPtsEntity.getPosdirection());
            float cx = xyEntity1.getX()-directionBitmap.getWidth()/2;
            float cy =xyEntity1.getY()-directionBitmap.getHeight()*13/20;
            matrix1.reset();
            matrix1.postTranslate(cx,cy);
            matrix1.postRotate(-angle+(float) surfaceTouchEventHandler.getAngle(),xyEntity1.getX(),xyEntity1.getY());
            canvas.drawBitmap(directionBitmap,matrix1,paint);
        }

    }

    /**
     *显示机器人行走路径
     * @param canvas
     */
    private void drawPath(Canvas canvas){
        int size=robotPath.size();
        if (size>1){
            Path path=new Path();
            for (int i=0;i<size;i++){
                XyEntity xyEntity = coordinatesToMat(robotPath.get(i).getX(), robotPath.get(i).getY());
                xyEntity=surfaceTouchEventHandler.coordinatesToCanvas(xyEntity.getX(),xyEntity.getY());
                if (i==0){
                    path.moveTo(xyEntity.getX(),xyEntity.getY());
                }else {
                    path.lineTo(xyEntity.getX(),xyEntity.getY());
                }
            }
            canvas.drawPath(path,pathPaint);
        }
        XyEntity xyEntity=coordinatesToMat(0,0);
        xyEntity=surfaceTouchEventHandler.coordinatesToCanvas(xyEntity.getX(),xyEntity.getY());
        float cx=xyEntity.getX()-targetBitmap.getWidth()/2;
        float cy=xyEntity.getY()-targetBitmap.getHeight()/2;
        matrix2.reset();
        matrix2.postTranslate(cx,cy);
        matrix2.postRotate((float) surfaceTouchEventHandler.getAngle(),xyEntity.getX(),xyEntity.getY());
        canvas.drawBitmap(targetBitmap,matrix2,paint);
    }


    private Mat srcMat;
    /**
     * 检查矩阵的索引,计算总共需要多少块像素承载数据,并改变相应位置的像素
     */
    private void checkMatNumber(){
        Map<GridItem,Mat> matMap=openCVUtility.getMatMap();
        eachPixelW=notifyLidarCurSubMap.getWidth();
        int minX=0,minY=0,maxX=0,maxY=0;
        int numberX,numberY;
        for (GridItem gridItem:matMap.keySet()){
            int cx=gridItem.getX();
            int cy=gridItem.getY();
            if (minX>=cx){ minX=cx;}
            if (minY>=cy){ minY=cy;}
            if (maxX<=cx){ maxX=cx;}
            if (maxY<=cy){ maxY=cy;}
        }
        numberX=maxX-minX+1;
        numberY=maxY-minY+1;
        int w=eachPixelW*numberY;
        int h=eachPixelW*numberX;
        srcMat=openCVUtility.getSrcMat();
        if (matMap.size()==1){
            srcMat=openCVUtility.createSrcMat(w,h);
            Mat mat=matMap.get(new GridItem(0,0));
            mat.copyTo(srcMat.submat(0,h,0,w));
            originY=eachPixelW/2;
            originX=eachPixelW/2;
        }else {
            if (w>srcMat.width()|h>srcMat.height()){
                srcMat.release();
                srcMat=openCVUtility.createSrcMat(w,h);
                for (GridItem gridItem:matMap.keySet()){
                    Mat mat=matMap.get(gridItem);
                    int rowStart=(maxX-gridItem.getX())*eachPixelW;
                    int rowEnd=rowStart+eachPixelW;
                    int colStart=(maxY-gridItem.getY())*eachPixelW;
                    int colEnd=colStart+eachPixelW;
                    mat.copyTo(srcMat.submat(rowStart,rowEnd,colStart,colEnd));
                    if (gridItem.getX()==0&&gridItem.getY()==0){
                        Logger.e("------rowStart:"+rowStart+"colStart:"+colStart);
                        originY=rowStart+eachPixelW/2;
                        originX=colStart+eachPixelW/2;
                    }
                }
            }else {
                Mat mat=matMap.get(notifyLidarCurSubMap.getGridItem());
                int rowStart=(maxX-notifyLidarCurSubMap.getGridItem().getX())*notifyLidarCurSubMap.getHeight();
                int rowEnd=rowStart+notifyLidarCurSubMap.getHeight();
                int colStart=(maxY-notifyLidarCurSubMap.getGridItem().getY())*notifyLidarCurSubMap.getWidth();
                int colEnd=colStart+notifyLidarCurSubMap.getWidth();
                mat.copyTo(srcMat.submat(rowStart,rowEnd,colStart,colEnd));
/*
                for (GridItem gridItem:matMap.keySet()){
                    int rowStart1=(maxX-gridItem.getX())*eachPixelW;
                    int colStart1=(maxY-gridItem.getY())*eachPixelW;
                    if (gridItem.getX()==0&&gridItem.getY()==0){
                        Logger.e("------rowStart:"+rowStart+"colStart:"+colStart);
                        originY=rowStart1+eachPixelW/2;
                        originX=colStart1+eachPixelW/2;
                    }
                }
*/

            }
        }

        srcBitmap=openCVUtility.matToBitmap(srcMat);
        srcMat.release();
        surfaceTouchEventHandler.setDefaultBitmap(srcBitmap);
       // Logger.e("生成的图片的大小："+srcBitmap.getWidth()+";"+srcBitmap.getHeight());
        perMeter=eachPixelW/(lidarRange*2);
    }

    /**
     * 计算相对于图片的坐标
     * @param x
     * @param y
     */
    private XyEntity coordinatesToMat(float x, float y){
        //Logger.d("-----:"+x+";"+y);
        float cx=(-y)*perMeter+originX;
        float cy=(-x)*perMeter+originY;
        //Logger.e("坐标："+cx+";"+cy+";"+perMeter+";"+originX+";"+originY);
        return new XyEntity(cx,cy);
    }
    /**
     * 弧度转角度
     */
    private float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.e("-------surfaceChanged:"+width+";"+height);
        surfaceTouchEventHandler=SurfaceTouchEventHandler.getInstance(width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (surfaceTouchEventHandler!=null){
            surfaceTouchEventHandler.onDestroy();
        }
        EventBusManager.unregister(this);
        openCVUtility.onDestroy();
    }


    public boolean onTouchEvent(MotionEvent event) {
        if (surfaceTouchEventHandler!=null){
            //Logger.e("---触摸");
            surfaceTouchEventHandler.touchEvent(event);
        }
        return true;
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void receiveMessage(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case receiveLidarMap:
                Logger.d("---------------receiveLidarMap");
                checkMatNumber();
                break;
            case receivePointCloud:
                XyEntity xyEntity=new XyEntity(notifyLidarPtsEntity.getPosX(),notifyLidarPtsEntity.getPosY());
                robotPath.add(xyEntity);
                break;
        }
    }
}
