package ddr.example.com.nddrandroidclient.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjq.bar.TitleBar;
import com.jaygoo.widget.VerticalRangeSeekBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.OnClick;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.widget.view.RockerView;
import ddr.example.com.nddrandroidclient.widget.view.ZoomImageView;

/**
 * time  : 2019/10/29
 * desc  : 地图编辑页面
 * remark：包括 编辑虚拟墙 、添加目标点、添加路径、添加任务、编辑任务等功能
 */
public class MapEditActivity extends DDRActivity {
    @BindView(R.id.title_layout)
    TitleBar titleLayout;
    @BindView(R.id.tv_target_point)
    TextView tvTargetPoint;
    @BindView(R.id.tv_path)
    TextView tvPath;
    @BindView(R.id.tv_025m)
    TextView tv025m;
    @BindView(R.id.tv_05m)
    TextView tv05m;
    @BindView(R.id.tv_1m)
    TextView tv1m;
    @BindView(R.id.tv_2m)
    TextView tv2m;
    @BindView(R.id.speed_layout)
    LinearLayout speedLayout;         //速度调节布局
    @BindView(R.id.zmap)
    ZoomImageView zmap;
    @BindView(R.id.iv_center)
    ImageView ivCenter;
    @BindView(R.id.tv_mark_current)
    TextView tvMarkCurrent;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;
    @BindView(R.id.seek_bar)
    VerticalRangeSeekBar seekBar;
    @BindView(R.id.fixed_speed)
    CheckBox fixedSpeed;
    @BindView(R.id.add_poi)
    ImageView addPoi;
    @BindView(R.id.my_rocker)
    RockerView myRocker;
    @BindView(R.id.my_rocker_zy)
    RockerView myRockerZy;

    private Bitmap bitmap;

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void update(MessageEvent messageEvent){
        switch (messageEvent.getType()){
            case addNewPoint:
                Logger.e("新建点");
                titleLayout.setLeftTitle("新建标记点");
                tvMarkCurrent.setVisibility(View.VISIBLE);
                bitmap= (Bitmap) messageEvent.getData();
                zmap.setImageBitmap(bitmap);
                break;
            case addNewPath:
                Logger.e("新建路径");
                titleLayout.setLeftTitle("新建路径");
                bitmap= (Bitmap) messageEvent.getData();
                zmap.setImageBitmap(bitmap);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_map_edit;
    }


    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
    }


    @Override
    public boolean statusBarDarkFont() {
        return false;
    }



    @OnClick({R.id.tv_target_point, R.id.tv_path, R.id.tv_025m, R.id.tv_05m, R.id.tv_1m, R.id.tv_2m, R.id.tv_mark_current, R.id.fixed_speed, R.id.add_poi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_target_point:
                break;
            case R.id.tv_path:
                break;
            case R.id.tv_025m:
                break;
            case R.id.tv_05m:
                break;
            case R.id.tv_1m:
                break;
            case R.id.tv_2m:
                break;
            case R.id.tv_mark_current:
                if (speedLayout.getVisibility()==View.VISIBLE){
                    speedLayout.setVisibility(View.GONE);
                    myRocker.setVisibility(View.GONE);
                    myRockerZy.setVisibility(View.INVISIBLE);
                    tvMarkCurrent.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.nocheckedwg),null);
                }else {
                    speedLayout.setVisibility(View.VISIBLE);
                    myRocker.setVisibility(View.VISIBLE);
                    myRockerZy.setVisibility(View.VISIBLE);
                    tvMarkCurrent.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.mipmap.checkedwg),null);
                }
                break;
            case R.id.fixed_speed:
                break;
            case R.id.add_poi:
                break;
        }
    }
}
