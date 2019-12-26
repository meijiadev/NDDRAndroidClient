package ddr.example.com.nddrandroidclient.widget.layout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * time ：2019/12/25
 * desc : 机器人重定位（对布局中的内容进行平移、缩放、旋转）
 */
public class ZoomLayout extends RelativeLayout {
    // 属性变量
    private float translationX; // 移动X
    private float translationY; // 移动Y
    private float scale = 1; // 伸缩比例
    private float rotation; // 旋转角度
    private float centerPointX;//记录两指同时放在屏幕上时，中心点的横坐标值
    private float centerPointY;//记录两指同时放在屏幕上时，中心点的纵坐标值
    private float currentBitmapWidth,currentBitmapHeight;   //图片缩放后的图片宽高
    // 移动过程中临时变量
    private float actionX;
    private float actionY;
    private float spacing;
    private float degree;
    private int moveType; // 0=未选择，1=拖动，2=缩放



    private Paint paint;

    public ZoomLayout(Context context) {
        this(context, null);
    }

    public ZoomLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        paint=new Paint();
        paint.setStrokeWidth(8);
        paint.setColor(Color.RED);
    }

    public ZoomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }




    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(ev);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                moveType = 1;
                actionX = event.getRawX();
                actionY = event.getRawY();
                //Log.e("点击处相对于屏幕的坐标：","x:"+actionX+"y:"+actionY);
                //Log.e("点击处坐标：",event.getX()+";"+event.getY());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                moveType = 2;
                spacing = getSpacing(event);
                degree = getDegree(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveType == 1) {
                    translationX = translationX + event.getRawX() - actionX;
                    translationY = translationY + event.getRawY() - actionY;
                    Logger.e("平移的距离-------x:"+translationX+"---y:"+translationY);
                    //Log.e("画布相对于父布局的位置：","x:"+getX()+"--y:"+getY());
                    int w=getChildAt(0).getWidth();
                    int h=getChildAt(0).getHeight();
                    float x=getChildAt(0).getX();
                    float y=getChildAt(0).getY();
                    //Log.e("-------","onDraw"+w+";"+h+"---x:"+x+"----y:"+y);
                    setTranslationX(translationX);
                    setTranslationY(translationY);
                    actionX = event.getRawX();
                    actionY = event.getRawY();
                } else if (moveType == 2) {
                    scale = scale * getSpacing(event) / spacing;
                    centerPointBetweenFingers(event);
                    if (scale>10){
                        scale=10;
                    }
                    if (scale<1){
                        scale=1;
                    }
                    rotation = rotation + getDegree(event) - degree;
                    if (rotation > 360) {
                        rotation = rotation - 360;
                    }
                    if (rotation < -360) {
                        rotation = rotation + 360;
                    }
                    post(new ScaleRunnable(centerPointX,centerPointY,scale,rotation));

                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                moveType = 0;
        }
        return super.onTouchEvent(event);
    }

    // 触碰两点间距离
    private float getSpacing(MotionEvent event) {
        //通过三角函数得到两点间的距离
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    // 取旋转角度
    private float getDegree(MotionEvent event) {
        //得到两个手指间的旋转角度
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 计算两个手指之间中心点的坐标。
     *
     * @param event
     */
    private void centerPointBetweenFingers(MotionEvent event) {
        float xPoint0 = event.getX(0);
        float yPoint0 = event.getY(0);
        float xPoint1 = event.getX(1);
        float yPoint1 = event.getY(1);
        centerPointX = (xPoint0 + xPoint1) / 2;
        centerPointY = (yPoint0 + yPoint1) / 2;
    }

    public class ScaleRunnable implements Runnable{

        private float centerPointX;
        private float centerPointY;
        private float scale;
        private float rotation;

        public ScaleRunnable(float x,float y,float scale,float rotation) {
            this.centerPointX=x;
            this.centerPointY=y;
            this.scale=scale;
            this.rotation=rotation;
        }

        @Override
        public void run() {
            setScaleX(scale);
            setScaleY(scale);
            setRotation(rotation);
        }
    }
}
