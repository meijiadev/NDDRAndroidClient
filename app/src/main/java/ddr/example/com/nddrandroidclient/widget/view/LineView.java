package ddr.example.com.nddrandroidclient.widget.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

import DDRVLNMapProto.DDRVLNMap;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.point.PathLine;
import ddr.example.com.nddrandroidclient.entity.point.SpaceItem;
import ddr.example.com.nddrandroidclient.entity.point.XyEntity;
import ddr.example.com.nddrandroidclient.other.Logger;

/**
 *  time : 2019/11/18
 *  desc : 路径绘制
 */
public class LineView {
    public static LineView lineView;
    private Paint linePaint,textPaint,linePaint1,selectPaint;
    private List<PathLine> pathLines;
    private List<PathLine> pathLines1;
    private List<PathLine.PathPoint> pathPoints;

    private List<DDRVLNMap.space_pointEx> lines;       //线段
    private List<DDRVLNMap.space_pointEx> polygons;    //多边形

    private List<SpaceItem> spaceItems;

    private Bitmap startBitamap,endBitamp;
    /**
     *用于裁剪源图像的矩形（可重复使用）。
     */
    private Rect mRectSrc=new Rect(0,0,22,22);

    /**
     * 用于在画布上指定绘图区域的矩形（可重新使用）。
     */
    private Rect mRectDst;

    public static LineView getInstance(Context c){
        if (lineView==null){
            synchronized (LineView.class){
                if (lineView==null){
                    lineView=new LineView(c);
                }
            }
        }
        return lineView;
    }

    private LineView(Context context) {
        linePaint=new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(3);
        linePaint.setAntiAlias(true);
        linePaint1=new Paint();
        linePaint1.setColor(Color.BLACK);
        linePaint1.setStrokeWidth(2);
        linePaint1.setAntiAlias(true);
        selectPaint=new Paint();
        selectPaint.setColor(Color.RED);
        selectPaint.setStrokeWidth(5);
        selectPaint.setAntiAlias(true);
        textPaint=new Paint();
        textPaint.setStrokeWidth(8);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setStyle(Paint.Style.FILL);
        startBitamap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.start_default);
        endBitamp=BitmapFactory.decodeResource(context.getResources(),R.mipmap.end_defalut);
    }

    /**
     * 设置需要绘制的路径
     * @param pathLines
     */
    public void setLineViews(List<PathLine> pathLines){
        this.pathLines=pathLines;
    }

    /**
     * 设置需要显示的路径（多选）
     * @param pathLines
     */
    public void setPathLines(List<PathLine> pathLines){
        this.pathLines1=pathLines;
    }

    public void setPoints(List<PathLine.PathPoint> pathPoints){
        this.pathPoints=pathPoints;
    }

    /**
     * 设置虚拟墙的显示
     * @param lines
     */
    public void setLines(List<DDRVLNMap.space_pointEx> lines){
        this.lines=lines;
    }

    public void setPolygons(List<DDRVLNMap.space_pointEx> polygons){
        this.polygons=polygons;
    }

    /**
     * 显示所有虚拟墙（直线)
     * @param spaceItems
     */
    public void setSpaceItems(List<SpaceItem> spaceItems){
        this.spaceItems=spaceItems;
    }


    /**
     * 绘制路到画布上
     * @param canvas
     * @param zoomImageView
     */
    public void drawLine(Canvas canvas,ZoomImageView zoomImageView){
        //绘制路径
        if (pathLines!=null){
            for (int i=0;i<pathLines.size();i++){
                if (pathLines.get(i).isInTask()){
                    List<PathLine.PathPoint> pathPoints=pathLines.get(i).getPathPoints();
                    if (pathPoints.size()>1){
                        for (int j=0;j<pathPoints.size();j++){
                            if (j<pathPoints.size()-1){
                                XyEntity xyEntity1=zoomImageView.toXorY(pathPoints.get(j).getX(),pathPoints.get(j).getY());
                                xyEntity1=zoomImageView.coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                                XyEntity xyEntity2=zoomImageView.toXorY(pathPoints.get(j+1).getX(),pathPoints.get(j+1).getY());
                                xyEntity2=zoomImageView.coordinate2View(xyEntity2.getX(),xyEntity2.getY());
                                canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),linePaint);
                                if (j==0){
                                    mRectDst=new Rect((int)xyEntity1.getX()-11,(int)xyEntity1.getY()-11,(int)xyEntity1.getX()+11,(int)xyEntity1.getY()+11);
                                    canvas.drawBitmap(startBitamap,mRectSrc,mRectDst,linePaint);
                                }else if (j==pathPoints.size()-2){
                                    mRectDst=new Rect((int)xyEntity2.getX()-11,(int)xyEntity2.getY()-11,(int)xyEntity2.getX()+11,(int)xyEntity2.getY()+11);
                                    canvas.drawBitmap(endBitamp,mRectSrc,mRectDst,linePaint);
                                }

                            }
                        }
                    }
                }
            }
        }

        if (pathLines1!=null){
            for (int i=0;i<pathLines1.size();i++){
                if (pathLines1.get(i).isMultiple()){
                    List<PathLine.PathPoint> pathPoints=pathLines1.get(i).getPathPoints();
                    if (pathPoints.size()>1){
                        for (int j=0;j<pathPoints.size();j++){
                            if (j<pathPoints.size()-1){
                                XyEntity xyEntity1=zoomImageView.toXorY(pathPoints.get(j).getX(),pathPoints.get(j).getY());
                                xyEntity1=zoomImageView.coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                                XyEntity xyEntity2=zoomImageView.toXorY(pathPoints.get(j+1).getX(),pathPoints.get(j+1).getY());
                                xyEntity2=zoomImageView.coordinate2View(xyEntity2.getX(),xyEntity2.getY());
                                canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),linePaint);
                                if (j==0){
                                    mRectDst=new Rect((int)xyEntity1.getX()-11,(int)xyEntity1.getY()-11,(int)xyEntity1.getX()+11,(int)xyEntity1.getY()+11);
                                    canvas.drawBitmap(startBitamap,mRectSrc,mRectDst,linePaint);
                                }else if (j==pathPoints.size()-2){
                                    mRectDst=new Rect((int)xyEntity2.getX()-11,(int)xyEntity2.getY()-11,(int)xyEntity2.getX()+11,(int)xyEntity2.getY()+11);
                                    canvas.drawBitmap(endBitamp,mRectSrc,mRectDst,linePaint);
                                }

                            }
                        }
                    }
                }
            }

        }


        if (pathPoints!=null){
            for (int j=0;j<pathPoints.size();j++){
                XyEntity xyEntity1=zoomImageView.toXorY(pathPoints.get(j).getX(),pathPoints.get(j).getY());
                xyEntity1=zoomImageView.coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                if (j==0){
                    mRectDst=new Rect((int)xyEntity1.getX()-11,(int)xyEntity1.getY()-11,(int)xyEntity1.getX()+11,(int)xyEntity1.getY()+11);
                    canvas.drawBitmap(startBitamap,mRectSrc,mRectDst,linePaint);
                }
                if (pathPoints.size()>1){
                    if (j<pathPoints.size()-1){
                        XyEntity xyEntity2=zoomImageView.toXorY(pathPoints.get(j+1).getX(),pathPoints.get(j+1).getY());
                        xyEntity2=zoomImageView.coordinate2View(xyEntity2.getX(),xyEntity2.getY());
                        canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),linePaint);
                    }
                    if (j==pathPoints.size()-1){
                        mRectDst=new Rect((int)xyEntity1.getX()-11,(int)xyEntity1.getY()-11,(int)xyEntity1.getX()+11,(int)xyEntity1.getY()+11);
                        canvas.drawBitmap(endBitamp,mRectSrc,mRectDst,linePaint);
                    }
                }
            }
        }

        if (lines!=null){
            for (int i=0;i<lines.size();i++){
                XyEntity xyEntity1=zoomImageView.toXorY(lines.get(i).getX(),lines.get(i).getY());
                xyEntity1=zoomImageView.coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                canvas.drawCircle(xyEntity1.getX(),xyEntity1.getY(),3,linePaint1);
                if (i<lines.size()-1){
                    XyEntity xyEntity2=zoomImageView.toXorY(lines.get(i+1).getX(),lines.get(i+1).getY());
                    xyEntity2=zoomImageView.coordinate2View(xyEntity2.getX(),xyEntity2.getY());
                    canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),linePaint1);
                }

            }
        }

        if (polygons!=null){
            for (int i=0;i<polygons.size();i++){
                if (i<polygons.size()-1){
                    XyEntity xyEntity1=zoomImageView.toXorY(polygons.get(i).getX(),polygons.get(i).getY());
                    xyEntity1=zoomImageView.coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                    XyEntity xyEntity2=zoomImageView.toXorY(polygons.get(i+1).getX(),polygons.get(i+1).getY());
                    xyEntity2=zoomImageView.coordinate2View(xyEntity2.getX(),xyEntity2.getY());
                    canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),linePaint1);
                }
            }
        }

        if (spaceItems!=null){
            for (int i=0;i<spaceItems.size();i++){
                List<DDRVLNMap.space_pointEx> space_pointExes=spaceItems.get(i).getLines();
                for (int j=0;j<space_pointExes.size();j++){
                    if (j<space_pointExes.size()-1){
                        XyEntity xyEntity1=zoomImageView.toXorY(space_pointExes.get(j).getX(),space_pointExes.get(j).getY());
                        xyEntity1=zoomImageView.coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                        XyEntity xyEntity2=zoomImageView.toXorY(space_pointExes.get(j+1).getX(),space_pointExes.get(j+1).getY());
                        xyEntity2=zoomImageView.coordinate2View(xyEntity2.getX(),xyEntity2.getY());
                        if (i==selectPosition){
                            canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),selectPaint);
                        }else {
                            canvas.drawLine(xyEntity1.getX(),xyEntity1.getY(),xyEntity2.getX(),xyEntity2.getY(),linePaint1);
                        }
                    }
                }
            }
        }

    }

    public int selectPosition=-1;

    /**
     * 点击区域的坐标
     * @param x
     * @param y
     */
    public void onClick(ZoomImageView zoomImageView,float x,float y){
        if (spaceItems!=null){
            for (int i=0;i<spaceItems.size();i++){
                List<DDRVLNMap.space_pointEx> space_pointExes=spaceItems.get(i).getLines();
                for (int j=0;j<space_pointExes.size();j++){
                    if (j<space_pointExes.size()-1){
                        XyEntity xyEntity1=zoomImageView.toXorY(space_pointExes.get(j).getX(),space_pointExes.get(j).getY());
                        xyEntity1=zoomImageView.coordinate2View(xyEntity1.getX(),xyEntity1.getY());
                        float x1=xyEntity1.getX(); float y1=xyEntity1.getY();
                        XyEntity xyEntity2=zoomImageView.toXorY(space_pointExes.get(j+1).getX(),space_pointExes.get(j+1).getY());
                        xyEntity2=zoomImageView.coordinate2View(xyEntity2.getX(),xyEntity2.getY());
                        float x2=xyEntity2.getX(); float y2=xyEntity2.getY();
                        float minX=Math.min(x1,x2);
                        float maxX=Math.max(x1,x2);
                        if (x>minX-18&&x<maxX+18){
                            double L=((y2-y1)*x+(x1-x2)*y+(x2*y1-x1*y2))/Math.sqrt(Math.pow(y2-y1,2)+Math.pow(x1-x2,2));
                            if (L<18){
                                selectPosition=i;
                                Logger.e("------:"+L+"-----"+selectPosition);
                                zoomImageView.invalidate();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }






    /**
     * 主动清空列表
     */
    public void clearDraw(){
        pathLines=null;
        pathPoints=null;
        polygons=null;
        lines=null;
        pathLines1=null;
        selectPosition=-1;
        spaceItems=null;
    }




}
