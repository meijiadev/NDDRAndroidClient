package ddr.example.com.ddrandroidclient.ui.fragment;

import ddr.example.com.ddrandroidclient.R;
import ddr.example.com.ddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.ddrandroidclient.ui.activity.HomeActivity;

/**
 * time: 2019/10/26
 * desc: 基础状态界面
 */
public final class StatusFragment extends DDRLazyFragment<HomeActivity> {

    public static StatusFragment newInstance(){
        return new StatusFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_status;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
