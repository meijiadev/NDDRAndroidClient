package ddr.example.com.nddrandroidclient.ui.dialog;

import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.MyDialogFragment;
import ddr.example.com.nddrandroidclient.http.DownloadProgress;
import ddr.example.com.nddrandroidclient.http.HttpManage;
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

        }
        private void getUpdateProgress(){
            new Thread(()->{
                while (isUpdating){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    HttpManage.getServer().getProgress()
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
            getUpdateProgress();
            return this;
        }
    }
}
