package ddr.example.com.nddrandroidclient.protocobuf.dispatcher;

import DDRCommProto.BaseCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.other.Logger;

import ddr.example.com.nddrandroidclient.protocobuf.processor.NotifyBaseStatusExProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.NotifyEnvInfoProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.NotifyLidarPtsProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspClientGetMapInfoProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspCmdMoveProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspCmdSetWorkPathProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspDDRVLNMapExProcess;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspGetDDRVLNMapExProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspGetSysVersionProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspHeartBeatProcess;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspLoginProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.ServerInformationProcessor;


public class ClientMessageDispatcher extends BaseMessageDispatcher {
    static ClientMessageDispatcher clientMessageDispatcher;

    public static ClientMessageDispatcher getInstance(){
        if (clientMessageDispatcher==null){
            clientMessageDispatcher=new ClientMessageDispatcher();
        }
        return clientMessageDispatcher;
    }

    public ClientMessageDispatcher(){
        BaseCmd.HeartBeat heartBeat=BaseCmd.HeartBeat.newBuilder().build();
        m_ProcessorMap.put(heartBeat.getClass().toString(),new RspHeartBeatProcess());

        BaseCmd.rspLogin rspLogin=BaseCmd.rspLogin.newBuilder().build();
        Logger.e("------"+rspLogin.getClass().toString());
        m_ProcessorMap.put(rspLogin.getClass().toString(),new RspLoginProcessor());

        BaseCmd.bcLSAddr bcLSAddr=BaseCmd.bcLSAddr.newBuilder().build();
        m_ProcessorMap.put(bcLSAddr.getClass().toString(),new ServerInformationProcessor());



        BaseCmd.rspCmdMove rspCmdMove=BaseCmd.rspCmdMove.newBuilder().build();
        m_ProcessorMap.put(rspCmdMove.getClass().toString(),new RspCmdMoveProcessor());
        BaseCmd.rspCmdSetWorkPath rspCmdSetWorkPath=BaseCmd.rspCmdSetWorkPath.newBuilder().build();
        m_ProcessorMap.put(rspCmdSetWorkPath.getClass().toString(),new RspCmdSetWorkPathProcessor());

        BaseCmd.notifyBaseStatusEx notifyBaseStatusEx=BaseCmd.notifyBaseStatusEx.newBuilder().build();
        m_ProcessorMap.put(notifyBaseStatusEx.getClass().toString(),new NotifyBaseStatusExProcessor());

        BaseCmd.notifyEnvInfo notifyEnvInfo=BaseCmd.notifyEnvInfo.newBuilder().build();
        m_ProcessorMap.put(notifyEnvInfo.getClass().toString(),new NotifyEnvInfoProcessor());

        DDRVLNMap.rspGetDDRVLNMapEx rspGetDDRVLNMapEx=DDRVLNMap.rspGetDDRVLNMapEx.newBuilder().build();
        m_ProcessorMap.put(rspGetDDRVLNMapEx.getClass().toString(),new RspGetDDRVLNMapExProcessor());

        DDRVLNMap.rspDDRVLNMapEx rspDDRVLNMapEx=DDRVLNMap.rspDDRVLNMapEx.newBuilder().build();
        m_ProcessorMap.put(rspDDRVLNMapEx.getClass().toString(),new RspDDRVLNMapExProcess());

        BaseCmd.rspGetSysVersion rspGetSysVersion=BaseCmd.rspGetSysVersion.newBuilder().build();
        m_ProcessorMap.put(rspGetSysVersion.getClass().toString(),new RspGetSysVersionProcessor());

        BaseCmd.rspClientGetMapInfo rspClientGetMapInfo=BaseCmd.rspClientGetMapInfo.newBuilder().build();
        m_ProcessorMap.put(rspClientGetMapInfo.getClass().toString(),new RspClientGetMapInfoProcessor());

        BaseCmd.notifyLidarPts notifyLidarPts=BaseCmd.notifyLidarPts.newBuilder().build();
        m_ProcessorMap.put(notifyLidarPts.getClass().toString(),new NotifyLidarPtsProcessor());


    }
}
