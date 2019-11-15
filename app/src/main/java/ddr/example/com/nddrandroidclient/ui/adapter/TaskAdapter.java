package ddr.example.com.nddrandroidclient.ui.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.widget.textview.GridTextView;
import ddr.example.com.nddrandroidclient.widget.view.NumEdit;

/**
 * time : 2019/11/12
 * desc : 任务列表适配器
 */
public class TaskAdapter extends BaseAdapter<TaskMode> {
    public TaskAdapter(int layoutResId) {
        super(layoutResId);
    }

    public TaskAdapter(int layoutResId, @Nullable List<TaskMode> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<TaskMode> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TaskMode item) {
        super.convert(helper, item);
        switch (viewType){
            case R.layout.item_target_point:
                helper.setText(R.id.tv_target_name,item.getName());
                break;
            case R.layout.item_recycle_tasklist:
                GridTextView gridTextView=helper.getView(R.id.iv_check);
                NumEdit numEdit=helper.getView(R.id.task_num_check);
                TextView tv_task_status=helper.getView(R.id.tv_task_status);
                TextView tv_task_time=helper.getView(R.id.tv_task_time);
               switch (item.getType()){
                   case 0:
                       gridTextView.setBackground(R.mipmap.nocheckedwg);
                       break;
                   case 2:
                       gridTextView.setBackground(R.mipmap.checkedwg);
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

               numEdit.setNum(item.getRunCounts());
                helper.setText(R.id.tv_map_list,item.getName())
                        .addOnClickListener(R.id.tv_task_time)
                        .setText(R.id.tv_task_time,starth+":"+startm+"-"+endh+":"+endm);
                break;
        }

    }

}
