package ddr.example.com.nddrandroidclient.widget.view;

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

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;

/**
 * time: 2019/11/1
 * desc: 自定义浮窗内容(状态信息和遥控弹窗的触发按钮）
 */
public class FloatView extends View  {
    private Bitmap bgBitmap;   //背景图片
    private Bitmap JTBitmap;   //急停
    private Bitmap YKBitmap;   //遥控
    private Bitmap SBBitmap;   //手柄
    private Bitmap CDBitmap;   //充电
    private Paint mPaint;
    private  int DEFAULT_WIDTH=74;         //单位都是像素
    private  int DEFAULT_HEIGHT=373;
    private OnFloatViewListener onFloatViewListener;
    private int oldStopStat,oldCharging;           // oldCharging :如果为0默认不在充电 为1 表示在充电状态

    private NotifyBaseStatusEx notifyBaseStatusEx;
    public FloatView(Context context) {
        super(context);
        init();
    }

    public FloatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void upDate(MessageEvent mainUpDate){
        switch (mainUpDate.getType()){
            case updateBaseStatus:
                initStatusBar();
                break;
        }
    }

    private void init(){
        mPaint=new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.parseColor("#979797"));
        EventBus.getDefault().register(this);
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        bgBitmap=BitmapFactory.decodeResource(getResources(), R.mipmap.float_bg);
        YKBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.yk_def);
        JTBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.jt_def);
        CDBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.chongd_def);
        SBBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.sb_def);
    }

    private void initStatusBar() {
        //robotStatusEntity=RobotStatusEntity.getInstance();
        switch (notifyBaseStatusEx.getStopStat()) {
            case 4:
                JTBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.jt_check);
                SBBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.sb_def);
                break;
            case 8:
                JTBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.jt_def);
                SBBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.sb_check);
                break;
            case 12:
                JTBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.jt_check);
                SBBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.sb_check);
                break;
            case 0:
                JTBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.jt_def);
                SBBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.sb_def);
                break;
        }
        if(notifyBaseStatusEx.isChargingStatus()) {
            CDBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.chongd_check);
            if (oldCharging==0){
                oldCharging=1;
                invalidate();
            }
        }else {
            CDBitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.chongd_def);
            if (oldCharging==1){
                invalidate();     //刷新界面 重绘view
            }
        }
        if (oldStopStat!=notifyBaseStatusEx.getStopStat()){
            invalidate();
            oldStopStat=notifyBaseStatusEx.getStopStat();
        }




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
        canvas.drawBitmap(CDBitmap,13,100,mPaint);
        canvas.drawBitmap(SBBitmap,13,178,mPaint);
        canvas.drawLine(16,256,59,256,mPaint);
        canvas.drawBitmap(YKBitmap,13,291,mPaint);
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
