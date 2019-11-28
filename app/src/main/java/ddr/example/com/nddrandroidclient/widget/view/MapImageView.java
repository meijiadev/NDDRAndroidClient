package ddr.example.com.nddrandroidclient.widget.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.GLPaint;
import com.chillingvan.canvasgl.glview.GLContinuousView;
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
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.SpaceItem;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;


/**
 * 放置激光地图的控件
 */
public class MapImageView extends GLContinuousView {
    private DDRVLNMap.reqDDRVLNMapEx data;
    private DDRVLNMap.DDRMapBaseData baseData;       // 存放基础信息，采集模式结束时就有的东西。
    private DDRVLNMap.affine_mat affine_mat;
    private DDRVLNMap.DDRMapTargetPointData targetPointData;
    private List<DDRVLNMap.targetPtItem> targetPtItems;          // 目标点列表
    private List<DDRVLNMap.path_line_itemEx> pathLineItemExes;  // 路径列表
    private List<DDRVLNMap.task_itemEx> taskItemExes;          //  任务列表

    private List<DDRVLNMap.path_elementEx> pathElementExes;    // 任务列表的元素
    private List<DDRVLNMap.path_line_itemEx> pathLineItemExesS;  // 路径列表  选中的任务中包含的路径
    private List<DDRVLNMap.targetPtItem> targetPtItemsS;          // 目标点列表 选中的任务中包含的目标点

    private List<PathLine> pathLines=new ArrayList<>();   //经过转换坐标的路径

    private MapFileStatus mapFileStatus;
    private List<BaseCmd.notifyLidarPts.Position> positionList=new ArrayList<>();    //雷达当前扫到的点云
    private List<SpaceItem> spaceItems;
    private NotifyLidarPtsEntity notifyLidarPtsEntity;

    private Bitmap mapBitmap;
    private Bitmap targetBitmap; //目标点
    private Bitmap directionBitmap,directionBitmap1;
    private GLPaint glPaint,radarPaint,linePaint1;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private double r00=0;
    private double r01=-61.5959;
    private double t0=375.501;
    private double r10=-61.6269;
    private double r11=0;
    private double t1=410.973;
    private float radian,angle;                /**经过矩阵变换后的坐标（相对于图片，单位是像素)**/
    private int posX,posY;

    private int scale;
    private boolean waitData=false;           //是否需要等待数据
    private String taskName;
    private boolean isStartRadar=false;       //是否雷达开始绘制
    private String mapName;


    /**
     *用于裁剪源图像的矩形（可重复使用）。
     */
    private final Rect mRectSrc = new Rect();

    /**
     * 用于在画布上指定绘图区域的矩形（可重新使用）。
     */
    private final Rect mRectDst = new Rect();

    private Matrix matrix,mapMatrix;


    public void setMapBitmap(String mapName){
        this.mapName=mapName;
        Logger.e("设置图片");
        String pngPath = Environment.getExternalStorageDirectory().getPath() + "/" + "机器人" + "/" + mapName + "/" + "bkPic.png";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(pngPath);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            mapBitmap=bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    public void setTaskName(String taskName){
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

    }

    public MapImageView(Context context) {
        super(context);
    }

    public MapImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Activity activity= (Activity) context;
        Logger.e("--------");

    }

    @Override
    protected void init() {
        super.init();
        EventBus.getDefault().register(this);
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        mapFileStatus=MapFileStatus.getInstance();
        directionBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        targetBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.action_default);
        matrix=new Matrix();
        glPaint=new GLPaint();
        glPaint.setColor(Color.GRAY);
        glPaint.setLineWidth(3);
        radarPaint=new GLPaint();
        radarPaint.setColor(Color.parseColor("#00CED1"));
        radarPaint.setLineWidth(1);

        linePaint1=new GLPaint();
        linePaint1.setLineWidth(3);
        linePaint1.setColor(Color.BLACK);



    }

    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        drawBitmap(canvas);
        drawLine(canvas);
        drawRadarLine(canvas);
        onDrawWall(canvas);

    }


    /**
     * 绘制图片
     * @param canvas
     */
    private void drawBitmap(ICanvasGL canvas){
        int width=getWidth();
        int height=getHeight();
        if (mapBitmap!=null){
            int bitmapWidth=mapBitmap.getWidth();
            int bitmapHeight=mapBitmap.getHeight();
            mRectSrc.left=0;
            mRectSrc.top=0;
            mRectSrc.right=bitmapWidth;
            mRectSrc.bottom=bitmapHeight;
            if (bitmapWidth<width&bitmapHeight<height){

            }else if (bitmapWidth>width&&bitmapHeight>height){
                scale=Math.max(bitmapWidth/width,bitmapHeight/height);
                bitmapWidth=bitmapWidth/scale;
                bitmapHeight=bitmapHeight/scale;

            }else if (bitmapWidth>width){
                scale=bitmapWidth/width;
                bitmapWidth=bitmapWidth/scale;
                bitmapHeight=bitmapHeight/scale;

            }else if (bitmapHeight>height){
                scale=bitmapHeight/height;
                bitmapWidth=bitmapWidth/scale;
                bitmapHeight=bitmapHeight/scale;
            }
            mRectDst.left=(width-bitmapWidth)/2;
            mRectDst.top=(height-bitmapHeight)/2;
            mRectDst.right=(width+bitmapWidth)/2;
            mRectDst.bottom=(height+bitmapHeight)/2;
            canvas.drawBitmap(mapBitmap,mRectSrc,mRectDst);
        }
    }



    /**
     * 绘制路径和点
     */
    private void drawLine(ICanvasGL canvasGL){
        //绘制目标点
        if (targetPtItemsS!=null)
        for (int i=0;i<targetPtItemsS.size();i++){
            XyEntity xyEntity=toXorY(targetPtItemsS.get(i).getPtData().getX(),targetPtItemsS.get(i).getPtData().getY());   //转成像素坐标
            canvasGL.drawBitmap(targetBitmap,mRectDst.left+(int)xyEntity.getX()-10,mRectDst.top+(int)xyEntity.getY()-10);
        }
       if (pathLineItemExesS!=null){
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

            for (int i=0;i<pathLines.size();i++){
                List<PathLine.PathPoint>pathPoints=pathLines.get(i).getPathPoints();
                for (int j=0;j<pathPoints.size();j++){
                    int x=mRectDst.left+(int)pathPoints.get(j).getX();
                    int y=mRectDst.top+(int) pathPoints.get(j).getY();
                    if (j<pathPoints.size()-1){
                        canvasGL.drawLine(x,y,(int)pathPoints.get(j+1).getX()+mRectDst.left,pathPoints.get(j+1).getY()+mRectDst.top,glPaint);
                    }
                }
            }
       }

    }

    /**
     * 绘制虚拟墙
     */
    private void onDrawWall(ICanvasGL canvasGL){
        if (spaceItems!=null){
            for (int i=0;i<spaceItems.size();i++){
                List<DDRVLNMap.space_pointEx> space_pointExes=spaceItems.get(i).getLines();
                for (int j=0;j<space_pointExes.size();j++){
                    if (j<space_pointExes.size()-1){
                        XyEntity xyEntity1=toXorY(space_pointExes.get(j).getX(),space_pointExes.get(j).getY());
                        XyEntity xyEntity2=toXorY(space_pointExes.get(j+1).getX(),space_pointExes.get(j+1).getY());
                        canvasGL.drawLine(xyEntity1.getX()+mRectDst.left,xyEntity1.getY()+mRectDst.top,xyEntity2.getX()+mRectDst.left,xyEntity2.getY()+mRectDst.top,linePaint1);
                    }
                }
            }

        }
    }

    /**
     * 绘制雷达扫到的区域
     * @param canvasGL
     */
    private void drawRadarLine(ICanvasGL canvasGL){
        if (isStartRadar){
            positionList=notifyLidarPtsEntity.getPositionList();
            if (positionList!=null){
                int size =positionList.size();
                for (int i=0;i<size;i++){
                    float x= (float) (r00*positionList.get(i).getPtX()+r01*positionList.get(i).getPtY()+t0)+mRectDst.left;
                    float y=(float)(r10*positionList.get(i).getPtX()+r11*positionList.get(i).getPtY()+t1)+mRectDst.top;
                    canvasGL.drawLine(posX+mRectDst.left,posY+mRectDst.top,x,y,radarPaint);
                }
                matrix.setRotate(-angle);
                directionBitmap1=Bitmap.createBitmap(directionBitmap,0,0,60,60,matrix,true);
                if (mapBitmap!=null){
                    canvasGL.drawBitmap(directionBitmap1,mRectDst.left+posX-30,mRectDst.top+posY-30);
                }
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
        float x1=(float)( r00*x+r01*y+t0);
        float y1=(float) (r10*x+r11*y+t1);
        return new XyEntity(x1,y1);
    }

    public void transformXy(){

    }

    /**
     * 实时绘制（将世界坐标经过矩阵变换成图片上的像素坐标)
     */
    private void realTimeDraw(){
        float x=notifyBaseStatusEx.getPosX();
        float y=notifyBaseStatusEx.getPosY();
        radian=notifyBaseStatusEx.getPosDirection();
        posX=(int) (r00*x+r01*y+t0);
        posY=(int) (r10*x+r11*y+t1);
        angle=radianToangle(radian);
    }

    /**
     * 弧度转角度
     */
    public float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }

    /**
     * 解除监听
     */
    public void unRegister(){
        EventBus.getDefault().unregister(this);
        onPause();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void upDate(MessageEvent mainUpDate){
        switch (mainUpDate.getType()){
            case updateDDRVLNMap:
                data=mapFileStatus.getReqDDRVLNMapEx();
                baseData=data.getBasedata();
                Logger.e("--------"+baseData.getName().toStringUtf8());
                //验证返回的地图信息是否是当前运行的地图
                if (baseData.getName().toStringUtf8().equals(mapName)){
                    Logger.e("---------验证通过");
                    affine_mat=baseData.getAffinedata();
                    r00=affine_mat.getR11();
                    r01=affine_mat.getR12();
                    t0=affine_mat.getTx();
                    r10=affine_mat.getR21();
                    r11=affine_mat.getR22();
                    t1=affine_mat.getTy();
                    targetPtItems=data.getTargetPtdata().getTargetPtList();
                    pathLineItemExes=data.getPathSet().getPathLineDataList();
                    taskItemExes=data.getTaskSetList();
                    spaceItems=mapFileStatus.getcSpaceItems();
                }
                break;
            case updateBaseStatus:
                realTimeDraw();
                mapName=NotifyBaseStatusEx.getInstance().getCurroute();
                break;
            case receivePointCloud:
                isStartRadar=true;
                break;

        }
    }

}
