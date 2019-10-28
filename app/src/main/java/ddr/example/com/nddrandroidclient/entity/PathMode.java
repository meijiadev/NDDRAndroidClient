package ddr.example.com.nddrandroidclient.entity;

public class PathMode {
    private String pathModeName;
    private int modeValue;


    public PathMode(String pathModeName,int modeValue){
        this.pathModeName=pathModeName;
        this.modeValue=modeValue;
    }
    public PathMode(String pathModeName){
        this.pathModeName=pathModeName;
    }
    public PathMode(int modeValue){
        this.modeValue=modeValue;
    }

    public String getPathModeName() {
        return pathModeName;
    }

    public int getModeValue(){return modeValue;}
    public void setPathModeName(String pathModeName) {
        this.pathModeName = pathModeName;
    }

    public void setModeValue(int modeValue){this.modeValue=modeValue;}

}
