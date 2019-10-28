package ddr.example.com.ddrandroidclient.common;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ddr.example.com.ddrandroidclient.base.BaseLazyFragment;
import ddr.example.com.ddrandroidclient.helper.EventBusManager;

/**
 *  time   : 2019/10/28
 *  desc   : 项目中 Fragment 懒加载基类
 * @param <A>
 */
public abstract class DDRLazyFragment<A extends DDRActivity> extends BaseLazyFragment<A> {

    /** 状态栏沉浸 */
    private ImmersionBar mImmersionBar;
    /** ButterKnife 注解 */
    private Unbinder mButterKnife;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            mButterKnife = ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    protected void initFragment() {
        initImmersion();
        super.initFragment();
        EventBusManager.register(this);
    }


    /**
     * 初始化沉浸式
     */
    protected void initImmersion() {

        // 初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            statusBarConfig().init();
        }
    }

    /**
     * 是否在Fragment使用沉浸式
     */
    public boolean isStatusBarEnabled() {
        return false;
    }

    /**
     * 获取状态栏沉浸的配置对象
     */
    protected ImmersionBar getStatusBarConfig() {
        return mImmersionBar;
    }

    /**
     * 初始化沉浸式
     */
    private ImmersionBar statusBarConfig() {
        //在BaseActivity里初始化
        mImmersionBar = ImmersionBar.with(this)
                // 默认状态栏字体颜色为黑色
                .statusBarDarkFont(statusBarDarkFont())
                // 解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                .keyboardEnable(true);
        return mImmersionBar;
    }

    /**
     * 获取状态栏字体颜色
     */
    protected boolean statusBarDarkFont() {
        // 返回真表示黑色字体
        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isStatusBarEnabled() && isLazyLoad()) {
            // 重新初始化状态栏
            statusBarConfig().init();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mButterKnife != null) {
            mButterKnife.unbind();
        }
        EventBusManager.unregister(this);
    }

    /**
     * 显示吐司
     */
    public void toast(CharSequence text) {
        ToastUtils.show(text);
    }

    public void toast(@StringRes int id) {
        ToastUtils.show(id);
    }

    public void toast(Object object) {
        ToastUtils.show(object);
    }


}
