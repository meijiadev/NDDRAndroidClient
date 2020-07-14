package ddr.example.com.nddrandroidclient.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import butterknife.OnClick;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 *    time   : 2019/10/27
 *    desc   : 崩溃捕捉界面
 */
public final class CrashActivity extends DDRActivity {
    
    private CaocConfig mConfig;
    private DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); //用于格式化日期，作为日志文件名的一部分

    @Override
    protected int getLayoutId() {
        return R.layout.activity_crash;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mConfig = CustomActivityOnCrash.getConfigFromIntent(getIntent());
        if (mConfig == null) {
            // 这种情况永远不会发生，只要完成该活动就可以避免递归崩溃。
            finish();
        }
        saveCrashInfo2File(CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent()));
    }

    @OnClick({R.id.btn_crash_restart, R.id.btn_crash_log})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crash_restart:
                CustomActivityOnCrash.restartApplication(CrashActivity.this, mConfig);
                break;
            case R.id.btn_crash_log:
                AlertDialog dialog = new AlertDialog.Builder(CrashActivity.this)
                        .setTitle(R.string.crash_error_details)
                        .setMessage(CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent()))
                        .setPositiveButton(R.string.crash_close, null)
                        .setNeutralButton(R.string.crash_copy_log, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                copyErrorToClipboard();
                            }
                        })
                        .show();
                TextView textView = dialog.findViewById(android.R.id.message);
                if (textView != null) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 复制报错信息到剪贴板
     */
    @SuppressWarnings("all")
    private void copyErrorToClipboard() {
        String errorInformation = CustomActivityOnCrash.getAllErrorDetailsFromIntent(CrashActivity.this, getIntent());
        ContextCompat.getSystemService(this, ClipboardManager.class).setPrimaryClip(ClipData.newPlainText(getString(R.string.crash_error_info), errorInformation));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * errorMessage
     * @param errorMessage
     * @return
     */
    private String saveCrashInfo2File(String errorMessage){
        File dir=new File(GlobalParameter.ROBOT_FOLDER_LOG);
        if (dir.exists()){
            //  Logger.e("文件夹已存在，无须创建");
        }else {
            Logger.e("创建文件");
            dir.mkdirs();
        }
        StringBuffer sb=new StringBuffer();
        sb.append(errorMessage);
        //存到文件
        String time=dateFormat.format(new Date());
        String fileName = "crash-" + time + ".txt";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                Logger.e("建立log文件");
                File path = new File(GlobalParameter.ROBOT_FOLDER_LOG);
                FileOutputStream fos = new FileOutputStream(path +"/"+ fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return fileName;
    }
}