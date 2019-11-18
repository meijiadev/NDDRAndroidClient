package ddr.example.com.nddrandroidclient.ui.adapter;

import android.util.Log;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.widget.edit.DDREditText;
import ddr.example.com.nddrandroidclient.widget.textview.GridImageView;
import ddr.example.com.nddrandroidclient.widget.view.NumEdit;

/**
 * time : 2019/11/12
 * desc : 任务列表适配器
 */
public class TaskAdapter extends BaseAdapter<TaskMode> {
    public GridImageView gridImageView;
    public NumEdit numEdit;
    public DDREditText ddrEditText;
    public TextView tv_task_status;
    public TextView tv_task_time;
    public TaskAdapter(int layoutResId) {
        super(layoutResId);
    }

    public TaskAdapter(int layoutResId, @Nullable List<TaskMode> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<TaskMode> data) {
        super.setNewData(data);
        Logger.e("------"+data.size());
    }

    /**
     *
     * @param index
     * @param data
     */
    @Override
    public void setData(int index, @NonNull TaskMode data) {
        super.setData(index, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TaskMode item) {
        super.convert(helper, item);
        switch (viewType){
            case R.layout.item_target_point:
                helper.setText(R.id.tv_target_name,item.getName());
                break;
            case R.layout.item_recycle_tasklist:
                 gridImageView=helper.getView(R.id.iv_check);
                 ddrEditText=helper.getView(R.id.task_num_check);
                 tv_task_status=helper.getView(R.id.tv_task_status);
                 tv_task_time=helper.getView(R.id.tv_task_time);
               switch (item.getType()){
                   case 0:
                       Logger.e("未在列表中");
                       gridImageView.setBackgroundResource(R.mipmap.intask_def);
                       gridImageView.setSelected(false);
                       break;
                   case 1:
                       Logger.e("临时列表中");
                       gridImageView.setBackgroundResource(R.mipmap.intask_def);
                       gridImageView.setSelected(false);
                       break;
                   case 2:
                       Logger.e("在列表中");
                       gridImageView.setBackgroundResource(R.mipmap.intask_check);
                       gridImageView.setSelected(true);
                       break;
               }
               switch (item.getTaskState()){
                   case 0:
                       break;
                   case 1:
                       tv_task_status.setText("等待运行");
                       break;
                   case 2:
                       tv_task_status.setText("运行中");
                       break;
                   case 3:
                       tv_task_status.setText("已终止");
                       break;
               }
                String starth=null;
                String startm=null;
                String endh=null;
                String endm=null;
               if (item.getStartHour()<10){
                   starth="0"+item.getStartHour();
               }else {
                   starth=""+item.getStartHour();
               }
                if (item.getStartMin()<10){
                    startm="0"+item.getStartMin();
                }else {
                    startm=""+item.getStartMin();
                }
                if (item.getEndHour()<10){
                    endh="0"+item.getEndHour();
                }else {
                    endh=""+item.getEndHour();
                }
                if (item.getEndMin()<10){
                    endm="0"+item.getEndMin();
                }else {
                    endm=""+item.getEndMin();
                }
                ddrEditText.setText(item.getRunCounts());
                helper.setText(R.id.tv_map_list,item.getName())
                        .addOnClickListener(R.id.tv_task_time,R.id.iv_check,R.id.tv_task_pause,R.id.tv_task_stop,R.id.task_num_check)
                        .setText(R.id.tv_task_time,starth+":"+startm+"-"+endh+":"+endm);
                break;
        }
    }

    @Nullable
    @Override
    public TaskMode getItem(int position) {
        Logger.e("---------:"+position);
        return super.getItem(position);
    }
}
