package ddr.example.com.nddrandroidclient.widget.edit;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

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
    private PointView pointView;
    private String gaugeName;
    private ZoomImageView zoomImageView;
    private Activity activity;
    private int editType;


    public MyEditTextChangeListener(Activity activity, int type, PointView pointView, String gaugeName, ZoomImageView zoomImageView) {
        this.activity=activity;
        this.pointView=pointView;
        this.gaugeName=gaugeName;
        this.zoomImageView=zoomImageView;
        this.editType=type;
    }
    public MyEditTextChangeListener(Activity activity, int type){
        this.activity=activity;
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
                    if(s.length()==0 || s.toString().equals("-") || s.toString().equals("+")){
                        Toast.makeText(activity,"输入有误，请重新输入",Toast.LENGTH_SHORT).show();
                    }else {
                        if(Double.parseDouble(s.toString())<-9999.99 || Double.parseDouble(s.toString())>9999.99 ){
                            Toast.makeText(activity,"输入超出范围,请输入正确的数值",Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case ET_Y:
                try{
                    if(s.length()==0 || s.toString().equals("-") || s.toString().equals("+")){
                        Toast.makeText(activity,"输入有误，请重新输入",Toast.LENGTH_SHORT).show();
                    }else {
                        if(Double.parseDouble(s.toString())<-9999.99 || Double.parseDouble(s.toString())>9999.99 ){
                            Toast.makeText(activity,"输入超出范围,请输入正确的数值",Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case ET_Towards:
                try {
                    if(Double.parseDouble(s.toString())>180 || Double.parseDouble(s.toString())<-180) {
                        Toast.makeText(activity, "输入超出范围,请输入正确的数值", Toast.LENGTH_SHORT).show();
                    }else {
                        //pointView.setActionPointTowards(gaugeName,Float.valueOf(s.toString()));
                        zoomImageView.invalidate();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case ET_Speed:
                try {
                    if(s.length()==0 || s.toString().equals("0") || s.toString().equals("0.0")){
                        Toast.makeText(activity,"输入有误，请重新输入",Toast.LENGTH_SHORT).show();
                    }else {
                        if(Double.parseDouble(s.toString())<0 || Double.parseDouble(s.toString())>1 ){
                            Toast.makeText(activity,"输入超出范围,请输入正确的数值",Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){}
                break;
            case ET_distance:
                try {
                    if(s.length()==0 || s.toString().equals("-") || s.toString().equals("+")){
                        Toast.makeText(activity,"输入有误，请重新输入",Toast.LENGTH_SHORT).show();
                    }else {
                        if(Double.parseDouble(s.toString())<-9999.99 || Double.parseDouble(s.toString())>9999.99 ){
                            Toast.makeText(activity,"输入超出范围,请输入正确的数值",Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){

                }
                break;


        }


    }
}
