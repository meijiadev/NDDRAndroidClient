package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import DDRCommProto.RemoteCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEdition;
import ddr.example.com.nddrandroidclient.entity.other.ComputerEditions;
import ddr.example.com.nddrandroidclient.entity.other.Parameter;
import ddr.example.com.nddrandroidclient.entity.other.Parameters;
import ddr.example.com.nddrandroidclient.other.Logger;

public class RspGetParameterProcessor extends BaseProcessor{
    private Parameters parameters;
    private List<Parameter> parameterList;

    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        BaseCmd.rspConfigOperational rspConfigOperational= (BaseCmd.rspConfigOperational) msg;
        List<BaseCmd.configData> configDataList=rspConfigOperational.getDataList();
        parameters= Parameters.getInstance();
        parameterList=new ArrayList<>();
        for (int i=0;i<configDataList.size();i++){
            Logger.e("key"+configDataList.get(1).getData().getKey());
            Parameter parameter=new Parameter();
            parameter.setKey(configDataList.get(i).getData().getKey().toStringUtf8());
            parameter.setValue(configDataList.get(i).getData().getValue().toStringUtf8());
            parameter.setdValue(configDataList.get(i).getData().getDefauleValue().toStringUtf8());
            parameter.setWritable(configDataList.get(i).getData().getWritable().toStringUtf8());
            parameter.setAlias(configDataList.get(i).getData().getAlias().toStringUtf8());
            parameterList.add(parameter);
        }
        Logger.e("数量"+parameterList.size());
        parameters.setParameterList(parameterList);
        Logger.e("实际数量"+parameters.getParameterList().size());
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataParameter));
    }
}
