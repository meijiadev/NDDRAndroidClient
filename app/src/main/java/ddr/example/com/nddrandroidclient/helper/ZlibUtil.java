package ddr.example.com.nddrandroidclient.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Inflater;

import ddr.example.com.nddrandroidclient.other.Logger;

/**
 * 解压缩工具
 */
public class ZlibUtil {

    public static byte[] unZip(byte[] data){
        long startTime=System.currentTimeMillis();
        byte[] output;
        //解压的类
        Inflater decompress=new Inflater();
        decompress.reset();
        decompress.setInput(data,0,data.length);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream(data.length);
        try {
            byte[] buf=new byte[1024];
            while (!decompress.finished()){
                int i=decompress.inflate(buf);
                byteArrayOutputStream.write(buf,0,i);
            }
            output=byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            output=data;
            e.printStackTrace();
        }finally {
            try {
                byteArrayOutputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        decompress.end();
        long endTime=System.currentTimeMillis();
        Logger.e("解压耗费的时间："+(endTime-startTime)+"-----解压出的数组大小："+output.length);
        return output;
    }
}
