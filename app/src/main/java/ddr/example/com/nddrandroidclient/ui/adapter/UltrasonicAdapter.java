package ddr.example.com.nddrandroidclient.ui.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.other.Ultrasonic;
import ddr.example.com.nddrandroidclient.other.Logger;

public class UltrasonicAdapter extends BaseAdapter<Ultrasonic> {
    private TextView textNum;
    private EditText textContant;

    public UltrasonicAdapter(int layoutResId) {
        super(layoutResId);
    }

    public UltrasonicAdapter(int layoutResId, @Nullable List<Ultrasonic> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<Ultrasonic> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull Ultrasonic data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull Ultrasonic data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Ultrasonic item) {
        super.convert(helper, item);
        textNum=helper.getView(R.id.tv_test_num);
        textContant=helper.getView(R.id.ed_cs_content);
        helper.setText(R.id.tv_text_num,item.getTextNum())
                .setText(R.id.ed_cs_content,item.getTextContant());
        editListener(item);
    }

    @Nullable
    @Override
    public Ultrasonic getItem(int position) {
        return super.getItem(position);
    }

    public void editListener(Ultrasonic item){
        if (textContant.getTag() instanceof TextWatcher){
            textContant.removeTextChangedListener((TextWatcher) textContant.getTag());
        }
        textContant.setText(item.getTextContant());
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable)) {
                    Logger.e("数值"+editable.toString());
                    item.setTextContant(editable.toString());
                }
            }
        };
        textContant.addTextChangedListener(watcher);
        textContant.setTag(watcher);
    }
}
