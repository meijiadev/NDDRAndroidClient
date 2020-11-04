package ddr.example.com.nddrandroidclient.socket.hwSocker;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ddr.example.com.nddrandroidclient.entity.other.HwParkID;
import ddr.example.com.nddrandroidclient.other.Logger;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUtilh {
    public static void sendOkHttpRequest(okhttp3.Callback callback) {
        HwParkID hwParkID=HwParkID.getInstance();
        String id ="io.device.dadao.robot";
        String appkey="rnjiKsIkJOh/wVj17AItCw==";
        try {
            if (hwParkID.getHwID()!=null){
                id=hwParkID.getHwID();
            }
            if (hwParkID.getHwAppKey()!=null){
                appkey=hwParkID.getHwAppKey();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Logger.e("id"+id+"appkey"+appkey);
        String url = "https://119.3.203.242:1443/robot/device/queryMapAllBuildingInfo";
        try {
            String json = "{\"gisType\":\"SUPER_MAP\",\"mapName\":\"areaJ\"}";
            MediaType mediaType= MediaType.parse("application/json");
            RequestBody body= RequestBody.create(mediaType,json);
            OkHttpClient client =getUnsafeOkHttpClient();
            Request request = new Request.Builder().url(url)
                    .method("POST",body)
                    .addHeader("X-HW-ID",id)
                    .addHeader("X-HW-APPKEY",appkey)
                    .addHeader("Content-Type","application/json").build();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response response=client.newCall(request).execute();
                        ResponseBody resultBody = response.body();
                        String urlStr = resultBody.string().replaceAll("^.*url\":\"(.*?)\".*$","$1");
                        Logger.e("截取信息"+urlStr);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }).start();
            client.newCall(request).enqueue(callback);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final javax.net.ssl.SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);

            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
