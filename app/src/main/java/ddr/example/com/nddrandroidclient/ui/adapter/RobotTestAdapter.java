package ddr.example.com.nddrandroidclient.ui.adapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.other.RobotTest;

public class RobotTestAdapter extends BaseAdapter<RobotTest> {
    private TextView tv_t_name;
    private TextView tv_t_result;
    private TextView tv_t_time;
    private TextView tv_t_rnum;
    public RobotTestAdapter(int layoutResId) {
        super(layoutResId);
    }

    public RobotTestAdapter(int layoutResId, @Nullable List<RobotTest> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RobotTest item) {
        super.convert(helper, item);
        tv_t_name=helper.getView(R.id.tv_test_name);
        tv_t_result=helper.getView(R.id.tv_test_result);
        tv_t_time=helper.getView(R.id.tv_test_time);
        tv_t_rnum=helper.getView(R.id.tv_test_num);
        helper.setText(R.id.tv_test_name,item.getName())
                .setText(R.id.tv_test_result,item.getResult())
                .setText(R.id.tv_test_time,item.getTime());
        switch (item.getRnum()){
            case 0:
                helper.setText(R.id.tv_test_num,"自检完成");
                tv_t_rnum.setBackgroundResource(R.drawable.status_button);
                break;
            case 1:
                helper.setText(R.id.tv_test_num,"自检完成");
                tv_t_rnum.setBackgroundResource(R.drawable.robot_test_bg);
                break;
        }
    }

    @Override
    public void setNewData(@Nullable List<RobotTest> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(int position, @NonNull RobotTest data) {
        super.addData(position, data);
    }

    @Override
    public void setData(int index, @NonNull RobotTest data) {
        super.setData(index, data);
    }

    @Nullable
    @Override
    public RobotTest getItem(int position) {
        return super.getItem(position);
    }
}
