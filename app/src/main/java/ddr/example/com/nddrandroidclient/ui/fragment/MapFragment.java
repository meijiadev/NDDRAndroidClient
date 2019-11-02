package ddr.example.com.nddrandroidclient.ui.fragment;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.activity.CollectingActivity;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;

/**
 * time: 2019/10/26
 * desc: 地图管理界面
 */
public class MapFragment extends DDRLazyFragment<HomeActivity> {
    @BindView(R.id.bt_create_map)
    TextView btCreatMap;

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
    @OnClick({R.id.bt_create_map})
    public void onViewClicked(View view){
        switch (view.getId()){
            case R.id.bt_create_map:
            startActivity(CollectingActivity.class);
            break;
        }
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
