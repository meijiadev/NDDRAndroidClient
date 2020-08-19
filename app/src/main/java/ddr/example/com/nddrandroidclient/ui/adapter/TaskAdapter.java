package ddr.example.com.nddrandroidclient.ui.adapter;

import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseAdapter;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
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
    private NotifyBaseStatusEx notifyBaseStatusEx;
    public TaskAdapter(int layoutResId) {
        super(layoutResId);
    }

    public TaskAdapter(int layoutResId, @Nullable List<TaskMode> data) {
        super(layoutResId, data);
    }

    @Override
    public void setNewData(@Nullable List<TaskMode> data) {
        checkTask(data);
        super.setNewData(data);
    }
    private void checkTask(List<TaskMode> taskModes){
        for (int i=0;i<taskModes.size();i++){
            if (taskModes.get(i).getName().equals("AB_Task.task")){
                taskModes.remove(i);
            }
        }
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
                notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
                tv_task_status=helper.getView(R.id.tv_task_status);
                tv_task_time=helper.getView(R.id.tv_task_time);
                tvPause=helper.getView(R.id.tv_task_pause);
                //Logger.e("-------TaskState:"+item.getTaskState());
                switch (item.getTaskState()){
                    case 0:
                    case 4:
                        tv_task_status.setText(R.string.common_terminated);
                        tvPause.setText(R.string.common_recover);
                        tvPause.setTextColor(Color.WHITE);
                        break;
                    case 1:
                        tv_task_status.setText(R.string.common_wait_execute);
                        tvPause.setText(R.string.common_stop);
                        tvPause.setTextColor(Color.WHITE);
                        break;
                    case 2:
                        tv_task_status.setText(R.string.common_executing);
                        tvPause.setText(R.string.common_pause);
                        tvPause.setTextColor(Color.WHITE);
                        break;
                    case 3:
                        tv_task_status.setText(R.string.common_executed);
                        tvPause.setText(R.string.common_recover);
                        tvPause.setTextColor(Color.parseColor("#99ffffff"));
                        break;
                    case 5:
                        tv_task_status.setText(R.string.common_put_up);
                        tvPause.setText(R.string.common_recover);
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

                switch (notifyBaseStatusEx.geteTaskMode()){
                    case 1:
                        break;
                    case 2:
                        tvPause.setClickable(true);
                        tvPause.setTextColor(Color.parseColor("#eaf1f5"));
                        break;
                    case 3:
                        break;
                    case 4:
                        tvPause.setClickable(false);
                        tvPause.setTextColor(Color.parseColor("#99ffffff"));
                        break;
                    case 5:
                        tvPause.setTextColor(Color.parseColor("#99ffffff"));
                        tvPause.setClickable(false);
                        break;
                }
                break;
        }
    }
    /**
     * 将某个item直接隐藏
     * @param isVisible
     * @param helper
     */
    public void setVisibility(boolean isVisible,BaseViewHolder helper){
        RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)helper.getConvertView().getLayoutParams();
        if (isVisible){
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            helper.getConvertView().setVisibility(View.VISIBLE);
        }else{
            helper.getConvertView().setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        helper.getConvertView().setLayoutParams(param);
    }



    @Nullable
    @Override
    public TaskMode getItem(int position) {
        return super.getItem(position);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateBaseStatus:
                break;
        }
    }
}
