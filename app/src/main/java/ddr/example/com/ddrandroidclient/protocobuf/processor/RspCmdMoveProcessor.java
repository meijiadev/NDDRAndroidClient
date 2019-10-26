package ddr.example.com.ddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import DDRCommProto.BaseCmd;

public class RspCmdMoveProcessor extends BaseProcessor {
    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context,commonHeader, msg);
        BaseCmd.rspCmdMove rspCmdMove= (BaseCmd.rspCmdMove) msg;
        //Logger.e("接受移动返回数据:"+rspCmdMove.getTypeValue());

    }
}
