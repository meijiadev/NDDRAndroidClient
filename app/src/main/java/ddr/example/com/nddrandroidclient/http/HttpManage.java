package ddr.example.com.nddrandroidclient.http;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * desc: http管理类
 * time：2020/7/1
 */
public class HttpManage {
    private static HttpServer httpServer;
    private static String baseUrl="http://127.0.0.1:8081/";            //获取版本信息接口

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
}
