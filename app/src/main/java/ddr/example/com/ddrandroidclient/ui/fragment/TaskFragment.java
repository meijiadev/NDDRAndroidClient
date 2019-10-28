package ddr.example.com.ddrandroidclient.ui.fragment;

import ddr.example.com.ddrandroidclient.R;
import ddr.example.com.ddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.ddrandroidclient.ui.activity.HomeActivity;

/**
 * time：2019/10/28
 * desc：任务管理界面
 */
public class TaskFragment extends DDRLazyFragment<HomeActivity> {

    public static TaskFragment newInstance(){
        return new TaskFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_task;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
