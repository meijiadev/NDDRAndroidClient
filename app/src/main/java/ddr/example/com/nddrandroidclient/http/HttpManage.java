package ddr.example.com.nddrandroidclient.http;

import ddr.example.com.nddrandroidclient.other.Logger;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * desc: http管理类
 * time：2020/7/1
 */
public class HttpManage {
    private static HttpServer httpServer;
    private static String baseUrl="http://192.168.1.87:8081/";            //获取版本信息接口

    public static HttpServer getServer(){
        if (httpServer==null){
            synchronized (HttpManage.class){
                if (httpServer==null){
                    Retrofit retrofit=new Retrofit.Builder()            //创建Retrofit的实例
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())          //请求结果转换成实体类
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())  //适配Rxjava
                            .build();
                    httpServer=retrofit.create(HttpServer.class);                      //获得接口的实例
                }
            }
        }
        return httpServer;
    }

    public static void setBaseUrl(String IP) {
        HttpManage.baseUrl ="http://"+IP+":8081/";
        Logger.e("------baseUrl:"+baseUrl);
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
}
