package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Arrays;

import DDRVLNMapProto.DDRVLNMap;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * desc :  通过Matrix实现可缩放、平移、旋转图片的控件
 * time ： 2020/04/29
 * author：EZReal.mei
 */
public class ZoomImageView1 extends View {
    private Context context;

    // 绘制的图片
    private Bitmap sourceBitmap;
    // 当前操作的状态
    private int currentStatus;
    private static final int DEFAULT_BITMAP=0;      // 默认状态下

    private Paint pointPaint;

    private NotifyBaseStatusEx notifyBaseStatusEx;
    public double r00=0;
    public double r01=-61.5959;
    public double t0=375.501;

    public double r10=-61.6269;
    public double r11=0;
    public double t1=410.973;


    public ZoomImageView1(Context context) {
        super(context);
        init(context);
    }

    public ZoomImageView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomImageView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context=context;
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bkpic);
        currentStatus=DEFAULT_BITMAP;
        pointPaint=new Paint();
        pointPaint.setColor(Color.RED);
        Logger.e("------图片的大小："+sourceBitmap.getWidth()+";"+sourceBitmap.getHeight());
    }

    /**
     * 设置图片
     * @param path
     */
    public void setImageBitmap(String path){
        if (path!=null){
            try {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(path);
                    sourceBitmap = BitmapFactory.decodeStream(fis);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                Logger.e("图片的宽高："+sourceBitmap.getWidth()+"；"+sourceBitmap.getHeight());
            }catch (Exception e){
                e.printStackTrace();
            }
           initAffine();
        }
    }

    /**
     * 设置图片
     * @param bitmap
     */
    public void setImageBitmap(Bitmap bitmap){
        if (bitmap!=null){
            sourceBitmap=bitmap;
        }
        initAffine();
    }

    private void initAffine(){
        MapFileStatus mapFileStatus=MapFileStatus.getInstance();
        DDRVLNMap.affine_mat affine_mat=mapFileStatus.getAffine_mat();
        r00=affine_mat.getR11();
        r01=affine_mat.getR12();
        t0=affine_mat.getTx();
        r10=affine_mat.getR21();
        r11=affine_mat.getR22();
        t1=affine_mat.getTy();
        currentStatus=DEFAULT_BITMAP;
        invalidate();
    }


    /**
     * 世界坐标——>相对于图片像素坐标
     * @param x
     * @param y
     * @return
     */
    public XyEntity toXorY(float x, float y){
        float x1=(float)( r00*x+r01*y+t0);
        float y1=(float) (r10*x+r11*y+t1);
        return new XyEntity(x1,y1);
    }
    /**
     * 将像素坐标变成（世界坐标）
     * @param x
     * @param y
     * @return
     */
    public XyEntity toPathXy(float x,float y){
        float k= (float) (r00*r11-r10*r01);
        float j= (float) (r10*r01-r00*r11);
        float ax= (float) (r11*x-r01*y+r01*t1-r11*t0);
        float ay= (float) (r10*x-r00*y+r00*t1-r10*t0);
        float sX=txfloat(ax,k);
        float sY=txfloat(ay,j);
        return new XyEntity(sX,sY);
    }

    /**
     * 从世界坐标直接得到相对于画布的坐标
     * @param x
     * @param y
     * @return
     */
    public XyEntity toCanvas(float x,float y){
        XyEntity xyEntity=toXorY(x,y);
        return coordinatesToCanvas(xyEntity.getX(),xyEntity.getY());
    }

    public XyEntity toWorld(float x,float y){

    }

    private float txfloat(float a,float b) {
        DecimalFormat df=new DecimalFormat("0.0000");//设置保留位数
        return Float.parseFloat(df.format((float)a/b));
    }





    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            // 分别获取到ImageView的宽度和高度

        }
    }

    protected void onDraw(Canvas canvas) {
       if (currentStatus==DEFAULT_BITMAP){
           //initBitmap(canvas);
       }else{
           canvas.save();
           canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG| Paint.FILTER_BITMAP_FLAG));
           //canvas.drawBitmap(sourceBitmap, matrix, null);
           canvas.restore();
       }
        PointView.getInstance(context).drawPoint(canvas,this);
        LineView.getInstance(context).drawLine(canvas,this);
        GridLayerView.getInstance(this).drawGrid(canvas);
        RectangleView.getRectangleView().draw(canvas,this);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

}
