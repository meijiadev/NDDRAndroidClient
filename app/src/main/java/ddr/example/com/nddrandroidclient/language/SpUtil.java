package ddr.example.com.nddrandroidclient.language;

import android.content.Context;
import android.content.SharedPreferences;

import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * desc：用于保存设置的参数
 */
public class SpUtil {
    public static final String LANGUAGE = "language";
    private static final String SP_NAME = "poemTripSpref";
    public static final String CHARGE_STATUS="chargeStatus";
    private static SpUtil spUtil;
    private static SharedPreferences hmSpref;
    private static SharedPreferences.Editor editor;

    private SpUtil(Context context) {
        hmSpref = context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor = hmSpref.edit();
    }

    public static SpUtil getInstance(Context context) {
        if (spUtil == null) {
            synchronized (SpUtil.class) {
                if (spUtil == null) {
                    spUtil = new SpUtil(context);
                }
            }
        }
        return spUtil;
    }

    public void putString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
        Logger.e("设置的语言："+value);
    }

    public void putBoolean(String key,boolean value){
        editor.putBoolean(key,value);
        editor.commit();
    }

    public String getString(String key) {
        return hmSpref.getString(key,"");
    }

    public boolean getBoolean(String key){
        return hmSpref.getBoolean(key,false);
    }
}
