package ddr.example.com.nddrandroidclient.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpClient;
import ddr.example.com.nddrandroidclient.ui.dialog.InputDialog;
import ddr.example.com.nddrandroidclient.ui.dialog.WaitDialog;
import ddr.example.com.nddrandroidclient.widget.view.MapEditView;
import ddr.example.com.nddrandroidclient.widget.view.RobotLocationView;
import ddr.example.com.nddrandroidclient.widget.layout.ZoomLayout;

/**
 * time : 2019/12/25
 * desc : 手动定位
 */
public class RelocationActivity extends DDRActivity {
    @BindView(R.id.zoom_view)
    ZoomLayout zoomView;
    @BindView(R.id.iv_content)
    MapEditView ivContent;
    @BindView(R.id.robot_location)
    RobotLocationView robotLocationView;         //当前机器人的位置
    @BindView(R.id.map_layout)
    RelativeLayout mapLayout;
    @BindView(R.id.iv_back)
    ImageView ivBack;
  /*  @BindView(R.id.tv_robot_position)
    TextView tvRobotPosition;*/
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
            ivContent.setImageBitmap(currentBitmap);
            ivContent.refreshMap();
            robotLocationView.setBitmapSize(zoomView,mapName,currentBitmap.getWidth(),currentBitmap.getHeight());
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
        BaseCmd.CommonHeader commonHeader = BaseCmd.CommonHeader.newBuilder()
                .setFromCltType(BaseCmd.eCltType.eLocalAndroidClient)
                .setToCltType(BaseCmd.eCltType.eLSMSlamNavigation)
                .addFlowDirection(BaseCmd.CommonHeader.eFlowDir.Forward)
                .build();
        DDRModuleCmd.reqObstacleInfo reqObstacleInfo=DDRModuleCmd.reqObstacleInfo.newBuilder().build();
        if (tcpClient!=null){
            tcpClient.sendData(commonHeader,reqObstacleInfo);
        }
    }


    @OnClick({R.id.tv_finish,R.id.iv_back,R.id.tv_look})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.tv_finish:
                setCenterTouch();
                XyEntity xyEntity=ivContent.getCenterCoordinate();
                Logger.e("-x:"+xyEntity.getX()+";"+xyEntity.getY());
                XyEntity xyEntity1=robotLocationView.toPathXy(xyEntity.getX(),xyEntity.getY());
                float rotation=(float) Math.toRadians(zoomView.getRotation());
                Logger.e("-x:"+xyEntity1.getX()+";"+xyEntity1.getY()+"弧度："+rotation);
                ivContent.invalidate();
                reqCmdReloc(xyEntity1.getX(),xyEntity1.getY(),rotation);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_look:
                setCenterTouch();
                XyEntity xyEntity2=ivContent.getCenterCoordinate();
                Logger.e("-x:"+xyEntity2.getX()+";"+xyEntity2.getY());
                XyEntity xyEntity3=robotLocationView.toPathXy(xyEntity2.getX(),xyEntity2.getY());
                float rotation1=(float) Math.toRadians(zoomView.getRotation());
                Logger.e("-x:"+xyEntity3.getX()+";"+xyEntity3.getY()+"弧度："+rotation1);
                toast("X:"+xyEntity3.getX()+",Y:"+xyEntity3.getY()+",弧度："+rotation1);
                ivContent.refreshMap();
                break;
        }
    }

    /**
     * 模拟中心处（机器人当前位置处的点击事件）
     */
    private void setCenterTouch(){
        int [] location=new int[2];
        mapLayout.getLocationOnScreen(location); //布局在整个屏幕中的位置
        int marginLeft=location[0];             // 距离屏幕左边的距离
        int marginTop=location[1];             //  距离屏幕上方的距离
        Logger.e("marginLeft:"+marginLeft+";"+"marginTop:"+marginTop);
        XyEntity xyEntity=robotLocationView.getRobotLocationInWindow();
        float x=xyEntity.getX();
        float y=xyEntity.getY();
        Logger.e("机器人在布局中的位置："+x+";"+y);
        MotionEvent eventDown =MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis(), MotionEvent.ACTION_DOWN, x+marginLeft, y+marginTop, 0);
        dispatchTouchEvent(eventDown);
        MotionEvent eventUp=MotionEvent.obtain(System.currentTimeMillis(),
                System.currentTimeMillis(), MotionEvent.ACTION_UP,x+marginLeft, y+marginTop, 0);
        dispatchTouchEvent(eventUp);
        eventDown.recycle();
        eventUp.recycle();
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
        tcpClient.sendData(null,reqCmdReloc);
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
                        toast("重新定位失败，请重新设置机器人位姿");
                        if (waitDialog!=null&&waitDialog.isShowing()){
                            waitDialog.dismiss();
                        }
                        break;
                    case 1:
                        toast("定位成功");
                        if (waitDialog!=null&&waitDialog.isShowing()){
                            waitDialog.dismiss();
                        }
                        robotLocationView.onStop();
                        finish();
                        break;
                    case 2:
                        waitDialog=new WaitDialog.Builder(this)
                                .setMessage("正在重新定位中可能需要1~3分钟时间...")
                                .show();
                        break;
                }
                break;
            case notifyTCPDisconnected:
                netWorkStatusDialog();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tcpClient!=null&&!tcpClient.isConnected())
        netWorkStatusDialog();
    }

    /**
     * 显示网络连接弹窗
     */
    private void  netWorkStatusDialog(){
        waitDialog=new WaitDialog.Builder(this).setMessage("网络正在连接...").show();
        postDelayed(()->{
            if (waitDialog.isShowing()){
                toast("网络无法连接，请退出重连！");
                ActivityStackManager.getInstance().finishAllActivities();
                startActivity(LoginActivity.class);
            }
        },6000);
    }
}

