package ddr.example.com.nddrandroidclient.ui.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.ui.adapter.TaskAdapter;
import ddr.example.com.nddrandroidclient.widget.textview.GridImageView;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.PickValueView;

/**
 * time：2019/10/28
 * desc：任务管理界面
 */
public class TaskFragment extends DDRLazyFragment<HomeActivity> implements PickValueView.onSelectedChangeListener{

    @BindView(R.id.recycle_task_list)
    RecyclerView recycle_task_list;

    private CustomPopuWindow customPopuWindow;
    private DpOrPxUtils dpOrPxUtils;
    private PickValueView pickValueView;
    private PickValueView pickValueViewNum;
    private  TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private MapFileStatus mapFileStatus;
    private TaskAdapter taskAdapter;
    private List<TaskMode> taskModeList =new ArrayList<>();
    private TaskMode taskMode;

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateDDRVLNMap:
                Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
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
        taskAdapter.setNewData(taskModeList);
        Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
        taskModeList=mapFileStatus.getcTaskModes();
        taskAdapter.setNewData(taskModeList);
        onItemClick(1);
    }

    private int mPosition;
    public void onItemClick(int type){
        switch (type){
            case 1:
                //任务列表点击事件
                Logger.e("task列表"+taskModeList.size());
                // Java 8 新特性 Lambda表达式，原来写法即下方注释
                taskAdapter.setOnItemChildClickListener((adapter, view, position) ->  {
                    mPosition=position;
                            TextView tv_task_time=view.findViewById(R.id.tv_task_time);
                            TextView tv_task_pause=view.findViewById(R.id.tv_task_pause);
                            GridImageView gridImageView=view.findViewById(R.id.iv_check);
                            switch (view.getId()){
                                case R.id.tv_task_time:
                                    Logger.e("点击----");
                                    showTimePopupWindow(tv_task_time,1);
                                    break;
                                case R.id.iv_check:
                                    Logger.e("gggg"+gridImageView.getSelected());
                                    if (!gridImageView.getSelected()){
                                        Logger.e("未在列表中");
                                        gridImageView.setSelected(true);
                                        gridImageView.setBackgroundResource(R.mipmap.intask_check);
                                    }else {
                                        Logger.e("在列表中");
                                        gridImageView.setSelected(false);
                                        gridImageView.setBackgroundResource(R.mipmap.intask_def);
                                    }
                                    break;
                                case R.id.tv_task_pause:
                                    if (tv_task_pause.getText().equals("暂停")) {
                                        pauseOrResume("Pause");
                                        tv_task_pause.setText("开始");
                                    }else {
                                        pauseOrResume("Resume");
                                        tv_task_pause.setText("暂停");
                                    }
                                    break;
                                case R.id.tv_task_stop:

                                    break;
                            }

                });
                break;
        }

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
     */
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

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("------onRestart");
        Logger.e("列表数"+mapFileStatus.getcTaskModes().size());
        taskModeList=mapFileStatus.getcTaskModes();
        taskAdapter.setNewData(taskModeList);
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
            if (mPosition>0){
                TaskMode taskModeold=taskModeList.get(mPosition-1);
                if (taskModeold.getEndHour()==starth && startm>taskModeold.getEndMin()){
                    taskMode1.setStartHour(starth);
                    taskMode1.setStartMin(startm);
                }else if (taskModeold.getEndHour()<starth){
                    taskMode1.setStartHour(starth);
                    taskMode1.setStartMin(startm);
                }else {
                    toast("开始必须大于上一次结束时间");
                }
            }else {
                taskMode1.setStartHour(starth);
                taskMode1.setStartMin(startm);
            }

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
