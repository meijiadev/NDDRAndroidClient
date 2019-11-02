package ddr.example.com.nddrandroidclient.ui.activity;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.RobotIDEntity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.socket.UdpClient;
import ddr.example.com.nddrandroidclient.ui.adapter.RobotIdAdapter;

/**
 *    time   : 2019/10/26
 *    desc   : 登录页
 */
public final class LoginActivity extends DDRActivity {
    @BindView(R.id.account)
    EditText account;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login_in)
    Button loginIn;
    @BindView(R.id.layout_robot)
    RelativeLayout layoutRobot;
    @BindView(R.id.robot_id)
    TextView robot_id;
    @BindView(R.id.iv_robot)
    ImageView iv_robot;
    @BindView(R.id.layout_account)
    RelativeLayout layout_account;
    @BindView(R.id.layout_password)
    RelativeLayout layout_password;
    @BindView(R.id.recycle_robotId)
    RecyclerView recycleRobotId;
    @BindView(R.id.layout_robot_list)
    RelativeLayout layoutRobotList;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_rb)
    TextView tv_rb;

    private RobotIdAdapter robotIdAdapter;
    public  int tcpPort = 0;
    private String accountName = "", passwordName = "";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public TcpClient tcpClient;
    public  List<String> robotList=new ArrayList<>();

    public UdpClient udpClient;
    private int port=28888;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upDate(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateIPList:
                String ip= (String) messageEvent.getData();
                if (!robotList.contains(ip)){
                    robotList.add(ip);
                    robotIdAdapter.setNewData(robotList);
                }
                break;
            case updatePort:
                tcpPort= (int) messageEvent.getData();
                break;
            case LoginSuccess:
                UdpClient.getInstance(context,ClientMessageDispatcher.getInstance()).close();
                editor.putString("account", accountName);
                editor.putString("password", passwordName);
                editor.commit();
               // startActivity(HomeActivity.class);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void initView() {
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        robotIdAdapter=new RobotIdAdapter(R.layout.item_recycle_robot_id,robotList);
        recycleRobotId.setLayoutManager(linearLayoutManager);
        recycleRobotId.setAdapter(robotIdAdapter);
        onItemClick();
    }

    @Override
    protected void initData() {
        receiveBroadcast();
        robotList.clear();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        account.setText(sharedPreferences.getString("account", ""));
        password.setText(sharedPreferences.getString("password", ""));
        tcpClient=TcpClient.getInstance(context,ClientMessageDispatcher.getInstance());
    }

    @OnClick({R.id.login_in,  R.id.layout_robot,R.id.tv_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_in:
                accountName = account.getText().toString().trim();
                passwordName = password.getText().toString().trim();
                startActivity(HomeActivity.class);
                if (tcpClient.isConnected()){
                    doLogin(accountName,passwordName);
                    Logger.e("登录");
                } else {
                    toast("服务器尚未连接，无法登录");
                }
                break;
            case R.id.layout_robot:
                layoutRobotList.setVisibility(View.VISIBLE);
                loginIn.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_cancel:
                layoutRobotList.setVisibility(View.GONE);
                loginIn.setVisibility(View.VISIBLE);
                break;

        }
    }


    /**
     * 接收广播
     */
    private void receiveBroadcast(){
        udpClient= UdpClient.getInstance(this,ClientMessageDispatcher.getInstance());
        try {
            udpClient.connect(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 子项点击事件
     */
    private void onItemClick(){
        robotIdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Logger.e("点击子项");
                String ip=robotList.get(position);
                tcpClient.disConnect();     //在切换服务器时，要先关闭当前服务器再连接新的服务器
                Logger.e("----连接");
                tcpClient.creatConnect(ip,tcpPort);
                RobotIDEntity.getInstance().setIp(ip);
                RobotIDEntity.getInstance().setPort(tcpPort);
                robot_id.setText(ip);
                layoutRobotList.setVisibility(View.GONE);
                loginIn.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 登陆操作
     * @param accountName
     * @param passwordName
     */
    private void doLogin(String accountName,String passwordName){
        if (accountName.equals("")&passwordName.equals("")){
            toast("用户名和密码不能为空");
        }else {
            tcpClient.sendData(null, CmdSchedule.localLogin(accountName,passwordName));
            Logger.e("登录局域网服务器");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        receiveBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tcpClient=null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tcpClient.disConnect();
    }

    /**
     * 状态栏是否启动深色字体
     * @return false 不启动
     */
    @Override
    public boolean statusBarDarkFont() {
        return false;
    }
}