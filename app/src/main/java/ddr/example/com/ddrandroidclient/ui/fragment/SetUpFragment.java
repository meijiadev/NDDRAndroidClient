package ddr.example.com.ddrandroidclient.ui.fragment;

import ddr.example.com.ddrandroidclient.R;
import ddr.example.com.ddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.ddrandroidclient.ui.activity.HomeActivity;

/**
 * time: 2019/10/26
 * desc: 高级设置界面
 */
public class SetUpFragment extends DDRLazyFragment<HomeActivity> {

    public static SetUpFragment newInstance(){
        return new SetUpFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setup;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
