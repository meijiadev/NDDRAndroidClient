package ddr.example.com.ddrandroidclient.ui.activity;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.gyf.immersionbar.BarHide;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

import butterknife.BindView;

import ddr.example.com.ddrandroidclient.R;
import ddr.example.com.ddrandroidclient.common.DDRActivity;

/**
 * time:2019/10/26
 * desc:闪屏页面
 */
public class SplashActivity extends DDRActivity implements OnPermission,Animation.AnimationListener {
    private static final int ANIM_TIME = 1000;
    @BindView(R.id.iv_splash)
    ImageView ivSplash;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        AlphaAnimation alphaAnimation=new AlphaAnimation(0.4f,1.0f);
        alphaAnimation.setDuration(ANIM_TIME);
        alphaAnimation.setAnimationListener(this);
        ivSplash.startAnimation(alphaAnimation);

        // 设置状态栏和导航栏参数
        getStatusBarConfig()
                // 有导航栏的情况下，activity全屏显示，也就是activity最下面被导航栏覆盖，不写默认非全屏
                .fullScreen(true)
                // 隐藏状态栏
                .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
                // 透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
                .transparentNavigationBar()
                .init();
    }


    @Override
    public void onBackPressed() {
        //禁用返回键
        //super.onBackPressed();

    }

    /**
     * 请求权限
     */
    private void requestPermission(){
        XXPermissions.with(this)
                .permission(Permission.Group.STORAGE)
                .request(this);
    }

    @Override
    public void hasPermission(List<String> granted, boolean isAll) {
        startActivityFinish(LoginActivity.class);
    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {
        if (quick){
            toast("授权失败");
            XXPermissions.gotoPermissionSettings(SplashActivity.this, true);
        }else {
            toast("请先授予权限");
            postDelayed(this::requestPermission, 1000);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (XXPermissions.isHasPermission(SplashActivity.this, Permission.Group.STORAGE)) {
            hasPermission(null, true);
        } else {
            requestPermission();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        requestPermission();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

}
