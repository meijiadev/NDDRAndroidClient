package ddr.example.com.ddrandroidclient.ui.fragment;

import ddr.example.com.ddrandroidclient.R;
import ddr.example.com.ddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.ddrandroidclient.ui.activity.HomeActivity;

/**
 * time: 2019/10/26
 * desc: 地图管理界面
 */
public class MapFragment extends DDRLazyFragment<HomeActivity> {

    public static MapFragment newInstance(){
        return new MapFragment();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_map;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
