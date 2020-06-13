package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.protobuf.ByteString;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import DDRAIServiceProto.DDRAIServiceCmd;
import DDRCommProto.BaseCmd;
import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.other.Voice;
import ddr.example.com.nddrandroidclient.entity.other.VoiceS;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.protocobuf.CmdSchedule;
import ddr.example.com.nddrandroidclient.protocobuf.dispatcher.ClientMessageDispatcher;
import ddr.example.com.nddrandroidclient.socket.TcpAiClient;
import ddr.example.com.nddrandroidclient.widget.textview.GridTextView;

/**
 * 语料播报设置界面
 * data：2020/05/26
 */

public class VoiceSetFragment extends DDRLazyFragment {

    @BindView(R.id.tv_v_close)
    GridTextView tvVClose;
    @BindView(R.id.tv_v_obstacle)
    GridTextView tvVObstacle;
    @BindView(R.id.tv_v_last)
    GridTextView tvVLast;
    @BindView(R.id.ed_yl_content)
    EditText edYlContent;
    @BindView(R.id.ed_yl_time)
    EditText edYlTime;
    @BindView(R.id.tv_yl_choose)
    TextView tvYlChoose;
    @BindView(R.id.tv_secondBack)
    TextView tvSecondBack;
    @BindView(R.id.tv_yl_content)
    TextView tvYlContent;
    @BindView(R.id.tv_yl_time)
    TextView tvYlTime;
    @BindView(R.id.tv_v_save)
    TextView tvVSave;

    private int voice_num = 0;

    private TcpAiClient tcpClient;
    private VoiceS voiceS;
    private List<Voice> voiceList;
    private String content="未设置";
    private int time=0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_voice;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        tcpClient= TcpAiClient.getInstance(getContext(), ClientMessageDispatcher.getInstance());
        postVoice("播报");
        voiceS=VoiceS.getInstance();
    }

    public static VoiceSetFragment newInstance(){
        return new VoiceSetFragment();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {
            case updataVoice:
                getResultAndSet();
                break;
        }
    }

    @OnClick({R.id.tv_v_close, R.id.tv_v_obstacle, R.id.tv_v_last, R.id.ed_yl_content, R.id.ed_yl_time,R.id.tv_v_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_v_close:
                if (!tvVClose.getSelected()) {
                    setIconDefault1();
                    tvVClose.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
                    tvVClose.setSelected(true);
                } else {
                    toast("已选择");
                }
                voice_num = 0;
                break;
            case R.id.tv_v_obstacle:
                if (!tvVObstacle.getSelected()) {
                    setIconDefault1();
                    tvVObstacle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
                    tvVObstacle.setSelected(true);
                } else {
                    toast("已选择");
                }
                voice_num = 1;
                break;
            case R.id.tv_v_last:
                if (!tvVLast.getSelected()) {
                    setIconDefault1();
                    tvVLast.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
                    tvVLast.setSelected(true);
                } else {
                    toast("已选择");
                }
                voice_num = 2;
                break;
            case R.id.ed_yl_content:
                break;
            case R.id.ed_yl_time:
                break;
            case R.id.tv_v_save:
                if (edYlContent.getText()!=null && voice_num>0 && edYlContent.getText().length()>0){
                    content = edYlContent.getText().toString();
                }
                if (edYlTime.getText()!=null && voice_num>0 && edYlTime.getText().length()>0){
                    time= Integer.parseInt(edYlTime.getText().toString());
                }

                switch (voice_num){
                    case 0:
                        postVoiceResult(content,time,1,0);
                        break;
                    case 1:
                        postVoiceResult(content,time,1,1);
                        break;
                    case 2:
                        postVoiceResult(content,time,1,2);
                        break;
                }
                toast("保存成功");
                Logger.e("内容---"+content+"---"+"间隔---"+time+"选项"+voice_num);
                postVoice("播报");
                break;
        }
    }

    /**
     * 设置图标默认状态
     */
    private void setIconDefault1() {
        tvVClose.setSelected(false);
        tvVObstacle.setSelected(false);
        tvVLast.setSelected(false);
        tvVClose.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
        tvVObstacle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
        tvVLast.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.nocheckedwg), null, null, null);
    }

    /**
     * 发送获取播报请求
     */
    private void postVoice(String param){
        try {
            DDRAIServiceCmd.reqSpeechConfig reqSpeechConfig=DDRAIServiceCmd.reqSpeechConfig.newBuilder()
                    .setParam(param)
                    .build();
            tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer), reqSpeechConfig);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * 发送修改请求
     */
    private void postVoiceResult(String text,int interval,int priority,int type){
        switch (type){
            case 0:
                DDRAIServiceCmd.reqUpdateSpeechConfig.ObstacleSpeech obstacleSpeech=DDRAIServiceCmd.reqUpdateSpeechConfig.ObstacleSpeech.newBuilder()
                        .setText(ByteString.copyFromUtf8("null"))
                        .setInterval(0)
                        .setPriority(1)
                        .build();
                DDRAIServiceCmd.reqUpdateSpeechConfig.SustainedSpeech sustainedSpeech=DDRAIServiceCmd.reqUpdateSpeechConfig.SustainedSpeech.newBuilder()
                        .setText(ByteString.copyFromUtf8("null"))
                        .setInterval(0)
                        .setPriority(1)
                        .build();
                DDRAIServiceCmd.reqUpdateSpeechConfig reqUpdateSpeechConfig=DDRAIServiceCmd.reqUpdateSpeechConfig.newBuilder()
                        .setClosestatus(true)
                        .setObstacle(obstacleSpeech)
                        .setSustained(sustainedSpeech)
                        .build();
                tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer), reqUpdateSpeechConfig);
                break;
            case 1:
                DDRAIServiceCmd.reqUpdateSpeechConfig.ObstacleSpeech obstacleSpeech1=DDRAIServiceCmd.reqUpdateSpeechConfig.ObstacleSpeech.newBuilder()
                        .setText(ByteString.copyFromUtf8(text))
                        .setInterval(interval)
                        .setPriority(priority)
                        .build();
                DDRAIServiceCmd.reqUpdateSpeechConfig.SustainedSpeech sustainedSpeech1=DDRAIServiceCmd.reqUpdateSpeechConfig.SustainedSpeech.newBuilder()
                        .setText(ByteString.copyFromUtf8("null"))
                        .setInterval(0)
                        .setPriority(1)
                        .build();
                DDRAIServiceCmd.reqUpdateSpeechConfig reqUpdateSpeechConfig1=DDRAIServiceCmd.reqUpdateSpeechConfig.newBuilder()
                        .setClosestatus(false)
                        .setObstacle(obstacleSpeech1)
                        .setSustained(sustainedSpeech1)
                        .build();
                tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer), reqUpdateSpeechConfig1);
                break;
            case 2:
                DDRAIServiceCmd.reqUpdateSpeechConfig.ObstacleSpeech obstacleSpeech2=DDRAIServiceCmd.reqUpdateSpeechConfig.ObstacleSpeech.newBuilder()
                        .setText(ByteString.copyFromUtf8("null"))
                        .setInterval(0)
                        .setPriority(1)
                        .build();
                DDRAIServiceCmd.reqUpdateSpeechConfig.SustainedSpeech sustainedSpeech2=DDRAIServiceCmd.reqUpdateSpeechConfig.SustainedSpeech.newBuilder()
                        .setText(ByteString.copyFromUtf8(text))
                        .setInterval(interval)
                        .setPriority(priority)
                        .build();
                DDRAIServiceCmd.reqUpdateSpeechConfig reqUpdateSpeechConfig2=DDRAIServiceCmd.reqUpdateSpeechConfig.newBuilder()
                        .setClosestatus(false)
                        .setObstacle(obstacleSpeech2)
                        .setSustained(sustainedSpeech2)
                        .build();
                tcpClient.sendData(CmdSchedule.commonHeader(BaseCmd.eCltType.eAIServer), reqUpdateSpeechConfig2);
                break;
        }

    }
    /**
     * 获取结果解析
     */
    private void getResultAndSet(){
        if (voiceS.isClose()){
            setIconDefault1();
            tvVClose.setSelected(true);
            tvVObstacle.setSelected(false);
            tvVLast.setSelected(false);
            tvVClose.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
            voice_num=0;
            edYlContent.setText("");
            edYlTime.setText("");
        }else {
            voiceList=voiceS.getVoiceList();
            for (int i=0;i<voiceList.size();i++){
                if (!voiceList.get(i).getText().equals("null")){
                    switch (voiceList.get(i).getType()){
                        case 1:
                            setIconDefault1();
                            tvVClose.setSelected(false);
                            tvVObstacle.setSelected(true);
                            tvVLast.setSelected(false);
                            tvVObstacle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
                            voice_num=1;
                            edYlContent.setText(voiceList.get(i).getText());
                            edYlTime.setText(String.valueOf(voiceList.get(i).getInterval()));
                            break;
                        case 2:
                            setIconDefault1();
                            tvVClose.setSelected(false);
                            tvVObstacle.setSelected(false);
                            tvVLast.setSelected(true);
                            tvVLast.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.checkedwg), null, null, null);
                            voice_num=2;
                            edYlContent.setText(voiceList.get(i).getText());
                            edYlTime.setText(String.valueOf(voiceList.get(i).getInterval()));
                            break;
                    }
                }
            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        postVoice("播报");
    }
}
