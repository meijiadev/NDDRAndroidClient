package ddr.example.com.nddrandroidclient.widget.zoomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.DecimalFormat;


import DDRVLNMapProto.DDRVLNMap;
import androidx.annotation.Nullable;
import ddr.example.com.nddrandroidclient.entity.info.MapFileStatus;
import ddr.example.com.nddrandroidclient.entity.info.NotifyBaseStatusEx;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;
import ddr.example.com.nddrandroidclient.widget.view.GridLayerView;
import ddr.example.com.nddrandroidclient.widget.view.LineView;
import ddr.example.com.nddrandroidclient.widget.view.PointView;
import ddr.example.com.nddrandroidclient.widget.view.RectangleView;

/**
 * desc :  通过Matrix实现可缩放、平移、旋转图片的控件
 * time ： 2020/04/29
 * author：EZReal.mei
 */
public class ZoomImageView extends View {
    private Context context;
    // 绘制的图片
    private Bitmap sourceBitmap;
    private int width;//ZoomImageView控件的宽度
    private int height;//ZoomImageView控件的高度
    private Paint pointPaint;
    private NotifyBaseStatusEx notifyBaseStatusEx;
    public double r00=0;
    public double r01=-61.5959;
    public double t0=375.501;
    public double r10=-61.6269;
    public double r11=0;
    public double t1=410.973;
    private TouchEvenHandler touchEvenHandler;
    private float scale=1f;   //当前地图的缩放值
    private Mat sourceMat;

    public ZoomImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        this.context=context;
        sourceMat=new Mat();
        notifyBaseStatusEx=NotifyBaseStatusEx.getInstance();
        pointPaint=new Paint();
        pointPaint.setColor(Color.RED);
    }

    /**
     * 设置图片
     * @param path
     */
    public void setImageBitmap(String path){
        if (fileIsExits(path)){
            try {
                try {
                    Mat mat1;
                    mat1= Imgcodecs.imread(path,Imgcodecs.IMREAD_UNCHANGED);
                    Imgproc.cvtColor(mat1,sourceMat,Imgproc.COLOR_BGR2RGB);
                    sourceBitmap=Bitmap.createBitmap(sourceMat.width(),sourceMat.height(),Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(sourceMat,sourceBitmap);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                Logger.e("图片的宽高："+sourceBitmap.getWidth()+"；"+sourceBitmap.getHeight());
                initAffine();
            }catch (UnsatisfiedLinkError e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    private boolean fileIsExits(String path){
        try {
            File file=new File(path);
            if (!file.exists()){
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * 获取图片的Mat数据
     * @return
     */
    public Mat getSourceMat() {
        return sourceMat;
    }

    /**
     * 获取图片的Bitmap
     * @return
     */
    public Bitmap getSourceBitmap() {
        return sourceBitmap;
    }

    /**
     * 获取图片缩放值
     * @return
     */
    public float getScale() {
        scale=(float) touchEvenHandler.getZoomX();
        return scale;
    }

    /**
     * 获取图片旋转的度数
     * @return
     */
    public float getDegrees(){
        return (float) touchEvenHandler.getAngle();
    }

    /**
     * 设置图片
     * @param bitmap
     */
    public void setImageBitmap(Bitmap bitmap){
        if (bitmap!=null){
            sourceBitmap=bitmap;
        }
        //initAffine();
    }

    public void setSourceMat(Mat mat){
        sourceBitmap=Bitmap.createBitmap(mat.width(),mat.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat,sourceBitmap);
    }

    /**
     *初始化参数
     */
    private void initAffine(){
        touchEvenHandler=new TouchEvenHandler.Builder()
                .setView(this)
                .setBitmap(sourceBitmap)
                .setAuRefresh(false)
                .build();
        MapFileStatus mapFileStatus=MapFileStatus.getInstance();
        DDRVLNMap.affine_mat affine_mat=mapFileStatus.getAffine_mat();
        r00=affine_mat.getR11();
        r01=affine_mat.getR12();
        t0=affine_mat.getTx();
        r10=affine_mat.getR21();
        r11=affine_mat.getR22();
        t1=affine_mat.getTy();
        invalidate();
    }

    /**
     * 设置图片是否可以旋转
     * @param canRotate
     */
    public void setCanRotate(boolean canRotate){
        if (touchEvenHandler!=null){
            touchEvenHandler.setCanRotate(canRotate);
            touchEvenHandler.initMatrix();
        }
    }

    /**
     * 重置矩阵
     */
    public void initMatrix(){
        if (touchEvenHandler!=null){
            touchEvenHandler.initMatrix();
        }
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
     * 将相对于图片的像素坐标变成（世界坐标）
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
        //世界坐标转成相对于图片位置的像素坐标
        XyEntity xyEntity=toXorY(x,y);
        //再将相对于图片的位置转成相对于画布的位置
        return touchEvenHandler.coordinatesToCanvas(xyEntity.getX(),xyEntity.getY());
    }

    /**
     * 将相对于画布坐标转成世界坐标
     * @param x
     * @param y
     * @return
     */
    public XyEntity toWorld(float x,float y){
        //先将相对于画布的坐标转成相对于图片的坐标
        XyEntity xyEntity=touchEvenHandler.coordinatesToImage(x,y);
        return toPathXy(xyEntity.getX(),xyEntity.getY());
    }
    /**
     * 获取目标点的坐标
     * @return
     */
    public XyEntity getGaugePoint(){
        float x=width/2;
        float y=height/2;
        return toWorld(x,y);
    }

    /**
     * 获取中心标签处在地图上的点
     * @return 返回的是世界坐标
     */
    public XyEntity getTargetPoint(){
        float x=width/2;
        float y=height/2;
        return toWorld(x,y);
    }

    /**
     * 获取相对于图片的坐标
     * @return
     */
    public XyEntity getPoint(){
        float x=width/2;
        float y=height/2;
        XyEntity xyEntity=touchEvenHandler.coordinatesToImage(x,y);
        return xyEntity;
    }

   /**
     * 设置保留小数点位数的除法
     * @param a
     * @param b
     * @return
     */
    private float txfloat(float a,float b) {
        DecimalFormat df=new DecimalFormat("0.0000");//设置保留位数
        return Float.parseFloat(df.format((float)a/b));
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            // 分别获取到ImageView的宽度和高度
            width=getWidth();
            height=getHeight();
            touchEvenHandler=new TouchEvenHandler.Builder()
                    .setView(this)
                    .setBitmap(sourceBitmap)
                    .setAuRefresh(false)
                    .build();

            Logger.e("布局大小发生改变");
        }
    }

    protected void onDraw(Canvas canvas) {
        try {
            canvas.drawBitmap(sourceBitmap, touchEvenHandler.getMatrix(), null);
        }catch (Exception e){
            e.printStackTrace();
        }
        PointView.getInstance(context).drawPoint(canvas,this);
        LineView.getInstance(context).drawLine(canvas,this);
        GridLayerView.getInstance(this).drawGrid(canvas);
        RectangleView.getRectangleView().draw(canvas,this);
    }

    /**
     * 处理点击事件
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (sourceBitmap!=null){
            if (touchEvenHandler!=null){
                touchEvenHandler.touchEvent(event);
                if (event.getAction()==MotionEvent.ACTION_UP){
                    //当手指抬起时
                    LineView.getInstance(context).onClick(this,event.getX(),event.getY());
                    PointView.getInstance(context).onClick(this,event.getX(),event.getY());
                }
                GridLayerView.getInstance(this).setScalePrecision((float) touchEvenHandler.getZoomX());
            }else {
                touchEvenHandler=new TouchEvenHandler.Builder()
                        .setView(this)
                        .setBitmap(sourceBitmap)
                        .setAuRefresh(false)
                        .build();
            }
        }
        return true;
    }




}
