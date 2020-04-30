package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.other.DpOrPxUtils;
import ddr.example.com.nddrandroidclient.other.Logger;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;


/**
 *自定义圆形进度条
 */
public class CircleBarView extends View {
    private RectF mRectF;//绘制圆弧的矩形区域
    private Paint bgPaint;//绘制背景圆弧的画笔
    private Paint progressPaint;//绘制圆弧的画笔
    private Paint textPaint;
    private int progressNum;  //可以更新的进度条数值
    private float MaxNum;       //进度条最大值

    private float progressSweepAngle;//进度条圆弧扫过的角度
    private float startAngle;  //背景圆弧的起始角度
    private float startProgressAngle;
    private float sweepAngle;   //背景圆弧扫过的角度

    private float barWidth;    // 圆弧进度条宽度
    private int defaultSize;   //自定义的View默认的宽高

    private int progressColor=Color.GREEN;  //设置进度条的颜色


    private CircleBarAnim circleBarAnim;

    private Bitmap centerBg;
    private Paint centerPaint;



    public CircleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.CircleView);
        Drawable imageBackground=typedArray.getDrawable(R.styleable.CircleView_centerBackground);
        if (null!=imageBackground){
            if (imageBackground instanceof BitmapDrawable){
                centerBg=((BitmapDrawable) imageBackground).getBitmap();
            }
        }else {
            //如何没有设置背景图片
        }
        defaultSize = DpOrPxUtils.dip2px(context,134);
        barWidth = DpOrPxUtils.dip2px(context,6);
        mRectF = new RectF();

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);//只描边，不填充
        progressPaint.setAntiAlias(true);//设置抗锯齿
        progressPaint.setStrokeJoin(Paint.Join.ROUND);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(barWidth);      //设置一个画笔的宽度
        circleBarAnim=new CircleBarAnim();

        textPaint=new Paint();
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(defaultSize/4);
        textPaint.setColor(Color.WHITE);
        bgPaint=new Paint();
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setColor(Color.parseColor("#272829"));
        bgPaint.setAntiAlias(true);
        bgPaint.setStrokeWidth(barWidth);

        centerPaint=new Paint();
        centerPaint.setAntiAlias(true);


        progressNum=0;
        MaxNum=100;
        startAngle=0;
        startProgressAngle=90;
        sweepAngle=360;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //RectF rectF = new RectF(x,y,x+300,y+300);//建一个大小为300 * 300的正方形区域
        int measuredWidth = getMeasuredWidth();          //获取View原始大小
        int measuredHeight = getMeasuredHeight();
        progressPaint.setColor(progressColor);
        canvas.drawArc(mRectF,startAngle,sweepAngle,false,bgPaint);//这里角度0对应的是三点钟方向，顺时针方向递增   背景圆弧
        canvas.drawArc(mRectF,startProgressAngle,progressSweepAngle,false,progressPaint);      //进度圆弧
        canvas.drawText(String.valueOf(progressNum)+"%",measuredWidth/2,measuredHeight*2/3 ,textPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height=measureSize(defaultSize,heightMeasureSpec);

        int width=measureSize(defaultSize,widthMeasureSpec);
        int min=Math.min(width,height);    //获取View最短的边
        setMeasuredDimension(min,min);

        if(min >= barWidth*2){//这里简单限制了圆弧的最大宽度
            mRectF.set(barWidth/2,barWidth/2,min-barWidth/2,min-barWidth/2);
        }
    }

    private int measureSize(int defaultSize,int measureSpec) {
        int result = defaultSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }

    public class CircleBarAnim extends Animation{

        public CircleBarAnim(){

        }
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            progressSweepAngle=interpolatedTime*sweepAngle*progressNum/MaxNum;
            postInvalidate();
        }
    }
    

    public void setProgress(float progressNum,int time,int progressColor){
        circleBarAnim.setDuration(time);
        this.startAnimation(circleBarAnim);
        this.progressNum=(int)progressNum;
        this.progressColor=progressColor;
    }

    public void setProgress(float progressNum,int progressColor){
        this.progressNum=(int)progressNum;
        this.progressColor=progressColor;
        postInvalidate();
    }


}

