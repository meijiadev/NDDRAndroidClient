package ddr.example.com.nddrandroidclient.entity.info;

import android.os.Environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.common.GlobalParameter;
import ddr.example.com.nddrandroidclient.entity.point.BaseMode;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.SpaceItem;
import ddr.example.com.nddrandroidclient.entity.point.TargetPoint;
import ddr.example.com.nddrandroidclient.entity.point.TaskMode;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * 用于保存解析后的地图详情信息的列表
 */
public class MapFileStatus {
    public static MapFileStatus mapFileStatus;
    private List<String> mapNames=new ArrayList<>();                    // 服务端返回的地图名字列表
    private String currentBitmapPath,currentMapName;
    private byte[] currentBitmap;
    private List<String> pictureUrls;                 //激光地图的http连接列表
    private DDRVLNMap.reqDDRVLNMapEx reqDDRVLNMapEx;     //获取指定某一地图的相关信息
    private DDRVLNMap.reqDDRVLNMapEx currentMapEx;      //当前地图信息对象
    private List<MapInfo> mapInfos=new ArrayList<>();          //所有地图列表
    private List<DDRVLNMap.targetPtItem> targetPtItems;          // 接收到的目标点列表
    private List<DDRVLNMap.path_line_itemEx> pathLineItemExes;  // 接收到路径列表
    private List<DDRVLNMap.task_itemEx> taskItemExes;          //  接收到任务列表
    private List<DDRVLNMap.space_item> space_items;           //接收到的空间数据
    /*** 查看的地图详情信息*/
    private List<TargetPoint> targetPoints=new ArrayList<>();         // 解析后的目标点数据
    private List<PathLine> pathLines=new ArrayList<>();               //解析后的路径数据
    private List<TaskMode> taskModes=new ArrayList<>();               //解析后的任务数据

    private List<SpaceItem> spaceItems=new ArrayList<>();             //解析出来后的空间信息
    private List<SpaceItem> cSpaceItems=new ArrayList<>();
    private String mapName;                                    //解析出获取到的地图信息的名字
    /**获取当前运行地图的详情信息*/
    private List<TargetPoint> cTargetPoints=new ArrayList<>();         // 解析后的目标点数据
    private List<PathLine> cPathLines=new ArrayList<>();               //解析后的路径数据
    private List<TaskMode> cTaskModes=new ArrayList<>();               //解析后的任务数据

    private List<byte[]> bitmapBytes=new ArrayList<>();               //图片的字节数组

    private DDRVLNMap.affine_mat affine_mat;                          //地图的矩阵

    public int AllCount=0; //定时任务总次数

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
        this.reqDDRVLNMapEx=rspGetDDRVLNMapEx.getData();
        affine_mat=rspGetDDRVLNMapEx.getData().getBasedata().getAffinedata();
        mapName=rspGetDDRVLNMapEx.getData().getBasedata().getName().toStringUtf8();
        targetPtItems = rspGetDDRVLNMapEx.getData().getTargetPtdata().getTargetPtList();
        pathLineItemExes = rspGetDDRVLNMapEx.getData().getPathSet().getPathLineDataList();
        taskItemExes = rspGetDDRVLNMapEx.getData().getTaskSetList();
        space_items=reqDDRVLNMapEx.getSpacedata().getSpaceSetList();
        if (mapName.equals(NotifyBaseStatusEx.getInstance().getCurroute())){
           currentMapEx=reqDDRVLNMapEx;
           // Logger.e("返回信息为当前地图"+mapName+";"+taskItemExes.size());
            cTargetPoints.clear();
            cPathLines.clear();
            cTaskModes.clear();
            cSpaceItems.clear();
            for (int i = 0; i < targetPtItems.size(); i++) {
                TargetPoint targetPoint = new TargetPoint();
                targetPoint.setName(targetPtItems.get(i).getPtName().toStringUtf8());
                targetPoint.setX(targetPtItems.get(i).getPtData().getX());
                targetPoint.setY(targetPtItems.get(i).getPtData().getY());
                targetPoint.setTheta((int) targetPtItems.get(i).getPtData().getTheta());
                cTargetPoints.add(targetPoint);
            }

            for (int i = 0; i < pathLineItemExes.size(); i++) {
                List<PathLine.PathPoint> pathPoints = new ArrayList<>();
                List<DDRVLNMap.path_line_itemEx.path_lint_pt_Item> path_lint_pt_items = pathLineItemExes.get(i).getPointSetList();
                for (int j = 0; j < path_lint_pt_items.size(); j++) {
                    PathLine.PathPoint pathPoint = new PathLine.PathPoint();
                    pathPoint.setName(path_lint_pt_items.get(j).getPtName().toStringUtf8());
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
                pathLine.setPathType(pathLineItemExes.get(i).getTypeValue());
                pathLine.setVelocity(pathLineItemExes.get(i).getVelocity());
                pathLine.setbStartFromSeg0(pathLineItemExes.get(i).getBStartFromSeg0());
                pathLine.setbNoCornerSmoothing(pathLineItemExes.get(i).getBNoCornerSmoothing());
                cPathLines.add(pathLine);
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
                taskMode.setType(taskItemExes.get(i).getType().getNumber());
                taskMode.setTaskState(taskItemExes.get(i).getStateValue());
                AllCount=taskItemExes.get(i).getRunCount();
                cTaskModes.add(taskMode);
            }

            for (int i=0;i<space_items.size();i++){
                SpaceItem spaceItem=new SpaceItem();
                spaceItem.setName(space_items.get(i).getName().toStringUtf8());
                spaceItem.setType(space_items.get(i).getTypeValue());
                float x=space_items.get(i).getCircledata().getCenter().getX();
                float y=space_items.get(i).getCircledata().getCenter().getY();
                float radius=space_items.get(i).getCircledata().getRadius();
                SpaceItem.Circle circle=new SpaceItem.Circle(x,y,radius);
                spaceItem.setCircle(circle);
                spaceItem.setLines(space_items.get(i).getLinedata().getPointsetList());
                spaceItem.setPolygons(space_items.get(i).getPolygondata().getPointsetList());
                cSpaceItems.add(spaceItem);
            }

        }
        targetPoints.clear();
        pathLines.clear();
        taskModes.clear();
        spaceItems.clear();
        for (int i = 0; i < targetPtItems.size(); i++) {
            TargetPoint targetPoint = new TargetPoint();
            targetPoint.setName(targetPtItems.get(i).getPtName().toStringUtf8());
            targetPoint.setX(targetPtItems.get(i).getPtData().getX());
            targetPoint.setY(targetPtItems.get(i).getPtData().getY());
            targetPoint.setTheta((int)targetPtItems.get(i).getPtData().getTheta());
            targetPoints.add(targetPoint);
        }
        for (int i = 0; i < pathLineItemExes.size(); i++) {
            List<PathLine.PathPoint> pathPoints = new ArrayList<>();
            List<DDRVLNMap.path_line_itemEx.path_lint_pt_Item> path_lint_pt_items = pathLineItemExes.get(i).getPointSetList();
            for (int j = 0; j < path_lint_pt_items.size(); j++) {
                PathLine.PathPoint pathPoint = new PathLine.PathPoint();
                pathPoint.setName(path_lint_pt_items.get(j).getPtName().toStringUtf8());
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
            pathLine.setPathType(pathLineItemExes.get(i).getTypeValue());
            pathLine.setVelocity(pathLineItemExes.get(i).getVelocity());
            pathLine.setbStartFromSeg0(pathLineItemExes.get(i).getBStartFromSeg0());
            pathLine.setbNoCornerSmoothing(pathLineItemExes.get(i).getBNoCornerSmoothing());
            pathLines.add(pathLine);
        }
        //Logger.e("-------------任务的数量："+taskItemExes.size());
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
            taskMode.setType(taskItemExes.get(i).getType().getNumber());
            taskMode.setTaskState(taskItemExes.get(i).getStateValue());
            taskModes.add(taskMode);
        }


        for (int i=0;i<space_items.size();i++){
            SpaceItem spaceItem=new SpaceItem();
            spaceItem.setName(space_items.get(i).getName().toStringUtf8());
            spaceItem.setType(space_items.get(i).getTypeValue());
            float x=space_items.get(i).getCircledata().getCenter().getX();
            float y=space_items.get(i).getCircledata().getCenter().getY();
            float radius=space_items.get(i).getCircledata().getRadius();
            SpaceItem.Circle circle=new SpaceItem.Circle(x,y,radius);
            spaceItem.setCircle(circle);
            spaceItem.setLines(space_items.get(i).getLinedata().getPointsetList());
            spaceItem.setPolygons(space_items.get(i).getPolygondata().getPointsetList());
            spaceItems.add(spaceItem);
        }

    }

    public DDRVLNMap.reqDDRVLNMapEx getReqDDRVLNMapEx() {
        return reqDDRVLNMapEx;
    }

    /**
     * 获取当前地图信息的对象
     * @return
     */
    public DDRVLNMap.reqDDRVLNMapEx getCurrentMapEx() {
        return currentMapEx;
    }

    public void setMapInfos(List<MapInfo> mapInfos) {
        for (int i = 0; i < mapInfos.size(); i++) {
            String dirName = mapInfos.get(i).getMapName();
            String pngPath = GlobalParameter.ROBOT_FOLDER + dirName + "/" + "bkPic.png";
            if (pngPath != null) {
                mapInfos.get(i).setBitmap(pngPath);
            } else {
                mapInfos.remove(i);
            }
            if (dirName.equals(NotifyBaseStatusEx.getInstance().getCurroute())) {
                mapInfos.get(i).setUsing(true);
                currentBitmapPath=mapInfos.get(i).getBitmap();
                currentMapName=mapInfos.get(i).getMapName();
                currentBitmap=mapInfos.get(i).getBytes();
                Collections.swap(mapInfos,0,i);
            } else {
                mapInfos.get(i).setUsing(false);
            }
        }
        this.mapInfos = mapInfos;
    }

    /**
     * 获取当前正在使用的地图存储路径
     * @return
     */
    public String getCurrentBitmapPath() {
        return currentBitmapPath;
    }
    /**
     * 获取正在使用中的地图名
     * @return
     */
    public String getCurrentMapName() {
        return currentMapName;
    }

    /**
     * 获取正在使用中的地图字节流
     * @return
     */
    public byte[] getCurrentBitmap() {
        return currentBitmap;
    }

    public List<MapInfo> getMapInfos() {
        return mapInfos;
    }

    public void setBitmapBytes(List<byte[]> bitmapBytes) {
        this.bitmapBytes = bitmapBytes;
    }

    public List<byte[]> getBitmapBytes() {
        return bitmapBytes;
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


    /**
     * 获取空间信息
     * @return
     */
    public List<SpaceItem> getSpaceItems() {
        return spaceItems;
    }

    /**
     * 获取当前地图的空间信息
     * @return
     */
    public List<SpaceItem> getcSpaceItems() {
        return cSpaceItems;
    }

    /**
     * 获取当前地图的目标点
     * @return
     */
    public List<TargetPoint> getcTargetPoints() {
        return cTargetPoints;
    }

    /**
     * 获取当前地图的路径
     * @return
     */
    public List<PathLine> getcPathLines() {
        return cPathLines;
    }

    /**
     * 获取当前地图的任务
     * @return
     */
    public List<TaskMode> getcTaskModes() {
        return cTaskModes;
    }

    public String getMapName() {
        return mapName;
    }

    /**
     * 获取图片信息的变换矩阵
     * @return
     */
    public DDRVLNMap.affine_mat getAffine_mat() {
        return affine_mat;
    }
}
