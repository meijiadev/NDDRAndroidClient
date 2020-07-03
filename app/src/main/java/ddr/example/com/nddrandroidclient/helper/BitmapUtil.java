package ddr.example.com.nddrandroidclient.helper;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class BitmapUtil {
    public static byte[] Bitmap2Bytes(Bitmap bitmap){
        ByteArrayOutputStream baos =new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
