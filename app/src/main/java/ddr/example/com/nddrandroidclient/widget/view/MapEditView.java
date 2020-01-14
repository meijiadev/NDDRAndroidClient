package ddr.example.com.nddrandroidclient.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time：2019/12/25
 * desc: 对图片进行绘制加工
 */
@SuppressLint("AppCompatCustomView")
public class MapEditView extends ImageView {
    private Paint paint;
    public float x,y;
    public MapEditView(Context context) {
        super(context);
        paint=new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
    }

    public MapEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint=new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);
    }

    public void refreshMap(){
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPoint(x,y,paint);
    }

    /**
     * 返回地图中心处的坐标
     * @return
     */
    public XyEntity getCenterCoordinate(){
        XyEntity xyEntity=new XyEntity(x,y);
       return xyEntity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x=event.getX();
        y=event.getY();
        Logger.e("onTouchEvent -x："+x+"----y:"+y);
        return super.onTouchEvent(event);
    }

}
