package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.chillingvan.canvasgl.ICanvasGL;
import com.chillingvan.canvasgl.glcanvas.GLPaint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time ：2019/12/13
 * desc : 利用SurfaceView绘制地图
 */
public class CollectingView2 extends SurfaceView implements SurfaceHolder.Callback,Runnable{
    private NotifyLidarPtsEntity notifyLidarPtsEntity;
    private NotifyLidarPtsEntity notifyLidarPtsEntity1;
    private List<NotifyLidarPtsEntity> ptsEntityList=new ArrayList<>();  //存储雷达扫到的点云
    private List<XyEntity>poiPoints=new ArrayList<>();
    private float posX,posY;
    private float radian;
    private float angle;
    private float minX=0,minY=0,maxX=0,maxY=0;  //雷达扫到的最大坐标和最小坐标
    private double ratio=1;         //地图比例
    private double oldRatio=1;
    private int measureWidth, measureHeight;
    private Bitmap directionBitmap,directionBitmap1;
    private Bitmap poiBitmap;
    private Bitmap bgBitmap;
    private Matrix matrix;
    private SurfaceHolder holder;
    public boolean isRunning=false;
    private Thread thread;

    private Paint paint,lastFrame,pathPaint,pointPaint,bitmapPaint;


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void upDate(MessageEvent mainUpDate) {
        switch (mainUpDate.getType()) {
            case receivePointCloud:
                posX=notifyLidarPtsEntity.getPosX();
                posY=notifyLidarPtsEntity.getPosY();
                radian=notifyLidarPtsEntity.getPosdirection();
                angle=radianToangle(radian);
                notifyLidarPtsEntity1=new NotifyLidarPtsEntity();
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

    public CollectingView2(Context context) {
        super(context);
    }

    public CollectingView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        Logger.e("--------实例化");
        notifyLidarPtsEntity=NotifyLidarPtsEntity.getInstance();
        holder=getHolder();
        holder.addCallback(this);
        paint=new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        lastFrame=new Paint();
        lastFrame.setStrokeWidth(1);
        lastFrame.setColor(Color.parseColor("#00CED1"));
        pathPaint=new Paint();
        pathPaint.setColor(Color.BLACK);
        pathPaint.setStrokeWidth(2);
        pointPaint=new Paint();
        pointPaint.setColor(Color.BLUE);
        pointPaint.setStrokeWidth(3);
        bitmapPaint=new Paint();
        matrix=new Matrix();
        poiBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.poi_default);
        directionBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.direction);
        bgBitmap=Bitmap.createBitmap(1000,1000,Bitmap.Config.ARGB_8888);
        bgBitmap.eraseColor(Color.parseColor("#646464"));
        EventBus.getDefault().register(this);       //注册监听
    }

    /**
     * 绘制
     */
    private void doDraw(){
        Canvas canvas=null;
        try {
            canvas=holder.lockCanvas();
            if (canvas!=null){
                drawMap(canvas);
                drawPath(canvas);
                drawPoint(canvas);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (canvas!=null)
            holder.unlockCanvasAndPost(canvas);
        }
    }



    /**
     * 实时绘制地图
     * @param canvas
     */
    private void drawMap(Canvas canvas){
            int ptsSize=ptsEntityList.size();
            long startTime1=System.currentTimeMillis();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            canvas.drawColor(Color.parseColor("#101112"));
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
                    canvas.drawPoint(ptX,ptY,pointPaint);
                    if (i==ptsSize-1){
                        canvas.drawLine(x,y,ptX,ptY,lastFrame);
                    }
                }
            }
            long endTime1=System.currentTimeMillis();
            matrix.setRotate(-angle);
            directionBitmap1=Bitmap.createBitmap(directionBitmap,0,0,60,60,matrix,true);
            float posx=(float)(-posY*ratio+measureWidth/2);
            float posy=(float)(-posX*ratio+measureHeight/2);
            canvas.drawBitmap(directionBitmap1,(int)posx-30,(int)posy-30,paint);
            Logger.e("------绘制耗时："+(endTime1-startTime1)+"列表长度："+ptsSize);

    }

    /**
     * 绘制行走路线
     */
    private void drawPath(Canvas canvas){
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

    private void drawPoint(Canvas canvas){
        int pts=poiPoints.size();
        for (int i=0;i<pts;i++){
            float y=(float)((-poiPoints.get(i).getX())*ratio+measureHeight/2);
            float x=(float)((-poiPoints.get(i).getY())*ratio+measureWidth/2);
            canvas.drawBitmap(poiBitmap,(int) x-10,(int) y-10,paint);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread=new Thread(this);
        isRunning=true;
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.e("-------surfaceChanged:"+width+";"+height);
        measureWidth=width;
        measureHeight=height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        ptsEntityList.clear();
        notifyLidarPtsEntity.setNull();
        Logger.e("-------取消注册，清空内存");
    }

    /**
     * 停止绘制
     */
    public void onStop(){
        EventBus.getDefault().unregister(this);
        isRunning=false;
    }

    /**
     * 计算缩放比例
     * @param list
     */
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
                    ratio=measureWidth/(xy)/2*0.98;
                }else {
                    ratio=measureHeight/(xy)/2*0.98;
                }
            }
        }
        long endTime=System.currentTimeMillis();
        //Logger.e("------计算耗时："+(endTime-startTime));

    }

    /**
     * 弧度转角度
     */
    public float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }

    @Override
    public void run() {
        while (isRunning){
            doDraw();
        }
    }
}
