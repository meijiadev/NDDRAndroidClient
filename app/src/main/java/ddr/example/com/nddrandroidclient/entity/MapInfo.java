package ddr.example.com.nddrandroidclient.entity;

import java.util.List;

import DDRCommProto.BaseCmd;

/**
 * 地图信息
 */
public class MapInfo {
    private String mapName;
    private String bitmap;
    private String time;
    private int width;
    private int height;
    private String author;
    private List<BaseCmd.rspClientGetMapInfo.MapInfoItem.TaskItem> taskItemList;
    private boolean isShowSelect=false;    //是否显示选中的按钮
    private boolean isUsing=false;         //是否在使用中，同时只有一张图能被使用



    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public String  getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setTaskItemList(List<BaseCmd.rspClientGetMapInfo.MapInfoItem.TaskItem> taskItemList) {
        this.taskItemList = taskItemList;
    }

    public List<BaseCmd.rspClientGetMapInfo.MapInfoItem.TaskItem> getTaskItemList() {
        return taskItemList;
    }

    public void setShowSelect(boolean showSelect) {
        isShowSelect = showSelect;
    }

    public boolean isShowSelect() {
        return isShowSelect;
    }

    public void setUsing(boolean using) {
        isUsing = using;
    }

    public boolean isUsing() {
        return isUsing;
    }
}
