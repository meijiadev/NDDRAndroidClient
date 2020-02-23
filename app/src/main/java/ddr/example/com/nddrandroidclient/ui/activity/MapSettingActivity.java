package ddr.example.com.nddrandroidclient.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;

/**
 * time： 2020/2/18
 * desc: 地图设置界面
 */
public class MapSettingActivity extends DDRActivity {
    @BindView(R.id.tv_recover)
    TextView tvRecover;
    @BindView(R.id.et_map_name)
    EditText etMapName;
    @BindView(R.id.tv_point_set)
    TextView tvPointSet;
    @BindView(R.id.tv_switch_mode)
    TextView tvSwitchMode;
    @BindView(R.id.tv_a_b_mode)
    TextView tvABMode;
    @BindView(R.id.tv_static_mode)
    TextView tvStaticMode;
    @BindView(R.id.tv_dynamic_mode)
    TextView tvDynamicMode;
    @BindView(R.id.et_a_b_speed)
    EditText etABSpeed;
    @BindView(R.id.tv_navigation)
    TextView tvNavigation;
    @BindView(R.id.tv_line_patrol)
    TextView tvLinePatrol;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_setting;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @OnClick({R.id.tv_title, R.id.tv_recover, R.id.tv_navigation, R.id.tv_line_patrol, R.id.tv_cancel, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_title:
                finish();
                break;
            case R.id.tv_recover:
                break;
            case R.id.tv_navigation:
                tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
                tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
                break;
            case R.id.tv_line_patrol:
                tvLinePatrol.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_blue),null,null,null);
                tvNavigation.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.mipmap.iv_selected_gray),null,null,null);
                break;
            case R.id.tv_cancel:

                break;
            case R.id.tv_confirm:

                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void update(MessageEvent messageEvent) {
        switch (messageEvent.getType()) {

        }
    }

}
