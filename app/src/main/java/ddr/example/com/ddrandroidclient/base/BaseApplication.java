package ddr.example.com.ddrandroidclient.base;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.hjq.toast.ToastInterceptor;
import com.hjq.toast.ToastUtils;
import com.squareup.leakcanary.LeakCanary;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import ddr.example.com.ddrandroidclient.helper.CrashHandlerManager;
import ddr.example.com.ddrandroidclient.helper.EventBusManager;
import ddr.example.com.ddrandroidclient.ui.activity.CrashActivity;
import ddr.example.com.ddrandroidclient.ui.activity.HomeActivity;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initSDK(this);
    }

    public static void initSDK(Application application){
        // 这个过程专门用于堆分析的 leak 金丝雀
        // 你不应该在这个过程中初始化你的应用程序
        if (LeakCanary.isInAnalyzerProcess(application)) {
            return;
        }
        //全局异常捕获
        CrashHandlerManager.getInstance().init(application.getApplicationContext());
        // 内存泄漏检测
        LeakCanary.install(application);
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
        ToastUtils.init(application);
        // EventBus 事件总线
        EventBusManager.init();

        // Crash 捕捉界面
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SHOW_CUSTOM)
                .enabled(true)
                .trackActivities(true)
                .minTimeBetweenCrashesMs(2000)
                // 重启的 Activity
                .restartActivity(HomeActivity.class)
                // 错误的 Activity
                .errorActivity(CrashActivity.class)
                // 设置监听器
                //.eventListener(new YourCustomEventListener())
                .apply();

    }
}
