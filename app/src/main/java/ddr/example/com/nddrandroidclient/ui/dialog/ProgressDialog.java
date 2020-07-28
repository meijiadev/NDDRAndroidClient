package ddr.example.com.nddrandroidclient.ui.dialog;

import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.fragment.app.FragmentActivity;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.helper.EventBusManager;
import ddr.example.com.nddrandroidclient.http.serverupdate.DownloadProgress;
import ddr.example.com.nddrandroidclient.http.HttpManager;
import ddr.example.com.nddrandroidclient.other.Logger;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 进度弹窗
 */
public final class ProgressDialog {
    public static final class Builder extends MyDialogFragment.Builder<Builder>{
        public final TextView tvProgress;
        public final ProgressBar progressBar;
        private String title;
        private boolean isUpdating=true;    //正在升级
        public Builder(FragmentActivity activity) {
            super(activity);
            setContentView(R.layout.dialog_progress);
            setAnimStyle(BaseDialog.AnimStyle.IOS);
            setCancelable(false);
            tvProgress=findViewById(R.id.tv_progress);
            progressBar=findViewById(R.id.process_bar);
            progressBar.setMax(100);
            EventBus.getDefault().register(this);
        }

        /**
         * 获取服务下载进度
         */
        private void getUpdateProgress(){
            new Thread(()->{
                while (isUpdating){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    HttpManager.getInstance().getHttpServer().getProgress()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<DownloadProgress>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    Logger.d("------------onSubscribe");
                                }

                                @Override
                                public void onNext(DownloadProgress downloadProgress) {
                                    if (!downloadProgress.getState().equals("Net Error")){
                                        int progress=(int)(downloadProgress.getProgress()*100);
                                        if (downloadProgress.getProgressName().equals("Idle")){
                                            isUpdating=false;
                                            tvProgress.setText(100+"%");
                                            progressBar.setProgress(100);
                                            toast("下载完成!");
                                            dismiss();
                                        }else {
                                            progressBar.setProgress(progress);
                                            tvProgress.setText(title+progress+"%");
                                        }
                                        Logger.e("下载进度："+progress+";"+downloadProgress.getProgressName());
                                    }else {
                                        toast("当前机器人无外网连接!");
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    isUpdating=false;
                                    Logger.d("-------------onError");
                                    dismiss();
                                }

                                @Override
                                public void onComplete() {
                                    Logger.d("-------onComplete()");
                                }
                            });
                }
            }).start();
        }

        public Builder setTitle(String title){
            this.title=title;
            return this;
        }

        /**
         *
         * @return
         */
        public Builder setUpdateServer(){
            getUpdateProgress();
            return this;
        }

        @Override
        protected void dismiss() {
            super.dismiss();
            EventBus.getDefault().unregister(this);
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void setProgress(MessageEvent messageEvent){
            switch (messageEvent.getType()){
                case updateProgress:
                    int progress= (int) messageEvent.getData();
                    tvProgress.setText(title+progress+"%");
                    progressBar.setProgress(progress);
                    break;
                case apkDownloadSucceed:
                case apkDownloadFailed:
                    dismiss();
                    break;
            }
        }
    }


}
