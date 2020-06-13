package ddr.example.com.nddrandroidclient.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import DDRCommProto.BaseCmd;
import DDRModuleProto.DDRModuleCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.base.BaseDialog;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.helper.ActivityStackManager;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.dialog.ControlPopupWindow;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.zoomview.RobotLocationView;

/**
 * time : 2019/12/25
 * desc : 手动定位
 */
public class RelocationActivity extends DDRActivity {
    @BindView(R.id.robot_location)
    RobotLocationView robotLocationView;         //当前机器人的位置
    @BindView(R.id.map_layout)
    RelativeLayout mapLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private Bitmap currentBitmap;

    private TcpClient tcpClient;
    private NotifyBaseStatusEx notifyBaseStatusEx;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_relocation;
    }

    @Override
    protected void initView() {
        super.initView();
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        tcpClient=TcpClient.getInstance(context,ClientMessageDispatcher.getInstance());
    }

    @Override
    protected void initData() {
        super.initData();
        String bitmap=getIntent().getStringExtra("currentBitmap");
        String mapName=getIntent().getStringExtra("currentMapName");
        Logger.e("-------bitmap:"+bitmap);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(bitmap);
            currentBitmap= BitmapFactory.decodeStream(fis);
            robotLocationView.setImageBitmap(currentBitmap);
            tcpClient.getMapInfo(ByteString.copyFromUtf8(mapName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        realTimeRequest();
        //reqObstacleInfo();
        robotLocationView.startThread();
    }

    /**
     * 请求当前障碍物信息
     */
    private void reqObstacleInfo(){
        DDRModuleCmd.reqObstacleInfo reqObstacleInfo=DDRModuleCmd.reqObstacleInfo.newBuilder().build();
        if (tcpClient!=null){
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqObstacleInfo);
        }
    }


    @OnClick({R.id.tv_finish,R.id.iv_back,R.id.tv_look})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.tv_finish:
                XyEntity xyEntity=robotLocationView.getRobotLocationInWindow();
                XyEntity xyEntity1=robotLocationView.toWorld(xyEntity.getX(),xyEntity.getY());
                float rotation=robotLocationView.getRadians();
                reqCmdReloc(xyEntity1.getX(),xyEntity1.getY(),rotation);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_look:
                XyEntity original=robotLocationView.getRobotLocationInWindow();
                XyEntity worldXY=robotLocationView.toWorld(original.getX(),original.getY());
                float rotation1=robotLocationView.getRadians();
                toast("X:"+worldXY.getX()+",Y:"+worldXY.getY()+",弧度："+rotation1);
                break;
        }
    }


    /**
     * 发送重定位
     * @param x
     * @param y
     * @param rotation
     */
    private void reqCmdReloc(float x,float y,float rotation){
        BaseCmd.reqCmdReloc reqCmdReloc=BaseCmd.reqCmdReloc.newBuilder()
                .setTypeValue(2)
                .setPosX0(x)
                .setPosY0(y)
                .setPosTh0(rotation)
                .build();
        tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eModuleServer),reqCmdReloc);
    }

    private boolean isRunning=true;

    /**
     * 实时请求雷达数据
     */
    private void realTimeRequest(){
        new Thread(()->{
            while (isRunning){
                reqObstacleInfo();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning=false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isRunning=false;
        robotLocationView.onStop();
    }

    private BaseDialog waitDialog;
    private int relocationStatus;      //重定位结果
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case updateRelocationStatus:
                relocationStatus= (int) messageEvent.getData();
                switch (relocationStatus){
                    case 0:
                        toast(R.string.relocation_failed);
                        if (waitDialog!=null&&waitDialog.isShowing()){
                            waitDialog.dismiss();
                        }
                        break;
                    case 1:
                        toast(R.string.relocation_succeed);
                        if (waitDialog!=null&&waitDialog.isShowing()){
                            waitDialog.dismiss();
                        }
                        robotLocationView.onStop();
                        finish();
                        break;
                    case 2:
                        waitDialog=new WaitDialog.Builder(this)
                                .setMessage(R.string.the_relocation)
                                .show();
                        break;
                }
                break;
            case notifyTCPDisconnected:
                netWorkStatusDialog();
                break;
            case touchFloatWindow:
                new ControlPopupWindow(this).showControlPopupWindow(findViewById(R.id.iv_back));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tcpClient!=null&&!tcpClient.isConnected()){
            Logger.e("网络已断开");
            //netWorkStatusDialog();
        }
    }

    /**
     * 显示网络连接弹窗
     */
    private void  netWorkStatusDialog(){
        waitDialog=new WaitDialog.Builder(this).setMessage(R.string.common_network_connecting).show();
        postDelayed(()->{
            if (waitDialog.isShowing()){
                toast(R.string.network_not_connect);
                ActivityStackManager.getInstance().finishAllActivities();
                startActivity(LoginActivity.class);
            }
        },6000);
    }
}

