package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import DDRCommProto.RemoteCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.DevicesInfo;
import ddr.example.com.nddrandroidclient.other.Logger;


/**
 * time: 2020/1/16
 * desc：接收登录成功的返回值
 */
public class RspRemoteLoginProcessor extends BaseProcessor {
    private List<RemoteCmd.rspRemoteLogin.LSEntity> list;
    private DevicesInfo devicesInfo;
    private List<DevicesInfo.Device> devices;
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        RemoteCmd.rspRemoteLogin rspRemoteLogin= (RemoteCmd.rspRemoteLogin) msg;
        devicesInfo=DevicesInfo.getInstance();
        devices=new ArrayList<>();
        switch (rspRemoteLogin.getRetcode().getNumber()){
            case 0:
                list=rspRemoteLogin.getLslistList();
                for (RemoteCmd.rspRemoteLogin.LSEntity lsEntity:list){
                    Logger.e("udid:"+lsEntity.getUdid()+"name:"+lsEntity.getName());
                    DevicesInfo.Device device=new DevicesInfo.Device(lsEntity.getUdid(),lsEntity.getName(),false);
                    devices.add(device);
                }
                devicesInfo.setDevices(devices);
                EventBus.getDefault().postSticky(new MessageEvent(MessageEvent.Type.wanLoginSuccess));
                Logger.e("登陆成功，返回机器人列表");
                break;
            case 1:
                Logger.e("服务器繁忙");
                break;
            case 2:
                Logger.e("服务器连接达到上限");
                break;
        }
    }
}
