package ddr.example.com.nddrandroidclient.widget.zoomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time :2020/05/13
 * desc :绘制机器人当前位置和雷达射线
 */
public class RobotLocationView extends SurfaceView implements SurfaceHolder.Callback {
    private TouchEvenHandler touchEvenHandler;
    private int mBackColor = Color.TRANSPARENT;       //背景色透明
    private Bitmap directionBitmap;
    private Paint paint,textPaint,linePaint;
    private float scale=1;                             //地图缩放的比例
    private int directionW,directionH,bitmapW,bitmapH;
    private Bitmap sourceBitmap;
    private MapFileStatus mapFileStatus;
    private double r00 = 0;
    private double r01 = -61.5959;
    private double t0 = 375.501;
    private double r10 = -61.6269;
    private double r11 = 0;
    private double t1 = 410.973;
    private float posX,posY;
    private List<TargetPoint> targetPoints1;
    private List<PathLine> pathLines1;
    private Bitmap targetBitmap;
    private Matrix matrix=new Matrix();
    private Bitmap startBitmap,endBitmap;


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
        targetBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.target_point);
        startBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.start_default);
        endBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.end_defalut);
        paint = new Paint();
        paint.setColor(Color.parseColor("#00CED1"));
        textPaint=new Paint();
        textPaint.setStrokeWidth(8);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(16);
        linePaint=new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(3);
        linePaint.setAntiAlias(true);
        directionW=directionBitmap.getWidth();
        directionH=directionBitmap.getHeight();
        bitmapW=startBitmap.getWidth();
        bitmapH=startBitmap.getHeight();
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
     * 显示被选中的点（多选）
     * @param targetPoints
     */
    public void setTargetPoints(List<TargetPoint> targetPoints){
        this.targetPoints1=targetPoints;
    }

    /**
     * 设置需要显示的路径（多选）
     * @param pathLines
     */
    public void setPathLines(List<PathLine> pathLines){
        this.pathLines1=pathLines;
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
        touchEvenHandler=new TouchEvenHandler.Builder()
                .setView(this)
                .setBitmap(sourceBitmap)
                .build();
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

    /*
     * 从世界坐标直接得到相对于画布的坐标
     * @param x
     * @param y
     * @return
     * */
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
        //Logger.e("---------x"+touchEvenHandler.getOriginalX()+";"+touchEvenHandler.getOriginalY());
        float x=xyEntity.getX()*touchEvenHandler.getInitRatio()+touchEvenHandler.getOriginalX();
        float y=xyEntity.getY()*touchEvenHandler.getInitRatio()+touchEvenHandler.getOriginalY();
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
                       drawPoint(canvas);
                       drawPath(canvas);
                       drawGrid(canvas);
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
                double angle=Math.toRadians(obstacleInfos.get(i).getStartAngle());  //角度转弧度
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


    private void drawPoint(Canvas canvas){
        if (targetPoints1 != null) {
            for (int i=0;i<targetPoints1.size();i++){
                if (targetPoints1.get(i).isMultiple()){
                    XyEntity xyEntity=toCanvas(targetPoints1.get(i).getX(),targetPoints1.get(i).getY());
                    float x= xyEntity.getX();
                    float y=  xyEntity.getY();
                    matrix.reset();
                    matrix.postTranslate(x,y);
                    matrix.postRotate(-targetPoints1.get(i).getTheta()+(float) touchEvenHandler.getAngle(),xyEntity.getX(),xyEntity.getY());
                    canvas.drawBitmap(targetBitmap,matrix,paint);
                    canvas.drawText(targetPoints1.get(i).getName(),xyEntity.getX(),xyEntity.getY()+15,textPaint);
                }
            }
        }
    }

    private void drawPath(Canvas canvas){
        if (pathLines1!=null){
            for (int i=0;i<pathLines1.size();i++){
                if (pathLines1.get(i).isMultiple()){
                    List<PathLine.PathPoint> pathPoints=pathLines1.get(i).getPathPoints();
                    for (int j=0;j<pathPoints.size();j++){
                        if (pathPoints.size()>1){
                            XyEntity xyEntity1=toCanvas(pathPoints.get(j).getX(),pathPoints.get(j).getY());
                            if (j<pathPoints.size()-1){
                                XyEntity xyEntity2=toCanvas(pathPoints.get(j+1).getX(),pathPoints.get(j+1).getY());
                                canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),linePaint);
                            }
                            if (j==0){
                                canvas.drawBitmap(startBitmap,xyEntity1.getX()-bitmapW/2,xyEntity1.getY()-bitmapH/2,linePaint);
                                canvas.drawText(pathPoints.get(j).getName(),xyEntity1.getX(),xyEntity1.getY()+15,textPaint);
                            }else if (j==pathPoints.size()-1){
                                canvas.drawBitmap(endBitmap,xyEntity1.getX()-bitmapW/2,xyEntity1.getY()-bitmapH/2,linePaint);
                                canvas.drawText(pathPoints.get(j).getName(),xyEntity1.getX(),xyEntity1.getY()+15,textPaint);
                            }else {
                                canvas.drawCircle(xyEntity1.getX(),xyEntity1.getY(),8,linePaint);
                                canvas.drawText(pathPoints.get(j).getName(),xyEntity1.getX(),xyEntity1.getY()+15,textPaint);
                            }
                        }
                    }

                }
            }

        }

    }

    public void drawGrid(Canvas canvas){
        if (precision!=0){
            int viewWidth=getWidth();       //得到画布的宽
            int viewHeight=getHeight();     //得到画布的高
            pixIntervalX=precision/Math.abs(1/r01)*touchEvenHandler.getZoomX();
            prxIntervalY=precision/Math.abs(1/r10)*touchEvenHandler.getZoomX();
            //画横线
            if (prxIntervalY!=0&&pixIntervalX!=0){
                for (int i=0;i<viewHeight/prxIntervalY;i++){
                    canvas.drawLine(0,(float) (i*prxIntervalY),viewWidth,(float)(i*prxIntervalY),paint);
                }
                //画竖线
                for (int i = 0; i < viewWidth / pixIntervalX; i++) {
                    canvas.drawLine((float) (i * pixIntervalX), 0, (float)(i * pixIntervalX), viewHeight, paint);
                }
            }
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
            touchEvenHandler=new TouchEvenHandler.Builder()
                    .setView(this)
                    .setBitmap(sourceBitmap)
                    .build();
            Logger.e("布局大小发生改变:"+width+";"+height);
        }
    }

    /**
     * 点击事件
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (sourceBitmap!=null){
            if (touchEvenHandler!=null){
                touchEvenHandler.touchEvent(event);
            }else {
                touchEvenHandler=new TouchEvenHandler.Builder()
                        .setView(this)
                        .setBitmap(sourceBitmap)
                        .build();
                Logger.e("---------onTouchEvent");
            }
        }
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
    private double pixIntervalX,prxIntervalY;
    private float precision=0;
    public void setPrecision(float precision){
        this.precision=precision;
    }
}
