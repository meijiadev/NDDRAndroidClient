package ddr.example.com.ddrandroidclient.entity;

public class QrParameter {
    private String qrParameter;
    private int value;

    public QrParameter(String qrParameter,int value){
        this.qrParameter=qrParameter;
        this.value=value;
    }
    public QrParameter(String qrParameter){
        this.qrParameter=qrParameter;
    }
    public QrParameter(int value){
        this.value=value;
    }

    public String getQrParameter(){
        return qrParameter;
    }

    public int getValue() {
        return value;
    }

    public void setQrParameter(String qrParameter){
        this.qrParameter=qrParameter;
    }

    public void setValue(int value){
        this.value=value;
    }
}
