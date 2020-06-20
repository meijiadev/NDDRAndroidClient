package ddr.example.com.nddrandroidclient.ui.adapter;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.other.Naparam;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.other.SlideButton;

public class NaparamAdapter extends BaseAdapter<Naparam> implements SlideButton.SlideButtonOnCheckedListener{
    private TextView tv_text;
    private TextView tv_title;
    private EditText ed_value;
    private TextView tv_cm;
    private SlideButton slideButton;
    private SlideButton.SlideButtonOnCheckedListener mListener;

    public NaparamAdapter(int layoutResId) {
        super(layoutResId);
    }

    public NaparamAdapter(int layoutResId, @Nullable List<Naparam> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<Naparam> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull Naparam data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull Naparam data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Naparam item) {
        super.convert(helper, item);
        tv_text=helper.getView(R.id.tv_naparam_txt);
        tv_title=helper.getView(R.id.tv_naparam_titer);
        ed_value=helper.getView(R.id.ed_bzRadius);
        tv_cm=helper.getView(R.id.tv_cm);
        slideButton=helper.getView(R.id.slide_is);
        slideButton.setSmallCircleModel(
                Color.parseColor("#00FFFFFF"), Color.parseColor("#999999"),Color.parseColor("#49c265"),
                Color.parseColor("#ffffff"), Color.parseColor("#ffffff"));
        slideButton.setOnCheckedListener(isChecked -> getChosseStatus());
        helper.setText(R.id.tv_naparam_txt,item.getText())
                .setText(R.id.tv_naparam_titer,item.getTitle())
                .addOnClickListener(R.id.tv_naparam_txt,R.id.slide_is);
        editListener(item);
        isHide(item);
        setChosseStatus(item);
    }

    @Nullable
    @Override
    public Naparam getItem(int position) {
        return super.getItem(position);
    }

    public void editListener(Naparam item){
        if (ed_value.getTag() instanceof TextWatcher){
            ed_value.removeTextChangedListener((TextWatcher) ed_value.getTag());
        }
        ed_value.setText(item.getValue());
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
                    item.setValue(editable.toString());
                }
            }
        };
        ed_value.addTextChangedListener(watcher);
        ed_value.setTag(watcher);
    }
    private void isHide(Naparam item){
        if (item.getText().contains("智能导航") || item.getText().contains("智能曲线路径")){
            Logger.e(item.getTitle());
            tv_cm.setVisibility(View.GONE);
            ed_value.setVisibility(View.GONE);
            slideButton.setVisibility(View.VISIBLE);
        }else {
            tv_cm.setVisibility(View.VISIBLE);
            ed_value.setVisibility(View.VISIBLE);
            slideButton.setVisibility(View.GONE);
        }
    }

    private void setChosseStatus(Naparam item) {
        if (item.getText().contains("智能导航")) {
            if (item.getValue().equals("0")) {
                slideButton.setChecked(false);
            } else {
                slideButton.setChecked(true);
            }
        }
        if (item.getText().contains("智能曲线路径")) {
            if (item.getValue().equals("0")) {
                slideButton.setChecked(false);
            } else {
                slideButton.setChecked(true);
            }
        }
    }

    @Override
    public void onCheckedChangeListener(boolean isChecked) {

    }
    public boolean isChecked;
    private void getChosseStatus(){
        isChecked=slideButton.isChecked();
        Logger.e("状态"+isChecked);
       if (isChecked){
           slideButton.setChecked(true);
       }else {
           slideButton.setChecked(false);
       }
    }
}
