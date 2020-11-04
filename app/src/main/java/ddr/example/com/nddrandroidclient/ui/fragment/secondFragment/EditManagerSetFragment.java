package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.BuildConfig;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEdition;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEditions;
import ddr.example.com.nddrandroidclient.entity.other.HwParkID;
import ddr.example.com.nddrandroidclient.helper.SpUtil;
import ddr.example.com.nddrandroidclient.http.HttpManager;
import ddr.example.com.nddrandroidclient.http.serverupdate.UpdateState;
import ddr.example.com.nddrandroidclient.http.serverupdate.VersionInformation;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.socket.hwSocker.HttpUtilh;
import ddr.example.com.nddrandroidclient.socket.hwSocker.Utilityhw;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.StringAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.VersionAdapter;
import ddr.example.com.nddrandroidclient.ui.dialog.HwBuildNameDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.ProgressDialog;
import ddr.example.com.nddrandroidclient.widget.view.CustomPopuWindow;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static ddr.example.com.nddrandroidclient.http.Api.SERVER_UPDATE_DOMAIN_NAME;

/**
 * time: 2020/03/24
 * desc: 高级设置版本管理界面
 */
public class EditManagerSetFragment extends DDRLazyFragment {

    @BindView(R.id.computer_type_recycle)
    RecyclerView computer_type_recycle;
    @BindView(R.id.tv_bb_type)
    TextView tv_bb_type;
    @BindView(R.id.tvUpdateServer)
    TextView tvUpdateServer;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_id)
    EditText editId;
    @BindView(R.id.tv_park_name)
    TextView tvParkName;
    @BindView(R.id.button_save)
    Button buttonSave;

    private TcpClient tcpClient;
    private VersionAdapter versionAdapter;
    private ComputerEditions computerEditions;
    private HwParkID hwParkID;
    private BaseDialog inputDialog;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(MessageEvent messageEvent) {
        if (messageEvent.getType() == MessageEvent.Type.updateVersion) {
            inBaseGetVersion();
        }
    }

    public static EditManagerSetFragment newInstance() {
        return new EditManagerSetFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_edition;
    }

    @Override
    protected void initView() {
        versionAdapter = new VersionAdapter(R.layout.item_computer_version, getAttachActivity());
        NLinearLayoutManager layoutManager = new NLinearLayoutManager(getAttachActivity());
        computer_type_recycle.setLayoutManager(layoutManager);
        computer_type_recycle.setAdapter(versionAdapter);
        BuildAdapter = new StringAdapter(R.layout.item_input_hw);
        try {
            if (hwParkID.getSectorID()!=null){
                tvParkName.setText(hwParkID.getSectorID());
            }
            if (hwParkID.getHwID()!=null){
                editId.setText(hwParkID.getHwID());
            }

            if (hwParkID.getHwAppKey()!=null){
                editName.setText(hwParkID.getHwAppKey());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void initData() {
        tcpClient = TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        computerEditions = ComputerEditions.getInstance();
        tcpClient.getHostComputerEdition();
        getAndroidEdition();
        hwParkID=HwParkID.getInstance();
        editId.setText(SpUtil.getInstance(getContext()).getString(SpUtil.HW_ID));
        editName.setText(SpUtil.getInstance(getContext()).getString(SpUtil.HW_APP_KEY));
        queryFromServer();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void inBaseGetVersion() {
        List<ComputerEdition> computerEditionList = new ArrayList<>();
        Logger.e("版本信息" + computerEditions.getComputerEditionList().size());
        for (int i = 0; i < computerEditions.getComputerEditionList().size(); i++) {
            ComputerEdition computerEdition = new ComputerEdition();
            computerEdition.setVersion(computerEditions.getComputerEditionList().get(i).getVersion());
            computerEdition.setData(computerEditions.getComputerEditionList().get(i).getData());
            computerEdition.setType(computerEditions.getComputerEditionList().get(i).getType());
            computerEditionList.add(computerEdition);
            Logger.e("信息内容" + computerEditionList.get(i).getVersion());
        }
        versionAdapter.setNewData(computerEditionList);

    }


    /**
     * 获取安卓版本信息
     */
    @SuppressLint("SetTextI18n")
    private void getAndroidEdition() {
        String buildTime = BuildConfig.BUILD_TIME;
        String versionName = BuildConfig.VERSION_NAME;
        tv_bb_type.setText("V " + versionName + " " + buildTime);
    }

    @OnClick({R.id.tvUpdateServer,R.id.tv_park_name, R.id.button_save})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.tv_park_name:
//                showHWPopupWindow(tvParkName);
                queryFromServer();
                inputDialog=new HwBuildNameDialog.Builder(getAttachActivity())
                        .setAdapter(BuildAdapter)
                        .setAutoDismiss(false)
                        .setGravity(Gravity.CENTER)
                        .setListener(new HwBuildNameDialog.OnListener() {
                        }).show();
                onItemClick(1);
                break;
            case R.id.button_save:
                editId.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().equals("") || s.length()<0){
//                            toast("需要输入内容哦");
                        }else {
                            hwParkID.setHwID(s.toString());
                        }
                    }
                });
                editName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!s.toString().equals("") || s.length()<0){
//                            toast("需要输入内容哦");
                        }else {
                            Logger.e("内容"+s.toString());
                            hwParkID.setHwAppKey(s.toString());
                        }
                    }
                });
                if (!editName.getText().toString().equals("")){
                    Logger.e("内容"+editName.getText().toString());
                    hwParkID.setHwAppKey(editName.getText().toString());
                    SpUtil.getInstance(getContext()).putString(SpUtil.HW_APP_KEY,editName.getText().toString());
                }else {
                    toast("请输入APPKEY");
                }
                if (!editId.getText().toString().equals("")){
                    Logger.e("内容"+editId.getText().toString());
                    hwParkID.setHwID(editId.getText().toString());
                    SpUtil.getInstance(getContext()).putString(SpUtil.HW_ID,editId.getText().toString());
                    toast("保存成功");
                }else {
                    toast("请输入HWID");
                }
                break;
            case R.id.tvUpdateServer:
                Logger.e("点击检查更新升级");
                getVersions();
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 通过http获取服务版本信息
     */
    private void getVersions() {
        toast("正在查询版本信息!");
        Logger.e("-----url:" + RetrofitUrlManager.getInstance().fetchDomain(SERVER_UPDATE_DOMAIN_NAME).toString());
        HttpManager.getInstance().getHttpServer().getVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VersionInformation>() {            // Observable(被观察者) 通过subscribe(订阅)方法发送给所有的订阅者（Observer）
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("------------onSubscribe");
                    }

                    @Override
                    public void onNext(VersionInformation versionInformation) {
                        String title;
                        Logger.e("请求成功:" + versionInformation.getState());
                        if (!versionInformation.getState().equals("net error")) {
                            if (versionInformation.getLatestVersion().equals(versionInformation.getCurrentVersion())) {
                                //toast("当前版本为最新版本无需升级");
                                title = "当前版本为最新版本，是否仍要升级";
                            } else {
                                title = "最新版本为：" + versionInformation.getLatestVersion() + "是否升级";
                            }
                            showVersionDialog(title);
                        } else {
                            toast("当前机器人无外网连接!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Logger.e("----------onComplete:完成");
                    }
                });
    }


    /**
     * 是否更新
     *
     * @param versionTitle
     */
    private void showVersionDialog(String versionTitle) {
        new InputDialog.Builder(getAttachActivity())
                .setEditVisibility(View.GONE)
                .setTitle(versionTitle)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        tcpClient.reqCmdIpcMethod(BaseCmd.eCmdIPCMode.eExitModuleService);
                        updateServer();
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {
                        toast(R.string.common_cancel);
                    }
                }).show();
    }

    /**
     * 请求更新
     */
    private void updateServer() {
        HttpManager.getInstance().getHttpServer().updateServer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateState>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("------------onSubscribe");
                    }

                    @Override
                    public void onNext(UpdateState updateState) {
                        if (updateState.getState().equals("Net Error")) {
                            toast("当前机器人无外网连接!");
                        } else {
                            new ProgressDialog.Builder(getAttachActivity())
                                    .setTitle("下载进度：")
                                    .setUpdateServer()
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    private StringAdapter BuildAdapter;
    private CustomPopuWindow customPopWindow;
    private List<String> buildParkList=new ArrayList<>();
    private List<HwParkID> hwParkIDList=new ArrayList<>();
    private String buildID;
    private void showHWPopupWindow(View view) {
            Logger.e("---------showTaskPopupWindow");
                    View contentView = null;
                    contentView = LayoutInflater.from(getAttachActivity()).inflate(R.layout.recycle_task, null);
                    customPopWindow = new CustomPopuWindow.PopupWindowBuilder(getAttachActivity())
                            .setView(contentView)
                            .enableOutsideTouchableDissmiss(false)
                            .setClippingEnable(false)
                            .create()
                            .showAsDropDown(view, DpOrPxUtils.dip2px(getAttachActivity(), 0), 5);
                    RecyclerView recycler_task_check = contentView.findViewById(R.id.recycler_task_check);
                    NLinearLayoutManager layoutManager = new NLinearLayoutManager(getAttachActivity());
                    recycler_task_check.setLayoutManager(layoutManager);
                    recycler_task_check.setAdapter(BuildAdapter);
                    onItemClick(1);
                    customPopWindow.setOutsideTouchListener(() -> {
                        Logger.e("点击外部已关闭");
                        customPopWindow.dissmiss();
                        });
        }

    private void onItemClick(int type) {
        Logger.e("子项点击事件");
        switch (type) {
            case 1:
                BuildAdapter.setOnItemClickListener((adapter, view, position) -> {
                    tvParkName.setText(hwParkIDList.get(position).getName());
                    buildID = hwParkIDList.get(position).getId();
                    Logger.e("点击的ID" + buildID);
                    hwParkID.setSectorID(buildID);
                    inputDialog.dismiss();
                });
                break;
        }
    }

    private void queryFromServer() {
        HttpUtilh.sendOkHttpRequest(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                toast("获取信息失败，请检查网络情况");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Logger.e("截取信息" + responseText);
                boolean result = false;
                result = Utilityhw.handleProvinceResponse(responseText);
                buildParkList=new ArrayList<>();
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hwParkIDList=hwParkID.getHwBuilds();
                            for (int i=0;i<hwParkIDList.size();i++){
                                String parkname=hwParkIDList.get(i).getName();
                                buildParkList.add(parkname);
                            }
                            BuildAdapter.setNewData(buildParkList);
                        }
                    });
                }
            }
        });
    }


}
