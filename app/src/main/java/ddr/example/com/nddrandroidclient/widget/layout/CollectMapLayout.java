package ddr.example.com.nddrandroidclient.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import DDRCommProto.BaseCmd;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.entity.MessageEvent;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.info.NotifyLidarPtsEntity;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.widget.view.CollectingView2;
import ddr.example.com.nddrandroidclient.widget.view.CollectingView3;

/**
 * time : 2019/12/25
 * desc : 放置采集地图控件的布局
 */
public class CollectMapLayout extends FrameLayout {
    private Context mContext;
    private NotifyLidarPtsEntity notifyLidarPtsEntity;
    private NotifyLidarPtsEntity notifyLidarPtsEntity1;
    private List<NotifyLidarPtsEntity> ptsEntityList=new ArrayList<>();  //存储雷达扫到的点云
    private List<XyEntity>poiPoints=new ArrayList<>();
    //private List<NotifyLidarPtsEntity> ptsSwitchs=new ArrayList<>(); //经过坐标转化之后的点云列表（可以直接绘制到画布上的）
    private float posX,posY;
    private float radian;
    private float angle;
    private float minX=0,minY=0,maxX=0,maxY=0;  //雷达扫到的最大坐标和最小坐标
    private double ratio=1;         //地图比例
    private int measureWidth, measureHeight;

    private CollectingView2 collectingView2;
    private CollectingView3 collectingView3;
    /**
     * 弧度转角度
     */
    public float radianToangle(float angle){
        return (float)(180/Math.PI*angle);
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void upDate(MessageEvent mainUpDate) {
        switch (mainUpDate.getType()) {
            case receivePointCloud:
                if (NotifyBaseStatusEx.getInstance().getSonMode()==6){
                    posX=notifyLidarPtsEntity.getPosX();
                    posY=notifyLidarPtsEntity.getPosY();
                    radian=notifyLidarPtsEntity.getPosdirection();
                    angle=radianToangle(radian);
                    notifyLidarPtsEntity1=new NotifyLidarPtsEntity();
                    notifyLidarPtsEntity1.setPosX(notifyLidarPtsEntity.getPosX());
                    notifyLidarPtsEntity1.setPosY(notifyLidarPtsEntity.getPosY());
                    notifyLidarPtsEntity1.setPositionList(notifyLidarPtsEntity.getPositionList());
                    ptsEntityList.add(notifyLidarPtsEntity1);
                    maxOrmin(notifyLidarPtsEntity.getPositionList());
                }
                break;
            case addPoiPoint:
                poiPoints.add(new XyEntity(posX,posY));
                break;
        }
    }

    /**
     * 计算缩放比例
     * @param list
     */
    private void maxOrmin(List<BaseCmd.notifyLidarPts.Position> list){
        long startTime=System.currentTimeMillis();
        if (list!=null){
            int listSize=list.size();
            for (int i=0;i<listSize;i++){
                if (maxX<list.get(i).getPtX()) maxX=list.get(i).getPtX();
                if (maxY<list.get(i).getPtY()) maxY=list.get(i).getPtY();
                if (minX>list.get(i).getPtX()) minX=list.get(i).getPtX();
                if (minY>list.get(i).getPtY()) minY=list.get(i).getPtY();
            }
            if (maxX<posX) maxX=posX;
            if (maxY<posY) maxY=posY;
            if (minX>posX) minX=posX;
            if (minY>posY) minY=posY;
            float xy=Math.max(Math.max(maxX,Math.abs(minX)),Math.max(maxY,Math.abs(minY)));
            if (xy<=0){
                ratio=1;
            }else {
                if (measureWidth>measureHeight){
                    ratio=measureWidth/(xy)/2*1;
                }else {
                    ratio=measureHeight/(xy)/2*1;
                }
            }
        }
        long endTime=System.currentTimeMillis();
    }
    public CollectMapLayout(@NonNull Context context) {
        super(context);
        mContext=context;
    }

    public CollectMapLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    private void generateView(){
        collectingView2=new CollectingView2(mContext);
        collectingView3=new CollectingView3(mContext);
        this.addView(collectingView2,measureWidth,measureHeight);
        this.addView(collectingView3,measureWidth,measureHeight);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

            if (widthMode == View.MeasureSpec.EXACTLY) {
                // 具体的值和match_parent
                measureWidth = widthSize;
            } else {
                // wrap_content
                measureWidth = 1000;
            }

            if (heightMode == View.MeasureSpec.EXACTLY) {
                measureHeight = heightSize;
            } else {
                measureHeight = 1000;
            }
            setMeasuredDimension(measureWidth, measureHeight);
    }

}
