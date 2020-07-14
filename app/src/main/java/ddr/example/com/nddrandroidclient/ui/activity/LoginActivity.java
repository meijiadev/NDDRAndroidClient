package ddr.example.com.nddrandroidclient.ui.activity;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import ddr.example.com.nddrandroidclient.helper.NetWorkUtil;
import ddr.example.com.nddrandroidclient.http.HttpManage;
import ddr.example.com.nddrandroidclient.http.UpdateState;
import ddr.example.com.nddrandroidclient.http.VersionInformation;
import ddr.example.com.nddrandroidclient.helper.SpUtil;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.socket.UdpClient;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.ProgressDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.edit.LimitInputTextWatcher;
import ddr.example.com.nddrandroidclient.widget.edit.RegexEditText;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


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

    public  int tcpPort = 88;       //88  8081
    private String accountName = "", passwordName = "";
    public TcpClient tcpClient;

    public UdpClient udpClient;
    private BaseDialog waitDialog;
    private String LAN_IP="192.168.0.95";    //局域网IP        1.83
    private int port=28888;
    //private boolean hasReceiveBroadcast=false;            //是否接收到广播
    private boolean isLan=true;                                //是否是局域网  默认局域网登录
    private UdpIp udpIp=new UdpIp();
    private String localIP;         //本机IP


    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void upDate(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateIPList:
                //hasReceiveBroadcast=true;
                break;
            case updatePort:
                try {
                    getLocalIP();
                    udpIp= (UdpIp) messageEvent.getData();
                    if (udpIp.getIp().contains(localIP)){
                        tcpPort= udpIp.getPort();
                        LAN_IP=udpIp.getIp();
                    }
                    Logger.e("广播的IP和端口："+udpIp.getIp()+";"+udpIp.getPort());
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case LoginSuccess:
                UdpClient.getInstance(context,ClientMessageDispatcher.getInstance()).close();
                SpUtil.getInstance(context).putString(SpUtil.LOGIN_PASSWORD,passwordName);
                Logger.e("登录成功");
                HttpManage.setBaseUrl(LAN_IP);
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
        account.addTextChangedListener(new LimitInputTextWatcher(account, LimitInputTextWatcher.REGEX_ENGLISH));

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

    /**
     * 获取本机IP
     * @return
     */
    public String getLocalIP(){
        localIP= NetWorkUtil.getLocalIpAddress(context);
        int index=localIP.lastIndexOf(".");
        Logger.e("本机IP:"+localIP+";"+index);
        localIP=localIP.substring(0,index);
        return localIP;
    }

    @OnClick({R.id.login_in,R.id.tv_lan,R.id.tv_wan,R.id.login_log})
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
                                    tcpClient.disConnect();
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
            case R.id.login_log:
                getVersions();
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
    /**
     * 通过http获取服务版本信息
     */
    private void getVersions(){
        //tcpClient.reqCmdIpcMethod(BaseCmd.eCmdIPCMode.eExitModuleService);
        Logger.e("--------:"+HttpManage.getBaseUrl());
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
                        String title;
                        Logger.e("请求成功:"+versionInformation.getLatestVersion());
                        if (versionInformation.getLatestVersion().equals(versionInformation.getCurrentVersion())){
                            title="当前版本为最新版本，是否仍要升级";
                        }else {
                            title="最新版本为："+versionInformation.getLatestVersion()+"是否升级";
                        }
                        showVersionDialog(title);
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
     * 是否升级的弹窗
     * @param versionTitle
     */
    public void showVersionDialog(String versionTitle){
        new InputDialog.Builder(this)
                .setEditVisibility(View.GONE)
                .setTitle(versionTitle)
                .setCancelable(false)
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        updateServer();
                        new ProgressDialog.Builder(getActivity())
                                .setTitle("下载进度：")
                                .show();
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
    private void updateServer(){
        HttpManage.getServer().updateServer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateState>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Logger.e("------------onSubscribe");
                    }

                    @Override
                    public void onNext(UpdateState updateState) {
                        if (updateState.getState().equals("Net Error")){
                            toast("当前机器人无外网连接!");
                        }else if (updateState.getState().equals("Launched")){
                            new ProgressDialog.Builder(getActivity())
                                    .setTitle("下载进度：")
                                    .show();
                        }else if (updateState.getState().equals("Launched")){
                            new ProgressDialog.Builder(getActivity())
                                    .setTitle("下载进度：")
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


}