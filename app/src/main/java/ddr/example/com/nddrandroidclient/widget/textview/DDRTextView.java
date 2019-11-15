package ddr.example.com.nddrandroidclient.widget.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Map;

import androidx.annotation.Nullable;

/**
 * time  : 2019/11/12
 * desc :  输入值自动判断内容
 */
@SuppressLint("AppCompatCustomView")
public class DDRTextView extends TextView {
    private Map<Integer,String> map;
    public DDRTextView(Context context) {
        super(context);
    }

    public DDRTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        map=new ArrayMap<>();
        /*** 点动作类型*/
        map.put(1,"必到点");
        map.put(2,"投影点");
        map.put(3,"闸机点");
        map.put(4,"电梯点");
        map.put(5,"充点电");
        map.put(6,"QR点");
        map.put(7,"旋转点");
        map.put(8,"默认点");
        /**路径的模式**/
        map.put(64,"动态避障");
        map.put(65,"静态避障");
        map.put(66,"延边模式");
    }


    /**
     * 通过判断设置type值，自动设置文本
     * @param value
     */
    public void setValueText(int value){
        this.setText(map.get(value));
       /* switch (value){
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
        }*/
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    /**
     * 返回设置type值
     * @return
     */
    public int getTextVaule(){
       return getKey(this.getText().toString());
    }


    /**
     * 通过值找到对应的键
     * @param v
     * @return
     */
    public int getKey(String v){
        int key=0;
        for (Map.Entry<Integer,String> m :map.entrySet()){
            if (m.getValue().equals(v)){
                key=m.getKey();
            }
        }
        return key;
    }

    /**
     * 给UI层提供数据源接口
     * @return
     */
    public Map<Integer, String> getMap() {
        return map;
    }
}
