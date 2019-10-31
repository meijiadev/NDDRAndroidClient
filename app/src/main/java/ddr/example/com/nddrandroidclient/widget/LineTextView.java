package ddr.example.com.nddrandroidclient.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * time: 2019/10/31
 * desc: 能改变状态字体颜色并带下划线的TextView
 */
@SuppressLint("AppCompatCustomView")
public class LineTextView extends TextView {
    private int measureWidth, measureHeight;    //控件的宽高
    private boolean isSelected=false;
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
            canvas.drawLine(0,measureHeight-5,measureWidth,measureHeight-5,linePaint);
        }
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

}
