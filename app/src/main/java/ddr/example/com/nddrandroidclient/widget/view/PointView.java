package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * time ：2019/11/13
 * desc : 绘制点
 */
public class PointView {
    public static PointView pointView;

    private Paint pointPaint,textPaint;
    /**
     *用于裁剪源图像的矩形（可重复使用）。
     */
    private Rect mRectSrc;

    /**
     * 用于在画布上指定绘图区域的矩形（可重新使用）。
     */
    private Rect mRectDst;


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


    }

}
