package ddr.example.com.nddrandroidclient.http.serverupdate;

import java.util.List;

/**
 * desc : Json数据实体类
 * time :2020/7/1
 */
public class VersionInformation {

    /**
     *  baseVersion 基础版本 latestVersion最新版本  vers 所有版本列表   currentVersion 当前版本
     * baseVersion : Base
     * latestVersion : 0.5.6_Beta
     * vers : ["Base","V0.1","0.3.1_Beta","0.3.2_Beta","0.3.3_Beta","0.3.4_Beta","0.3.5_Beta","0.3.6_Beta","0.3.7_Beta","0.3.8_Beta","0.3.9_Beta","0.4.0_Release","0.4.1_Beta","0.4.1_Release","0.4.2_Release","0.5.0_Beta","0.5.2_Release","0.5.3_Beta","0.5.3_Release","0.5.4_Beta","0.5.5_Beta","0.5.6_Beta"]
     * currentVersion : 0.5.6_Beta
     */

    private String baseVersion;
    private String latestVersion;
    private String currentVersion;
    private List<String> vers;
    private String state;

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public List<String> getVers() {
        return vers;
    }

    public void setVers(List<String> vers) {
        this.vers = vers;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
