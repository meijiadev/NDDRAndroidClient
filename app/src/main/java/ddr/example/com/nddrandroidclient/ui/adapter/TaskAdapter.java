package ddr.example.com.nddrandroidclient.ui.adapter;

import android.graphics.Color;
import android.text.Editable;
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
    public GridImageView gridImageView;
    public NumEdit numEdit;
    public DDREditText ddrEditText;
    public EditText et_content;
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
                 gridImageView=helper.getView(R.id.iv_check);
                 ddrEditText=helper.getView(R.id.task_num_check);
                 ddrEditText.setViewType(1);
                 et_content=ddrEditText.et_content;
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
//                ddrEditText.setText(item.getRunCounts());
                String taskName1=item.getName();
                taskName1=taskName1.replaceAll("DDRTask_","");
                taskName1=taskName1.replaceAll(".task","");
                helper.setText(R.id.tv_map_list,taskName1)
                        .addOnClickListener(R.id.tv_task_time,R.id.iv_check,R.id.tv_task_pause,R.id.tv_task_stop,R.id.task_num_check)
                        .setText(R.id.tv_task_time,starth+":"+startm+"-"+endh+":"+endm);
                editListen(item);
                break;
        }
    }
    public void editListen(TaskMode item){
        //        通过tag判断当前editText是否已经设置监听，有监听的话，移除监听再给editText赋值
        if (et_content.getTag() instanceof TextWatcher){
            et_content.removeTextChangedListener((TextWatcher) et_content.getTag());
        }
//        必须在判断tag后给editText赋值，否则会数据错乱
        et_content.setText(item.getRunCounts()+"");
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
                    try {
                        int count=Integer.valueOf(editable.toString());
                        Logger.e("-----------"+count);
                        if (count>999 ||count<=0 || editable.equals("") || editable.length()==0 ){
                            Logger.e("数值"+Integer.valueOf(editable.toString()));
                        }else {
                            item.setRunCounts(Integer.valueOf(editable.toString()));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
//        给item中的editText设置监听
        et_content.addTextChangedListener(watcher);
//        给editText设置tag，以便于判断当前editText是否已经设置监听
        et_content.setTag(watcher);

    }


    @Nullable
    @Override
    public TaskMode getItem(int position) {
        Logger.e("---------:"+position);
        return super.getItem(position);
    }
}
