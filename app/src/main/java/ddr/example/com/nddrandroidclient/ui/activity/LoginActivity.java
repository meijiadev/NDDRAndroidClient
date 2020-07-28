package ddr.example.com.nddrandroidclient.ui.activity;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import androidx.core.content.FileProvider;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.BuildConfig;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.UdpIp;
import ddr.example.com.nddrandroidclient.helper.NetWorkUtil;
import ddr.example.com.nddrandroidclient.http.AppVersion;
import ddr.example.com.nddrandroidclient.http.HttpManager;
import ddr.example.com.nddrandroidclient.http.serverupdate.UpdateState;
import ddr.example.com.nddrandroidclient.http.serverupdate.VersionInformation;
import ddr.example.com.nddrandroidclient.helper.SpUtil;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.server.DownloadServer;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.socket.UdpClient;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.ProgressDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.edit.LimitInputTextWatcher;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

import static ddr.example.com.nddrandroidclient.http.Api.SERVER_UPDATE_DOMAIN_NAME;


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
    private boolean isLan=true;                                //是否是局域网  默认局域网登录
    private UdpIp udpIp=new UdpIp();
    private String localIP;         //本机IP


    @Subscribe(threadMode = ThreadMode.MAIN)
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
                    Logger.d("广播的IP和端口："+udpIp.getIp()+";"+udpIp.getPort());
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case LoginSuccess:
                UdpClient.getInstance(context,ClientMessageDispatcher.getInstance()).close();
                SpUtil.getInstance(context).putString(SpUtil.LOGIN_PASSWORD,passwordName);
                Logger.e("登录成功");
                String url ="http://"+LAN_IP+":8081/";
                RetrofitUrlManager.getInstance().putDomain(SERVER_UPDATE_DOMAIN_NAME,url);
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
                    toast("服务器连接成功!");
                    tcpClient.sendData(null, CmdSchedule.localLogin(accountName,passwordName));
                    GlobalParameter.setAccount(accountName);
                    GlobalParameter.setPassword(passwordName);
                }else {
                    Logger.e("-----广域网连接成功，开始登录");
                    tcpClient.sendData(null,CmdSchedule.remoteLogin(accountName,passwordName));
                }
                break;
            case apkDownloadSucceed:
                File file= (File) messageEvent.getData();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
                    Uri uri=Uri.fromFile(file);
                    intent.setDataAndType(uri,"application/vnd.android.package-archive");
                    startActivity(intent);
                }else {
                    Logger.e("---------"+getPackageName());
                    Uri uriFile= FileProvider.getUriForFile(context,"ddr.example.com.nddrandroidclient.fileprovider",file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(uriFile,"application/vnd.android.package-archive");
                    startActivity(intent);
                }
                break;
            case apkDownloadFailed:
                toast("下载失败!");
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
    }

    /**
     * 获取本机IP
     * @return
     */
    public String getLocalIP(){
        localIP= NetWorkUtil.getLocalIpAddress(context);
        int index=localIP.lastIndexOf(".");
        Logger.d("本机IP:"+localIP+";"+index);
        localIP=localIP.substring(0,index);
        return localIP;
    }

    @OnClick({R.id.login_in,R.id.tv_lan,R.id.tv_wan,R.id.login_log,R.id.ivUpdateApk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_in:
                accountName = account.getText().toString().trim();
                passwordName = password.getText().toString().trim();
                if (accountName.equals("")|passwordName.equals("")){
                    toast(getString(R.string.account_not_empty));
                }else {
                    if (isLan){                                   //局域网登录
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
                            },3500);
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
                break;
            case R.id.ivUpdateApk:
                if (NetWorkUtil.checkEnable(context)){
                    HttpManager.getInstance().getHttpServer().queryAppVersion().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<AppVersion>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }
                        @Override
                        public void onNext(AppVersion appVersion) {
                            if (appVersion!=null){
                                String versionName = BuildConfig.VERSION_NAME;
                                if (appVersion.getLatestVersion().equals(versionName)){
                                    toast("当前为最新版本"+versionName);
                                }else {
                                }
                                isDownloadApk(versionName);
                            }else {
                                toast("查询版本信息失败");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            toast("网络不可用!");
                        }

                        @Override
                        public void onComplete() {
                            Logger.e("onComplete");
                        }
                    });
                }else {
                    toast("网络不可用!");
                }
                break;

        }
    }

    private void isDownloadApk(String versionName){
        new InputDialog.Builder(this)
                .setEditVisibility(View.GONE)
                .setTitle("是否更新到最新版本")
                .setListener(new InputDialog.OnListener() {
                    @Override
                    public void onConfirm(BaseDialog dialog, String content) {
                        new ProgressDialog.Builder(getActivity())
                                .setTitle("下载进度：")
                                .show();
                        Intent intent=new Intent(LoginActivity.this, DownloadServer.class);
                        intent.putExtra("version",versionName);
                        startService(intent);
                    }

                    @Override
                    public void onCancel(BaseDialog dialog) {

                    }
                }).show();
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
        Logger.e("--------:"+ RetrofitUrlManager.getInstance().fetchDomain(SERVER_UPDATE_DOMAIN_NAME));
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