package ddr.example.com.nddrandroidclient.base;


import android.app.Application;


import android.content.Context;
import android.os.Build;
import android.util.Log;


import android.widget.Toast;

import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
//import com.squareup.leakcanary.LeakCanary;
import com.hjq.toast.style.ToastAliPayStyle;
import com.hjq.toast.style.ToastQQStyle;
import com.hjq.toast.style.ToastWhiteStyle;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;

import ddr.example.com.nddrandroidclient.glide.ImageLoader;
import ddr.example.com.nddrandroidclient.helper.CrashHandlerManager;
import ddr.example.com.nddrandroidclient.helper.EventBusManager;
import ddr.example.com.nddrandroidclient.language.LanguageUtil;
import ddr.example.com.nddrandroidclient.language.SpUtil;
import ddr.example.com.nddrandroidclient.ui.activity.CrashActivity;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.activity.LoginActivity;
import ddr.example.com.nddrandroidclient.ui.activity.RelocationActivity;
import ddr.example.com.nddrandroidclient.widget.view.FloatView;

/**
 * time :  2019/10/28
 * desc :  项目中的 application 基类
 */
public class BaseApplication extends Application implements FloatView.OnFloatViewListener {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        /**
         * 对于7.0以下，需要在Application创建的时候进行语言切换
         */
        String language = SpUtil.getInstance(this).getString(SpUtil.LANGUAGE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(BaseApplication.getContext(), language);
        }
        initSDK(this);
    }

    public  void initSDK(Application application) {
        // 这个过程专门用于堆分析的 leak 金丝雀
        // 你不应该在这个过程中初始化你的应用程序
       /* if (LeakCanary.isInAnalyzerProcess(application)) {
            return;
        }*/
        // 图片加载器
        ImageLoader.init(application);

        // 内存泄漏检测
        //LeakCanary.install(application);
        // 设置 Toast 拦截器
        ToastUtils.setToastInterceptor(new ToastInterceptor() {
            @Override
            public boolean intercept(Toast toast, CharSequence text) {
                boolean intercept = super.intercept(toast, text);
                if (intercept) {
                    Log.e("Toast", "空 Toast");
                } else {
                    Log.i("Toast", text.toString());
                }
                return intercept;
            }
        });
        // 吐司工具类
        ToastUtils.init(application,new ToastWhiteStyle(application));

        // EventBus 事件总线
        EventBusManager.init();
        // Crash 捕捉界面
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
                .enabled(true)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(2000)
                // 重启的 Activity
                .restartActivity(LoginActivity.class)
                // 错误的 Activity
                .errorActivity(CrashActivity.class)
                // 设置监听器
                //.eventListener(new YourCustomEventListener())
                .apply();
        initFloatView();
        //初始化数据库
        LitePal.initialize(this);
    }

    /**
     * 显示悬浮窗
     * @param
     */
    private void initFloatView(){
        FloatView floatView=new FloatView(this);
        floatView.setOnFloatViewListener(this);
        FloatWindow
                .with(getApplicationContext())
                .setView(floatView)
                .setX(Screen.width,0.5f)
                .setY(120)
                .setMoveType(MoveType.active)
                .setFilter(true, HomeActivity.class,RelocationActivity.class)
                .setDesktopShow(false)
                .build();
    }

    @Override
    public void onClickBottom() {
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.touchFloatWindow));
        try {
            FloatWindow.get().hide();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Context getContext() {
        return context;
    }





}




