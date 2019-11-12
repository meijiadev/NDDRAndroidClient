package ddr.example.com.nddrandroidclient.entity.info;

import java.util.ArrayList;
import java.util.List;

import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * 用于保存解析后的地图详情信息的列表
 *
 */
public class MapFileStatus {
    public static MapFileStatus mapFileStatus;
    private List<String> mapNames=new ArrayList<>();                    // 服务端返回的地图名字列表
    private List<String> pictureUrls;                 //激光地图的http连接列表
    private DDRVLNMap.rspGetDDRVLNMapEx rspGetDDRVLNMapEx;     //获取指定某一地图的相关信息
    private List<MapInfo> mapInfos;                             //所有地图列表

    private List<DDRVLNMap.targetPtItem> targetPtItems;          // 接收到的目标点列表
    private List<DDRVLNMap.path_line_itemEx> pathLineItemExes;  // 接收到路径列表
    private List<DDRVLNMap.task_itemEx> taskItemExes;          //  接收到任务列表
    private List<TargetPoint> targetPoints=new ArrayList<>();         // 解析后的目标点数据
    private List<PathLine> pathLines=new ArrayList<>();               //解析后的路径数据
    private List<TaskMode> taskModes=new ArrayList<>();               //解析后的任务数据
    private String mapName;                                    //解析出获取到的地图信息的名字

    /**
     * 单例模式，用于保存地图相关信息
     * @return
     */
    public static MapFileStatus getInstance(){
        if (mapFileStatus==null){
            synchronized (MapFileStatus.class){
                if (mapFileStatus==null){
                    mapFileStatus=new MapFileStatus();
                }
            }
        }
        return mapFileStatus;
    }

    public List<String> getMapNames() {
       // Logger.e("------:"+mapNames.size());
        return mapNames;
    }

    public void setMapNames(List<String> mapNames) {
        this.mapNames = mapNames;
    }

    public List<String> getPictureUrls() {
        return pictureUrls;
    }

    public void setPictureUrls(List<String> pictureUrls) {
        this.pictureUrls = pictureUrls;
       // Logger.e("连接数量："+pictureUrls.size());
    }

    /**
     * 保存地图详情的返回值，并解析
     * @param rspGetDDRVLNMapEx
     */
    public void setRspGetDDRVLNMap(DDRVLNMap.rspGetDDRVLNMapEx rspGetDDRVLNMapEx) {
        this.rspGetDDRVLNMapEx = rspGetDDRVLNMapEx;
        targetPoints.clear();
        pathLines.clear();
        taskModes.clear();
        mapName=rspGetDDRVLNMapEx.getData().getBasedata().getName().toStringUtf8();
        targetPtItems = rspGetDDRVLNMapEx.getData().getTargetPtdata().getTargetPtList();
        pathLineItemExes = rspGetDDRVLNMapEx.getData().getPathSet().getPathLineDataList();
        taskItemExes = rspGetDDRVLNMapEx.getData().getTaskSetList();
        for (int i = 0; i < targetPtItems.size(); i++) {
            TargetPoint targetPoint = new TargetPoint();
            targetPoint.setName(targetPtItems.get(i).getPtName().toStringUtf8());
            targetPoint.setX(targetPtItems.get(i).getPtData().getX());
            targetPoint.setY(targetPtItems.get(i).getPtData().getY());
            targetPoint.setTheta(targetPtItems.get(i).getPtData().getTheta());
            targetPoints.add(targetPoint);
        }
        for (int i = 0; i < pathLineItemExes.size(); i++) {
            List<PathLine.PathPoint> pathPoints = new ArrayList<>();
            List<DDRVLNMap.path_line_itemEx.path_lint_pt_Item> path_lint_pt_items = pathLineItemExes.get(i).getPointSetList();
            for (int j = 0; j < path_lint_pt_items.size(); j++) {
                PathLine.PathPoint pathPoint = new PathLine().new PathPoint();
                pathPoint.setName("路径点-" + j);
                pathPoint.setX(path_lint_pt_items.get(j).getPt().getX());
                pathPoint.setY(path_lint_pt_items.get(j).getPt().getY());
                pathPoint.setPointType(path_lint_pt_items.get(j).getTypeValue());
                pathPoint.setRotationAngle(path_lint_pt_items.get(j).getRotationangle());
                pathPoints.add(pathPoint);
            }
            PathLine pathLine = new PathLine();
            pathLine.setName(pathLineItemExes.get(i).getName().toStringUtf8());
            pathLine.setPathPoints(pathPoints);
            pathLine.setPathModel(pathLineItemExes.get(i).getModeValue());
            pathLine.setVelocity(pathLineItemExes.get(i).getVelocity());
            pathLines.add(pathLine);
        }
        for (int i = 0; i < taskItemExes.size(); i++) {
            List<DDRVLNMap.path_elementEx> path_elementExes = taskItemExes.get(i).getPathSetList();
            List<BaseMode> baseModes = new ArrayList<>();
            for (int j = 0; j < path_elementExes.size(); j++) {
                if (path_elementExes.get(j).getTypeValue() == 1) {
                    PathLine pathLine = new PathLine(1);
                    pathLine.setName(path_elementExes.get(j).getName().toStringUtf8());
                    baseModes.add(pathLine);
                } else if (path_elementExes.get(j).getTypeValue() == 2) {
                    TargetPoint targetPoint = new TargetPoint(2);
                    targetPoint.setName(path_elementExes.get(j).getName().toStringUtf8());
                    baseModes.add(targetPoint);
                }
            }
            TaskMode taskMode = new TaskMode();
            taskMode.setName(taskItemExes.get(i).getName().toStringUtf8());
            taskMode.setBaseModes(baseModes);
            taskMode.setRunCounts(taskItemExes.get(i).getRunCount());
            taskMode.setStartHour(taskItemExes.get(i).getTimeSet().getStartHour());
            taskMode.setStartMin(taskItemExes.get(i).getTimeSet().getStartMin());
            taskMode.setEndHour(taskItemExes.get(i).getTimeSet().getEndHour());
            taskMode.setEndMin(taskItemExes.get(i).getTimeSet().getEndMin());
            taskMode.setType(taskItemExes.get(i).getType());
            taskMode.setTaskState(taskItemExes.get(i).getStateValue());
            taskModes.add(taskMode);

        }
        Logger.e("----------:" + targetPoints.size());

    }

    public DDRVLNMap.rspGetDDRVLNMapEx getRspGetDDRVLNMapEx() {
        return rspGetDDRVLNMapEx;
    }

    public void setMapInfos(List<MapInfo> mapInfos) {
        this.mapInfos = mapInfos;
    }

    public List<MapInfo> getMapInfos() {
        return mapInfos;
    }

    /**
     * 获取解析后的目标点列表
     * @return
     */
    public List<TargetPoint> getTargetPoints() {
        return targetPoints;
    }

    /**
     * 获取解析后的路径列表
     * @return
     */
    public List<PathLine> getPathLines() {
        return pathLines;
    }

    /**
     * 获取解析后的任务列表
     * @return
     */
    public List<TaskMode> getTaskModes() {
        return taskModes;
    }
}
