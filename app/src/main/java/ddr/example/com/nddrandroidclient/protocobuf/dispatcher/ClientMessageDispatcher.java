package ddr.example.com.nddrandroidclient.protocobuf.dispatcher;

import DDRCommProto.BaseCmd;
import DDRCommProto.RemoteCmd;
import DDRModuleProto.DDRModuleCmd;
import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.other.Logger;

import ddr.example.com.nddrandroidclient.protocobuf.processor.NotifyBaseStatusExProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.NotifyEnvInfoProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.NotifyHardStateProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.NotifyLidarPtsProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.NotifyMapGenStatProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.NotifyRelocaStatusProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspClientGetMapInfoProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspCmdMoveProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspCmdRelocProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspCmdSetWorkPathProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspDDRVLNMapExProcess;

import ddr.example.com.nddrandroidclient.protocobuf.processor.RspCmdStartActionModelProcessor;

import ddr.example.com.nddrandroidclient.protocobuf.processor.RspDetecLoopProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspGetDDRVLNMapExProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspGetParameterProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspGetSensorProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspGetSysVersionProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspGetTaskOperational;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspHeartBeatProcess;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspLoginProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspMapOperationalProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspObstacleInfoProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspRemoteLoginProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspRemoteServerListProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspRunControlExProcessor;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspRunSpecificPoint;
import ddr.example.com.nddrandroidclient.protocobuf.processor.RspSelectLSProcessor;
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

        RemoteCmd.rspRemoteServerList rspRemoteServerList=RemoteCmd.rspRemoteServerList.newBuilder().build();
        m_ProcessorMap.put(rspRemoteServerList.getClass().toString(),new RspRemoteServerListProcessor());

        RemoteCmd.rspRemoteLogin rspRemoteLogin=RemoteCmd.rspRemoteLogin.newBuilder().build();
        m_ProcessorMap.put(rspRemoteLogin.getClass().toString(),new RspRemoteLoginProcessor());

        RemoteCmd.rspSelectLS rspSelectLS=RemoteCmd.rspSelectLS.newBuilder().build();
        m_ProcessorMap.put(rspSelectLS.getClass().toString(),new RspSelectLSProcessor());


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

        BaseCmd.rspCmdStartActionMode rspCmdStartActionMode=BaseCmd.rspCmdStartActionMode.newBuilder().build();
        m_ProcessorMap.put(rspCmdStartActionMode.getClass().toString(),new RspCmdStartActionModelProcessor());

        DDRVLNMap.rspMapOperational rspMapOperational=DDRVLNMap.rspMapOperational.newBuilder().build();
        m_ProcessorMap.put(rspMapOperational.getClass().toString(),new RspMapOperationalProcessor());

        DDRVLNMap.rspRunControlEx rspRunControlEx=DDRVLNMap.rspRunControlEx.newBuilder().build();
        m_ProcessorMap.put(rspRunControlEx.getClass().toString(),new RspRunControlExProcessor());

        DDRModuleCmd.rspObstacleInfo rspObstacleInfo=DDRModuleCmd.rspObstacleInfo.newBuilder().build();
        m_ProcessorMap.put(rspObstacleInfo.getClass().toString(),new RspObstacleInfoProcessor());

        BaseCmd.rspCmdReloc rspCmdReloc=BaseCmd.rspCmdReloc.newBuilder().build();
        m_ProcessorMap.put(rspCmdReloc.getClass().toString(),new RspCmdRelocProcessor());

        BaseCmd.notifyMapGenStat mapGenStat=BaseCmd.notifyMapGenStat.newBuilder().build();
        m_ProcessorMap.put(mapGenStat.getClass().toString(),new NotifyMapGenStatProcessor());

        BaseCmd.rspDetectLoop rspDetectLoop=BaseCmd.rspDetectLoop.newBuilder().build();
        m_ProcessorMap.put(rspDetectLoop.getClass().toString(),new RspDetecLoopProcessor());


        BaseCmd.rspConfigOperational rspConfigOperational=BaseCmd.rspConfigOperational.newBuilder().build();
        m_ProcessorMap.put(rspConfigOperational.getClass().toString(),new RspGetParameterProcessor());


        BaseCmd.notifyHardwareStat notifyHardwareStat=BaseCmd.notifyHardwareStat.newBuilder().build();
        m_ProcessorMap.put(notifyHardwareStat.getClass().toString(),new NotifyHardStateProcessor());

        BaseCmd.rspSensorConfigOperational rspSensorConfigOperational=BaseCmd.rspSensorConfigOperational.newBuilder().build();
        m_ProcessorMap.put(rspSensorConfigOperational.getClass().toString(),new RspGetSensorProcessor());

        DDRVLNMap.rspTaskOperational rspTaskOperational=DDRVLNMap.rspTaskOperational.newBuilder().build();
        m_ProcessorMap.put(rspTaskOperational.getClass().toString(),new RspGetTaskOperational());

        DDRVLNMap.rspRunSpecificPoint rspRunSpecificPoint=DDRVLNMap.rspRunSpecificPoint.newBuilder().build();
        m_ProcessorMap.put(rspRunSpecificPoint.getClass().toString(),new RspRunSpecificPoint());

        BaseCmd.notifyRelocStatus relocStatus=BaseCmd.notifyRelocStatus.newBuilder().build();
        m_ProcessorMap.put(relocStatus.getClass().toString(),new NotifyRelocaStatusProcessor());
    }
}
