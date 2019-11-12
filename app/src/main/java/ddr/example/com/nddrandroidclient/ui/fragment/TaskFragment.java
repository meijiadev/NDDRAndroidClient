package ddr.example.com.nddrandroidclient.ui.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.MapInfo;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import ddr.example.com.nddrandroidclient.widget.view.PickValueView;

/**
 * time：2019/10/28
 * desc：任务管理界面
 */
public class TaskFragment extends DDRLazyFragment<HomeActivity> implements PickValueView.onSelectedChangeListener{
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_num)
    TextView tv_num;
    @BindView(R.id.tv_new_task_list)
    TextView tv_new_task_list;

    private CustomPopuWindow customPopuWindow;
    private DpOrPxUtils dpOrPxUtils;
    private PickValueView pickValueView;
    private PickValueView pickValueViewNum;
    private  TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private MapFileStatus mapFileStatus;

    public static TaskFragment newInstance(){
        return new TaskFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();

    }


    @OnClick({R.id.tv_num,R.id.tv_time})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_time:
                showTimePopupWindow(tv_time,1);
                break;
            case R.id.tv_num:
                showTimePopupWindow(tv_num,2);
                break;

        }
    }

    /**
     * 时间，次数弹窗
     * @param view
     */
    private void showTimePopupWindow(View view,int type) {
        Integer value[] = new Integer[20];
        for (int i = 0; i < value.length; i++) {
            value[i] = i + 1;
        }
        Integer middle[] = new Integer[15];
        for (int i = 0; i < middle.length; i++) {
            middle[i] = i + 1;
        }
        Integer right[] = new Integer[10];
        for (int i = 0; i < right.length; i++) {
            right[i] = i;
        }
        Integer four[] = new Integer[5];
        for (int i = 0; i < four.length; i++) {
            four[i] = i;
        }
        View contentView = null;
        switch (type){
            case 1:
                Logger.e("---------showTimePopupWindow");
                contentView = getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.dialog_time_check, null);
                customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                        .setView(contentView)
                        .create()
                        .showAsDropDown(view, DpOrPxUtils.dip2px(getAttachActivity(), 0), 5);
                pickValueView =contentView.findViewById(R.id.pickValue);
                pickValueView.setOnSelectedChangeListener(this);
                pickValueView.setValueData(value, value[0], middle, middle[0], right, right[0]);
                break;
            case 2:
                contentView = getAttachActivity().getLayoutInflater().from(getAttachActivity()).inflate(R.layout.dialog_num_check, null);
                customPopuWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                        .setView(contentView)
                        .create()
                        .showAsDropDown(view, DpOrPxUtils.dip2px(getAttachActivity(), 0), 5);
                pickValueViewNum =contentView.findViewById(R.id.pickValueNum);
                pickValueViewNum.setOnSelectedChangeListener(this);
                pickValueViewNum.setValueData(value, value[0], middle, middle[0], right, right[0],four,four[0]);
                break;

        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("------onRestart");
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
    public void onSelected(PickValueView view, Object leftValue, Object middleValue, Object rightValue, Object fourValue) {
        if (view == pickValueView) {
            int left = (int) leftValue;
            int middle = (int) middleValue;
            int right = (int) rightValue;
            tv_new_task_list.setText("selected: left:" + left + "  middle:" + middle + "  right:" + right);
        } else if (view == pickValueViewNum) {
            int left = (int) leftValue;
            int middle = (int) middleValue;
            int right = (int) rightValue;
            int four = (int) fourValue;
            tv_new_task_list.setText("selected: left:" + left + "  middle:" + middle + "  right:" + right+"four"+four);
        } else {
            String selectedStr = (String) leftValue;
            tv_new_task_list.setText(selectedStr);
        }

    }
}
