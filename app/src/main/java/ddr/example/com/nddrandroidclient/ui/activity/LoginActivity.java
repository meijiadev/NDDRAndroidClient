package ddr.example.com.nddrandroidclient.ui.activity;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.UdpIp;
import ddr.example.com.nddrandroidclient.language.SpUtil;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.socket.UdpClient;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;


/**
 *    time   : 2019/10/26
 *    desc   : 登录页
 */
public  class LoginActivity extends DDRActivity {
    @BindView(R.id.account)
    EditText account;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login_in)
    Button loginIn;

    @BindView(R.id.layout_account)
    RelativeLayout layout_account;
    @BindView(R.id.layout_password)
    RelativeLayout layout_password;
    @BindView(R.id.tv_lan)
    TextView tv_lan;        //局域网
    @BindView(R.id.tv_wan)
    TextView tv_wan;        //广域网

    public  int tcpPort = 88;
    private String accountName = "", passwordName = "";
    public TcpClient tcpClient;

    public UdpClient udpClient;
    private BaseDialog waitDialog;
    private static final String LAN_IP="192.168.0.95";    //局域网IP
    private int port=28888;
    //private boolean hasReceiveBroadcast=false;            //是否接收到广播
    private boolean isLan=true;                                //是否是局域网  默认局域网登录
    private UdpIp udpIp=new UdpIp();


    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void upDate(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateIPList:
                //hasReceiveBroadcast=true;
                break;
            case updatePort:
                try {
                    udpIp= (UdpIp) messageEvent.getData();
                    tcpPort= udpIp.getPort();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case LoginSuccess:
                UdpClient.getInstance(context,ClientMessageDispatcher.getInstance()).close();
                SpUtil.getInstance(context).putString(SpUtil.LOGIN_PASSWORD,passwordName);
                Logger.e("登录成功");
                toast(R.string.login_succeed);
                postDelayed(()->{
                    if (waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    startActivityFinish(HomeActivity.class);
                },1000);
                break;
            case wanLoginSuccess:
                UdpClient.getInstance(context,ClientMessageDispatcher.getInstance()).close();
                SpUtil.getInstance(context).putString(SpUtil.LOGIN_PASSWORD,passwordName);
                Logger.e("广域网登录成功");
                postDelayed(()->{
                    if (waitDialog!=null&&waitDialog.isShowing()){
                        waitDialog.dismiss();
                    }
                    startActivity(DeviceSelectActivity.class);
                },1000);
                break;
            case tcpConnected:
                if (isLan){
                    Logger.e("-----连接成功，开始登录");
                    tcpClient.sendData(null, CmdSchedule.localLogin(accountName,passwordName));
                    GlobalParameter.setAccount(accountName);
                    GlobalParameter.setPassword(passwordName);
                }else {
                    Logger.e("-----广域网连接成功，开始登录");
                    tcpClient.sendData(null,CmdSchedule.remoteLogin(accountName,passwordName));
                }
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


    }

    @Override
    protected void initData() {
        receiveBroadcast();
        password.setText(SpUtil.getInstance(context).getString(SpUtil.LOGIN_PASSWORD));
        tcpClient=TcpClient.getInstance(context,ClientMessageDispatcher.getInstance());
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int memorySize = activityManager.getMemoryClass();
        Logger.e("分配的内存上限："+memorySize+";"+activityManager.getLargeMemoryClass());
    }

    @OnClick({R.id.login_in,R.id.tv_lan,R.id.tv_wan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_in:
                accountName = account.getText().toString().trim();
                passwordName = password.getText().toString().trim();
                if (accountName.equals("")|passwordName.equals("")){
                    toast(getString(R.string.account_not_empty));
                }else {
                    if (isLan){                                   //局域网登录
                        //if (hasReceiveBroadcast){
                        if (tcpClient.isConnected()) tcpClient.disConnect();
                            tcpClient.createConnect(LAN_IP,tcpPort);
                            waitDialog=new WaitDialog.Builder(this)
                                    .setMessage(R.string.in_login)
                                    .show();
                            postDelayed(()->{
                                if (waitDialog.isShowing()){
                                    toast(R.string.login_failed);
                                    waitDialog.dismiss();
                                }
                                },5000);
                       // }else {
                            //toast("无法连接，请检查机器人服务是否正常开启！");
                       // }
                    }else {                                     //广域网登录
                        if (tcpClient.isConnected())
                            tcpClient.disConnect();
                        tcpClient.createConnect(CmdSchedule.broadcastServerIP,CmdSchedule.broadcastServerPort);      //连接地方服务器
                        waitDialog=new WaitDialog.Builder(this)
                                    .setMessage(R.string.in_login)
                                    .show();
                        postDelayed(()->{
                            if (waitDialog.isShowing()){
                                toast(R.string.login_failed);
                                waitDialog.dismiss();
                            }
                            },5000);
                    }
                }
                break;
            case R.id.tv_lan:
                isLan=true;
                tcpClient.disConnect();
                tv_lan.setBackgroundResource(R.mipmap.left_blue_bg);
                tv_wan.setBackgroundResource(R.mipmap.right_black_bg);
                break;
            case R.id.tv_wan:
                tv_lan.setBackgroundResource(R.mipmap.left_black_bg);
                tv_wan.setBackgroundResource(R.mipmap.right_blue_bg);
                isLan=false;
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



    @Override
    protected void onRestart() {
        super.onRestart();
        receiveBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tcpClient=null;
        Logger.e("销毁登录页面");
        EventBus.getDefault().removeAllStickyEvents();
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