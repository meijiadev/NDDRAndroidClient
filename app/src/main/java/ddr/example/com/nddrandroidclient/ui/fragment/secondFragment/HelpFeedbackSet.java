package ddr.example.com.nddrandroidclient.ui.fragment.secondFragment;

import android.view.CollapsibleActionView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.entity.other.HelpAndBack;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.adapter.HelpBackAdapter;
import ddr.example.com.nddrandroidclient.ui.adapter.VersionAdapter;

public class HelpFeedbackSet extends DDRLazyFragment {
    @BindView(R.id.recycle_help)
    RecyclerView recycle_help;
    @BindView(R.id.tv_idea_back)
    TextView tv_idea_back;

    private HelpBackAdapter helpBackAdapter;
    private HelpAndBack helpAndBack;
    private List<HelpAndBack> helpAndBackList;
    public static HelpFeedbackSet newInstance(){return new HelpFeedbackSet();}

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_s_help;
    }

    @Override
    protected void initView() {
        helpBackAdapter=new HelpBackAdapter(R.layout.item_recycle_help);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getAttachActivity());
        recycle_help.setLayoutManager(layoutManager);
        recycle_help.setAdapter(helpBackAdapter);

    }

    @Override
    protected void initData() {
        setData();
        onClick();
    }

    @OnClick({R.id.tv_idea_back})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.tv_idea_back:
                toast("目前还未开通此项功能");
                break;
        }
    }

    /**
     *插入数据
     */
    private void setData(){
        int number=2;
        helpAndBackList=new ArrayList<>();
        for (int i=0;i<number;i++){
            helpAndBack=new HelpAndBack();
            switch (i){
                case 0:
                    helpAndBack.setQuestion("为什么机器人无法通过比它大的空间？");
                    helpAndBack.setAnswer("可能是导航参数-避障半径设置有误，机器人能否通过取决于避障半径。");
                    break;
                case 1:
                    helpAndBack.setQuestion("机器人突然断开连接怎么办？");
                    helpAndBack.setAnswer("检查WiFi是否连接正确，查看工控机程序是否正常启动。");
                    break;
            }
            helpAndBackList.add(helpAndBack);
        }
        helpBackAdapter.setNewData(helpAndBackList);
    }

    /**
     * 列表下的点击事件
     */
    public TextView tv_question;
    public TextView tv_answer;
    public ImageView iv_help_xia;
    private boolean isOpen=false;
    private void onClick(){
        helpBackAdapter.setOnItemChildClickListener(((adapter, view, position) -> {
            tv_answer=recycle_help.getLayoutManager().findViewByPosition(position).findViewById(R.id.tv_answer);
            switch (view.getId()){
                case R.id.iv_help_xia:
                    iv_help_xia=(ImageView) view;
                    Logger.e("点击获取答案----");
                    if (isOpen){
                        tv_answer.setVisibility(View.GONE);
                        isOpen=false;
                        iv_help_xia.setImageResource(R.mipmap.xlright_5);
                    }else {
                        tv_answer.setVisibility(View.VISIBLE);
                        iv_help_xia.setImageResource(R.mipmap.xl_5);
                        isOpen=true;
                    }
                    break;
                case R.id.tv_answer:
//                    tv_answer=(TextView) view;
                    break;
                case R.id.tv_question:
                    tv_question=(TextView) view;
                    break;
            }
        }));
    }
}
