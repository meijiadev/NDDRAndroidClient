package ddr.example.com.nddrandroidclient.widget.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * time  : 2019/11/12
 * desc :  输入值自动判断内容
 */
@SuppressLint("AppCompatCustomView")
public class DDRTextView extends TextView {
    public DDRTextView(Context context) {
        super(context);
    }

    public DDRTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setValueText(int value){
        switch (value){
            case 1:
                this.setText("必到点");
                break;
            case 2:
                this.setText("投影点");
                break;
            case 3:
                this.setText("闸机点");
                break;
            case 4:
                this.setText("电梯点");
                break;
            case 5:
                this.setText("充点电");
                break;
            case 6:
                this.setText("QR点");
                break;
            case 7:
                this.setText("旋转点");
                break;
            case 8:
                this.setText("默认点");      // 普通路径点
                break;
            case 64:
                this.setText("动态避障");
                break;
            case 65:
                this.setText("静态避障");
                break;
            case 66:
                this.setText("延边模式");
                break;
        }
    }
}
