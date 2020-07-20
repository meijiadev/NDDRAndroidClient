package ddr.example.com.nddrandroidclient.http;

import ddr.example.com.nddrandroidclient.http.serverupdate.DownloadProgress;
import ddr.example.com.nddrandroidclient.http.serverupdate.UpdateState;
import ddr.example.com.nddrandroidclient.http.serverupdate.VersionInformation;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Streaming;

import static ddr.example.com.nddrandroidclient.http.Api.APP_UPDATE_DOMAIN_NAME;
import static ddr.example.com.nddrandroidclient.http.Api.SERVER_UPDATE_DOMAIN_NAME;
import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

/**
 * desc:请求接口
 * time:2020/7/1
 */

public interface HttpServer {
    //请求服务器版本信息
    @Headers({DOMAIN_NAME_HEADER + SERVER_UPDATE_DOMAIN_NAME})
    @GET("ver")
    Observable<VersionInformation>getVersion();

    @Headers({DOMAIN_NAME_HEADER + SERVER_UPDATE_DOMAIN_NAME})
    @GET("upgrade")
    Observable<UpdateState> updateServer();

    @Headers({DOMAIN_NAME_HEADER + SERVER_UPDATE_DOMAIN_NAME})
    @GET("progress")
    Observable<DownloadProgress> getProgress();


    //下载apk
    @Headers({DOMAIN_NAME_HEADER+APP_UPDATE_DOMAIN_NAME})
    @Streaming
    @GET("links/4636")
    Observable<ResponseBody>downloadApk();




}
