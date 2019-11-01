package ddr.example.com.nddrandroidclient.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time: 2019/11/1
 * desc: 自定义浮窗内容
 */
public class FloatView extends View  {
    private Bitmap bgBitmap;   //背景图片
    private Bitmap JTBitmap;   //急停
    private Paint mPaint;
    private  int DEFAULT_WIDTH=74;         //单位都是像素
    private  int DEFAULT_HEIGHT=373;

    private OnFloatViewListener onFloatViewListener;

    public FloatView(Context context) {
        super(context);
        init();
    }

    public FloatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mPaint=new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.parseColor("#979797"));
        bgBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.float_bg);
        JTBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.float_jt);

    }

    /**
     * 设置监听接口对象
     * @param onFloatViewListener
     */
    public void setOnFloatViewListener(OnFloatViewListener onFloatViewListener){
        this.onFloatViewListener=onFloatViewListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bgBitmap,0,0,mPaint);
        canvas.drawBitmap(JTBitmap,13,18,mPaint);
        canvas.drawBitmap(JTBitmap,13,100,mPaint);
        canvas.drawBitmap(JTBitmap,13,178,mPaint);
        canvas.drawLine(16,256,59,256,mPaint);
        canvas.drawBitmap(JTBitmap,13,291,mPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                float x=event.getX();
                float y=event.getY();
                Logger.e("------点击");
                if (x>10&&y>260&&onFloatViewListener!=null){
                    onFloatViewListener.onClickBottom();
                    invalidate();
                }
                break;
        }
        return false;
    }


    /**
     * desc： 底部点击事件的监听接口
     */
    public interface OnFloatViewListener{
        //点击底部
        void onClickBottom();
    }


}
