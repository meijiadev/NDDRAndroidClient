package ddr.example.com.nddrandroidclient.socket.hwSocker;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ddr.example.com.nddrandroidclient.entity.other.HwBuild;
import ddr.example.com.nddrandroidclient.entity.other.HwFloor;
import ddr.example.com.nddrandroidclient.entity.other.HwParkID;
import ddr.example.com.nddrandroidclient.other.Logger;

public class Utilityhw {
    /**
     * 解析返回的build建筑数据
     */
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONObject all=new JSONObject(response);
                JSONArray allArray =all.getJSONArray("result");


                HwBuild hwBuild=HwBuild.getInstance();
                HwFloor hwFloor=HwFloor.getInstance();
                HwParkID hwParkID=HwParkID.getInstance();
                List<HwBuild> hwBuildList=new ArrayList<>();
                List<HwFloor> hwFloorList=new ArrayList<>();
                List<HwParkID> hwParkIDList=new ArrayList<>();
                for (int a=0;a<allArray.length();a++){
                    JSONArray parkJson=allArray.getJSONObject(a).getJSONArray("parkInfoList");
                    for (int b=0;b<parkJson.length();b++){
                        JSONObject parkObject = parkJson.getJSONObject(b);
                        HwParkID hwParkID1=new HwParkID();
                        hwParkID1.setId(parkObject.getString("parkCode"));
                        hwParkID1.setName(parkObject.getString("parkName"));
                        String parkId=parkObject.getString("parkCode");
                        hwParkIDList.add(hwParkID1);
                        JSONArray buildProvince=parkJson.getJSONObject(b).getJSONArray("buildingList");
                        for (int j=0;j<buildProvince.length();j++){
                            JSONObject buildObject = buildProvince.getJSONObject(j);
                            HwBuild hwBuild1=new HwBuild();
                            hwBuild1.setId(buildObject.getString("buildingID"));
                            hwBuild1.setName(buildObject.getString("name"));
                            hwBuild1.setParkId(parkId);
                            String buildId =buildObject.getString("buildingID");
                            hwBuildList.add(hwBuild1);
                            Logger.e("地址"+hwBuild1.getName());
                            JSONArray floorArray =buildObject.getJSONArray("floorList");
                            for (int i=0;i<floorArray.length();i++){
                                JSONObject floorObject = floorArray.getJSONObject(i);
                                HwFloor hwFloor1=new HwFloor();
                                hwFloor1.setId(floorObject.getString("floorID"));
                                hwFloor1.setName(floorObject.getString("name"));
                                hwFloor1.setBuildId(buildId);
                                hwFloorList.add(hwFloor1);
                                Logger.e("地址"+hwFloor1.getName()+"建筑ID"+buildId);
                            }
                            hwFloor.setHwFloors(hwFloorList);
                        }
                        hwBuild.setHwBuilds(hwBuildList);
                    }
                }
                hwParkID.setHwBuilds(hwParkIDList);
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
}
