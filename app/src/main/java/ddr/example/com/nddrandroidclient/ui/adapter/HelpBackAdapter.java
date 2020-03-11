package ddr.example.com.nddrandroidclient.ui.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.other.HelpAndBack;
import ddr.example.com.nddrandroidclient.other.Logger;

public class HelpBackAdapter extends BaseAdapter<HelpAndBack> {
    public TextView tv_question;
    public TextView tv_answer;
    public ImageView iv_help_xia;
    public HelpBackAdapter(int layoutResId) {
        super(layoutResId);
    }

    public HelpBackAdapter(int layoutResId, @Nullable List<HelpAndBack> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<HelpAndBack> data) {
        super.setNewData(data);
        for (int i=0;i<data.size();i++){
            Logger.e("------"+data.get(i).getAnswer());
        }
    }

    @Override
    public void addData(int position, @NonNull HelpAndBack data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull HelpAndBack data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HelpAndBack item) {
        super.convert(helper, item);
                tv_answer=helper.getView(R.id.tv_answer);
                tv_question=helper.getView(R.id.tv_question);
                iv_help_xia=helper.getView(R.id.iv_help_xia);
                helper.setText(R.id.tv_question,item.getQuestion())
                        .addOnClickListener(R.id.tv_answer,R.id.tv_question,R.id.iv_help_xia)
                        .setText(R.id.tv_answer,item.getAnswer());
    }

    @Nullable
    @Override
    public HelpAndBack getItem(int position) {
        return super.getItem(position);
    }
}
