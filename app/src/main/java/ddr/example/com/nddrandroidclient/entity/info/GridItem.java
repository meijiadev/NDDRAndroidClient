package ddr.example.com.nddrandroidclient.entity.info;

import org.opencv.core.Point;

import androidx.annotation.Nullable;

/**
 * desc:像素块的索引,用于作为Map集合的Key
 */
public class GridItem {
    private int x;
    private int y;

    public GridItem(int x,int y){
        this.x=x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this==obj){ return true; }
        if (!(obj instanceof GridItem)){ return false; }
        GridItem gridItem= (GridItem) obj;
        return x==gridItem.x&&y==gridItem.y;
    }

    @Override
    public int hashCode() {
        return x^y*137;
    }
}
