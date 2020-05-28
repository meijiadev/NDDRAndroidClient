package ddr.example.com.nddrandroidclient.ui.adapter;

import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    //public GridImageView gridImageView;
   // public NumEdit numEdit;
    public TextView tv_task_status;
    public TextView tv_task_time;
    public TextView tvPause;
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
                String taskName=item.getName();
                taskName=taskName.replaceAll("DDRTask_","");
                taskName=taskName.replaceAll(".task","");
                if (item.isSelected()){
                    helper.setText(R.id.tv_target_name,taskName).setTextColor(R.id.tv_target_name,Color.parseColor("#0399ff"));
                }else {
                    helper.setText(R.id.tv_target_name,taskName)
                            .setTextColor(R.id.tv_target_name,Color.parseColor("#ffffff"));
                }
                break;
            case R.layout.item_recycle_tasklist:
                 tv_task_status=helper.getView(R.id.tv_task_status);
                 tv_task_time=helper.getView(R.id.tv_task_time);
                 tvPause=helper.getView(R.id.tv_task_pause);
                 Logger.e("-------TaskState:"+item.getTaskState());
               switch (item.getTaskState()){
                   case 0:
                   case 4:
                       tv_task_status.setText("已终止");
                       tvPause.setText("恢复");
                       tvPause.setTextColor(Color.WHITE);
                       break;
                   case 1:
                       tv_task_status.setText("等待执行");
                       tvPause.setText("停止");
                       tvPause.setTextColor(Color.WHITE);
                       break;
                   case 2:
                       tv_task_status.setText("正在执行");
                       tvPause.setText("暂停");
                       tvPause.setTextColor(Color.WHITE);
                       break;
                   case 3:
                       tv_task_status.setText("执行完毕");
                       tvPause.setText("恢复");
                       tvPause.setTextColor(Color.parseColor("#99ffffff"));
                       break;
                   case 5:
                       tv_task_status.setText("挂起");
                       tvPause.setText("恢复");
                       tvPause.setTextColor(Color.WHITE);
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
                String taskName1=item.getName();
                taskName1=taskName1.replaceAll("DDRTask_","");
                taskName1=taskName1.replaceAll(".task","");
                helper.setText(R.id.tv_map_list,taskName1)
                        .addOnClickListener(R.id.layout_time,R.id.tv_task_pause,R.id.tv_task_stop)
                        .setText(R.id.tv_task_time,starth+":"+startm+"-"+endh+":"+endm);
                break;
        }
    }


    @Nullable
    @Override
    public TaskMode getItem(int position) {
        return super.getItem(position);
    }
}
