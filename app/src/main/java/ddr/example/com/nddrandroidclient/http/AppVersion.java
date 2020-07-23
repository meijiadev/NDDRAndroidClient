package ddr.example.com.nddrandroidclient.http;

import java.util.List;

public class AppVersion {

    /**
     * latestVersion : 0.6.3
     * versionList : [{"versionName":"0.6.3"}]
     */

    private String latestVersion;
    private List<VersionListBean> versionList;

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public List<VersionListBean> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<VersionListBean> versionList) {
        this.versionList = versionList;
    }

    public static class VersionListBean {
        /**
         * versionName : 0.6.3
         */

        private String versionName;

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }
    }
}
