package ddr.example.com.nddrandroidclient.common;

import android.os.Environment;

/**
 * time： 2019/11/11
 * desc： 全局参数
 */
public class GlobalParameter {
    public static final String ROBOT_FOLDER=Environment.getExternalStorageDirectory().getPath()+"/"+"DDRMap"+"/";      //存储地址
    public static final String ROBOT_FOLDER_LOG=Environment.getExternalStorageDirectory().getPath()+"/"+"DDRMapLog"+"/";      //日志存储地址
    public static final String ROBOT_FOLDER_DOWNLOAD=Environment.getExternalStorageDirectory().getPath()+"/"+"DDRMapDownload"+"/"; //下载文件夹
    private static String account;            //用户名
    private static String password;          //密码

    public static void setAccount(String account) {
        GlobalParameter.account = account;
    }

    public static void setPassword(String password) {
        GlobalParameter.password = password;
    }

    public static String getAccount() {
        return account;
    }

    public static String getPassword() {
        return password;
    }

}
