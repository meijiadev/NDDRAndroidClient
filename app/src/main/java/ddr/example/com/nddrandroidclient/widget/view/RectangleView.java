package ddr.example.com.nddrandroidclient.widget.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import ddr.example.com.nddrandroidclient.entity.other.Rectangle;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.widget.zoomview.ZoomImageView;

/**
 * desc：绘制矩形 原图去噪相关
 * time：2020/4/2
 */
public class RectangleView extends Shape {
    public Paint paint,paint1;
    public static RectangleView rectangleView;
    private ZoomImageView zoomImageView;
    private XyEntity firstPoint;

    private List<Rectangle> rectangles=new ArrayList<>();



    public static RectangleView getRectangleView() {
        if (rectangleView==null){
            synchronized (RectangleView.class){
                if (rectangleView==null){
                    rectangleView=new RectangleView();
                }
            }
        }
        return rectangleView;
    }

    private RectangleView() {
        paint=new Paint();
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint1=new Paint();
        paint1.setColor(Color.WHITE);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setAntiAlias(true);

    }


    @Override
    public void draw(Canvas canvas, Object o) {
        super.draw(canvas, o);
        zoomImageView= (ZoomImageView) o;
        if (rectangles!=null&&rectangles.size()>0){
            for (Rectangle rectangle:rectangles){
                XyEntity xyEntity1=zoomImageView.toCanvas(rectangle.getFirstPoint().getX(),rectangle.getFirstPoint().getY());
                XyEntity xyEntity2=zoomImageView.toCanvas(rectangle.getSecondPoint().getX(),rectangle.getSecondPoint().getY());
                canvas.drawRect(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),paint1);

            }
        }
        if (firstPoint!=null){
            XyEntity xyEntity=zoomImageView.toCanvas(firstPoint.getX(),firstPoint.getY());
            XyEntity xyEntity1=zoomImageView.toCanvas(zoomImageView.getTargetPoint().getX(),zoomImageView.getTargetPoint().getY());
            Logger.e("-------xyEntity1:"+xyEntity1.getX()+";"+xyEntity1.getY());
            canvas.drawRect(xyEntity.getX(),xyEntity.getY(),xyEntity1.getX(),xyEntity1.getY(),paint);

        }

    }



    public void setFirstPoint(XyEntity xyEntity){
        firstPoint=xyEntity;
    }


    public void setRectangles(List<Rectangle> rectangles){
        this.rectangles=rectangles;
    }

    public void clearDraw(){
        rectangles=null;
        firstPoint=null;

    }
}
