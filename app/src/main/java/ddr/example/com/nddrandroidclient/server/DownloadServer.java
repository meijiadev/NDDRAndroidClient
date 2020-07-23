package ddr.example.com.nddrandroidclient.server;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.http.HttpManager;
import ddr.example.com.nddrandroidclient.other.Logger;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.ResponseBody;

import static ddr.example.com.nddrandroidclient.http.Api.APP_UPDATE_DOMAIN;
import static ddr.example.com.nddrandroidclient.http.Api.APP_UPDATE_DOMAIN_NAME;

/**
 * desc:下载的服务
 */
public class DownloadServer extends Service {
    private File file;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.e("onStartCommand");
        String version=intent.getStringExtra("version");
        RetrofitUrlManager.getInstance().putDomain(APP_UPDATE_DOMAIN_NAME,APP_UPDATE_DOMAIN+"Release/"+version+"/"+"AndroidClient.apk");
        HttpManager.getInstance().getHttpServer().downloadApk()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
            @Override
            public void onSubscribe(Disposable d) {
                Logger.e("----onSubscribe");
            }
            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    long contentLength=responseBody.contentLength();
                    InputStream inputStream=responseBody.byteStream();
                    File directory=new File(GlobalParameter.ROBOT_FOLDER_DOWNLOAD);
                    if (directory.exists()){
                        //  Logger.e("文件夹已存在，无须创建");
                    }else {
                        Logger.e("创建文件");
                        directory.mkdirs();
                    }
                    file=new File(directory,"RobotAndroid.apk");
                    FileOutputStream outputStream=new FileOutputStream(file);
                    byte[] bytes=new byte[1024];
                    int len=0;
                    //循环读取文件的内容，把他放到新的文件目录里面
                    while ((len=inputStream.read(bytes))!=-1){
                        outputStream.write(bytes,0,len);
                        long length =file.length();
                        //获取下载的大小
                        int progress=(int) (length*100/contentLength);
                        Logger.e("---------下载进度："+progress);
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updateProgress,progress));
                    }
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.apkDownloadSucceed,file));
                }catch (Exception e){
                    e.printStackTrace();
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.apkDownloadFailed));
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.e("-------onError:"+e.getMessage());
                EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.apkDownloadFailed));
            }

            @Override
            public void onComplete() {
                Logger.e("onComplete订阅事件完成！");
                stopSelf();
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("onDestroy");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
