package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time : 2019/12/25
 * desc : 采集时的机器人的位置和激光雷达扫射的范围
 */
public class CollectingView3 extends SurfaceView implements SurfaceHolder.Callback {
    private int measureWidth, measureHeight;
    private List<NotifyLidarPtsEntity> ptsEntityList=new ArrayList<>();  //存储雷达扫到的点云
    public boolean isRunning=false;
    private DrawRobotThread drawRobotThread;          //绘制线程
    private SurfaceHolder holder;
    private float ratio=1;         //地图比例
    private float angle;
    private Matrix matrix;
    private Bitmap directionBitmap,directionBitmap1;
    private Paint paint,lastFrame;
    private int mBackColor=Color.TRANSPARENT;       //背景色透明
    public CollectingView3(Context context) {
        super(context);
        init();
    }

    /**
     * 初始化相关参数对象
     */
    private void init(){
        holder=getHolder();
        holder.addCallback(this);
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
        matrix=new Matrix();
        directionBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        paint=new Paint();
        lastFrame=new Paint();
        lastFrame.setStrokeWidth(1);
        lastFrame.setStyle(Paint.Style.FILL);
        lastFrame.setColor(Color.parseColor("#00CED1"));
    }

    /**
     * 设置参数
     */
    public void setData(List<NotifyLidarPtsEntity> ptsEntityList,float ratio,float angle){
        this.ptsEntityList=ptsEntityList;
        this.ratio=ratio;
        this.angle=angle;
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
        drawRobotThread=new DrawRobotThread();
        drawRobotThread.start();
    }

    /**
     * 停止绘制
     */
    public void onStop(){
        drawRobotThread.stopThread();
    }


    /**
     * 绘制机器人线程
     */
    public class DrawRobotThread extends Thread{
        public DrawRobotThread() {
            super();
            isRunning=true;
        }

        public void stopThread(){
            isRunning=false;
            boolean workIsNotFinish=true;
            while (workIsNotFinish){
                try {
                    drawRobotThread.join();   //保证run方法执行完毕
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
                        drawRobot(canvas);
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

    /**
     * 实时绘制机器人位置+雷达扫射位置
     * @param canvas
     */
    private void drawRobot(Canvas canvas){
        canvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR);
        int size=ptsEntityList.size();
        float posY=ptsEntityList.get(size-1).getPosY();
        float posX=ptsEntityList.get(size-1).getPosX();
        float x= (-posY*ratio+measureWidth/2);
        float y= (-posX*ratio+measureHeight/2);
        List<BaseCmd.notifyLidarPts.Position> positions=ptsEntityList.get(size-1).getPositionList();
        int pSize=positions.size();
        for (int j=0;j<pSize;j++){
            float ptX=(-positions.get(j).getPtY()*ratio+measureWidth/2);
            float ptY=(-positions.get(j).getPtX()*ratio+measureHeight/2);
            canvas.drawLine(x,y,ptX,ptY,lastFrame);

        }
        matrix.setRotate(-angle);
        directionBitmap1=Bitmap.createBitmap(directionBitmap,0,0,60,60,matrix,true);
        canvas.drawBitmap(directionBitmap1,x-30,y-30,paint);

    }



}
