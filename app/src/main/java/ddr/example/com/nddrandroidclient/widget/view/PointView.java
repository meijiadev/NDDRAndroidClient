package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.List;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;

/**
 * time ：2019/11/13
 * desc : 绘制点
 */
public class PointView {
    public static PointView pointView;
    private List<TargetPoint> targetPoints;
    private Paint pointPaint,textPaint;
    private TargetPoint targetPoint;
    /**
     *用于裁剪源图像的矩形（可重复使用）。
     */
    private Rect mRectSrc;

    /**
     * 用于在画布上指定绘图区域的矩形（可重新使用）。
     */
    private Rect mRectDst;

    private Bitmap autoBitmap;

    /**
     * 设置需要显示的点列表
     */
    public void setPoints(List<TargetPoint> targetPoints){
        this.targetPoints=targetPoints;
    }

    public void setPoint(TargetPoint targetPoint){
        this.targetPoint=targetPoint;
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
    }


    public void drawPoint(Canvas canvas,ZoomImageView zoomImageView){

        if (targetPoints!=null){
            for (int i=0;i<targetPoints.size();i++){
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

        if (targetPoint!=null){
            XyEntity xyEntity=zoomImageView.toXorY(targetPoint.getX(),targetPoint.getY());
            xyEntity=zoomImageView.coordinate2View(xyEntity.getX(),xyEntity.getY());
            int x= (int) xyEntity.getX();
            int y= (int) xyEntity.getY();
            mRectSrc=new Rect(0,0,22,22);
            mRectDst=new Rect(x-11,y-11,x+11,y+11);
            canvas.drawBitmap(autoBitmap,mRectSrc,mRectDst,pointPaint);
            canvas.drawText(targetPoint.getName(),x,y+15,textPaint);
        }
    }

}
