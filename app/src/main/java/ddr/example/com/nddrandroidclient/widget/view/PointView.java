package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

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
import ddr.example.com.nddrandroidclient.widget.zoomview.ZoomImageView;

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
    private int bitmapW,bitmapH;



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
        bitmapW=targetBitmap.getWidth();
        bitmapH=targetBitmap.getHeight();

    }


    public void drawPoint(Canvas canvas,ZoomImageView zoomImageView){
        if (targetPoints!=null){
            for (int i=0;i<targetPoints.size();i++){
                if (targetPoints.get(i).isInTask()){
                    XyEntity xyEntity=zoomImageView.toCanvas(targetPoints.get(i).getX(),targetPoints.get(i).getY());
                    float x= xyEntity.getX();
                    float y= xyEntity.getY();
                    canvas.drawBitmap(autoBitmap,x-autoBitmap.getWidth()/2,y-autoBitmap.getHeight()/2,pointPaint);
                    canvas.drawText(targetPoints.get(i).getName(),x,y+15,textPaint);
                }
            }
        }
        if (targetPoints1 != null) {
            for (int i=0;i<targetPoints1.size();i++){
                if (targetPoints1.get(i).isMultiple()){
                    XyEntity xyEntity=zoomImageView.toCanvas(targetPoints1.get(i).getX(),targetPoints1.get(i).getY());
                    float x= xyEntity.getX();
                    float y=  xyEntity.getY();
                    matrix.setRotate(-targetPoints1.get(i).getTheta()+zoomImageView.getDegrees());
                    targetBitmap1=Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getWidth()  ,matrix,true);
                    canvas.drawBitmap(targetBitmap1,x-bitmapW/2,y-bitmapH/2,pointPaint);
                    canvas.drawText(targetPoints1.get(i).getName(),x,y+15,textPaint);
                }
            }
        }

        if (targetPoint!=null){
            XyEntity xyEntity=zoomImageView.toCanvas(targetPoint.getX(),targetPoint.getY());
            float x=  xyEntity.getX();
            float y=  xyEntity.getY();
            matrix.setRotate(-targetPoint.getTheta()+zoomImageView.getDegrees());
            targetBitmap1=Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getHeight(),matrix,true);
            canvas.drawBitmap(targetBitmap1,x-bitmapW/2,y-bitmapH/2,pointPaint);
            canvas.drawText(targetPoint.getName(),x,y+15,textPaint);
        }

        if (pathPoint!=null){
            XyEntity xyEntity=zoomImageView.toCanvas(pathPoint.getX(),pathPoint.getY());
            float x=  xyEntity.getX();
            float y=  xyEntity.getY();
            matrix.setRotate(-pathPoint.getRotationAngle()+zoomImageView.getDegrees());
            targetBitmap1=Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getWidth(),matrix,true);
            canvas.drawBitmap(targetBitmap1,x-bitmapW/2,y-bitmapH/2,pointPaint);
        }
        if (isRuning){
            XyEntity xyEntity=zoomImageView.toCanvas(x,y);
            float cx=xyEntity.getX()-directionBitmap.getWidth()/2;
            float cy=xyEntity.getY()-directionBitmap.getHeight()/2;
            matrix.reset();
            matrix.postTranslate(cx,cy);
            matrix.postRotate(-angle+zoomImageView.getDegrees(),xyEntity.getX(),xyEntity.getY());
            canvas.drawBitmap(directionBitmap,matrix,pointPaint);
        }

        if (selectPoints != null) {
            for (int i=0;i<selectPoints.size();i++){
                    XyEntity xyEntity=zoomImageView.toCanvas(selectPoints.get(i).getX(),selectPoints.get(i).getY());
                    float x= (int) xyEntity.getX();
                    float y= (int) xyEntity.getY();
                    matrix.setRotate(-selectPoints.get(i).getTheta()+zoomImageView.getDegrees());
                    targetBitmap1=Bitmap.createBitmap(targetBitmap,0,0,targetBitmap.getWidth(),targetBitmap.getHeight(),matrix,true);
                    canvas.drawBitmap(targetBitmap1,x-bitmapW/2,y-bitmapH/2,pointPaint);
                    canvas.drawText(selectPoints.get(i).getName(),x,y+15,textPaint);
            }
        }

    }


    /**
     * 点击区域的坐标
     * @param x
     * @param y
     */
    public void onClick(ZoomImageView zoomImageView, float x, float y){
        if (isCheckPoint){
            if (selectPoints!=null){
                for (int i=0;i<selectPoints.size();i++){
                    TargetPoint targetPoint=selectPoints.get(i);
                    XyEntity xyEntity1=zoomImageView.toCanvas(targetPoint.getX(),targetPoint.getY());
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
