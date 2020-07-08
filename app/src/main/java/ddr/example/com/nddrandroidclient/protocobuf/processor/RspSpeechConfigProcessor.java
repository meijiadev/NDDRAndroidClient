package ddr.example.com.nddrandroidclient.protocobuf.processor;

import android.content.Context;

import com.google.protobuf.GeneratedMessageLite;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.Voice;
import ddr.example.com.nddrandroidclient.entity.other.VoiceS;
import ddr.example.com.nddrandroidclient.other.Logger;

public class RspSpeechConfigProcessor extends BaseProcessor{
    private VoiceS voiceS;
    private List<Voice> voiceList;

    @Override
    public void process(Context context, BaseCmd.CommonHeader commonHeader, GeneratedMessageLite msg) {
        super.process(context, commonHeader, msg);
        DDRAIServiceCmd .rspSpeechConfig rspSpeechConfig= (DDRAIServiceCmd.rspSpeechConfig) msg;
        voiceS=VoiceS.getInstance();
        voiceList = new ArrayList<>();
        Logger.e("接收内容是-----"+rspSpeechConfig.getSpeechconfig().getClosestatus());
        Voice voice = new Voice();
        voice.setText(rspSpeechConfig.getSpeechconfig().getObstacle().getText().toStringUtf8());
        voice.setInterval(rspSpeechConfig.getSpeechconfig().getObstacle().getInterval());
        voice.setPriority(rspSpeechConfig.getSpeechconfig().getObstacle().getPriority());
        voice.setType(1);
        Logger.e("内容是" + rspSpeechConfig.getSpeechconfig().getObstacle().getText().toStringUtf8());

        Voice voice1 = new Voice();
        voice1.setText(rspSpeechConfig.getSpeechconfig().getSustained().getText().toStringUtf8());
        voice1.setInterval(rspSpeechConfig.getSpeechconfig().getSustained().getInterval());
        voice1.setPriority(rspSpeechConfig.getSpeechconfig().getSustained().getPriority());
        voice1.setType(2);
        Logger.e("内容是" + rspSpeechConfig.getSpeechconfig().getSustained().getText().toStringUtf8());
        switch (rspSpeechConfig.getSpeechconfig().getClosestatus()) {
            case 1:
                voiceS.setClose(true);
                break;
            case 2:
                voiceS.setClose(false);
                voice.setIsclose(rspSpeechConfig.getSpeechconfig().getClosestatus());
                break;
            case 3:
                voiceS.setClose(false);
                voice1.setIsclose(rspSpeechConfig.getSpeechconfig().getClosestatus());
                break;

        }
        voiceList.add(voice);
        voiceList.add(voice1);
        voiceS.setVoiceList(voiceList);
        EventBus.getDefault().post(new MessageEvent(MessageEvent.Type.updataVoice));

    }
}
