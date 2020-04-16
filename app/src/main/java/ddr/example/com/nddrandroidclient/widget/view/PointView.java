package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import org.greenrobot.eventbus.EventBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time ：2019/11/13
 * desc : 绘制点
 */
public class PointView extends Shape {
    public static PointView pointView;
    private List<TargetPoint> targetPoints;
    private List<TargetPoint> targetPoints1;
    private List<TargetPoint> selectPoints;
    private Paint pointPaint,textPaint;
    private TargetPoint targetPoint;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private PathLine.PathPoint pathPoint;
    /**
     *用于裁剪源图像的矩形（可重复使用）。
     */
    private Rect mRectSrc;

    /**
     * 用于在画布上指定绘图区域的矩形（可重新使用）。
     */
    private Rect mRectDst;

    private Bitmap autoBitmap;
    private float x,y;
    private float angle; //角度
    public boolean isRuning;
    private Matrix matrix=new Matrix();
    private Bitmap directionBitmap,directionBitmap1;
    private Bitmap targetBitmap,targetBitmap1;
    private Bitmap beginBitmap,chargeBitmap;        //初始点、充点电
    private boolean isCheckPoint;                   //是否通过点击选择目标点
    private int directionW,directionH;


    /**
     * 设置需要显示的点列表
     */
    public void setPoints(List<TargetPoint> targetPoints){
        this.targetPoints=targetPoints;
    }

    /**
     * 显示被选中的点（多选）
     * @param targetPoints
     */
    public void setTargetPoints(List<TargetPoint> targetPoints){
        this.targetPoints1=targetPoints;
    }

    /**
     * 显示路径中的某个点
     * @param pathPoint
     */
    public void setPathPoint(PathLine.PathPoint pathPoint){
        this.pathPoint=pathPoint;
    }

    /**
     * 显示点击的该点（单选）
     * @param targetPoint
     */
    public void setPoint(TargetPoint targetPoint){
        this.targetPoint=targetPoint;
    }

    /**
     * 是否可以通过点击选择目标点组建成路径
     * @param isCheckPoint
     */
    public void setIsTouch(boolean isCheckPoint){
        this.isCheckPoint=isCheckPoint;
    }

    /**
     * 用于点击的目标点列表
     * @param selectPoints
     */
    public void set2TouchPoints(List<TargetPoint> selectPoints){
        this.selectPoints=selectPoints;
    }



    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateBaseStatus:
                x=notifyBaseStatusEx.getPosX();
                y=notifyBaseStatusEx.getPosY();
                angle=radianToangle(notifyBaseStatusEx.getPosDirection());
                break;
        }
    }


    /**
     * 单例模式 避免频繁实例化该类
     * @param context
     * @return
     */
    public static PointView getInstance(Context context){
        if (pointView==null){
            synchronized (PointView.class){
                if (pointView==null){
                    pointView=new PointView(context);
                }
            }
        }
        return pointView;
    }

    private PointView(Context context) {
        pointPaint=new Paint();
        pointPaint.setStrokeWidth(5);
        pointPaint.setColor(Color.GRAY);
        pointPaint.setAntiAlias(true);
        textPaint=new Paint();
        textPaint.setStrokeWidth(8);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(16);
        autoBitmap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.auto_default);
        directionBitmap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.direction);
        targetBitmap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.target_point);
        beginBitmap=BitmapFactory.decodeResource(context.getResources(),R.mipmap.begin_point);
        chargeBitmap=BitmapFactory.decodeResource(context.getResources(),R.mipmap.charge_point);
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        EventBus.getDefault().register(this);
        directionW=directionBitmap.getWidth();
        directionH=directionBitmap.getHeight();
    }


    public void drawPoint(Canvas canvas,ZoomImageView zoomImageView){
        if (targetPoints!=null){
            for (int i=0;i<targetPoints.size();i++){
                if (targetPoints.get(i).isInTask()){
                    XyEntity xyEntity=zoomImageView.toXorY(targetPoints.get(i).getX(),targetPoints.get(i).getY());
                    xyEntity=zoomImageView.coordinate2View(xyEntity.getX(),xyEntity.getY());
                    int x= (int) xyEntity.getX();
                    int y= (int) xyEntity.getY();
                    mRectSrc=new Rect(0,0,22,22);
                    mRectDst=new Rect(x-11,y-11,x+11,y+11);
                    canvas.drawBitmap(autoBitmap,mRectSrc,mRectDst,pointPaint);
                    canvas.drawText(targetPoints.get(i).getName(),x,y+15,textPaint);
                }
            }
        }
        if (targetPoints1 != null) {
            for (int i=0;i<targetPoints1.size();i++){
                if (targetPoints1.get(i).isMultiple()){
                    XyEntity xyEntity=zoomImageView.toXorY(targetPoints1.get(i).getX(),targetPoints1.get(i).getY());
                    xyEntity=zoomImageView.coordinate2View(xyEntity.getX(),xyEntity.getY());
                    int x= (int) xyEntity.getX();
                    int y= (int) xyEntity.getY();
                    mRectSrc=new Rect(0,0,40,40);
                    mRectDst=new Rect(x-20,y-20,x+20,y+20);
                    matrix.setRotate(-targetPoints1.get(i).getTheta());
                    targetBitmap1=Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getWidth()  ,matrix,true);
                    canvas.drawBitmap(targetBitmap1,mRectSrc,mRectDst,pointPaint);
                    canvas.drawText(targetPoints1.get(i).getName(),x,y+15,textPaint);
                }
            }
        }

        if (targetPoint!=null){
            XyEntity xyEntity=zoomImageView.toXorY(targetPoint.getX(),targetPoint.getY());
            xyEntity=zoomImageView.coordinate2View(xyEntity.getX(),xyEntity.getY());
            int x= (int) xyEntity.getX();
            int y= (int) xyEntity.getY();
            matrix.setRotate(-targetPoint.getTheta());
            targetBitmap1=Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getHeight(),matrix,true);
            canvas.drawBitmap(targetBitmap1,x-20,y-20,pointPaint);
            canvas.drawText(targetPoint.getName(),x,y+15,textPaint);
        }

        if (pathPoint!=null){
            XyEntity xyEntity=zoomImageView.toXorY(pathPoint.getX(),pathPoint.getY());
            xyEntity=zoomImageView.coordinate2View(xyEntity.getX(),xyEntity.getY());
            int x= (int) xyEntity.getX();
            int y= (int) xyEntity.getY();
            matrix.setRotate(-pathPoint.getRotationAngle());
            targetBitmap1=Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getWidth(),matrix,true);
            canvas.drawBitmap(targetBitmap1,x-20,y-20,pointPaint);
        }
        if (isRuning){
            XyEntity xyEntity=zoomImageView.toXorY(x,y);
            xyEntity=zoomImageView.coordinate2View(xyEntity.getX(),xyEntity.getY());
            matrix.setRotate(-angle);
            directionBitmap1=Bitmap.createBitmap(directionBitmap,0,0,directionW,directionW,matrix,true);
            canvas.drawBitmap(directionBitmap1,(int)xyEntity.getX()-directionW/2,(int)xyEntity.getY()-directionH/2,pointPaint);
        }

        if (selectPoints != null) {
            for (int i=0;i<selectPoints.size();i++){
                    XyEntity xyEntity=zoomImageView.toXorY(selectPoints.get(i).getX(),selectPoints.get(i).getY());
                    xyEntity=zoomImageView.coordinate2View(xyEntity.getX(),xyEntity.getY());
                    int x= (int) xyEntity.getX();
                    int y= (int) xyEntity.getY();
                    mRectSrc=new Rect(0,0,40,40);
                    mRectDst=new Rect(x-20,y-20,x+20,y+20);
                    matrix.setRotate(-selectPoints.get(i).getTheta());
                    targetBitmap1=Bitmap.createBitmap(targetBitmap,0,0,40,40,matrix,true);
                    canvas.drawBitmap(targetBitmap1,mRectSrc,mRectDst,pointPaint);
                    canvas.drawText(selectPoints.get(i).getName(),x,y+15,textPaint);
            }
        }

    }


    /**
     * 点击区域的坐标
     * @param x
     * @param y
     */
    public void onClick(ZoomImageView zoomImageView,float x,float y){
        if (isCheckPoint){
            if (selectPoints!=null){
                for (int i=0;i<selectPoints.size();i++){
                    TargetPoint targetPoint=selectPoints.get(i);
                    XyEntity xyEntity1=zoomImageView.toXorY(targetPoint.getX(),targetPoint.getY());
                    xyEntity1=zoomImageView.coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                    float x1=xyEntity1.getX(); float y1=xyEntity1.getY();
                    double L=Math.sqrt(Math.pow(x1-x,2)+Math.pow(y1-y,2));
                    if (L<25){
                        Logger.e("点击选中点："+targetPoint.getName());
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.touchSelectPoint,i));
                        zoomImageView.invalidate();
                    }
                }
            }
        }
    }


    /**
     * 弧度转角度
     */
    public float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }

    public void clearDraw(){
        targetPoints=null;
        targetPoint=null;
        targetPoints1=null;
        selectPoints=null;
        isRuning=false;
        pathPoint=null;
    }

}
