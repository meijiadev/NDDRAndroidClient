package ddr.example.com.nddrandroidclient.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRLazyFragment;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.ui.activity.HomeActivity;

/**
 * time: 2019/10/26
 * desc：版本管理界面
 */
public class VersionFragment extends DDRLazyFragment<HomeActivity> {

    public static VersionFragment newInstance() {
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
    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
