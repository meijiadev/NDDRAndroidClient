package ddr.example.com.nddrandroidclient.helper;

import java.util.Comparator;

import ddr.example.com.nddrandroidclient.entity.MapInfo;

/**
 * time:  2019/10/10
 * desc:  降序排列
 */
public class SortClass implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        MapInfo mapInfo1= (MapInfo) o1;
        MapInfo mapInfo2= (MapInfo) o2;
        int flag=mapInfo2.getTime().compareTo(mapInfo1.getTime());
        return flag;
    }
}
