package ddr.example.com.nddrandroidclient.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time : 2020/04/16
 * desc : 状态切换按钮
 */
public class StatusSwitchButton extends LinearLayout implements View.OnClickListener {
    private OnStatusSwitchListener mListener;
    private NotifyBaseStatusEx notifyBaseStatusEx;

    public StatusSwitchButton(Context context) {
        super(context);
    }

    public StatusSwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        EventBus.getDefault().register(this);      // 注册监听器
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateBaseStatus:
                isAutoMode();
                break;
        }
    }
    public void isAutoMode() {
        switch (notifyBaseStatusEx.geteSelfCalibStatus()) {
            case 0:
                //自标定
                break;
            case 1:
                initTextViewBackground();
                switch (notifyBaseStatusEx.getMode()) {
                    case 1:
                        //待命模式
                        rightText.setBackgroundResource(R.drawable.bt_switch_button);
                       break;
                    case 3:
                        //自动模式
                        switch (notifyBaseStatusEx.getSonMode()){
                            case 16:
                                leftText.setBackgroundResource(R.drawable.bt_switch_button);
                                break;
                            case 17:
                                centreText.setBackgroundResource(R.drawable.bt_switch_button);
                                break;
                        }
                       break;
                }
                break;
        }

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.layout_switch_buttom,this);
        initWidget();

    }
    public TextView leftText,centreText,rightText;
    private void initWidget(){
        leftText=findViewById(R.id.tv_left);
        centreText=findViewById(R.id.tv_center);
        rightText=findViewById(R.id.tv_right);

        leftText.setOnClickListener(this::onClick);
        centreText.setOnClickListener(this::onClick);
        rightText.setOnClickListener(this::onClick);
    }


    /**
     * 注册监听事件
     * @param onStatusListener
     */
    public void setOnStatusListener(OnStatusSwitchListener onStatusListener){
        mListener=onStatusListener;
    }



    public void onDestroy(){
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener!=null){
            initTextViewBackground();
            switch (v.getId()){
                case R.id.tv_left:
                    mListener.onLeftClick();
                    leftText.setBackgroundResource(R.drawable.bt_switch_button);
                    break;
                case R.id.tv_center:
                    mListener.onCentreClick();
                    centreText.setBackgroundResource(R.drawable.bt_switch_button);
                    break;
                case R.id.tv_right:
                    mListener.onRightClick();
                    rightText.setBackgroundResource(R.drawable.bt_switch_button);
                    break;

            }

        }
    }

    /**
     * 初始化TextView背景
     */
    private void initTextViewBackground(){
        leftText.setBackgroundResource(R.drawable.bg_switch_button);
        centreText.setBackgroundResource(R.drawable.bg_switch_button);
        rightText.setBackgroundResource(R.drawable.bg_switch_button);

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
