package ddr.example.com.nddrandroidclient.ui.fragment;

import android.view.animation.Animation;

import butterknife.BindView;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;
import ddr.example.com.nddrandroidclient.widget.StatusSwitchButton;

/**
 * time: 2019/10/26
 * desc: 基础状态界面
 */
public final class StatusFragment extends DDRLazyFragment<HomeActivity> implements Animation.AnimationListener ,StatusSwitchButton.OnStatusSwitchListener {
    @BindView(R.id.status_switch_bt)
    StatusSwitchButton statusSwitchButton;

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
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e("------onRestart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e("-----onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.e("------onPause");
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

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
}
