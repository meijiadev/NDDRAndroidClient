package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import DDRCommProto.BaseCmd;
import DDRCommProto.RemoteCmd;

import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;

public class RspRemoteServerListProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        RemoteCmd.rspRemoteServerList rspRemoteServerList= (RemoteCmd.rspRemoteServerList) msg;
        List<RemoteCmd.rspRemoteServerList.RemoteServer> remoteServerList=rspRemoteServerList.getServersList();
        Logger.e("返回地区服务器IP和端口！");
        if (remoteServerList.size()>0){
            EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.receiveServerList,remoteServerList.get(0)));
        }
    }
}
