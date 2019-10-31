package ddr.example.com.nddrandroidclient.ui.fragment;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;

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
}
