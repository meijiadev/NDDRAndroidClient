package ddr.example.com.nddrandroidclient.widget.zoomview;

import android.annotation.SuppressLint;
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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.SpaceItem;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * desc: 基于SurfaceView的实时绘制当前机器人位置和路线的图片
 * time: 2020/5/12
 */
public class MapImageView extends SurfaceView implements SurfaceHolder.Callback {
    // Matrix getValues 矩阵参数
    private float[] values=new float[9];
    private TouchEvenHandler touchEvenHandler;
    private SurfaceHolder holder;
    private int mBackColor = Color.TRANSPARENT;       //背景色透明
    //承载点云数据的基类，并保存最新一帧的数据
    private NotifyLidarPtsEntity notifyLidarPtsEntity;
    private List<BaseCmd.notifyLidarPts.Position> positionList=new ArrayList<>();    //雷达当前扫到的点云
    private Paint radarPaint,paint,linePaint1,textPaint;
    private Matrix mapMatrix;
    private Bitmap directionBitmap,directionBitmap1;
    private int directionW,directionH;
    private Bitmap sourceBitmap;
    private Bitmap startBitmap,endBitmap;
    private Bitmap targetBitmap; //目标点
    private TargetPoint targetPoint;         //目标点 AB点模式的B点
    private MapFileStatus mapFileStatus;
    private List<SpaceItem> spaceItems;                    //虚拟墙
    private DDRVLNMap.reqDDRVLNMapEx data;
    private List<TaskMode> taskModes=new ArrayList<>();    // 任务列表
    private List<PathLine> pathLines=new ArrayList<>();   //经过转换坐标的路径
    private boolean isRunAbPointLine;
    public double r00=0;
    public double r01=-61.5959;
    public double t0=375.501;
    public double r10=-61.6269;
    public double r11=0;
    public double t1=410.973;
    private Matrix matrix = new Matrix();//对图片进行移动和缩放变换的矩阵
    private NotifyBaseStatusEx notifyBaseStatusEx;

    public MapImageView(Context context) {
        super(context);
        init();

    }
    public MapImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 显示将要去的目标点
     * @param targetPoint
     */
    public void setTargetPoint(TargetPoint targetPoint){
        this.targetPoint=targetPoint;
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
        //Logger.e("设置图片");
        String pngPath = GlobalParameter.ROBOT_FOLDER + path + "/" + "bkPic.png";
        if (fileIsExits(pngPath)){
            try {
                Mat mat= Imgcodecs.imread(pngPath,Imgcodecs.IMREAD_UNCHANGED);
                Mat mat1=new Mat();
                //先将BRGMat转成RGB格式，再转换成图片
                Imgproc.cvtColor(mat,mat1,Imgproc.COLOR_BGR2RGB);
                mat.release();
                sourceBitmap=Bitmap.createBitmap(mat1.width(),mat1.height(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat1,sourceBitmap);
                mat1.release();
                Logger.e("图片的宽高：" + sourceBitmap.getWidth() + "；" + sourceBitmap.getHeight());
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            initAffine();
        }
    }

    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    private boolean fileIsExits(String path){
        try {
            File file=new File(path);
            if (!file.exists()){
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private List<BaseMode> baseModes;
    private List<PathLine.PathPoint> taskPoints;      //将要绘制的点路径
    /**
     *设置行走的路径
     * @param taskName
     */
    public void setTaskName(String taskName){
        if (!taskName.equals("PathError")){
            data=mapFileStatus.getCurrentMapEx();
            taskModes=mapFileStatus.getcTaskModes();
            List<PathLine> pathLineList;
            if (isRunAbPointLine){
                pathLineList=mapFileStatus.getABPaths();
            }else {
                pathLineList=mapFileStatus.getcPathLines();
            }
            try {
                for (TaskMode taskMode1:taskModes){
                    if (taskName.equals(taskMode1.getName())){
                        baseModes=taskMode1.getBaseModes();
                    }
                }
                if (baseModes!=null&&baseModes.size()>0){
                    taskPoints=new ArrayList<>();
                    for (int i=0;i<baseModes.size();i++){
                        BaseMode baseMode=baseModes.get(i);
                        if (baseMode.getType()==1){
                            PathLine pathLine= (PathLine) baseMode;
                            String lineName=pathLine.getName();
                            List<PathLine.PathPoint> pathPoints=new ArrayList<>();
                            for (PathLine pathLine1:pathLineList){
                                if (lineName.equals(pathLine1.getName())){
                                    pathPoints=pathLine1.getPathPoints();
                                    //Logger.d("当前选择的路径名称："+lineName);
                                }
                            }
                            taskPoints.addAll(pathPoints);
                        }else if (baseMode.getType()==2){
                            TargetPoint targetPoint= (TargetPoint) baseMode;
                            List<TargetPoint> targetPoints=mapFileStatus.getcTargetPoints();
                            for (TargetPoint targetPoint1:targetPoints ){
                                if (targetPoint.getName().equals(targetPoint1.getName())){
                                    targetPoint=targetPoint1;
                                    PathLine.PathPoint pathPoint=new PathLine.PathPoint();
                                    pathPoint.setName(targetPoint.getName());
                                    pathPoint.setX(targetPoint.getX());
                                    pathPoint.setY(targetPoint.getY());
                                    taskPoints.add(pathPoint);
                                }
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    /**
     * 是否显示ab点路径
     */
    public void setABPointLine(boolean isRunAbPointLine){
        Logger.e("是否在跑AB点路径："+isRunAbPointLine);
        taskPoints=null;
        this.isRunAbPointLine=isRunAbPointLine;
    }

    /**
     * 初始化参数
     */
    private void init(){
        holder=getHolder();
        holder.addCallback(this);
        //setZOrderOnTop(true);
        //holder.setFormat(PixelFormat.TRANSPARENT);//设置背景透明
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        mapFileStatus=MapFileStatus.getInstance();
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        radarPaint=new Paint();
        radarPaint.setStrokeWidth(1);
        radarPaint.setColor(Color.parseColor("#6600CED1"));
        paint=new Paint();
        linePaint1=new Paint();
        linePaint1.setStrokeWidth(3);
        linePaint1.setColor(Color.BLACK);
        linePaint1.setAntiAlias(true);
        textPaint=new Paint();
        textPaint.setStrokeWidth(8);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(16);
        textPaint.setAntiAlias(true);
        mapMatrix=new Matrix();
        targetBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.target_point);
        directionBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        startBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.start_default);
        endBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.end_defalut);
        /*directionW=directionBitmap.getWidth();
        directionH=directionBitmap.getHeight();*/
    }

    /**
     * 初始化地图的矩阵参数
     */
    private void initAffine(){
        touchEvenHandler=new TouchEvenHandler.Builder()
                .setView(this)
                .setBitmap(sourceBitmap)
                .setAuRefresh(true)
                .build();
        //touchEvenHandler.setCanRotate(false);
        try {
            DDRVLNMap.affine_mat affine_mat=mapFileStatus.getAffine_mat();
            r00=affine_mat.getR11();
            r01=affine_mat.getR12();
            t0=affine_mat.getTx();
            r10=affine_mat.getR21();
            r11=affine_mat.getR22();
            t1=affine_mat.getTy();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
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
     * 绘制雷达扫到的区域
     * @param canvas
     */
    private void drawRadarLine(Canvas canvas){
        if (sourceBitmap!=null){
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            canvas.drawColor(Color.parseColor("#101112"));
            canvas.drawBitmap(sourceBitmap,touchEvenHandler.getMatrix(),paint);
            positionList = notifyLidarPtsEntity.getPositionList();
            //Logger.d("-------点云数量：" + positionList.size());
            int size = positionList.size();
            if (positionList != null && size > 0) {
                XyEntity xyEntity1 = toCanvas(notifyLidarPtsEntity.getPosX(), notifyLidarPtsEntity.getPosY());
                for (int i = 0; i < size; i++) {
                    XyEntity xyEntity = toCanvas(positionList.get(i).getPtX(), positionList.get(i).getPtY());
                    canvas.drawLine(xyEntity1.getX(), xyEntity1.getY(), xyEntity.getX(), xyEntity.getY(), radarPaint);
                }
                float angle = -radianToangle(notifyLidarPtsEntity.getPosdirection()) + (float)touchEvenHandler.getAngle();
                float cx = xyEntity1.getX()-directionBitmap.getWidth()/2;
                float cy = xyEntity1.getY()-directionBitmap.getHeight()*13/20;
                mapMatrix.reset();
                mapMatrix.postTranslate(cx,cy);
                mapMatrix.postRotate(angle,xyEntity1.getX(),xyEntity1.getY());
                //Logger.d("------------机器人当前在地图上的位置（像素）:" + cx + ";" + cy);
                canvas.drawBitmap(directionBitmap,mapMatrix,paint);
            }
        }
    }

    /**
     * 绘制路径和点
     */
    private void drawLine(Canvas canvas){
        if (taskPoints!=null){
            for (int j = 0; j < taskPoints.size(); j++) {
                XyEntity xyEntity1 = toCanvas(taskPoints.get(j).getX(), taskPoints.get(j).getY());
                canvas.drawBitmap(startBitmap, xyEntity1.getX() - startBitmap.getWidth() / 2, xyEntity1.getY() - startBitmap.getHeight() / 2, paint);
                if (taskPoints.size() > 1) {
                    if (j < taskPoints.size() - 1) {
                        XyEntity xyEntity2 = toCanvas(taskPoints.get(j + 1).getX(), taskPoints.get(j + 1).getY());
                        canvas.drawLine(xyEntity1.getX(), xyEntity1.getY(), xyEntity2.getX(), xyEntity2.getY(), paint);
                    }
                    if (j == 0) {
                        canvas.drawBitmap(startBitmap, xyEntity1.getX() - startBitmap.getWidth() / 2, xyEntity1.getY() - startBitmap.getHeight() / 2, paint);
                        canvas.drawText(taskPoints.get(j).getName(), xyEntity1.getX(), xyEntity1.getY() + 15, textPaint);
                    } else if (j == taskPoints.size() - 1) {
                        canvas.drawBitmap(endBitmap, xyEntity1.getX() - endBitmap.getWidth() / 2, xyEntity1.getY() - endBitmap.getHeight() / 2, paint);
                        canvas.drawText(taskPoints.get(j).getName(), xyEntity1.getX(), xyEntity1.getY() + 15, textPaint);
                    } else {
                        canvas.drawCircle(xyEntity1.getX(), xyEntity1.getY(), 8, paint);
                        canvas.drawText(taskPoints.get(j).getName(), xyEntity1.getX(), xyEntity1.getY() + 15, textPaint);
                    }
                }
            }

        }
        if (targetPoint!=null){
            XyEntity xyEntity=toCanvas(targetPoint.getX(),targetPoint.getY());
            int x= (int) xyEntity.getX();
            int y= (int) xyEntity.getY();
            matrix.setRotate(-targetPoint.getTheta());
            Bitmap targetBitmap2=Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getHeight(),matrix,true);
            canvas.drawBitmap(targetBitmap2,x -targetBitmap2.getWidth()/2,y-targetBitmap2.getHeight()/2,paint);
            canvas.drawText(targetPoint.getName(),x,y+15,textPaint);
        }
    }


    /**
     * 绘制虚拟墙
     */
    private void onDrawWall(Canvas canvas){
        spaceItems=mapFileStatus.getcSpaceItems();
        if (spaceItems!=null&&spaceItems.size()>0){
            //Logger.e("绘制虚拟墙");
            for (int i=0;i<spaceItems.size();i++){
                List<DDRVLNMap.space_pointEx> space_pointExes=spaceItems.get(i).getLines();
                for (int j=0;j<space_pointExes.size();j++){
                    if (j<space_pointExes.size()-1){
                        XyEntity xyEntity1=toCanvas(space_pointExes.get(j).getX(),space_pointExes.get(j).getY());
                        XyEntity xyEntity2=toCanvas(space_pointExes.get(j+1).getX(),space_pointExes.get(j+1).getY());
                        canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),linePaint1);                    }
                }
            }
        }
    }



    /**
     * 绘制
     */
    private void doDraw(){
        long startTime=System.currentTimeMillis();
        Canvas canvas=null;
        try {
            canvas=holder.lockCanvas();
            if (canvas!=null&&sourceBitmap!=null){
                if (isRunAbPointLine) {
                    setTaskName("AB_Task.task");
                }else {
                    //Logger.e("---设置任务"+notifyBaseStatusEx.getCurrpath());
                    setTaskName(notifyBaseStatusEx.getCurrpath());
                }
                drawRadarLine(canvas);
                drawLine(canvas);
                onDrawWall(canvas);
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
        if (time<100){
            try {
                Thread.sleep(100-time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Logger.i("------------绘制时间："+time);
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
     * 开始绘制
     */
    public void startThread(){
        drawThread=new DrawThread();
        drawThread.start();
        Logger.e("开启线程");
    }
    /**
     * 停止绘制
     */
    public void onStop(){
        if (drawThread!=null){
            Logger.e("线程停止");
            drawThread.stopThread();
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
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //Logger.e("surfaceView的大小："+width+";"+height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (sourceBitmap!=null){
            if (touchEvenHandler!=null){
                touchEvenHandler.touchEvent(event);
            }else {
                touchEvenHandler=new TouchEvenHandler.Builder()
                        .setView(this)
                        .setBitmap(sourceBitmap)
                        .setAuRefresh(true)
                        .build();
            }
        }
        return true;
    }
}
