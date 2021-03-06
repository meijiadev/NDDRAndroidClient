package ddr.example.com.nddrandroidclient.helper;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.util.HashMap;
import java.util.Map;

import ddr.example.com.nddrandroidclient.entity.info.GridItem;

/**
 * desc：openCv操作类
 */
public class OpenCVUtility {
    private static OpenCVUtility openCVUtility;
    private Mat srcMat;            //原始灰度图
    private Map<GridItem,Mat> matMap=new HashMap<>();

    public static OpenCVUtility getInstance(){
        if (openCVUtility==null){
            synchronized (OpenCVUtility.class){
                if (openCVUtility==null){
                    openCVUtility=new OpenCVUtility();
                }
            }
        }
        return openCVUtility;
    }
    private OpenCVUtility() {
    }


    public Mat createMat(Size size,Scalar scalar){
        Mat mat=new Mat(size,CvType.CV_8UC3,scalar);
        return mat;
    }

    /**
     * 创建指定大小的Mat
     * @param w
     * @param h
     * @return
     */
    public Mat createSrcMat(int w,int h){
        //创建三通道的灰色(127)Mat
         srcMat=new Mat(new Size(w,h),CvType.CV_8UC3,new Scalar(18,17,16));
         return srcMat;
    }

    /**
     * 将Mat转成图片Bitmap
     * @param source
     * @return
     */
    public Bitmap matToBitmap(Mat source){
        Bitmap bitmap=Bitmap.createBitmap(source.width(),source.height(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(source,bitmap);
        return bitmap;
    }

    public Mat getSrcMat() {
        if (srcMat==null){
            srcMat=createSrcMat(700,700);
        }
        return srcMat;
    }

    /**
     * 保存不同索引的Mat数据
     * @param gridItem
     * @param mat
     */
    public void putValue(GridItem gridItem,Mat mat){
        matMap.put(gridItem, mat);
    }

    public Map<GridItem, Mat> getMatMap() {
        return matMap;
    }

    public void onDestroy(){
        if (srcMat!=null){
            matMap.clear();
            srcMat.release();
            srcMat=null;
            openCVUtility=null;
        }
    }
}
