package ddr.example.com.nddrandroidclient.widget.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ddr.example.com.nddrandroidclient.other.Logger;


/**
 * 网格层
 */
public class GridLayerView {
    public static GridLayerView gridLayerView;
    private ZoomImageView zoomImageView;
    private Paint paint;
    private double pixIntervalX,prxIntervalY;
    private float precision=0;

    public static GridLayerView getInstance(ZoomImageView zoomImageView){
        if (gridLayerView==null){
            synchronized (GridLayerView.class){
                if (gridLayerView==null){
                    gridLayerView=new GridLayerView(zoomImageView);
                }
            }
        }
        return gridLayerView;
    }

    private GridLayerView(ZoomImageView zoomImageView){
        paint=new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        this.zoomImageView=zoomImageView;
    }

    public void setPrecision(float precision){
        Logger.e("--------"+zoomImageView.r01+";"+zoomImageView.r10);
        this.precision=precision;
        pixIntervalX=precision/Math.abs(1/zoomImageView.r01)*zoomImageView.totalRatio;
        prxIntervalY=precision/Math.abs(1/zoomImageView.r10)*zoomImageView.totalRatio;

    }

    public void setScalePrecision(float scale){
        pixIntervalX=precision/Math.abs(1/zoomImageView.r01)*scale;
        prxIntervalY=precision/Math.abs(1/zoomImageView.r10)*scale;
    }

    public void drawGrid(Canvas canvas){
        int viewWidth=zoomImageView.getWidth();       //得到画布的宽
        int viewHeight=zoomImageView.getHeight();     //得到画布的高
        //画横线
        if (prxIntervalY!=0&&pixIntervalX!=0){
            for (int i=0;i<viewHeight/prxIntervalY;i++){
                canvas.drawLine(0,(float) (i*prxIntervalY),viewWidth,(float)(i*prxIntervalY),paint);
            }
            //画竖线
            for (int i = 0; i < viewWidth / pixIntervalX; i++) {
                canvas.drawLine((float) (i * pixIntervalX), 0, (float)(i * pixIntervalX), viewHeight, paint);
            }
        }
    }

    public void onDestroy(){
        gridLayerView=null;
        pixIntervalX=0;
        prxIntervalY=0;
    }

}
