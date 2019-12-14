package ddr.example.com.nddrandroidclient.widget.edit;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hjq.toast.ToastUtils;

import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time: 2019/11/9
 * desc: 左右带加减号的EditText
 */
public class DDREditText extends LinearLayout {
    public EditText et_content;
    private ImageView iv_add;
    private ImageView iv_reduce;
    private Context context;
    private int ET_SPEED=3;



    public DDREditText(Context context) {
        super(context);
    }

    public DDREditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.ddr_edit_text,this);
        init_widget();
        addListener();
    }

    public void init_widget(){
        et_content=findViewById(R.id.et_content);
        iv_add=findViewById(R.id.iv_add);
        iv_reduce=findViewById(R.id.iv_reduce);

    }

    public void addListener(){
        iv_add.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Logger.e("-------点击");
                try {
                    Logger.e("--------:"+et_content.getText().toString());
                    float num=Float.valueOf(et_content.getText().toString());

                    if (viewType1==3){
                        num=num+0.1f;
                    }else {
                        num++;
                    }
                    if (viewType==0){
                        String n=String.format("%.2f",num);
                        et_content.setText(n);
                    }else {
                        int num1= (int) num;
                        et_content.setText(String.valueOf(num1));
                    }
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
                return false;
            }
        });


        iv_reduce.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Logger.e("-------点击");
                try {
                    Logger.e("--------:"+et_content.getText().toString());
                    float num=Float.valueOf(et_content.getText().toString());
                    if (viewType1==3){
                        num=num-0.1f;
                    }else {
                        num--;
                    }
                    if (viewType==0){
                        String n=String.format("%.2f",num);
                        et_content.setText(n);
                    }else {
                        int num1= (int) num;
                        et_content.setText(String.valueOf(num1));
                    }
                }catch (NumberFormatException e){
                    e.printStackTrace();
                }
                return false;
            }
        });
    }


    /**
     * 获取当前EditText的内容
     * @return
     */
    public String getText(){
        return et_content.getText().toString();
    }

    /**
     * 返回float格式的内容
     * @return
     */
    public float getFloatText(){
        try {
            return Float.valueOf(et_content.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.show("数据格式不对，请重输！");
            return 0;
        }
    }

    /**
     * 返回Integer格式内容
     * @return
     */
    public int getIntegerText(){
        try {
            float a=Float.valueOf(et_content.getText().toString());
            int b= (int) a;
            return b;
        }catch (Exception e){
            e.printStackTrace();
            ToastUtils.show("数据格式不对，请重输！");
            return 0;
        }

    }



    /**
     * 设置EditText float类型的值
     * @param text
     */
    public void setText(float text){
        try {
            String text1=String.format("%.2f",text);
            et_content.setText(text1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setText(int text){
        try {
            et_content.setText(String.valueOf(text));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 设置EditText String类型的值
     * @param text
     */
    public void setText(String text){
        et_content.setText(text);
    }

    private int viewType;
    public void  setViewType(int type){
        viewType=type;
    }
    private int viewType1;
    public void setEt_content(int viewType){
        this.viewType1=viewType;
    }
}
