package ddr.example.com.nddrandroidclient.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import DDRCommProto.RemoteCmd;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.DevicesInfo;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.DevicesAdapter;


/**
 * time：2020/1/16
 * desc: 设备选择
 */
public class DeviceSelectActivity extends DDRActivity {
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.devices_recycler)
    RecyclerView devicesRecycler;

    private DevicesAdapter devicesAdapter;
    private DevicesInfo devicesInfo;
    private TcpClient tcpClient;
    private List<DevicesInfo.Device> devices;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_select;
    }


    @Override
    protected void initView() {
        super.initView();
        tcpClient=TcpClient.getInstance(context,ClientMessageDispatcher.getInstance());
        devicesInfo=DevicesInfo.getInstance();
        devices=devicesInfo.getDevices();
        devicesAdapter=new DevicesAdapter(R.layout.item_device_recycler,devices);
        @SuppressLint("WrongConstant")
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4, LinearLayoutManager.VERTICAL, false);
        devicesRecycler.setLayoutManager(gridLayoutManager);
        devicesRecycler.setAdapter(devicesAdapter);
        onItemClick();
        if (devices.size()==0){
            toast("当前无设备连接");
        }

    }

    @Override
    protected void initData() {
        super.initData();

    }

    @OnClick({R.id.tv_back})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
        }
    }

    private void onItemClick(){
        devicesAdapter.setOnItemClickListener(((adapter, view, position) -> {
            RemoteCmd.reqSelectLS reqSelectLS=RemoteCmd.reqSelectLS.newBuilder()
                    .setName(devices.get(position).getName())
                    //.setUdid(devices.get(position).getUdid())
                    .build();
            Logger.e("------udip"+devices.get(position).getUdid()+"------name:"+devices.get(position).getName());
            tcpClient.sendData(null,reqSelectLS);
            toast("正在连接机器人...");
        }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case connectedToRobot:
                Intent intent_login = new Intent();
                intent_login.setClass(DeviceSelectActivity.this, HomeActivity.class);
                intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //关键的一句，将新的activity置为栈顶
                startActivity(intent_login);
                finish();
                break;
        }
    }


}
