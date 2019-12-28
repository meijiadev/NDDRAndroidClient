package ddr.example.com.nddrandroidclient.ui.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.common.DDRActivity;
import ddr.example.com.nddrandroidclient.widget.view.MapEditView;
import ddr.example.com.nddrandroidclient.widget.view.RobotLocationView;
import ddr.example.com.nddrandroidclient.widget.layout.ZoomLayout;

/**
 * time : 2019/12/25
 * desc : 手动定位
 */
public class RelocationActivity extends DDRActivity {
    @BindView(R.id.zoom_view)
    ZoomLayout zoomView;
    @BindView(R.id.iv_content)
    MapEditView ivContent;
    @BindView(R.id.robot_location)
    RobotLocationView robotLocationView;         //当前机器人的位置
    private Bitmap currentBitmap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_relocation;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        String bitmap=getIntent().getStringExtra("currentBitmap");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(bitmap);
            currentBitmap= BitmapFactory.decodeStream(fis);
            ivContent.setImageBitmap(currentBitmap);
            ivContent.refreshMap();
            robotLocationView.setBitmapSize(currentBitmap.getWidth(),currentBitmap.getHeight());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
