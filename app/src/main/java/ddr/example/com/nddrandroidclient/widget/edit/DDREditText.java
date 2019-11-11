package ddr.example.com.nddrandroidclient.widget.edit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time: 2019/11/9
 * desc: 左右带加减号的EditText
 */
public class DDREditText extends LinearLayout {
    private EditText et_content;
    private ImageView iv_add;
    private ImageView iv_reduce;


    public DDREditText(Context context) {
        super(context);
    }

    public DDREditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
        iv_add.setOnClickListener(v -> {
            try {
                Logger.e("--------:"+et_content.getText().toString());
                float num=Float.valueOf(et_content.getText().toString());
                num++;
                et_content.setText(Float.toString(num));
                Logger.e("-------"+Float.toString(num));
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        });

        iv_reduce.setOnClickListener((v ->{
            try {
                float num=Float.valueOf(et_content.getText().toString());
                num--;
                et_content.setText(Float.toString(num));
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }));
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
    public float getTextFloat(){
        try {
            return Float.valueOf(et_content.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }
}
