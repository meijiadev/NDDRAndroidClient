package ddr.example.com.ddrandroidclient.ui.fragment;

import ddr.example.com.ddrandroidclient.R;
import ddr.example.com.ddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.ddrandroidclient.ui.activity.HomeActivity;

/**
 * time: 2019/10/26
 * desc：版本管理界面
 */
public class VersionFragment extends DDRLazyFragment<HomeActivity> {

    public static VersionFragment newInstance(){
        return new VersionFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_version;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
