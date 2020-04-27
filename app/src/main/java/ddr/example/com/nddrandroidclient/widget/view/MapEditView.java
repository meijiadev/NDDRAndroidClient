package ddr.example.com.nddrandroidclient.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
    private Bitmap sourceBitmap;
    private int width,height;           //屏幕可视范围的大小
    private int bitmapWidth,bitmapHeight;
    private int measureWidth,measureHeight;
    private float scale=1f;
    /**
     *用于裁剪源图像的矩形（可重复使用）。
     */
    private final Rect mRectSrc = new Rect();

    /**
     * 用于在画布上指定绘图区域的矩形（可重新使用）。
     */
    private final Rect mRectDst = new Rect();

    public MapEditView(Context context) {
        super(context);
        paint=new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
    }

    public MapEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint=new Paint();

    }

    public void setBitmap(Bitmap bitmap){
        this.sourceBitmap=bitmap;

    }

    public int getBitmapWidth() {
        return bitmapWidth;
    }

    public int getBitmapHeight() {
        return bitmapHeight;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public int getMarginTop() {
        return marginTop;
    }

    private int marginLeft,marginTop;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            width = getWidth();
            height = getHeight();
            if (sourceBitmap!=null){
                bitmapWidth=sourceBitmap.getWidth();
                bitmapHeight=sourceBitmap.getHeight();
                mRectSrc.left=0;
                mRectSrc.top=0;
                mRectSrc.right=bitmapWidth;
                mRectSrc.bottom=bitmapHeight;
                Logger.e("-----:"+width+";"+height+";"+bitmapWidth+";"+bitmapHeight);
                if (bitmapWidth<width&bitmapHeight<height){
                    scale=1;
                }else if (bitmapWidth>width&&bitmapHeight>height){
                    scale=Math.max((float) bitmapWidth/width,(float) bitmapHeight/height);
                    bitmapWidth=(int) (bitmapWidth/scale);
                    bitmapHeight=(int)(bitmapHeight/scale);
                    //Logger.e("--宽高都大于画布的宽高："+scale);
                }else if (bitmapWidth>width){
                    scale=(float)bitmapWidth/width ;
                    bitmapWidth=(int)(bitmapWidth/scale);
                    bitmapHeight=(int)(bitmapHeight/scale);
                    //Logger.e("--宽大于画布的宽："+scale);
                }else if (bitmapHeight>height){
                    scale=(float) bitmapHeight/height;
                    bitmapWidth=(int)(bitmapWidth/scale);
                    bitmapHeight=(int)(bitmapHeight/scale);
                    Logger.e("--高大于画布的高："+scale);
                }
                mRectDst.left=(width-bitmapWidth)/2;
                mRectDst.top=(height-bitmapHeight)/2;
                mRectDst.right=(width+bitmapWidth)/2;
                mRectDst.bottom=(height+bitmapHeight)/2;
                marginLeft=mRectDst.left;
                marginTop=mRectDst.top;
                Logger.e("---------:"+marginLeft+";"+marginTop);
            }

        }

    }

    /**
     * 刷新地图
     */
    public void refreshMap(){
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(sourceBitmap,mRectSrc,mRectDst,paint);
    }






    public float getScale() {
        return scale;
    }

    /**
     * 返回地图中心处的坐标
     * @return
     */
    public XyEntity getCenterCoordinate(){
        XyEntity xyEntity=new XyEntity(x-mRectDst.left,y-mRectDst.top);
       return xyEntity;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x=event.getX();
        y=event.getY();
        Logger.e("onTouchEvent -x："+x+"----y:"+y);
        return super.onTouchEvent(event);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        //Logger.e("--------:"+widthSize+";"+heightSize);
        if (widthMode == View.MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
             measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = 1000;
        }

        if (heightMode == View.MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = 1000;
        }
        Logger.e("设置地图控件大小");
        setMeasuredDimension(measureWidth, measureHeight);
    }

}
