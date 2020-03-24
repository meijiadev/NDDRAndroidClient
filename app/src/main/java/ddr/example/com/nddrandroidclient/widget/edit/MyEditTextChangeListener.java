package ddr.example.com.nddrandroidclient.widget.edit;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


import com.hjq.toast.ToastUtils;

import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.widget.view.PointView;
import ddr.example.com.nddrandroidclient.widget.view.ZoomImageView;

/**
 * time :  2019/11/27
 * desc :  监听编辑框
 */
public class MyEditTextChangeListener implements TextWatcher {
    private final static int ET_X=0;
    private final static int ET_Y=1;
    private final static int ET_Towards=2;
    private final static int ET_Speed=3;
    private final static int ET_distance=4;
    private TargetPoint targetPoint1=new TargetPoint();
    private ZoomImageView zoomImageView;
    private PointView pointView;
    private int editType;
    private DDREditText editTextX,editTextY,editTextC;




    public MyEditTextChangeListener( int type,PointView pointView, TargetPoint targetPoint, ZoomImageView zoomImageView,DDREditText editTextX,DDREditText editTextY,DDREditText editTextC) {
        this.pointView=pointView;
        this.editTextX=editTextX;
        this.editTextY=editTextY;
        this.editTextC=editTextC;
        targetPoint1.setName(targetPoint.getName());
        targetPoint1.setX(targetPoint.getX());
        targetPoint1.setY(targetPoint.getY());
        targetPoint1.setTheta(targetPoint.getTheta());
        this.zoomImageView=zoomImageView;
        this.editType=type;
    }
    public MyEditTextChangeListener(int type){
        this.editType=type;
    }

    /**
     * 编辑框的内容发生改变之前的回调方法
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * 编辑框的内容正在发生改变时的回调方法 >>用户正在输入
     * 我们可以在这里实时地 通过搜索匹配用户的输入
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    /**
     * 编辑框的内容改变以后,用户没有继续输入时 的回调方法
     */
    @Override
    public void afterTextChanged(Editable s) {
        switch (editType){
            case ET_X:
                try {
                    float x=Float.valueOf(s.toString());
                    targetPoint1.setX(x);
                    targetPoint1.setY(editTextY.getFloatText());
                    targetPoint1.setTheta(editTextC.getIntegerText());
                    pointView.setPoint(targetPoint1);
                    zoomImageView.invalidate();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case ET_Y:
                try{
                   float y=Float.valueOf(s.toString());
                   targetPoint1.setY(y);
                   targetPoint1.setX(editTextX.getFloatText());
                   targetPoint1.setTheta(editTextC.getIntegerText());
                   pointView.setPoint(targetPoint1);
                   zoomImageView.invalidate();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case ET_Towards:
                try {
                    if(Double.parseDouble(s.toString())>180 || Double.parseDouble(s.toString())<-180) {
                        ToastUtils.show("输入超出范围,请输入正确的数值");
                    }else {
                        //pointView.setActionPointTowards(gaugeName,Float.valueOf(s.toString()));
                        float angle=Float.valueOf(s.toString());
                        targetPoint1.setX(editTextX.getFloatText());
                        targetPoint1.setY(editTextY.getFloatText());
                        targetPoint1.setTheta((int) angle);
                        pointView.setPoint(targetPoint1);
                        zoomImageView.invalidate();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case ET_Speed:
                try {
                    if(s.length()==0 || s.toString().equals("0") || s.toString().equals("0.0")){
                        ToastUtils.show("输入有误，请重新输入");
                    }else {
                        if(Double.parseDouble(s.toString())<0 || Double.parseDouble(s.toString())>1 ){
                            ToastUtils.show("输入超出范围,请输入正确的数值");
                        }
                    }
                }catch (Exception e){}
                break;
            case ET_distance:
                try {
                    if(s.length()==0 || s.toString().equals("-") || s.toString().equals("+")){
                        ToastUtils.show("输入有误，请重新输入");
                    }else {
                        if(Double.parseDouble(s.toString())<-9999.99 || Double.parseDouble(s.toString())>9999.99 ){
                            ToastUtils.show("输入超出范围,请输入正确的数值");
                        }
                    }
                }catch (Exception e){

                }
                break;


        }


    }
}
