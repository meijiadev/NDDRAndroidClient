package ddr.example.com.nddrandroidclient.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.google.protobuf.ByteString;
import org.greenrobot.eventbus.EventBus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import DDRVLNMapProto.DDRVLNMap;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;

import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.SpaceItem;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

@SuppressLint("AppCompatCustomView")
public class MapImageView0 extends ImageView {
    private String mapName="";
    public static final int STATUS_INIT = 1;//常量初始化
    public static final int STATUS_ZOOM_OUT = 2;//图片放大状态常量
    public static final int STATUS_ZOOM_IN = 3;//图片缩小状态常量
    public static final int STATUS_MOVE = 4;//图片拖动状态常量
    private Matrix matrix = new Matrix();//对图片进行移动和缩放变换的矩阵
    private Bitmap sourceBitmap;//待展示的Bitmap对象
    private int currentStatus;//记录当前操作的状态，可选值为STATUS_INIT、STATUS_ZOOM_OUT、STATUS_ZOOM_IN和STATUS_MOVE
    public int width;//ZoomImageView控件的宽度
    public int height;//ZoomImageView控件的高度
    private int measureWidth,measureHeight;
    private float centerPointX;//记录两指同时放在屏幕上时，中心点的横坐标值
    private float centerPointY;//记录两指同时放在屏幕上时，中心点的纵坐标值
    private float currentBitmapWidth;//记录当前图片的宽度，图片被缩放时，这个值会一起变动
    private float currentBitmapHeight;//记录当前图片的高度，图片被缩放时，这个值会一起变动
    private float lastXMove = -1;//记录上次手指移动时的横坐标
    private float lastYMove = -1;//记录上次手指移动时的纵坐标
    private float movedDistanceX;//记录手指在横坐标方向上的移动距离
    private float movedDistanceY;//记录手指在纵坐标方向上的移动距离
    private float totalTranslateX=0;//记录图片在矩阵上的横向偏移值 图片左上角顶点相当于画布的X坐标
    private float totalTranslateY=0;//记录图片在矩阵上的纵向偏移值 图片左上角顶点相当于画布的Y坐标
    public float totalRatio;//记录图片在矩阵上的总缩放比例
    private float scaledRatio;//记录手指移动的距离所造成的缩放比例
    private float initRatio;//记录图片初始化时的缩放比例
    private double lastFingerDis=0;//记录上次两指之间的距离
    private float degree=0;    //旋转角度
    private float rotation=0;      //旋转的角度

    private NotifyBaseStatusEx notifyBaseStatusEx;
    public double r00=0;
    public double r01=-61.5959;
    public double t0=375.501;

    public double r10=-61.6269;
    public double r11=0;
    public double t1=410.973;
    private TargetPoint targetPoint;         //目标点
    private MapFileStatus mapFileStatus;
    private Bitmap targetBitmap,targetBitmap1; //目标点
    private Bitmap directionBitmap,directionBitmap1;
    private Bitmap startBitamap,endBitamp;
    private Paint paint,radarPaint,linePaint1,textPaint;

    private List<DDRVLNMap.targetPtItem> targetPtItems;          // 目标点列表
    private List<DDRVLNMap.path_line_itemEx> pathLineItemExes;  // 路径列表
    private List<DDRVLNMap.task_itemEx> taskItemExes;          //  任务列表
    private List<DDRVLNMap.path_elementEx> pathElementExes;    // 任务列表的元素
    private List<DDRVLNMap.path_line_itemEx> pathLineItemExesS;  // 路径列表  选中的任务中包含的路径
    private List<DDRVLNMap.targetPtItem> targetPtItemsS;          // 目标点列表 选中的任务中包含的目标点
    private List<SpaceItem> spaceItems;
    private DDRVLNMap.reqDDRVLNMapEx data;

    private List<PathLine> pathLines=new ArrayList<>();   //经过转换坐标的路径
    private boolean isRunAbPointLine;
    private boolean isAutoPlanning=true;           //是否是自主导航


    public MapImageView0(Context context) {
        super(context);
        init();
    }

    public MapImageView0(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 显示将要去的目标点
     * @param targetPoint
     */
    public void setTargetPoint(TargetPoint targetPoint){
        this.targetPoint=targetPoint;
        invalidate();
    }


    /**
     * 设置地图
     * @param mapName
     */
    public void setMapBitmap(String mapName){
        if (!this.mapName.equals(mapName)){
            this.mapName=mapName;
            Logger.e("设置图片");
            String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人" + "/" + mapName + "/" + "bkPic.png";
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(pngPath);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                sourceBitmap=bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            Logger.e("图片的宽高："+sourceBitmap.getWidth()+"；"+sourceBitmap.getHeight());
            totalTranslateX=0;
            MapFileStatus mapFileStatus=MapFileStatus.getInstance();
            data=mapFileStatus.getCurrentMapEx();
            DDRVLNMap.affine_mat affine_mat=data.getBasedata().getAffinedata();
            r00=affine_mat.getR11();
            r01=affine_mat.getR12();
            t0=affine_mat.getTx();
            r10=affine_mat.getR21();
            r11=affine_mat.getR22();
            t1=affine_mat.getTy();
            totalTranslateY=0;
            totalRatio=1;
            scaledRatio=1;
            initRatio=1;
            lastFingerDis=0;
            degree=0;
            rotation=0;
            currentStatus=STATUS_INIT;
        }
        invalidate();
    }


    /**
     *设置行走的路径
     * @param taskName
     */
    public void setTaskName(String taskName){
        isRunAbPointLine=false;
        targetPtItems=data.getTargetPtdata().getTargetPtList();
        pathLineItemExes=data.getPathSet().getPathLineDataList();
        taskItemExes=data.getTaskSetList();
        spaceItems=mapFileStatus.getcSpaceItems();
        pathLineItemExesS=new ArrayList<>();
        targetPtItemsS=new ArrayList<>();
        Logger.e("设置任务");
        try {
            for (int i=0;i<taskItemExes.size();i++){
                if (taskItemExes.get(i).getName().toStringUtf8().equals(taskName)){
                    pathElementExes=taskItemExes.get(i).getPathSetList();
                }
            }
            for (int i=0;i<pathElementExes.size();i++){
                if (pathElementExes.get(i).getType().equals(DDRVLNMap.path_element_type.ePathElementTypeLine)){
                    ByteString lineName=pathElementExes.get(i).getName();
                    for (int j=0;j<pathLineItemExes.size();j++){
                        if (lineName.equals(pathLineItemExes.get(j).getName())){
                            pathLineItemExesS.add(pathLineItemExes.get(j));
                        }
                    }
                }else if (pathElementExes.get(i).getType().equals(DDRVLNMap.path_element_type.ePathElementTypeActionPoint)){
                    ByteString pointName=pathElementExes.get(i).getName();
                    for (int j=0;j<targetPtItems.size();j++){
                        if (pointName.equals(targetPtItems.get(j).getPtName())){
                            targetPtItemsS.add(targetPtItems.get(j));
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        invalidate();

    }

    /**
     * 是否显示ab点路径
     */
    public void setABPointLine(boolean isRunAbPointLine){
        this.isRunAbPointLine=isRunAbPointLine;
        if (!isRunAbPointLine) {
            setTaskName(notifyBaseStatusEx.getCurrpath());
        }

        switch (mapFileStatus.getCurrentMapEx().getBasedata().getAbNaviTypeValue()){
            case 1:
                isAutoPlanning=false;
                break;
            case 2:
                isAutoPlanning=true;
                break;
        }
        invalidate();
    }


    /**
     * 初始化相关参数控件
     */
    private void init() {
        currentStatus = STATUS_INIT;
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        mapFileStatus=MapFileStatus.getInstance();
        directionBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        targetBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.action_default);
        targetBitmap1=BitmapFactory.decodeResource(getResources(), R.mipmap.target_point);
        startBitamap=BitmapFactory.decodeResource(getResources(), R.mipmap.start_default);
        endBitamp=BitmapFactory.decodeResource(getResources(),R.mipmap.end_defalut);
        paint=new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        radarPaint=new Paint();
        radarPaint.setColor(Color.parseColor("#00CED1"));
        radarPaint.setStrokeWidth(1);
        linePaint1=new Paint();
        linePaint1.setStrokeWidth(3);
        linePaint1.setColor(Color.BLACK);
        linePaint1.setAntiAlias(true);
        textPaint=new Paint();
        textPaint.setStrokeWidth(8);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(16);
        textPaint.setAntiAlias(true);



    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            // 分别获取到ZoomImageView的宽度和高度
            width = getWidth();
            height = getHeight();
            Logger.e("----画布的宽高："+width+";"+height);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (initRatio == totalRatio) {
            getParent().requestDisallowInterceptTouchEvent(false);
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Logger.e("----点击的坐标："+event.getX()+";"+event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    // 当有两个手指按在屏幕上时，计算两指之间的距离
                    lastFingerDis = distanceBetweenFingers(event);
                    degree=getDegree(event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:

            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    // 只有单指按在屏幕上移动时，为拖动状态
                    float xMove = event.getX();
                    float yMove = event.getY();
                    if (lastXMove == -1 && lastYMove == -1) {
                        lastXMove = xMove;
                        lastYMove = yMove;
                    }
                    currentStatus = STATUS_MOVE;
                    movedDistanceX = xMove - lastXMove;
                    movedDistanceY = yMove - lastYMove;
                    // 进行边界检查，不允许将图片拖出边界
                    Logger.e("地图左上角在画布中的坐标："+totalTranslateX+";"+totalTranslateY);
                    if (totalTranslateX + movedDistanceX > width/2) {
                        movedDistanceX = 0;
                    } else if (width - (totalTranslateX + movedDistanceX) > currentBitmapWidth+width/2) {
                        movedDistanceX = 0;
                    }
                    if (totalTranslateY + movedDistanceY > height/2) {
                        movedDistanceY = 0;
                    } else if (height - (totalTranslateY + movedDistanceY) > currentBitmapHeight+height/2) {
                        movedDistanceY = 0;
                    }
                    // 调用onDraw()方法绘制图片
                    invalidate();
                    lastXMove = xMove;
                    lastYMove = yMove;
                } else if (event.getPointerCount() == 2) {
                    // 有两个手指按在屏幕上移动时，为缩放状态
                    centerPointBetweenFingers(event);
                    double fingerDis = distanceBetweenFingers(event);
                    if (fingerDis > lastFingerDis) {
                        currentStatus = STATUS_ZOOM_OUT;
                    } else {
                        currentStatus = STATUS_ZOOM_IN;
                    }
                    rotation=rotation+getDegree(event)-degree;
                    if (rotation>360){
                        rotation=rotation-360;
                    }
                    if (rotation<-360){
                        rotation=rotation+360;
                    }
                    // 进行缩放倍数检查，最大只允许将图片放大6倍，最小可以缩小到初始化比例
                    if ((currentStatus == STATUS_ZOOM_OUT && totalRatio < 6 * initRatio)
                            || (currentStatus == STATUS_ZOOM_IN && totalRatio > initRatio)) {
                        scaledRatio = (float) (fingerDis / lastFingerDis);
                        totalRatio = totalRatio * scaledRatio;
                        if (totalRatio > 6 * initRatio) {
                            totalRatio = 6 * initRatio;
                        } else if (totalRatio < initRatio) {
                            totalRatio = initRatio;
                        }
                        // 调用onDraw()方法绘制图片
                        invalidate();
                        lastFingerDis = fingerDis;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 2) {
                    // 手指离开屏幕时将临时值还原
                    lastXMove = -1;
                    lastYMove = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 手指离开屏幕时将临时值还原
                lastXMove = -1;
                lastYMove = -1;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 根据currentStatus的值来决定对图片进行什么样的绘制操作。
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (sourceBitmap != null) {
            switch (currentStatus) {
                case STATUS_ZOOM_OUT:
                case STATUS_ZOOM_IN:
                    zoom(canvas);
                    break;
                case STATUS_MOVE:
                    Logger.e("--------移动图片");
                    move(canvas);
                    break;
                case STATUS_INIT:
                    initBitmap(canvas);
                default:
                    canvas.drawBitmap(sourceBitmap, matrix, null);
                    break;
            }
        }
        drawLine(canvas);
        onDrawWall(canvas);


    }
    /**
     *用于裁剪源图像的矩形（可重复使用）。
     */
    private Rect mRectSrc=new Rect(0,0,22,22);

    /**
     * 用于在画布上指定绘图区域的矩形（可重新使用）。
     */
    private Rect mRectDst;

    /**
     * 绘制路径和点
     */
    private void drawLine(Canvas canvas){
        if (isRunAbPointLine){
           drawAbLine(canvas);
        }else {
            if (pathLineItemExesS!=null){
                pathLines=new ArrayList<>();
                for (int i=0;i<pathLineItemExesS.size();i++){
                    List<PathLine.PathPoint> pathPoints=new ArrayList<>();
                    List<DDRVLNMap.path_line_itemEx.path_lint_pt_Item> path_lint_pt_items=pathLineItemExesS.get(i).getPointSetList();
                    for (int j=0;j<path_lint_pt_items.size();j++){
                        XyEntity xyEntity=toXorY(path_lint_pt_items.get(j).getPt().getX(),path_lint_pt_items.get(j).getPt().getY());
                        PathLine.PathPoint pathPoint=new PathLine().new PathPoint();
                        pathPoint.setX(xyEntity.getX());
                        pathPoint.setY(xyEntity.getY());
                        pathPoints.add(pathPoint);
                    }
                    PathLine pathLine=new PathLine();
                    pathLine.setPathPoints(pathPoints);
                    pathLines.add(pathLine);
                }
                //  Logger.e("----------路径数量："+pathLines.size());
                for (int i=0;i<pathLines.size();i++){
                    List<PathLine.PathPoint>pathPoints=pathLines.get(i).getPathPoints();
                    // Logger.e("------路径点数量:"+pathPoints.size());
                    for (int j=0;j<pathPoints.size();j++){
                        if (pathPoints.size()>1){
                            XyEntity xyEntity1=coordinate2View(pathPoints.get(j).getX(),pathPoints.get(j).getY());
                            if (j<pathPoints.size()-1){
                                XyEntity xyEntity2=coordinate2View(pathPoints.get(j+1).getX(),pathPoints.get(j+1).getY());
                                canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),paint);
                            }
                            // Logger.e("绘制路径");
                            if (j==0){
                                mRectDst=new Rect((int)xyEntity1.getX()-11,(int)xyEntity1.getY()-11,(int)xyEntity1.getX()+11,(int)xyEntity1.getY()+11);
                                canvas.drawBitmap(startBitamap,mRectSrc,mRectDst,paint);
                                canvas.drawText(pathPoints.get(j).getName(),xyEntity1.getX(),xyEntity1.getY()+15,textPaint);
                                // Logger.e("绘制起点"+xyEntity1.getX()+";"+xyEntity1.getY());
                            }else if (j==pathPoints.size()-1){
                                mRectDst=new Rect((int)xyEntity1.getX()-11,(int)xyEntity1.getY()-11,(int)xyEntity1.getX()+11,(int)xyEntity1.getY()+11);
                                canvas.drawBitmap(endBitamp,mRectSrc,mRectDst,paint);
                                canvas.drawText(pathPoints.get(j).getName(),xyEntity1.getX(),xyEntity1.getY()+15,textPaint);
                                // Logger.e("绘制终点");
                            }else {
                                canvas.drawCircle(xyEntity1.getX(),xyEntity1.getY(),8,paint);
                                canvas.drawText(pathPoints.get(j).getName(),xyEntity1.getX(),xyEntity1.getY()+15,textPaint);
                            }
                        }
                    }
                }
            }

        }
        if (targetPoint!=null){
            XyEntity xyEntity=toXorY(targetPoint.getX(),targetPoint.getY());
            xyEntity=coordinate2View(xyEntity.getX(),xyEntity.getY());
            int x= (int) xyEntity.getX();
            int y= (int) xyEntity.getY();
            matrix.setRotate(-targetPoint.getTheta());
            Bitmap targetBitmap2=Bitmap.createBitmap(targetBitmap1,0,0,40,40,matrix,true);
            canvas.drawBitmap(targetBitmap2,x -20,y-20,paint);
            canvas.drawText(targetPoint.getName(),x,y+15,textPaint);
        }
    }

    /**
     * 绘制AB点
     * @param canvas
     */
    private void drawAbLine(Canvas canvas){
        List<PathLine> pathLineList=mapFileStatus.getcPathLines();
        List<PathLine.PathPoint>pathPoints =null;
        for (PathLine pathLine:pathLineList){
            if (isAutoPlanning){
                if (pathLine.getName().equals("ABPointLine")){
                    pathPoints=pathLine.getPathPoints();
                    Logger.e("------绘制AB点路径");
                    drawABLine1(canvas,pathPoints);
                    break;
                }
            }else {
                if (pathLine.getName().equals("DijkstraLine")){
                    pathPoints=pathLine.getPathPoints();
                    Logger.e("------绘制AB点路径");
                    drawABLine1(canvas,pathPoints);
                    break;
                }
            }
        }
    }

    /**
     * 绘制AB点路径和巡线路径
     * @param canvas
     * @param pathPoints
     */
    private void drawABLine1(Canvas canvas, List<PathLine.PathPoint>pathPoints ){
        for (int i=0;i<pathPoints.size();i++){
            if (pathPoints.size()>1){
                XyEntity xyEntity1=toXorY(pathPoints.get(i).getX(),pathPoints.get(i).getY());
                xyEntity1=coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                if (i<pathPoints.size()-1){
                    XyEntity xyEntity2= toXorY(pathPoints.get(i+1).getX(),pathPoints.get(i+1).getY());
                    xyEntity2=coordinate2View(xyEntity2.getX(),xyEntity2.getY());
                    canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),paint);
                }
                Logger.e("------ab:"+xyEntity1.getX()+";"+xyEntity1.getY());
                if (i==0){
                    mRectDst=new Rect((int)xyEntity1.getX()-11,(int)xyEntity1.getY()-11,(int)xyEntity1.getX()+11,(int)xyEntity1.getY()+11);
                    canvas.drawBitmap(startBitamap,mRectSrc,mRectDst,paint);
                    canvas.drawText(pathPoints.get(i).getName(),xyEntity1.getX(),xyEntity1.getY()+15,textPaint);
                }else if (i==pathPoints.size()-1){
                    mRectDst=new Rect((int)xyEntity1.getX()-11,(int)xyEntity1.getY()-11,(int)xyEntity1.getX()+11,(int)xyEntity1.getY()+11);
                    canvas.drawBitmap(endBitamp,mRectSrc,mRectDst,paint);
                    canvas.drawText(pathPoints.get(i).getName(),xyEntity1.getX(),xyEntity1.getY()+15,textPaint);
                }else {
                    canvas.drawCircle(xyEntity1.getX(),xyEntity1.getY(),8,paint);
                    canvas.drawText(pathPoints.get(i).getName(),xyEntity1.getX(),xyEntity1.getY()+15,textPaint);
                }
            }
        }
    }

    /**
     * 绘制虚拟墙
     */
    private void onDrawWall(Canvas canvas){
        if (spaceItems!=null){
            for (int i=0;i<spaceItems.size();i++){
                List<DDRVLNMap.space_pointEx> space_pointExes=spaceItems.get(i).getLines();
                for (int j=0;j<space_pointExes.size();j++){
                    if (j<space_pointExes.size()-1){
                        XyEntity xyEntity1=toXorY(space_pointExes.get(j).getX(),space_pointExes.get(j).getY());
                        xyEntity1=coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                        XyEntity xyEntity2=toXorY(space_pointExes.get(j+1).getX(),space_pointExes.get(j+1).getY());
                        xyEntity2=coordinate2View(xyEntity2.getX(),xyEntity2.getY());
                        canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),linePaint1);                    }
                }
            }
        }
    }


    public void clearDraw(){
        targetPoint=null;
    }

    /**
     * 将相对于图片的坐标转换成画布坐标
     * @return
     */
    public XyEntity coordinate2View(float x,float y){
        float cx=x*totalRatio+totalTranslateX;
        float cy=y*totalRatio+totalTranslateY;
        // Logger.e("-----像素坐标："+cx+";"+cy);
        return new XyEntity(cx,cy);
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

    /**
     * 世界坐标——>像素坐标
     * @param x
     * @param y
     * @return
     */
    public XyEntity toXorY(float x,float y){
        float x1=(float)( r00*x+r01*y+t0);
        float y1=(float) (r10*x+r11*y+t1);
        return new XyEntity(x1,y1);
    }

    private float txfloat(float a,float b) {
        DecimalFormat df=new DecimalFormat("0.0000");//设置保留位数
        return Float.parseFloat(df.format((float)a/b));
    }
    /**
     * 对图片进行缩放处理。
     *
     * @param canvas
     */
    private void zoom(Canvas canvas) {
        matrix.reset();
        // 将图片按总缩放比例进行缩放
        matrix.postScale(totalRatio, totalRatio);
        //Logger.e("-----缩放比例："+totalRatio+";");
        float scaledWidth = sourceBitmap.getWidth() * totalRatio;
        float scaledHeight = sourceBitmap.getHeight() * totalRatio;
        float translateX = 0f;
        float translateY = 0f;
        // 如果当前图片宽度小于屏幕宽度，则按屏幕中心的横坐标进行水平缩放。否则按两指的中心点的横坐标进行水平缩放
        if (currentBitmapWidth < width) {
            translateX = (width - scaledWidth) / 2f;
        } else {
            translateX = totalTranslateX * scaledRatio + centerPointX * (1 - scaledRatio);
            // 进行边界检查，保证图片缩放后在水平方向上不会偏移出屏幕
            if (translateX > 0) {
                translateX = 0;
            } else if (width - translateX > scaledWidth) {
                translateX = width - scaledWidth;
            }
        }
        // 如果当前图片高度小于屏幕高度，则按屏幕中心的纵坐标进行垂直缩放。否则按两指的中心点的纵坐标进行垂直缩放
        if (currentBitmapHeight < height) {
            translateY = (height - scaledHeight) / 2f;
        } else {
            translateY = totalTranslateY * scaledRatio + centerPointY * (1 - scaledRatio);
            // 进行边界检查，保证图片缩放后在垂直方向上不会偏移出屏幕
            if (translateY > 0) {
                translateY = 0;
            } else if (height - translateY > scaledHeight) {
                translateY = height - scaledHeight;
            }
        }
        // 缩放后对图片进行偏移，以保证缩放后中心点位置不变
        matrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        currentBitmapWidth = scaledWidth;
        currentBitmapHeight = scaledHeight;
        canvas.drawBitmap(sourceBitmap, matrix, null);
        //setRotation(rotation);
    }

    /**
     * 对图片进行平移处理
     *
     * @param canvas
     */
    private void move(Canvas canvas) {
        matrix.reset();
        // 根据手指移动的距离计算出总偏移值
        float translateX = totalTranslateX + movedDistanceX;
        float translateY = totalTranslateY + movedDistanceY;
        // 先按照已有的缩放比例对图片进行缩放
        matrix.postScale(totalRatio, totalRatio);
        //matrix.postRotate(90,currentBitmapWidth/2,currentBitmapHeight/2);
        // 再根据移动距离进行偏移
        matrix.postTranslate(translateX, translateY);
        totalTranslateX = translateX;
        totalTranslateY = translateY;
        canvas.drawBitmap(sourceBitmap, matrix, null);
    }

    /**
     * 对图片进行初始化操作，包括让图片居中，以及当图片大于屏幕宽高时对图片进行压缩。
     *
     * @param canvas
     */
    private void initBitmap(Canvas canvas) {
        if (sourceBitmap != null) {
            matrix.reset();
            int bitmapWidth = sourceBitmap.getWidth();
            int bitmapHeight = sourceBitmap.getHeight();
            if (bitmapWidth > width || bitmapHeight > height) {
                if (bitmapWidth - width > bitmapHeight - height) {
                    // 当图片宽度大于屏幕宽度时，将图片等比例压缩，使它可以完全显示出来
                    float ratio = width / (bitmapWidth * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateY = (height - (bitmapHeight * ratio)) / 2f;
                    // 在纵坐标方向上进行偏移，以保证图片居中显示
                    matrix.postTranslate(0, translateY);
                    totalTranslateY = translateY;
                    totalRatio = initRatio = ratio;
                } else {
                    // 当图片高度大于屏幕高度时，将图片等比例压缩，使它可以完全显示出来
                    float ratio = height / (bitmapHeight * 1.0f);
                    matrix.postScale(ratio, ratio);
                    float translateX = (width - (bitmapWidth * ratio)) / 2f;
                    // 在横坐标方向上进行偏移，以保证图片居中显示
                    matrix.postTranslate(translateX, 0);
                    totalTranslateX = translateX;
                    totalRatio = initRatio = ratio;
                }
                currentBitmapWidth = bitmapWidth * initRatio;
                currentBitmapHeight = bitmapHeight * initRatio;
            } else {
                // 当图片的宽高都小于屏幕宽高时，直接让图片居中显示
                float translateX = (width - sourceBitmap.getWidth()) / 2f;
                float translateY = (height - sourceBitmap.getHeight()) / 2f;
                matrix.postTranslate(translateX, translateY);
                totalTranslateX = translateX;
                totalTranslateY = translateY;
                totalRatio = initRatio = 1f;
                currentBitmapWidth = bitmapWidth;
                currentBitmapHeight = bitmapHeight;
            }
            canvas.drawBitmap(sourceBitmap, matrix, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        //Logger.e("--------:"+widthSize+";"+heightSize);
        if (widthMode == View.MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = 1000;
        }

        if (heightMode == View.MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = 1000;
        }
        int max=Math.max(measureWidth,measureHeight);
        setMeasuredDimension(max, max);
    }

    /**
     * 计算两个手指之间的距离。
     *
     * @param event
     * @return 两个手指之间的距离
     */
    private double distanceBetweenFingers(MotionEvent event) {
        float disX = Math.abs(event.getX(0) - event.getX(1));
        float disY = Math.abs(event.getY(0) - event.getY(1));
        return Math.sqrt(disX * disX + disY * disY);
    }


    /**
     * 计算两个手指之间中心点的坐标。
     *
     * @param event
     */
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        centerPointX = (xPoint0 + xPoint1) / 2;
        centerPointY = (yPoint0 + yPoint1) / 2;
    }

    /**
     * 计算两个手指间的旋转角度
     * @param event
     * @return
     */
    private float getDegree(MotionEvent event) {
        //得到两个手指间的旋转角度
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }



}
