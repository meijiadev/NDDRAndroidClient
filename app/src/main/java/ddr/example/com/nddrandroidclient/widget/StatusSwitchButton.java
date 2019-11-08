package ddr.example.com.nddrandroidclient.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time : 2019/10/31
 * desc : 状态切换按钮
 */
public class StatusSwitchButton extends View {
    private int measureWidth, measureHeight;    //控件的宽高
    private int DEFAULT_WIDTH=1232;
    private int DEFAULT_HEIGHT=200;
    private Bitmap bgBitmap;
    private Bitmap btBitmap;
    private int bt_width,bt_height;
    private Rect rectSrcbg;
    private Rect rectdstbg;
    private float baseline;
    private OnStatusSwitchListener mListener;
    private int bt_position; //滑块的位置
    private static final int DEFAULT=0;
    private static final int LEFT=1;
    private static final int CENTRE=2;
    private static final int RIGHT=3;
    private Paint defaultPaint;
    private NotifyEnvInfo notifyEnvInfo;
    private NotifyBaseStatusEx notifyBaseStatusEx;

    public StatusSwitchButton(Context context) {
        super(context);
    }

    public StatusSwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        defaultPaint=new Paint();
        defaultPaint.setColor(Color.WHITE);
        defaultPaint.setTextSize(24);
        defaultPaint.setTextAlign(Paint.Align.CENTER);
        bgBitmap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.status_bg);
        btBitmap=BitmapFactory.decodeResource(context.getResources(),R.mipmap.status_bt);
        DEFAULT_WIDTH=bgBitmap.getWidth();
        DEFAULT_HEIGHT=bgBitmap.getHeight();
        bt_width=btBitmap.getWidth();
        bt_height=btBitmap.getHeight();
        rectSrcbg=new Rect(0,0,bgBitmap.getWidth(),bgBitmap.getHeight());
        rectdstbg=new Rect(0,0,DEFAULT_WIDTH,DEFAULT_HEIGHT);
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        EventBus.getDefault().register(this);      // 注册监听器
        Paint.FontMetrics fontMetrics=defaultPaint.getFontMetrics();
        float distance=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
        baseline=DEFAULT_HEIGHT/2+distance;
        Logger.e("------:"+baseline+";"+distance);
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateBaseStatus:
                isAutoMode();
                break;
        }
    }
    public void isAutoMode() {
//        Logger.e("模式"+notifyBaseStatusEx.getMode());
//        Logger.e("模式"+notifyBaseStatusEx.geteSelfCalibStatus()+"子模式"+notifyBaseStatusEx.getSonMode());
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                //自标定

                break;
            case 1:
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //待命模式
                        bt_position = RIGHT;
                       break;
                    case 3:
                        //自动模式
                        bt_position = LEFT;
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                bt_position = LEFT;
                                break;
                            case 17:
                                bt_position = CENTRE;
                                break;
                        }
                       break;
                }
                break;
        }
        invalidate();
    }

    /**
     * 注册监听事件
     * @param onStatusListener
     */
    public void setOnStatusListener(OnStatusSwitchListener onStatusListener){
        mListener=onStatusListener;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bgBitmap!=null){
            canvas.drawBitmap(bgBitmap,0,0,defaultPaint);
            switch (bt_position){
                case DEFAULT:
                    canvas.drawBitmap(btBitmap,measureWidth-bt_width,(measureHeight-bt_height)/2,defaultPaint);
                    break;
                case LEFT:
                    canvas.drawBitmap(btBitmap,0,(measureHeight-bt_height)/2,defaultPaint);
                    break;
                case CENTRE:
                    canvas.drawBitmap(btBitmap,(measureWidth-bt_width)/2,(measureHeight-bt_height)/2,defaultPaint);
                    break;
                case RIGHT:
                    canvas.drawBitmap(btBitmap,measureWidth-bt_width,(measureHeight-bt_height)/2,defaultPaint);
                    break;
            }
            canvas.drawText("执行",bt_width/2,baseline,defaultPaint);
            canvas.drawText("暂停",measureWidth/2,baseline,defaultPaint);
            canvas.drawText("待命",measureWidth-100,baseline,defaultPaint);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = DEFAULT_WIDTH;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = DEFAULT_HEIGHT;
        }
        setMeasuredDimension(measureWidth, measureHeight);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        Logger.e("--------" + x);
        if (mListener != null) {
            if (x > 2 * measureWidth / 3) {
                mListener.onRightClick();
            } else if (x > measureWidth / 3) {
                mListener.onCentreClick();
            } else {
                mListener.onLeftClick();
            }
        }
        return super.onTouchEvent(event);
    }

    public void onDestroy(){
        EventBus.getDefault().unregister(this);
    }

    public interface OnStatusSwitchListener{
        /**
         * 点击左边
         */
        void onLeftClick();

        /**
         * 点击中间
         */
        void onCentreClick();

        /**
         * 点击右边
         */
        void onRightClick();
    }
}
