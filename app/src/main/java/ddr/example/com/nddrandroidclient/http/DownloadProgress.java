package ddr.example.com.nddrandroidclient.http;

/**
 * desc：Json下载进度实体类
 * time：2020/7/1
 */
public class DownloadProgress {

    /**
     * ProgressName : Idle
     * Progress : 0.0
     */

    private String ProgressName;
    private double Progress;

    public String getProgressName() {
        return ProgressName;
    }

    public void setProgressName(String ProgressName) {
        this.ProgressName = ProgressName;
    }

    public double getProgress() {
        return Progress;
    }

    public void setProgress(double Progress) {
        this.Progress = Progress;
    }
}
