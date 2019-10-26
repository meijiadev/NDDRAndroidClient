package ddr.example.com.ddrandroidclient.entity;

public class ImageUpdate {
    private int index;
    private String path;
    private String matPath;
    private String pngPath;
    private String markPath;
    private int listIndex;

    private Type type;

    public enum Type{
        updatePng,
        readFile,
        realTimeDraw,

    }

    public Type getType() {
        return type;
    }

    public ImageUpdate(Type type) {
        this.type=type;
    }


    public ImageUpdate(int index,String pngPath){
        this.index=index;
        this.pngPath=pngPath;
    }
    public ImageUpdate(int index,int listIndex) {
        this.index=index;
        this.listIndex=listIndex;
    }

    public ImageUpdate(int index,String path,String matPath,String markPath){
        this.index=index;
        this.path=path;
        this.matPath=matPath;
        this.markPath=markPath;
    }

    public int getIndex() {
        return index;
    }


    public String getPath() {
        return path;
    }


    public String getMatPath() {
        return matPath;
    }


    public String getPngPath() {
        return pngPath;
    }

    public int getListIndex(){
        return listIndex;
    }

    public String getMarkPath(){
        return markPath;
    }

}
