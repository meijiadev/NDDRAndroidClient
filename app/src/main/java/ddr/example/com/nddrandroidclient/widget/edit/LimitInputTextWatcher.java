package ddr.example.com.nddrandroidclient.widget.edit;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * desc: 限制输入
 * time:2020/7/14
 */
public class LimitInputTextWatcher implements TextWatcher {

    /** 手机号（只能以 1 开头） */
    public static final String REGEX_MOBILE = "[1]\\d{0,10}";
    /** 中文（普通的中文字符） */
    public static final String REGEX_CHINESE = "[\u4E00-\u9FA5]*";
    /** 英文（大写和小写的英文） */
    public static final String REGEX_ENGLISH = "[^a-zA-Z]";
    /** 计数（非 0 开头的数字） */
    public static final String REGEX_COUNT = "[1-9]\\d*";
    /** 用户名（中文、英文、数字） */
    public static final String REGEX_NAME = "[^a-zA-Z0-9\u4E00-\u9FA5]";
    /** 非空格的字符（不能输入空格） */
    public static final String REGEX_NONNULL = "\\S+";

    /**
     * 默认的筛选条件(正则:只能输入中文)
     */
    public static String DEFAULT_REGEX = "[^\u4E00-\u9FA5]";
    /**
     * et
     */
    private EditText et = null;
    /**
     * 筛选条件
     */
    private String regex;



    /**
     * 构造方法
     *
     * @param et
     */
    public LimitInputTextWatcher(EditText et) {
        this.et = et;
        this.regex = DEFAULT_REGEX;
    }

    /**
     * 构造方法
     *
     * @param et    et
     * @param regex 筛选条件
     */
    public LimitInputTextWatcher(EditText et, String regex) {
        this.et = et;
        this.regex = regex;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String str = editable.toString();
        String inputStr = clearLimitStr(regex, str);
        et.removeTextChangedListener(this);
        // et.setText方法可能会引起键盘变化,所以用editable.replace来显示内容
        editable.replace(0, editable.length(), inputStr.trim());
        et.addTextChangedListener(this);

    }

    /**
     * 清除不符合条件的内容
     *
     * @param regex
     * @return
     */
    private String clearLimitStr(String regex, String str) {
        return str.replaceAll(regex, "");
    }

}
