package ddr.example.com.nddrandroidclient.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.gyf.immersionbar.BarHide;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import butterknife.BindView;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.UdpClient;

/**
 * time:2019/10/26
 * desc:闪屏页面
 */
public class SplashActivity extends DDRActivity implements OnPermission,Animation.AnimationListener {
    private static final int ANIM_TIME = 2000;
    @BindView(R.id.iv_splash)
    ImageView ivSplash;
    public UdpClient udpClient;
    private int port=28888;

    private String [] permission=new String[]{ Permission.READ_EXTERNAL_STORAGE,Permission.WRITE_EXTERNAL_STORAGE,Permission.SYSTEM_ALERT_WINDOW};
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
        getStatusBarConfig().hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
                .init();
        receiveBroadcast();
    }

    /**
     * 沉浸式状态栏（已适配 ）
     */
    public  void initState(Activity activity) {
        //Logger.e("启动沉浸式状态栏");
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

    }

    /**
     * 接收广播
     */
    private void receiveBroadcast(){
        udpClient= UdpClient.getInstance(this,ClientMessageDispatcher.getInstance());
        try {
            udpClient.connect(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                .permission(permission)
                .request(this);
    }

    /**
     * 权限通过
     * @param granted
     * @param isAll
     */
    @Override
    public void hasPermission(List<String> granted, boolean isAll) {
        startActivityFinish(LoginActivity.class);
    }

    @Override
    public void noPermission(List<String> denied, boolean quick) {
        if (quick){
            toast(R.string.privilege_faild);
            XXPermissions.gotoPermissionSettings(SplashActivity.this, true);
        }else {
            toast(R.string.please_privilege);
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

    @Override
    protected void onResume() {
        super.onResume();

    }

}
