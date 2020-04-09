package ddr.example.com.nddrandroidclient.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;

/**
 * time : 2019/11/2
 * desc : String类型的列表适配器
 */
public class StringAdapter extends BaseAdapter<String> {
    private Context context;
    public StringAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setContext(Context context){
        this.context=context;
    }
    public StringAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<String> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        super.convert(helper, item);
        switch (viewType){
            case R.layout.item_recycle_task_check:
                String showName=item.replaceAll("DDRTask_","");
                showName=showName.replaceAll(".task","");
                helper.setText(R.id.item_recycle_t_chenck,showName);
                break;
            case R.layout.item_recycle_robot_id:
                helper.setText(R.id.tv_robot_id,item);
                break;
            case R.layout.item_path_mode:
                helper.setText(R.id.mode_name,item);
                break;
            case R.layout.item_show_recycler:
                helper.setText(R.id.tv_show_name,item);
                helper.setImageResource(R.id.iv_select,R.mipmap.checkedwg);
                break;
            case R.layout.item_edit_type_recycler:
                TextView textView=helper.getView(R.id.item_name);
                textView.setText(item);
                switch (item){
                    case "虚拟墙":
                        textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.virtual_wall_blue),null,null,null);
                        break;
                    case "原图去噪":
                        textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.iv_denoising_blue),null,null,null);
                        break;
                    case "直线":
                        textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.mipmap.iv_line_blue),null,null,null);
                        break;

                }
                break;
        }

    }




}
