package ddr.example.com.nddrandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

public class VoiceS {
    public static VoiceS voiceS;
    private List<Voice> voiceList =new ArrayList<>();
    private boolean isClose;
    private int type;
    public static VoiceS getInstance(){
        if (voiceS==null){
            synchronized (VoiceS.class){
                if (voiceS==null){
                    voiceS=new VoiceS();
                }
            }
        }
        return voiceS;
    }

    public List<Voice> getVoiceList() {
        return voiceList;
    }

    public void setVoiceList(List<Voice> voiceList) {
        this.voiceList = voiceList;
    }

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean close) {
        isClose = close;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
