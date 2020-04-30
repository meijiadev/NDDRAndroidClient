package ddr.example.com.nddrandroidclient.other;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * desc:输入限制
 */
public class InputFilterMinMax implements InputFilter {
    private float min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Float.parseFloat(min);
        this.max = Float.parseFloat(max);
    }

    /**
     * 输入过滤器
     * @param source 输入的文字
     * @param start
     * @param end
     * @param dest 原先显示的内容
     * @param dstart
     * @param dend
     * @return
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Logger.e("--------"+dest.toString()+";"+source.toString()+";"+start+";"+end+";"+dstart+";"+dend);
        try {
            if(source.toString().equals("-")){
                if (dest.toString().equals("")){
                    return null;
                }else {
                    float input = Float.parseFloat(source.toString()+dest.toString());
                    if (isInRange(min, max, input))
                        return null;
                }
            }else {
                float input = Float.parseFloat(dest.toString()+source.toString());
                if (isInRange(min, max, input))
                    return null;
            }
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
        return "";
    }


    /**
     * 判断输入的内容是否在范围内
     * @param a
     * @param b
     * @param c
     * @return
     */
    private boolean isInRange(float a, float b, float c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}

