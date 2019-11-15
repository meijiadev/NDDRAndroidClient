package ddr.example.com.nddrandroidclient.widget.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class GridImageView extends ImageView {
    private boolean isSelected=false;
    public GridImageView(Context context) {
        super(context);
    }

    public GridImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSelected(boolean isSelected){
        this.isSelected=isSelected;
    }

    public boolean getSelected(){
        return isSelected;
    }

}
