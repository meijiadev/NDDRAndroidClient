package ddr.example.com.nddrandroidclient.ui.fragment;

import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.widget.CircleBarView;
import ddr.example.com.nddrandroidclient.widget.StatusSwitchButton;

/**
 * time: 2019/10/26
 * desc: 基础状态界面
 */
public final class StatusFragment extends DDRLazyFragment<HomeActivity>implements StatusSwitchButton.OnStatusSwitchListener,Animation.AnimationListener{
    @BindView(R.id.status_switch_bt)
    StatusSwitchButton statusSwitchButton;
    @BindView(R.id.circle)
    CircleBarView circleBarView;
    @BindView(R.id.iv_shrink)
    ImageView ivShrink;      //点击头部伸缩
    @BindView(R.id.shrink_tail_layout)
    RelativeLayout shrinkTailLayout; // 伸缩的尾部布局
    @BindView(R.id.shrink_layout)
    RelativeLayout shrinkLayout;

    private Animation hideAnimation;  //布局隐藏时的动画
    private Animation showAnimation;  // 布局显示时的动画效果

    public static StatusFragment newInstance(){
        return new StatusFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_status;
    }

    @Override
    protected void initView() {
        statusSwitchButton.setOnStatusListener(this);
        hideAnimation=AnimationUtils.loadAnimation(getAttachActivity(),R.anim.view_hide);
        showAnimation=AnimationUtils.loadAnimation(getAttachActivity(),R.anim.view_show);
    }

    @Override
    protected void initData() {
        circleBarView.setProgress(80,2000, Color.parseColor("#02B5F8"));

    }

    @OnClick({R.id.iv_shrink})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.iv_shrink:
                if (shrinkTailLayout.getVisibility()==View.VISIBLE){
                    shrinkLayout.startAnimation(hideAnimation);
                    hideAnimation.setAnimationListener(this);
                }else if (shrinkTailLayout.getVisibility()==View.GONE){
                    shrinkTailLayout.setVisibility(View.VISIBLE);
                    shrinkLayout.startAnimation(showAnimation);
                }
                break;
        }
    }


    @Override
    public void onLeftClick() {

    }

    @Override
    public void onCentreClick() {

    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        shrinkTailLayout.setVisibility(View.GONE);
        Logger.e("----动画效果结束");
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
