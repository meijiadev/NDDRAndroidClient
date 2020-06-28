package ddr.example.com.nddrandroidclient.widget.zoomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.info.GridItem;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarCurSubMap;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.helper.OpenCVUtility;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 *  desc:生成栅格地图的View
 *  time:2020/06/19
 */
public class GenerateMapView extends SurfaceView implements SurfaceHolder.Callback {
    public boolean isRunning=false;
    private DrawMapThread drawThread;          //绘制线程
    private SurfaceHolder holder;
    private Bitmap srcBitmap;
    private Matrix matrix,matrix1;
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
    public GenerateMapView(Context context) {
        super(context);
        init();
    }

    public GenerateMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init(){
        matrix=new Matrix();
        matrix1=new Matrix();
        paint=new Paint();
        paint.setAntiAlias(true);
        openCVUtility=OpenCVUtility.getInstance();
        notifyLidarCurSubMap=NotifyLidarCurSubMap.getInstance();
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        holder=getHolder();
        holder.addCallback(this);
        directionBitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        targetBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.target_point);
        lastFrame=new Paint();
        lastFrame.setStrokeWidth(1);
        lastFrame.setStyle(Paint.Style.FILL);
        lastFrame.setColor(Color.parseColor("#9900CED1"));
        pathPaint=new Paint();
        pathPaint.setColor(Color.BLACK);
        pathPaint.setStrokeWidth(2);
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
                    checkMatNumber();
                    drawMap(canvas);
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
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            matrix=surfaceTouchEventHandler.getMatrix();
            canvas.drawBitmap(srcBitmap,matrix,paint);
        }
    }

    /**
     * 绘制机器人当前位置
     * @param canvas
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
            XyEntity xyEntity=coordinatesToMat(notifyLidarPtsEntity.getPosX(),notifyLidarPtsEntity.getPosY());
            xyEntity=surfaceTouchEventHandler.coordinatesToCanvas(xyEntity.getX(),xyEntity.getY());
            float cx = xyEntity.getX()-directionBitmap.getWidth()/2;
            float cy =xyEntity.getY()-directionBitmap.getHeight()*13/20;
            matrix1.reset();
            matrix1.postTranslate(cx,cy);
            matrix1.postRotate(-angle+(float) surfaceTouchEventHandler.getAngle(),xyEntity.getX(),xyEntity.getY());
            canvas.drawBitmap(directionBitmap,matrix1,paint);
        }

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
                        originY=rowStart*eachPixelW+eachPixelW/2;
                        originX=colStart*eachPixelW+eachPixelW/2;
                    }
                }
            }else {
                Mat mat=matMap.get(notifyLidarCurSubMap.getGridItem());
                int rowStart=(maxX-notifyLidarCurSubMap.getGridItem().getX())*notifyLidarCurSubMap.getHeight();
                int rowEnd=rowStart+notifyLidarCurSubMap.getHeight();
                int colStart=(maxY-notifyLidarCurSubMap.getGridItem().getY())*notifyLidarCurSubMap.getWidth();
                int colEnd=colStart+notifyLidarCurSubMap.getWidth();
                mat.copyTo(srcMat.submat(rowStart,rowEnd,colStart,colEnd));
            }
        }
        srcBitmap=openCVUtility.matToBitmap(srcMat);
        surfaceTouchEventHandler.setDefaultBitmap(srcBitmap);
        Logger.e("生成的图片的大小："+srcBitmap.getWidth()+";"+srcBitmap.getHeight());
        perMeter=eachPixelW/(lidarRange*2);
    }

    /**
     * 计算相对于图片的坐标
     * @param x
     * @param y
     */
    private XyEntity coordinatesToMat(float x, float y){
        //Logger.e("-----:"+x+";"+y);
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
        openCVUtility.onDestroy();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (surfaceTouchEventHandler!=null){
            surfaceTouchEventHandler.onDestroy();
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
