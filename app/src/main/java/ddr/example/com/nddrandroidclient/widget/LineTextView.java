package ddr.example.com.nddrandroidclient.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * time: 2019/10/31
 * desc: 带下划线的TextView
 */
@SuppressLint("AppCompatCustomView")
public class LineTextView extends TextView {
    public LineTextView(Context context) {
        super(context);
    }

    public LineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
