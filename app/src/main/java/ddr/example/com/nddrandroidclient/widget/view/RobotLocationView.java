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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import DDRModuleProto.DDRModuleCmd;
import DDRVLNMapProto.DDRVLNMap;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.widget.layout.ZoomLayout;

/**
 * time :2019/12/25
 * desc :绘制机器人当前位置和雷达射线
 */
public class RobotLocationView extends SurfaceView implements SurfaceHolder.Callback {
    private String mapName;
    private int bitmapWidth, bitmapHeight;           //图片的大小
    private int measureWidth, measureHeight;         //最初布局的大小
    private int mBackColor = Color.TRANSPARENT;       //背景色透明
    private Bitmap directionBitmap;
    private Paint paint;
    private float scale=1;                             //地图缩放的比例
    private ZoomLayout zoomLayout;

    public RobotLocationView(Context context) {
        super(context);
        init();
    }

    public RobotLocationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init(){
        holder=getHolder();
        holder.addCallback(this);
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
        mapFileStatus=MapFileStatus.getInstance();
        directionBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        paint = new Paint();
        paint.setColor(Color.parseColor("#00CED1"));
    }

    /**
     * 设置显示大小
     *
     * @param width
     * @param height
     */
    public void setBitmapSize(ZoomLayout zoomLayout,String mapName, int width, int height) {
        this.zoomLayout=zoomLayout;
        this.mapName = mapName;
        this.bitmapWidth = width;
        this.bitmapHeight = height;
        Logger.e("--------图片大小："+bitmapWidth+";"+bitmapHeight);
        EventBus.getDefault().register(this);
        invalidate();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == View.MeasureSpec.EXACTLY) {
                // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
                // wrap_content
            measureWidth = 1000;
        }if (heightMode == View.MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = 1000;
        }
        setMeasuredDimension(measureWidth, measureHeight);
        Logger.e("画布大小：" + measureWidth + ";" + measureHeight);
    }


    /**
     * 世界坐标——>像素坐标
     * @param x
     * @param y
     * @return
     */
    public XyEntity toXorY(float x, float y){
        float x1=(float)( r00*x+r01*y+t0)+(measureWidth-bitmapWidth)/2;
        float y1=(float) (r10*x+r11*y+t1)+(measureHeight-bitmapHeight)/2;
        return new XyEntity(x1,y1);
    }


    /**
     * 将像素坐标变成（世界坐标）
     * @param x
     * @param y
     * @return
     */
    public XyEntity toPathXy(float x,float y){
        float k= (float) (r00*r11-r10*r01);
        float j= (float) (r10*r01-r00*r11);
        float ax= (float) (r11*x-r01*y+r01*t1-r11*t0);
        float ay= (float) (r10*x-r00*y+r00*t1-r10*t0);
        float sX=txfloat(ax,k);
        float sY=txfloat(ay,j);
        return new XyEntity(sX,sY);
    }

    private float txfloat(float a,float b) {
        DecimalFormat df=new DecimalFormat("0.0000");//设置保留位数
        return Float.parseFloat(df.format((float)a/b));
    }

    /**
     * 获得当前机器人在窗口的位置
     * @return
     */
    public XyEntity getRobotLocationInWindow(){
        return toXorY(0,0);
    }


    private List<DDRModuleCmd.rspObstacleInfo.ObstacleInfo> obstacleInfos;    //雷达当前扫到的点云
    private DDRVLNMap.reqDDRVLNMapEx data;
    private DDRVLNMap.DDRMapBaseData baseData;       // 存放基础信息，采集模式结束时就有的东西。
    private DDRVLNMap.affine_mat affine_mat;
    private MapFileStatus mapFileStatus;
    private double r00 = 0;
    private double r01 = -61.5959;
    private double t0 = 375.501;
    private double r10 = -61.6269;
    private double r11 = 0;
    private double t1 = 410.973;
    private float posX,posY;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upDate(MessageEvent mainUpDate) {
        switch (mainUpDate.getType()) {
            case updateDDRVLNMap:
                data = mapFileStatus.getReqDDRVLNMapEx();
                baseData = data.getBasedata();
                Logger.e("--------" + baseData.getName().toStringUtf8());
                //验证返回的地图信息是否是当前运行的地图
                if (baseData.getName().toStringUtf8().equals(mapName)) {
                    Logger.e("---------验证通过");
                    affine_mat = baseData.getAffinedata();
                    r00 = affine_mat.getR11();
                    r01 = affine_mat.getR12();
                    t0 = affine_mat.getTx();
                    r10 = affine_mat.getR21();
                    r11 = affine_mat.getR22();
                    t1 = affine_mat.getTy();
                    Logger.e("---"+r00+";"+r01+";"+t0);
                }
                break;
            case receiveObstacleInfo:
                obstacleInfos= (List<DDRModuleCmd.rspObstacleInfo.ObstacleInfo>) mainUpDate.getData();
                Logger.e("--------接收雷达数据");
                break;
        }
    }

    /**
     * 开始绘制
     */
    public void startThread(){
        drawLocationThread=new DrawLocationThread();
        drawLocationThread.start();
    }

    /**
     * 停止绘制
     */
    public void onStop(){
        if (drawLocationThread!=null){
            drawLocationThread.stopThread();
        }
    }



    private boolean isRunning=false;
    private SurfaceHolder holder;
    private DrawLocationThread drawLocationThread;

    public class  DrawLocationThread extends Thread{
        public DrawLocationThread() {
            super();
            isRunning=true;
        }

        public void stopThread(){
            isRunning=false;
            boolean workIsNotFinish=true;
            while (workIsNotFinish){
                try {
                    drawLocationThread.join();   //保证run方法执行完毕
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
                long startTime=System.currentTimeMillis();
                Canvas canvas=null;
                try {
                    canvas=holder.lockCanvas();
                    if (canvas!=null){
                       doDraw(canvas);
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
                Logger.d("------机器人当前位置绘制耗时："+time);
                if (time<500){
                    try {
                        Thread.sleep(500-time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * 绘制激光雷达
     * @param canvas
     */
    private void doDraw(Canvas canvas){
        canvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR);
        scale=zoomLayout.getScale();
        posX=(float)t0+(measureWidth-bitmapWidth)/2;
        posY=(float)t1+(measureHeight-bitmapHeight)/2;
        if (obstacleInfos!=null){
            int size =obstacleInfos.size();
            for (int i=0;i<size;i++){
                double angle=Math.toRadians(obstacleInfos.get(i).getStartAngle());  //角度转弧度
                float distance=obstacleInfos.get(i).getDist();
                if (distance<1950&&distance>1){
                    distance=distance/100*scale;
                    float x=0,y=0;
                    x=(float)(distance*Math.cos(angle));
                    y=(float)(distance*Math.sin(angle));
                    XyEntity xyEntity1=toXorY(x,y);
                    canvas.drawLine(posX,posY,xyEntity1.getX(),xyEntity1.getY(),paint);
                }
            }
            canvas.drawBitmap(directionBitmap, posX-30, posY-30, paint);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
