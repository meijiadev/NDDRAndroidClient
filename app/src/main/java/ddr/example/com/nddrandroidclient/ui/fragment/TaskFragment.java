package ddr.example.com.nddrandroidclient.ui.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.helper.ListTool;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.activity.NewTaskActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.TaskAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.TimeDialog;
import ddr.example.com.nddrandroidclient.widget.edit.DDREditText;
import ddr.example.com.nddrandroidclient.widget.textview.GridImageView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.PickValueView;
import me.jessyan.autosize.utils.ScreenUtils;

/**
 * time：2019/10/28
 * desc：任务管理界面
 * author: ----
 */
public class TaskFragment extends DDRLazyFragment<HomeActivity>  {

    @BindView(R.id.recycle_task_list)
    RecyclerView recycle_task_list;
    @BindView(R.id.tv_task_save)
    TextView tv_task_save;
    @BindView(R.id.tv_create_task)
    TextView tvCreateTask;

    private CustomPopuWindow customPopuWindow;
    private PickValueView pickValueViewNum;
    private  TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private MapFileStatus mapFileStatus;
    private TaskAdapter taskAdapter;
    private List<TaskMode> taskModeList =new ArrayList<>();

    public final static int CREATE_NEW_TASK=0;      //创建新的任务
    public final static int REVAMP_TASK=1;          //修改任务
    public boolean isRevamp;                        //是否修改需要保存


    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateDDRVLNMap:
//                Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
                try {
                    taskModeList=ListTool.deepCopy(mapFileStatus.getcTaskModes());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                taskAdapter.setNewData(taskModeList);
                if (isRevamp){
                    toast(R.string.task_revamp_succeed);
                    isRevamp=false;
                }
                break;
        }
    }

    public static TaskFragment newInstance(){
        return new TaskFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task;
    }

    @Override
    protected void initView() {
        taskAdapter=new TaskAdapter(R.layout.item_recycle_tasklist);
        NLinearLayoutManager layoutManager=new NLinearLayoutManager(getAttachActivity());
        recycle_task_list.setLayoutManager(layoutManager);
        recycle_task_list.setAdapter(taskAdapter);



    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
        try {
            taskModeList=ListTool.deepCopy(mapFileStatus.getcTaskModes());
            taskAdapter.setNewData(taskModeList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        onItemClick(1);
    }


    //TextView tv_task_time;
    private int mPosition;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onItemClick(int type){
        switch (type){
            case 1:
                Logger.e("task列表"+taskModeList.size());
                // Java 8 新特性 Lambda表达式，原来写法即下方注释
                taskAdapter.setOnItemChildClickListener((adapter, view, position) ->  {
                    mPosition=position;
                    Logger.e("task列表对应"+taskModeList.get(position).getName());
                            switch (view.getId()){
                                case R.id.layout_time:
                                    selectStartTime(taskModeList.get(position).getStartHour(),taskModeList.get(position).getStartMin());
                                    break;
                                    //已修改成“修改”任务
                                case R.id.tv_task_pause:
                                    handleTask(position);
                                    submissionTask();
                                    taskAdapter.setNewData(taskModeList);
                                    break;
                                    // 已修改成“删除”任务
                                case R.id.tv_task_stop:
                                    if (taskModeList.get(position).getTaskState()==2){
                                        toast(R.string.in_runing_task);
                                    }else {
                                        new InputDialog.Builder(getAttachActivity())
                                                .setEditVisibility(View.GONE)
                                                .setTitle(R.string.is_delete_task)
                                                .setListener(new InputDialog.OnListener() {
                                                    @Override
                                                    public void onConfirm(BaseDialog dialog, String content) {
                                                        taskModeList.remove(position);
                                                        tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModeList);
                                                    }
                                                    @Override
                                                    public void onCancel(BaseDialog dialog) {

                                                    }
                                                }).show();

                                    }
                                    break;
                            }

                });
                break;
        }
        taskAdapter.setNewData(taskModeList);
    }

    // 临时设置的时间，在没有点击确定前不会设置到参数列表中
    private int temporaryHour,temporaryMinute;
    /**
     * 选择开始时间
     */
    private void selectStartTime(int hour,int minute){
        new TimeDialog.Builder(getAttachActivity())
                .setTitle(getString(R.string.time_start_title))
                .setCancel(getString(R.string.common_cancel))
                .setConfirm(getString(R.string.time_next))
                .setHour(hour)
                .setMinute(minute)
                .setIgnoreSecond()
                .setListener(new TimeDialog.OnListener() {
                    @Override
                    public void onSelected(BaseDialog dialog, int hour, int minute, int second) {
                        temporaryHour=hour;
                        temporaryMinute=minute;
                        selectEndTime();
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast(R.string.cancel_setting_time);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 选择结束时间
     */
    private void selectEndTime(){
        new TimeDialog.Builder(getAttachActivity())
                .setTitle(getString(R.string.time_end_time))
                .setCancel(getString(R.string.time_last))
                .setConfirm(getString(R.string.common_confirm))
                .setHour(taskModeList.get(mPosition).getEndHour())
                .setMinute(taskModeList.get(mPosition).getEndMin())
                .setIgnoreSecond()
                .setListener(new TimeDialog.OnListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSelected(BaseDialog dialog, int hour, int minute, int second) {
                        // 确定
                        if (hour>temporaryHour){
                            taskModeList.get(mPosition).setStartHour(temporaryHour);
                            taskModeList.get(mPosition).setStartMin(temporaryMinute);
                            taskModeList.get(mPosition).setEndHour(hour);
                            taskModeList.get(mPosition).setEndMin(minute);
                            taskModeList.get(mPosition).setType(2);
                            taskModeList.get(mPosition).setTaskState(1);
                            dialog.dismiss();
                            taskAdapter.setNewData(taskModeList);
                            submissionTask();
                        }else if (hour==temporaryHour){
                            if (minute>temporaryMinute){
                                taskModeList.get(mPosition).setStartHour(temporaryHour);
                                taskModeList.get(mPosition).setStartMin(temporaryMinute);
                                taskModeList.get(mPosition).setEndHour(hour);
                                taskModeList.get(mPosition).setEndMin(minute);
                                taskModeList.get(mPosition).setType(2);
                                taskModeList.get(mPosition).setTaskState(1);
                                dialog.dismiss();
                                taskAdapter.setNewData(taskModeList);
                                submissionTask();
                            }else {
                                toast(R.string.setting_time);
                            }
                        }else {
                            toast(R.string.setting_time);
                        }
                    }
                    @Override
                    public void onCancel(BaseDialog dialog) {
                        //返回上一步
                        selectStartTime(temporaryHour,temporaryMinute);
                        dialog.dismiss();
                    }
                })
                .show();

    }

    /**
     * 处理操作下面的点击事件，根据当前任务不同的状态发送不同的命令
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleTask(int position){
        int taskState=taskModeList.get(position).getTaskState();
        switch (taskState){
            case 0:
            case 4:
                taskModeList.get(position).setTaskState(1);       //  从终止状态改为等待执行
                taskModeList.get(position).setType(2);            // 从不在队列中改为在队列中
                break;
            case 1:
                taskModeList.get(position).setTaskState(4);       //  终止
                taskModeList.get(position).setType(0);            // 不在队列中
                break;
            case 2:
                tcpClient.exitModel();
                taskModeList.get(position).setTaskState(5);  //改为挂起状态
                taskModeList.get(position).setType(2);       //在队列中
                break;
            case 3:
                toast(R.string.not_in_time);
                break;
            case 5:
                taskModeList.get(position).setTaskState(2);  //从挂起状态改为运行
                taskModeList.get(position).setType(2);       //在队列中
                break;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick({R.id.tv_task_save,R.id.tv_create_task})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_task_save:
                for (int i=0;i<taskModeList.size();i++){
                    Logger.e("队列"+taskModeList.get(i).getType());
                }
                isRevamp=true;
                submissionTask();
//                new InputDialog.Builder(getAttachActivity()).setTitle(R.string.submit_task)
//                        .setEditVisibility(View.GONE)
//                        .setListener(new InputDialog.OnListener() {
//                            @Override
//                            public void onConfirm(BaseDialog dialog, String content) {
//                                submissionTask();
//                                toast(R.string.save_succeed);
//                        }
//                            @Override
//                            public void onCancel(BaseDialog dialog) {
//                                toast(R.string.cancel_sunmit_task);
//                            }
//                        }).show();
                break;
            case R.id.tv_create_task:
                showCreateTaskDialog();
                break;

        }
    }

    private BaseDialog inputDialog;
    /**
     * 新建任务
     */
    private void showCreateTaskDialog(){
       inputDialog= new InputDialog.Builder(getAttachActivity())
                .setTitle(R.string.enter_task_name)
                .setAutoDismiss(false)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        if (!content.equals("")){
                            String name="DDRTask_" +content+ ".task";
                            if (checkTaskName(name)){
                                toast(R.string.name_is_exist);
                            }else {
                                Intent intent=new Intent(getAttachActivity(),NewTaskActivity.class);
                                intent.putExtra("viewType",CREATE_NEW_TASK);
                                intent.putExtra("taskName",content);
                                startActivity(intent);
                                inputDialog.dismiss();
                            }
                        }
                    }
                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast(R.string.cancel_create_task);
                        inputDialog.dismiss();
                    }
                }).show();
    }

    /**
     * 防止任务重名
     * @return true 表示任务重名
     */
    private boolean checkTaskName(String taskName){
        for (TaskMode taskMode:taskModeList){
            if (taskMode.getName().equals(taskName)){
                Logger.e("------"+taskName+";"+taskMode.getName());
                return true;
            }
        }
        return false;
    }

    /**
     * 上传任务列表
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void submissionTask(){
        // 先按照执行顺序排列 再按照结束时间升序排列
        taskModeList.sort(Comparator.comparing(TaskMode::getType).reversed().thenComparing(TaskMode::getEndHour).thenComparing(TaskMode::getEndMin));
        taskAdapter.setNewData(taskModeList);
        int j=0;
        boolean isCheckTime;
        if (taskModeList.size()>1){
            for (int i=0;i<taskModeList.size();i++){
                Logger.e("列表排序后"+taskModeList.get(i).getName());
                if (taskModeList.get(i).getType()==2){
                    j++;
                }
            }
            Logger.e("选中列数"+j);
            if (j>1){
                for (int i=0;i<j-1;i++){
                    boolean isTrueTime;
                    if (taskModeList.get(i+1).getStartHour()>taskModeList.get(i).getEndHour()){
                        isTrueTime=true;
                    }else if (taskModeList.get(i+1).getStartHour()==taskModeList.get(i).getEndHour() &&
                            taskModeList.get(i+1).getStartMin()>taskModeList.get(i).getEndMin()){
                        isTrueTime=true;
                    }else {
                        isTrueTime=false;
                    }
                    if (isTrueTime){
                        isCheckTime=true;
                    }else {
                        Logger.e("前"+taskModeList.get(i).getEndHour()+"后"+taskModeList.get(i+1).getStartHour());
                        isCheckTime=false;
                        //toast("定时列表第"+(i+2)+"行时间设置有误");
                    }
                    if (isCheckTime){
                        tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModeList);
                    }

                }
            }else {
                tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModeList);
            }

        }else if (taskModeList.size()==1){
            tcpClient.saveTaskData(mapFileStatus.getCurrentMapEx(),taskModeList);
        }else {
            toast(R.string.no_timing_task);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("------onRestart");
        Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
        try {
            taskModeList=ListTool.deepCopy(mapFileStatus.getcTaskModes());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        taskAdapter.setNewData(taskModeList);
        submissionTask();
    }




}
