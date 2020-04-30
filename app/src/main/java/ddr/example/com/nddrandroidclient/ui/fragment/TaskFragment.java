package ddr.example.com.nddrandroidclient.ui.fragment;

import android.content.Intent;
import android.os.Build;
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
import ddr.example.com.nddrandroidclient.ui.adapter.TaskAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.widget.edit.DDREditText;
import ddr.example.com.nddrandroidclient.widget.textview.GridImageView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.PickValueView;

/**
 * time：2019/10/28
 * desc：任务管理界面
 * author: ----
 */
public class TaskFragment extends DDRLazyFragment<HomeActivity> implements PickValueView.onSelectedChangeListener {

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


    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateDDRVLNMap:
//                Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
                taskModeList=mapFileStatus.getcTaskModes();
                taskAdapter.setNewData(taskModeList);
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
        LinearLayoutManager layoutManager=new LinearLayoutManager(getAttachActivity());
        recycle_task_list.setLayoutManager(layoutManager);
        recycle_task_list.setAdapter(taskAdapter);



    }
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
    TextView tv_task_time;
    TextView tv_task_pause;
    GridImageView gridImageView;
    DDREditText task_num_check;
    private int mPosition;
    public void onItemClick(int type){
        switch (type){
            case 1:
                Logger.e("task列表"+taskModeList.size());
                // Java 8 新特性 Lambda表达式，原来写法即下方注释
                taskAdapter.setOnItemChildClickListener((adapter, view, position) ->  {
                    mPosition=position;
                    Logger.e("task列表对应"+taskModeList.get(position).getName());
                            switch (view.getId()){
                                case R.id.tv_task_time:
                                    Logger.e("点击----");
                                    tv_task_time= (TextView) view;
                                    showTimePopupWindow(tv_task_time,1);
                                    break;
                                case R.id.iv_check:
                                    gridImageView= (GridImageView) view;
                                    Logger.e("gggg"+gridImageView.getSelected());
                                    if (!gridImageView.getSelected()){
                                        Logger.e("未在列表中");
                                        gridImageView.setSelected(true);
                                        gridImageView.setBackgroundResource(R.mipmap.intask_check);
                                        taskModeList.get(position).setType(2);
                                        taskModeList.get(position).setTaskState(1);
                                        toast("加入定时队列，记得点保存哦");
                                    }else {
                                        Logger.e("在列表中");
                                        toast("退出定时队列，记得点保存哦");
                                        gridImageView.setSelected(false);
                                        gridImageView.setBackgroundResource(R.mipmap.intask_def);
                                        taskModeList.get(position).setType(0);
                                        taskModeList.get(position).setTaskState(3);
                                    }
                                    break;
                                    //已修改成“修改”任务
                                case R.id.tv_task_pause:
                                    Intent intent=new Intent(getAttachActivity(),NewTaskActivity.class);
                                    intent.putExtra("viewType",REVAMP_TASK);
                                    intent.putExtra("taskMode",position);
                                    startActivity(intent);
                                    break;
                                    // 已修改成“删除”任务
                                case R.id.tv_task_stop:
                                    new InputDialog.Builder(getAttachActivity())
                                            .setEditVisibility(View.GONE)
                                            .setTitle("是否要删除该任务")
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
                                    break;
                            }

                });
                break;
        }
        taskAdapter.setNewData(taskModeList);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick({R.id.tv_task_save,R.id.tv_create_task})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_task_save:
                for (int i=0;i<taskModeList.size();i++){
                    Logger.e("队列"+taskModeList.get(i).getType());
                }
                new InputDialog.Builder(getAttachActivity()).setTitle("提交任务")
                        .setEditVisibility(View.GONE)
                        .setListener(new InputDialog.OnListener() {
                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                submissionTask();
                                toast("保存成功");
                        }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast("取消提交任务");
                            }
                        }).show();
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
                .setTitle("输入任务名")
                .setAutoDismiss(false)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        if (!content.equals("")){
                            String name="DDRTask_" +content+ ".task";
                            if (checkTaskName(name)){
                                toast("名称已存在，请重新命名。");
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
                        toast("取消新建任务");
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
     * 时间弹窗
     * @param view
     */
    private void showTimePopupWindow(View view,int type) {
        Integer value[] = new Integer[24];
        for (int i = 0; i < value.length; i++) {
            value[i] = i + 1;
        }
        Integer middle[] = new Integer[60];
        for (int i = 0; i < middle.length; i++) {
            middle[i] = i ;
        }
        Integer right[] = new Integer[60];
        for (int i = 0; i < right.length; i++) {
            right[i] = i;
        }
        Integer three[] = new Integer[24];
        for (int i = 0; i < three.length; i++) {
            three[i] = i;
        }
        View contentView = null;
        switch (type){
            case 1:
                contentView = getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.dialog_num_check, null);
                customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                        .setView(contentView)
                        .create()
                        .showAsDropDown(view, DpOrPxUtils.dip2px(getAttachActivity(), 0), 5);
                pickValueViewNum =contentView.findViewById(R.id.pickValueNum);
                pickValueViewNum.setOnSelectedChangeListener(this);
                pickValueViewNum.setValueData(three, (int)taskModeList.get(mPosition).getStartHour(), middle, (int)taskModeList.get(mPosition).getStartMin(),
                        right, (int)taskModeList.get(mPosition).getEndMin(),three,(int)taskModeList.get(mPosition).getEndHour());
                break;

        }

    }

    /**
     * 机器人暂停/重新运动
     * @param value
     *//*
    private void pauseOrResume(String value){
        BaseCmd.reqCmdPauseResume reqCmdPauseResume=BaseCmd.reqCmdPauseResume.newBuilder()
                .setError(value)
                .build();
        BaseCmd.CommonHeader commonHeader=BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader,reqCmdPauseResume);
        Logger.e("机器人暂停/重新运动");
    }
*/
   /* *//**
     * 退出当前模式
     *//*
    private void exitModel() {
        BaseCmd.reqCmdEndActionMode reqCmdEndActionMode = BaseCmd.reqCmdEndActionMode.newBuilder()
                .setError("noError")
                .build();
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        tcpClient.sendData(commonHeader, reqCmdEndActionMode);
    }*/

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
                    if (taskModeList.get(i+1).getStartHour()>taskModeList.get(i).getEndHour()){
                        isCheckTime=true;
                    }else {
                        isCheckTime=false;
                        toast("定时列表第"+(i+2)+"行时间设置有误");
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
            toast("暂无定时任务");
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("------onRestart");
        Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
        taskModeList=mapFileStatus.getcTaskModes();
        taskAdapter.setNewData(taskModeList);
        submissionTask();
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.e("------onPause");
    }

    @Override
    public void onSelected(PickValueView view, Object leftValue, Object middleValue, Object rightValue, Object threeValue) {
       if (view == pickValueViewNum) {
            int starth = (int) leftValue;
            int startm = (int) middleValue;
            int endh = (int) threeValue;
            int endm = (int) rightValue;
            TaskMode taskMode1=taskModeList.get(mPosition);
                taskMode1.setStartHour(starth);
                taskMode1.setStartMin(startm);
            if (endh==starth && endm > startm){
                taskMode1.setEndHour(endh);
                taskMode1.setEndMin(endm);
            }else if (endh > starth){
                taskMode1.setEndHour(endh);
                taskMode1.setEndMin(endm);
            }else {
                toast("结束时间必须大于开始时间");
            }
            taskAdapter.setData(mPosition,taskMode1);
        } else {
            String selectedStr = (String) leftValue;
        }
    }

}
