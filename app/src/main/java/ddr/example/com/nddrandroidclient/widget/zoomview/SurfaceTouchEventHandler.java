package ddr.example.com.nddrandroidclient.widget.zoomview;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * 处理SurfaceView的点击事件
 */
public class SurfaceTouchEventHandler {
    private static SurfaceTouchEventHandler surfaceTouchEventHandler;
    // Matrix getValues 矩阵参数
    private float[] values=new float[9];
    private static final int MSCALE_X=0;
    private static final int MSKEW_X=1;
    private static final int MTRANS_X=2;
    private static final int MSKEW_Y=3;
    private static final int MSCALE_Y=4;
    private static final int MTRANS_Y=5;
    private static final int MPERSP_0=6;
    private static final int MPERSP_1=7;
    private static final int MPERSP_2=8;
    // 当前操作的状态
    public int currentStatus;
    public static final int DEFAULT_BITMAP=0;      // 默认状态下
    public static final int SCALE_BITMAP=1;        //  缩放状态下
    public static final int TRANSLATE_BITMAP=2;    //  平移
    public static final int ROTATION_BITMAP=3;    //   旋转
    public static final int NONE_BITMAP=4;        // 不作任何操作
    public static final int SCALE_BITMAP_OUT=5;   //放大
    public static final int SCALE_BITMAP_IN=6;    // 缩小
    private float x_down = 0;
    private float y_down = 0;
    private PointF mid = new PointF();
    private float oldDist = 1f;              //  平移的距离
    private float oldRotation = 0;          //   手指第一次放上去的角度
    private float rotation;                //    正在旋转中变化的角度
    private Matrix matrix = new Matrix();
    private Matrix matrix1 = new Matrix();
    private Matrix originalMatrix=new Matrix();        //地图最初显示时的矩阵
    //用于保存matrix
    private Matrix savedMatrix = new Matrix();
    // 检测是否出界
    boolean matrixCheck = false;
    // 控件的大小
    private int widthScreen;
    private int heightScreen;
    private boolean canRotate=true;             // 默认可旋转
    private Bitmap sourceBitmap;



    public static SurfaceTouchEventHandler getInstance(int widthScreen,int heightScreen){
        if (surfaceTouchEventHandler==null){
            synchronized (SurfaceTouchEventHandler.class){
                if (surfaceTouchEventHandler==null){
                    surfaceTouchEventHandler=new SurfaceTouchEventHandler(widthScreen,heightScreen);
                }
            }
        }
        return surfaceTouchEventHandler;
    }

    private SurfaceTouchEventHandler(int widthScreen,int heightScreen) {
        Logger.e("初始化TouchEvenHandler");
        this.widthScreen=widthScreen;
        this.heightScreen=heightScreen;
    }


    public void setDefaultBitmap(Bitmap srcBitmap){
        if (srcBitmap!=null){
            int bitmapWidth=srcBitmap.getWidth();
            int bitmapHeight=srcBitmap.getHeight();
            if (sourceBitmap==null||(sourceBitmap.getWidth()!=bitmapWidth||sourceBitmap.getHeight()!=bitmapHeight)){
                this.sourceBitmap=srcBitmap;
                matrix.reset();
                savedMatrix.set(matrix);
                matrix1.set(savedMatrix);
                float translateX=(widthScreen-bitmapWidth)/2f;
                float translateY=(heightScreen-bitmapHeight)/2f;
                matrix1.postTranslate(translateX,translateY);
                matrix.set(matrix1);
                matrix.getValues(values);
                originalMatrix.set(matrix);
            }
        }
    }
    /**
     * 获取最终用于绘制的矩阵变量
     * @return
     */
    public Matrix getMatrix(){
        return matrix;
    }

    /**
     * 选择是否可以旋转
     * @param canRotate
     */
    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
        Logger.e("是否可以旋转"+canRotate);
    }

    /**
     * 将矩阵还原最初
     */
    public void initMatrix(){
        matrix.set(originalMatrix);
    }

    /**
     * 获取图片缩放比例
     * @return
     */
    public double getZoomX(){
        return values[MSCALE_X]/getCosA();
    }


    public double getZoomY(){
        return values[MSCALE_Y]/getCosA();
    }

    /**
     * 获取图片左上角在画布中的坐标
     * @return X
     */
    public float getTranslateX(){
        return values[MTRANS_X];
    }

    /**
     * 获取图片左上角在画布中的坐标
     * @return Y
     */
    public float getTranslateY(){
        return values[MTRANS_Y];
    }

    /**
     * 返回图片旋转的弧度
     * @return
     */
    public double getRadians(){
        double radians= Math.atan2(values[Matrix.MSKEW_Y], values[Matrix.MSCALE_Y]);
        return radians;
    }

    /**
     * 返回图片旋转的角度
     * @return
     */
    public double getAngle(){
        double radians= Math.atan2(values[Matrix.MSKEW_Y], values[Matrix.MSCALE_Y]);
        return Math.toDegrees(radians);
    }

    /**
     * 返回图片弧度的余弦值
     * @return
     */
    public double getCosA(){
        return Math.cos(getRadians());
    }

    /**
     * 返回图片旋转弧度的正弦值
     * @return
     */
    public double getSinA(){
        return Math.sin(getRadians());
    }

    /**
     * 将values数组变成字符串输出
     * @returnhai
     */
    public String getValuesToString(){
        return Arrays.toString(values);
    }

    /**
     * 已知原始相对于图片的坐标，计算图片平移缩放或绕某点旋转之后相对于整个画布的坐标
     * @return 画布坐标
     */
    public XyEntity coordinatesToCanvas(float x, float y){
        matrix.getValues(values);
        double radians= Math.atan2(values[Matrix.MSKEW_Y], values[Matrix.MSCALE_Y]);
        double cosA= Math.cos(radians);
        double sinA= Math.sin(radians);
        double cx=x*getZoomX();
        double cy=y*getZoomY();
        double x1=cx*cosA-cy*sinA;
        double y1=cx*sinA+cy*cosA;
        double x2=getTranslateX()+x1;
        double y2=getTranslateY()+y1;
        return new XyEntity((float) x2,(float) y2);
    }

    /**
     * 处理点击触摸事件
     * @param event
     */
    public void touchEvent(@NotNull MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                currentStatus = TRANSLATE_BITMAP;
                x_down = event.getX();
                y_down = event.getY();
                savedMatrix.set(matrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                currentStatus = SCALE_BITMAP;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentStatus == SCALE_BITMAP) {
                    matrix1.set(savedMatrix);
                    rotation = rotation(event) - oldRotation;
                    float newDist = spacing(event);
                    if (newDist>oldDist){
                        currentStatus=SCALE_BITMAP_OUT;
                    }else {
                        currentStatus=SCALE_BITMAP_IN;
                    }
                    float scale = newDist / oldDist;
                    matrix1.postScale(scale, scale, mid.x, mid.y);// 缩放
                    if (canRotate){
                        matrix1.postRotate(rotation, mid.x, mid.y);// 旋转
                    }
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        currentStatus=SCALE_BITMAP;
                        matrix1.getValues(values);
                        matrix.set(matrix1);
                    }
                } else if (currentStatus == TRANSLATE_BITMAP) {
                    matrix1.set(savedMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);// 平移
                    matrixCheck = matrixCheck();
                    if (matrixCheck == false) {
                        currentStatus=TRANSLATE_BITMAP;
                        matrix1.getValues(values);
                        matrix.set(matrix1);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                currentStatus = NONE_BITMAP;
                break;
        }

    }

    /**
     * 边界检测
     * @return
     */
    private boolean matrixCheck() {
        float[] f = new float[9];
        matrix1.getValues(f);
        // 图片4个顶点的坐标
        float x1 = f[0] * 0 + f[1] * 0 + f[2];
        float y1 = f[3] * 0 + f[4] * 0 + f[5];
        float x2 = f[0] * widthScreen+ f[1] * 0 + f[2];
        float y2 = f[3] * widthScreen + f[4] * 0 + f[5];
        float x3 = f[0] * 0 + f[1] * heightScreen + f[2];
        float y3 = f[3] * 0 + f[4] * heightScreen+ f[5];
        float x4 = f[0] * widthScreen + f[1] * heightScreen + f[2];
        float y4 = f[3] * widthScreen + f[4] * heightScreen + f[5];
        // 图片现宽度
        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        // 缩放比率判断
        if (width < widthScreen/3|| width > widthScreen * 6) {
            return true;
        }
        // 出界判断
        if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
                && x3 < widthScreen / 3 && x4 < widthScreen / 3)
                || (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
                && x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
                || (y1 < heightScreen / 3 && y2 < heightScreen / 3
                && y3 < heightScreen / 3 && y4 < heightScreen / 3)
                || (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
                && y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
            return true;
        }
        return false;
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        float x = Math.abs(event.getX(0) - event.getX(1));
        float y = Math.abs(event.getY(0) - event.getY(1));
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 取旋转角度
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        // Logger.e("取弧度："+radians);
        return (float) Math.toDegrees(radians);
    }


    /**
     * 重置销毁当前类
     */
    public void onDestroy(){
        surfaceTouchEventHandler=null;
        sourceBitmap=null;
        matrix.reset();
    }

}
