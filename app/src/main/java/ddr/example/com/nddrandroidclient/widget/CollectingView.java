package ddr.example.com.nddrandroidclient.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;


import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.GLPaint;
import com.chillingvan.canvasgl.glview.GLContinuousView;
import com.chillingvan.canvasgl.glview.texture.GLContinuousTextureView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;


/**
 * 实时绘制采集时的地图
 * create by ezreal
 * 2019/10/14
 */
public class CollectingView extends GLContinuousView {
    private NotifyLidarPtsEntity notifyLidarPtsEntity;
    private List<NotifyLidarPtsEntity> ptsEntityList=new ArrayList<>();  //存储雷达扫到的点云
    private List<XyEntity>poiPoints=new ArrayList<>();
    private int DEFAULT_SIZE=800;
    private int measureWidth, measureHeight;
    private GLPaint paint,lastFrame,pathPaint;
    private float posX,posY;
    private float radian;
    private float angle;
    private Bitmap directionBitmap,directionBitmap1;
    private Bitmap poiBitmap;
    private Bitmap bgBitmap;
    private Matrix matrix;

    private double displayWidth=1.5;        //默认场景大小 3m
    private double displayheight=1.5;
    private float minX=0,minY=0,maxX=0,maxY=0;  //雷达扫到的最大坐标和最小坐标
    private double ratio=1;         //地图比例


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void upDate(MessageEvent mainUpDate) {
        switch (mainUpDate.getType()) {
            case receivePointCloud:
                posX=notifyLidarPtsEntity.getPosX();
                posY=notifyLidarPtsEntity.getPosY();
                radian=notifyLidarPtsEntity.getPosdirection();
                angle=radianToangle(radian);
                NotifyLidarPtsEntity notifyLidarPtsEntity1=new NotifyLidarPtsEntity();
                notifyLidarPtsEntity1.setPosX(notifyLidarPtsEntity.getPosX());
                notifyLidarPtsEntity1.setPosY(notifyLidarPtsEntity.getPosY());
                notifyLidarPtsEntity1.setPositionList(notifyLidarPtsEntity.getPositionList());
                ptsEntityList.add(notifyLidarPtsEntity1);
                maxOrmin(notifyLidarPtsEntity.getPositionList());
                break;
            case addPoiPoint:
                poiPoints.add(new XyEntity(posX,posY));
                break;
        }
    }

    public CollectingView(Context context) {
        super(context);
    }

    public CollectingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        Logger.e("--------实例化");
        matrix=new Matrix();
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        paint=new GLPaint();
        paint.setColor(Color.WHITE);
        paint.setLineWidth(3);
        lastFrame=new GLPaint();
        lastFrame.setLineWidth(1);
        lastFrame.setColor(Color.parseColor("#00CED1"));
        pathPaint=new GLPaint();
        pathPaint.setColor(Color.BLACK);
        pathPaint.setLineWidth(2);
        poiBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.poi_default);
        directionBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        bgBitmap=Bitmap.createBitmap(1000,1000,Bitmap.Config.ARGB_8888);
        bgBitmap.eraseColor(Color.parseColor("#646464"));
        EventBus.getDefault().register(this);       //注册监听

    }

    @Override
    protected void onGLDraw(ICanvasGL canvas) {
        //long startTime=System.currentTimeMillis();
        drawMap(canvas);
        drawPath(canvas);
        //long endTime=System.currentTimeMillis();
        //Logger.e("------绘制耗时："+(endTime-startTime)+"列表长度："+ptsEntityList.size());

    }



    /**
     * 实时绘制地图
     * @param canvas
     */
    private void drawMap(ICanvasGL canvas){
        long startTime1=System.currentTimeMillis();
        int ptsSize=ptsEntityList.size();
        for (int i=0;i<ptsSize;i++){
            //long startTime=System.currentTimeMillis();
            float y=(float)((-ptsEntityList.get(i).getPosX())*ratio+measureHeight/2);
            float x=(float)((-ptsEntityList.get(i).getPosY())*ratio+measureWidth/2);
            List<BaseCmd.notifyLidarPts.Position> positions=ptsEntityList.get(i).getPositionList();
            int pSize=positions.size();
            for (int j=0;j<pSize;j++){
                float ptX=(float)(-positions.get(j).getPtY()*ratio+measureWidth/2);
                float ptY=(float)(-positions.get(j).getPtX()*ratio+measureHeight/2);
                canvas.drawLine(x,y,ptX,ptY,paint);
                if (i==ptsSize-1){
                    canvas.drawLine(x,y,ptX,ptY,lastFrame);
                }
            }
        }
        long endTime1=System.currentTimeMillis();
        Logger.e("------绘制耗时："+(endTime1-startTime1)+"列表长度："+ptsSize);
        matrix.setRotate(-angle);
        directionBitmap1=Bitmap.createBitmap(directionBitmap,0,0,40,40,matrix,true);
        float posx=(float)(-posY*ratio+measureWidth/2);
        float posy=(float)(-posX*ratio+measureHeight/2);
        canvas.drawBitmap(directionBitmap1,(int)posx-20,(int)posy-20);
    }

    /**
     * 绘制行走路线
     */
    private void drawPath(ICanvasGL canvas){
        int ptsSize=ptsEntityList.size();
        if (ptsSize>1){
            for (int i=0;i<ptsSize;i++){
                if (i<ptsSize-1){
                    float y=(float)((-ptsEntityList.get(i).getPosX())*ratio+measureHeight/2);
                    float x=(float)((-ptsEntityList.get(i).getPosY())*ratio+measureWidth/2);
                    float y1=(float)((-ptsEntityList.get(i+1).getPosX())*ratio+measureHeight/2);
                    float x1=(float)((-ptsEntityList.get(i+1).getPosY())*ratio+measureWidth/2);
                    canvas.drawLine(x,y,x1,y1,pathPaint);
                }
            }
        }
    }



    private void maxOrmin(List<BaseCmd.notifyLidarPts.Position> list){
       //long startTime=System.currentTimeMillis();
       if (list!=null){
           int listSize=list.size();
           for (int i=0;i<listSize;i++){
               if (maxX<list.get(i).getPtX()) maxX=list.get(i).getPtX();
               if (maxY<list.get(i).getPtY()) maxY=list.get(i).getPtY();
               if (minX>list.get(i).getPtX()) minX=list.get(i).getPtX();
               if (minY>list.get(i).getPtY()) minY=list.get(i).getPtY();
           }
           if (maxX<posX) maxX=posX;
           if (maxY<posY) maxY=posY;
           if (minX>posX) minX=posX;
           if (minY>posY) minY=posY;
           float xy=Math.max(Math.max(maxX,Math.abs(minX)),Math.max(maxY,Math.abs(minY)));
           if (xy<=0){
               ratio=1;
           }else {
               if (measureWidth>measureHeight){
                   ratio=measureWidth/(xy)/2*0.9;
               }else {
                   ratio=measureHeight/(xy)/2*0.9;
               }
           }
       }
       long endTime=System.currentTimeMillis();
       //Logger.e("------计算耗时："+(endTime-startTime));

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
            measureWidth = DEFAULT_SIZE;
        }

        if (heightMode == View.MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = DEFAULT_SIZE;
        }
        setMeasuredDimension(measureWidth, measureHeight);

    }



    /**
     * 弧度转角度
     */
    public float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }




    public void unRegister(){
        EventBus.getDefault().unregister(this);
        onPause();
        ptsEntityList.clear();
        onFinishTemporaryDetach();
        notifyLidarPtsEntity.setNull();
        Logger.e("-------取消注册，清空内存");


    }




}
