package ddr.example.com.nddrandroidclient.widget.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ddr.example.com.nddrandroidclient.R;

/**
 * time: 2019/10/31
 * desc: 能改变状态字体颜色并带下划线的TextView
 */
@SuppressLint("AppCompatCustomView")
public class LineTextView extends TextView {
    private int measureWidth, measureHeight;    //控件的宽高
    private boolean isSelected=false;
    private boolean isStitle=false;
    private Paint linePaint;

    public LineTextView(Context context) {
        super(context);
    }

    public LineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        linePaint=new Paint();
        linePaint.setColor(Color.parseColor("#0399ff"));
        linePaint.setStrokeWidth(5);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected){
            if (isStitle){
                canvas.drawLine(measureWidth-5,0,measureWidth-5,measureHeight,linePaint);
            }else {
                canvas.drawLine(0,measureHeight-5,measureWidth,measureHeight-5,linePaint);
            }
            setTextColor(Color.parseColor("#0399ff"));
        }else {
            if (isStitle){
                setTextColor(Color.parseColor("#99ffffff"));
            }else {
                setTextColor(Color.WHITE);
            }

        }
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content

        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {

        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    public void isChecked(boolean isSelected){
        this.isSelected=isSelected;
        invalidate();
    }
    public void isStitle(boolean isStitle){
        this.isStitle=isStitle;
        invalidate();
    }

}
