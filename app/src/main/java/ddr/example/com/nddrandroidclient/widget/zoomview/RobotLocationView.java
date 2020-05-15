package ddr.example.com.nddrandroidclient.widget.zoomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.List;

import DDRModuleProto.DDRModuleCmd;
import DDRVLNMapProto.DDRVLNMap;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.widget.zoomview.TouchEvenHandler;

/**
 * time :2020/05/13
 * desc :绘制机器人当前位置和雷达射线
 */
public class RobotLocationView extends SurfaceView implements SurfaceHolder.Callback {
    private TouchEvenHandler touchEvenHandler;
    private int mBackColor = Color.TRANSPARENT;       //背景色透明
    private Bitmap directionBitmap;
    private Paint paint;
    private float scale=1;                             //地图缩放的比例
    private int directionW,directionH;
    private Bitmap sourceBitmap;
    private MapFileStatus mapFileStatus;
    private double r00 = 0;
    private double r01 = -61.5959;
    private double t0 = 375.501;
    private double r10 = -61.6269;
    private double r11 = 0;
    private double t1 = 410.973;
    private float posX,posY;
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
        holder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
        mapFileStatus=MapFileStatus.getInstance();
        directionBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        paint = new Paint();
        paint.setColor(Color.parseColor("#00CED1"));
        directionW=directionBitmap.getWidth();
        directionH=directionBitmap.getHeight();
        EventBus.getDefault().register(this);
    }

    /**
     * 设置图片
     * @param sourceBitmap
     */
    public void setImageBitmap(Bitmap sourceBitmap){
        this.sourceBitmap=sourceBitmap;
        initAffine();
    }

    /**
     * 设置图片的存储地址
     * @param path
     */
    public void setImageBitmap(String path){
        Logger.e("设置图片");
        String pngPath = GlobalParameter.ROBOT_FOLDER + path + "/" + "bkPic.png";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(pngPath);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            sourceBitmap = bitmap;
            Logger.e("图片的宽高：" + sourceBitmap.getWidth() + "；" + sourceBitmap.getHeight());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        initAffine();
    }

    /**
     * 初始化地图的矩阵参数
     */
    private void initAffine(){
        touchEvenHandler=new TouchEvenHandler(this,sourceBitmap,false);
        MapFileStatus mapFileStatus=MapFileStatus.getInstance();
        DDRVLNMap.affine_mat affine_mat=mapFileStatus.getAffine_mat();
        r00=affine_mat.getR11();
        r01=affine_mat.getR12();
        t0=affine_mat.getTx();
        r10=affine_mat.getR21();
        r11=affine_mat.getR22();
        t1=affine_mat.getTy();
    }
    /**
     * 世界坐标——>相对于图片像素坐标
     * @param x
     * @param y
     * @return
     */
    public XyEntity toXorY(float x, float y){
        float x1=(float)( r00*x+r01*y+t0);
        float y1=(float) (r10*x+r11*y+t1);
        return new XyEntity(x1,y1);
    }

    /**
     * 从世界坐标直接得到相对于画布的坐标
     * @param x
     * @param y
     * @return
     */
    public XyEntity toCanvas(float x,float y){
        //世界坐标转成相对于图片位置的像素坐标
        XyEntity xyEntity=toXorY(x,y);
        //再将相对于图片的位置转成相对于画布的位置
        return touchEvenHandler.coordinatesToCanvas(xyEntity.getX(),xyEntity.getY());
    }
    /**
     * 将相对于画布坐标转成世界坐标
     * @param x
     * @param y
     * @return
     */
    public XyEntity toWorld(float x,float y){
        //先将相对于画布的坐标转成相对于图片的坐标
        XyEntity xyEntity=touchEvenHandler.coordinatesToImage(x,y);
        return toPathXy(xyEntity.getX(),xyEntity.getY());
    }

    /**
     * 将相对于图片的像素坐标变成（世界坐标）
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
     * 获取地图旋转的弧度
     * @return
     */
    public float getRadians(){
        return (float) touchEvenHandler.getRadians();
    }

    /**
     * 获得当前机器人在窗口的位置
     * @return
     */
    public XyEntity getRobotLocationInWindow(){
        XyEntity xyEntity=toXorY(0,0);
        float x=xyEntity.getX()+touchEvenHandler.getOriginalX();
        float y=xyEntity.getY()+touchEvenHandler.getOriginalY();
        xyEntity.setX(x);
        xyEntity.setY(y);
        return xyEntity;
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
        if (drawLocationThread!=null&&isRunning){
            isRunning=false;
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
                //Logger.d("------机器人当前位置绘制耗时："+time);
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
     * 绘制激光雷达
     * @param canvas
     */
    private void doDraw(Canvas canvas){
        canvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(sourceBitmap,touchEvenHandler.getMatrix(),paint);
        scale= (float) touchEvenHandler.getZoomX();
        XyEntity xyEntity=getRobotLocationInWindow();
        posX=xyEntity.getX();
        posY=xyEntity.getY();
        if (obstacleInfos!=null){
            int size =obstacleInfos.size();
            for (int i=0;i<size;i++){
                double angle=Math.toRadians(-obstacleInfos.get(i).getStartAngle());  //角度转弧度
                float distance=obstacleInfos.get(i).getDist();
                if (distance<1950&&distance>1){
                    distance=distance/100*scale;
                    float x=0,y=0;
                    x=(float)(distance*Math.cos(angle));
                    y=(float)(distance*Math.sin(angle));
                    XyEntity xyEntity1=toXorY(x,y);
                    float originalX=touchEvenHandler.getOriginalX();
                    float originalY=touchEvenHandler.getOriginalY();
                    canvas.drawLine(posX,posY,xyEntity1.getX()+originalX,xyEntity1.getY()+originalY,paint);
                }
            }
            canvas.drawBitmap(directionBitmap, posX-directionW/2, posY-directionH/2, paint);
        }
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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            // 分别获取到ImageView的宽度和高度
            float width=getWidth();
            float height=getHeight();
            touchEvenHandler=new TouchEvenHandler(this,sourceBitmap,false);
            Logger.e("布局大小发生改变:"+width+";"+height);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        touchEvenHandler.touchEvent(event);
        return true;
    }

    private List<DDRModuleCmd.rspObstacleInfo.ObstacleInfo> obstacleInfos;    //雷达当前扫到的点云
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upDate(MessageEvent mainUpDate) {
        switch (mainUpDate.getType()) {
            case receiveObstacleInfo:
                obstacleInfos= (List<DDRModuleCmd.rspObstacleInfo.ObstacleInfo>) mainUpDate.getData();
                //Logger.e("--------接收雷达数据");
                break;
        }
    }
}
