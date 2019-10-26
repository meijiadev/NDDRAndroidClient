package ddr.example.com.ddrandroidclient.helper;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ddr.example.com.ddrandroidclient.other.Logger;

/**
 * 全局异常处理
 */
public class CrashHandlerManager implements Thread.UncaughtExceptionHandler {
    private Context mContext;
    //用于存储设备的信息和异常信息
    private Map<String,String> infos=new HashMap<>();
    //获取系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //用于格式化日期，作为日志文件名的一部分
    private DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private static CrashHandlerManager crashHandler;

    public void init(Context context){
        Logger.e("-----------初始化异常处理器--------");
        mContext=context;
        mDefaultHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等
     * @param throwable
     * @return
     */
    private boolean handleException(Throwable throwable){
        if (throwable==null){
            return false;
        }
        saveCrashInfo2File(throwable);
        return true;
    }

    /**
     * 收集错误信息并保存本地
     * @param throwable
     * @return
     */
    private String saveCrashInfo2File(Throwable throwable){
        StringBuffer sb=new StringBuffer();
        Writer writer=new StringWriter();
        PrintWriter printWriter=new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause=throwable.getCause();
        while (cause!=null){
            cause.printStackTrace(printWriter);
            cause=cause.getCause();
        }
        printWriter.close();
        //得到错误信息
        String result=writer.toString();
        sb.append(result);
        //存到文件
        String time=dateFormat.format(new Date());
        String fileName = "crash-" + time + ".txt";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            try {
                Logger.e("建立log文件");
                File path = new File(Environment.getExternalStorageDirectory().getPath() + "/" );
                FileOutputStream fos = new FileOutputStream(path +"/"+ fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return fileName;
    }


    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (!handleException(e)&&mDefaultHandler!=null){
            //如果用户没有处理则让系统默认的异常处理器来处理
            Logger.e("------用户未处理");
            mDefaultHandler.uncaughtException(t,e);
        }else {
          /*  Logger.e("用户处理");
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e1){

            }
            // 将Activity的栈清空
            ActivityStackManager.getInstance().finishAllActivities();*/
        }
    }
    public static CrashHandlerManager getInstance(){
        if (crashHandler==null){
            return crashHandler=new CrashHandlerManager();
        }else {
            return crashHandler;
        }
    }
}
