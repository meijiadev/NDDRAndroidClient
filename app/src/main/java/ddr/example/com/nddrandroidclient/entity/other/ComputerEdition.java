package ddr.example.com.nddrandroidclient.entity.other;

/**
 *
 */
public class ComputerEdition {
    private String version;
    private String data;
    private int type;


    public ComputerEdition() {
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public String getData() {
        return data;
    }

    public int getType() {
        return type;
    }
}
