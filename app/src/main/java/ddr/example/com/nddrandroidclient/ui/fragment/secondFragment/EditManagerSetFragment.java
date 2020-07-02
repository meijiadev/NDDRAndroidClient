package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.graphics.Canvas;
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
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.BuildConfig;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyEnvInfo;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEdition;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEditions;
import ddr.example.com.nddrandroidclient.http.HttpManage;
import ddr.example.com.nddrandroidclient.http.VersionInformation;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.NLinearLayoutManager;
import ddr.example.com.nddrandroidclient.ui.adapter.VersionAdapter;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    private NotifyEnvInfo notifyEnvInfo;
    private MapFileStatus mapFileStatus;
    private VersionAdapter versionAdapter;
    private List<ComputerEdition> computerEditionList= new ArrayList<>();
    private ComputerEditions computerEditions;
    private ComputerEdition computerEdition;

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updateVersion:
                inBasegetVersion();
                break;
        }
    }
    public static EditManagerSetFragment newInstance(){return new EditManagerSetFragment();}
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_edition;
    }

    @Override
    protected void initView() {
        versionAdapter=new VersionAdapter(R.layout.item_computer_version,getAttachActivity());
        NLinearLayoutManager layoutManager=new NLinearLayoutManager(getAttachActivity());
        computer_type_recycle.setLayoutManager(layoutManager);
        computer_type_recycle.setAdapter(versionAdapter);
    }

    @Override
    protected void initData() {
        tcpClient= TcpClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        notifyBaseStatusEx = NotifyBaseStatusEx.getInstance();
        notifyEnvInfo = NotifyEnvInfo.getInstance();
        mapFileStatus = MapFileStatus.getInstance();
        computerEditions = ComputerEditions.getInstance();
        getHostComputerEdition();
        getAndroidEdition();



    }
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void inBasegetVersion(){
        computerEditionList=new ArrayList<>();
        Logger.e("版本信息"+computerEditions.getComputerEditionList().size());
        for (int i=0;i<computerEditions.getComputerEditionList().size();i++){
            computerEdition=new ComputerEdition();
            computerEdition.setVersion(computerEditions.getComputerEditionList().get(i).getVersion());
            computerEdition.setData(computerEditions.getComputerEditionList().get(i).getData());
            computerEdition.setType(computerEditions.getComputerEditionList().get(i).getType());
            computerEditionList.add(computerEdition);
            Logger.e("信息内容"+computerEditionList.get(i).getVersion());
        }
        versionAdapter.setNewData(computerEditionList);

    }

    /**
     * 获取上位机版本信息
     */
    private void getHostComputerEdition() {
        BaseCmd.reqGetSysVersion reqGetSysVersion = BaseCmd.reqGetSysVersion.newBuilder()
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer), reqGetSysVersion);
    }

    /**
     * 获取安卓版本信息
     */
    private void getAndroidEdition() {
        String buildTime = BuildConfig.BUILD_TIME;
        String versionName = BuildConfig.VERSION_NAME;
        tv_bb_type.setText("V " + versionName + " " + buildTime);
    }

    @OnClick(R.id.tvUpdateServer)
    public void onViewClicked(View view) {
        Logger.e("点击检查更新升级");
        getVersions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 通过http获取服务版本信息
     */
    private void getVersions(){
        HttpManage.getServer().getVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VersionInformation>() {            // Observable(被观察者) 通过subscribe(订阅)方法发送给所有的订阅者（Observer）
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("------------onSubscribe");
                    }

                    @Override
                    public void onNext(VersionInformation versionInformation) {
                        Logger.e("请求成功");
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
}
