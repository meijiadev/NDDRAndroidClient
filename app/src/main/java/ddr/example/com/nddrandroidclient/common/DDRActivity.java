package ddr.example.com.nddrandroidclient.common;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.gyf.immersionbar.ImmersionBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.toast.ToastUtils;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.Screen;

import org.greenrobot.eventbus.EventBus;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseActivity;
import ddr.example.com.nddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.nddrandroidclient.helper.EventBusManager;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.widget.view.FloatView;

/**
 *  time    ：2019/10/25
 *  describe: Activity 基类
 */
public class DDRActivity extends BaseActivity implements OnTitleBarListener {
    /** 标题栏对象 */
    private TitleBar mTitleBar;
    /**
     * 状态栏沉浸
     */
    private ImmersionBar mImmersionBar;

    /**
     * ButterKnife 注解
     */
    private Unbinder mButterKnife;
    /**
     * 获取标题栏 id
     */
    protected int getTitleId() {
        return 0;
    }

    @Override
    protected void initActivity() {
        super.initActivity();
        ActivityStackManager.getInstance().onCreated(this);
    }

    @Override
    protected void initLayout() {
        super.initLayout();
        // 初始化标题栏的监听
        if (getTitleId() > 0) {
            // 勤快模式
            View view = findViewById(getTitleId());
            if (view instanceof TitleBar) {
                mTitleBar = (TitleBar) view;
            }
        } else if (getTitleId() == 0) {
            // 懒人模式
            mTitleBar = findTitleBar(getContentView());
        }
        if (mTitleBar != null) {
            mTitleBar.setOnTitleBarListener(this);
        }
        mButterKnife = ButterKnife.bind(this);
        EventBusManager.register(this);
        initImmersion();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityStackManager.getInstance().onResume(this);
        if (!OpenCVLoader.initDebug()) {
            Logger.e("Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Logger.e("OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    Logger.e("OpenCVLoader加载成功");
                    break;
                default:
                    break;
            }
            super.onManagerConnected(status);
        }
    };

    /**
     * 递归获取 ViewGroup 中的 TitleBar 对象
     */
    static TitleBar findTitleBar(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if ((view instanceof TitleBar)) {
                return (TitleBar) view;
            } else if (view instanceof ViewGroup) {
                TitleBar titleBar = findTitleBar((ViewGroup) view);
                if (titleBar != null) {
                    return titleBar;
                }
            }
        }
        return null;
    }


    /**
     * 是否使用沉浸式状态栏
     */
    public boolean isStatusBarEnabled() {
        return true;
    }

    /**
     * 初始化沉浸式
     */
    protected void initImmersion() {
        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            statusBarConfig().init();

            // 设置标题栏沉浸
            if (getTitleId() > 0) {
                ImmersionBar.setTitleBar(this, findViewById(getTitleId()));
            } else if (mTitleBar != null) {
                ImmersionBar.setTitleBar(this, mTitleBar);
            }
        }
    }
    /**
     * 初始化沉浸式状态栏
     */
    protected ImmersionBar statusBarConfig() {
        // 在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this)
                // 默认状态栏字体颜色为黑色
                .statusBarDarkFont(statusBarDarkFont());
        return mImmersionBar;
    }
    /**
     * 获取状态栏字体颜色
     */
    public boolean statusBarDarkFont() {
        // 返回真表示黑色字体
        return true;
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    public ImmersionBar getStatusBarConfig() {
        return mImmersionBar;
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(@StringRes int id) {
        setTitle(getString(id));
    }

    /**
     * 设置标题栏的标题
     */
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mTitleBar != null) {
            mTitleBar.setTitle(title);
        }
    }

    /**
     * 设置标题栏的左标题
     */
    public void setLeftTitle(int id) {
        if (mTitleBar != null) {
            mTitleBar.setLeftTitle(id);
        }
    }

    public void setLeftTitle(CharSequence text) {
        if (mTitleBar != null) {
            mTitleBar.setLeftTitle(text);
        }
    }

    public CharSequence getLeftTitle() {
        if (mTitleBar != null) {
            return mTitleBar.getLeftTitle();
        }
        return "";
    }

    /**
     * 设置标题栏的右标题
     */
    public void setRightTitle(int id) {
        if (mTitleBar != null) {
            mTitleBar.setRightTitle(id);
        }
    }

    public void setRightTitle(CharSequence text) {
        if (mTitleBar != null) {
            mTitleBar.setRightTitle(text);
        }
    }

    public CharSequence getRightTitle() {
        if (mTitleBar != null) {
            return mTitleBar.getRightTitle();
        }
        return "";
    }

    /**
     * 设置标题栏的左图标
     */
    public void setLeftIcon(int id) {
        if (mTitleBar != null) {
            mTitleBar.setLeftIcon(id);
        }
    }

    public void setLeftIcon(Drawable drawable) {
        if (mTitleBar != null) {
            mTitleBar.setLeftIcon(drawable);
        }
    }

    @Nullable
    public Drawable getLeftIcon() {
        if (mTitleBar != null) {
            return mTitleBar.getLeftIcon();
        }
        return null;
    }

    /**
     * 设置标题栏的右图标
     */
    public void setRightIcon(int id) {
        if (mTitleBar != null) {
            mTitleBar.setRightIcon(id);
        }
    }

    public void setRightIcon(Drawable drawable) {
        if (mTitleBar != null) {
            mTitleBar.setRightIcon(drawable);
        }
    }

    @Nullable
    public Drawable getRightIcon() {
        if (mTitleBar != null) {
            return mTitleBar.getRightIcon();
        }
        return null;
    }

    @Nullable
    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    /**
     * {@link OnTitleBarListener}
     */

    /**
     * TitleBar 左边的View被点击了
     */
    @Override
    public void onLeftClick(View v) {
        onBackPressed();
    }

    /**
     * TitleBar 中间的View被点击了
     */
    @Override
    public void onTitleClick(View v) {}

    /**
     * TitleBar 右边的View被点击了
     */
    @Override
    public void onRightClick(View v) {}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mButterKnife != null) {
            mButterKnife.unbind();
        }
        EventBusManager.unregister(this);
        ActivityStackManager.getInstance().onDestroyed(this);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_right_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_left_out);
    }

    /**
     * 显示吐司
     */
    public void toast(CharSequence text) {
        ToastUtils.show(text);
    }

    public void toast(@StringRes int id) {
        String text=getString(id);
        ToastUtils.show(text);
    }


    public void toast(Object object) {
        ToastUtils.show(object);
    }


}
