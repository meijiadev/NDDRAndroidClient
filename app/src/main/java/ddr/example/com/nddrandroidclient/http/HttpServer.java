package ddr.example.com.nddrandroidclient.http;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * desc:请求接口
 * time:2020/7/1
 */
public interface HttpServer {
    @GET("ver")
    Observable<VersionInformation>getVersion();

    @GET("upgrade")
    Observable<String> updateServer();

    @GET("progress")
    Observable<DownloadProgress> getProgress();
}
