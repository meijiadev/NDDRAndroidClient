package ddr.example.com.nddrandroidclient.common;

import android.os.Environment;

/**
 * time： 2019/11/11
 * desc： 全局参数
 */
public class GlobalParameter {
    public static final int DEFAULT=0;
    public static final String ROBOT_FOLDER=Environment.getExternalStorageDirectory().getPath()+"/"+"DDRMap"+"/";      //存储地址
    private static String account;         //用户名
    private static String password;        //密码
    private static boolean isAutoCharge;  //是否自动充电

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

    public static void setIsAutoCharge(boolean isAutoCharge) {
        GlobalParameter.isAutoCharge = isAutoCharge;
    }

    public static boolean isIsAutoCharge() {
        return isAutoCharge;
    }
}
