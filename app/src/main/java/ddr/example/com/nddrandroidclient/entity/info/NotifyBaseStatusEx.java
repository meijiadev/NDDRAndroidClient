package ddr.example.com.nddrandroidclient.entity.info;

import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * 接收基础信息
 */
public class NotifyBaseStatusEx {
    public static NotifyBaseStatusEx notifyBaseStatusEx;

    public static NotifyBaseStatusEx getInstance(){
        if (notifyBaseStatusEx==null){
            synchronized (NotifyBaseStatusEx.class){
                if (notifyBaseStatusEx==null){
                    notifyBaseStatusEx=new NotifyBaseStatusEx();
                }
            }
        }
        return notifyBaseStatusEx;
    }

    private int mode;
    private int sonMode;
    private int eDynamicOAStatus;
    private int stopStat;
    private String curroute="";
    private String currpath="";
    private float posX;
    private float posY;
    private float posDirection;
    private float posLinespeed;
    private float currspeed;
    private int eSelfCalibStatus;    // 充电子状态
    private boolean chargingStatus;    // true - 充电中
    private int taskCount;           // 任务次数
    private int taskDuration;         //任务时长，单位秒
    private boolean isHaveLocation=true;  //是否有定位  假设有定位（其实并不一定有）
    private boolean isFinishCollect;
    private int eTaskMode;
    /**
     * 异常状态  1：内存错误 ， 2：试图采集的地图名已存在 ，3：采集模式下文件IO错误，4：自动模式选择的地图不存在，5：自动模式重定位失败，
     *          6：自动模式下文件IO错误，7：自动模式下路径规划模块异常 , 8:自动模式下避障模块异常
     */
    private int exceptionValue;
    private boolean isLocationed;      //当前是否有定位


    public boolean isChargingStatus() {
        return chargingStatus;
    }

    public int geteDynamicOAStatus() {
        return eDynamicOAStatus;
    }

    public void seteDynamicOAStatus(int eDynamicOAStatus) {
        this.eDynamicOAStatus = eDynamicOAStatus;
    }
    public int geteSelfCalibStatus() {
        return eSelfCalibStatus;
    }

    public void seteSelfCalibStatus(int eSelfCalibStatus) {
        this.eSelfCalibStatus = eSelfCalibStatus;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getTaskDuration() {
        return taskDuration;
    }

    public void setTaskDuration(int taskDuration) {
        this.taskDuration = taskDuration;
    }

    private float posAngulauspeed;

    public int geteTaskMode() {
        return eTaskMode;
    }

    public void seteTaskMode(int eTaskMode) {
        this.eTaskMode = eTaskMode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getSonMode() {
        return sonMode;
    }

    public void setSonMode(int sonMode) {
        this.sonMode = sonMode;
        if (sonMode==8){
            setFinishCollect(true);
        }
    }

    public int getStopStat() {
        return stopStat;
    }

    public void setStopStat(int stopStat) {
        this.stopStat = stopStat;
    }

    public String getCurroute() {
        return curroute;
    }

    public void setCurroute(String curroute) {
        this.curroute = curroute;
    }

    public String getCurrpath() {
        return currpath;
    }

    public void setCurrpath(String currpath) {
        this.currpath = currpath;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getPosDirection() {
        return posDirection;
    }

    public void setPosDirection(float posDirection) {
        this.posDirection = posDirection;
    }

    public float getPosLinespeed() {
        return posLinespeed;
    }

    public void setPosLinespeed(float posLinespeed) {
        this.posLinespeed = posLinespeed;
    }

    public float getPosAngulauspeed() {
        return posAngulauspeed;
    }

    public float getCurrspeed(){return currspeed;}

    public void setCurrspeed(float currspeed){ this.currspeed=currspeed;}

    public void setPosAngulauspeed(float posAngulauspeed) {
        this.posAngulauspeed = posAngulauspeed;
    }

    public void setChargingStatus(boolean chargingStatus) {
        this.chargingStatus = chargingStatus;
    }

    public void setExceptionValue(int exceptionValue) {
        this.exceptionValue = exceptionValue;
        if (exceptionValue==5){
            setHaveLocation(false);
            Logger.e("定位不成功！");
        }
    }

    public int getExceptionValue() {
        return exceptionValue;
    }

    public void setLocationed(boolean locationed) {
        isLocationed = locationed;
    }

    public boolean isLocationed() {
        return isLocationed;
    }

    public void setNull(){
        notifyBaseStatusEx=null;
    }

    public void setHaveLocation(boolean haveLocation) {
        isHaveLocation = haveLocation;
    }

    public boolean isHaveLocation() {
        return isHaveLocation;
    }

    public void setFinishCollect(boolean finishCollect) {
        isFinishCollect = finishCollect;
    }

    public boolean isFinishCollect() {
        return isFinishCollect;
    }
}
