package ddr.example.com.nddrandroidclient.helper;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import java.nio.ByteBuffer;

public class OpenCVUtility {
    private static OpenCVUtility openCVUtility;
    private Mat srcMat;            //原始灰度图

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

    public Mat createMat(int w, int h, ByteBuffer byteBuffer){
        Mat mat=new Mat(w,h, CvType.CV_8UC3,byteBuffer);
        return mat;
    }

    public Mat createMat(Size size,Scalar scalar){
        Mat mat=new Mat(size,CvType.CV_8UC3,scalar);
        return mat;
    }

    public Mat createSrcMat(int w,int h){
        //创建三通道的灰色(127)Mat
         srcMat=new Mat(new Size(w,h),CvType.CV_8UC3,new Scalar(127,127,127));
         return srcMat;
    }




}
