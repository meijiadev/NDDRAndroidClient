package ddr.example.com.nddrandroidclient.entity.other;

public class Parameter {
    private String key;
    private String value;
    private String dValue;
    private String writable;
    private String alias;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getdValue() {
        return dValue;
    }

    public String getWritable() {
        return writable;
    }

    public String getAlias() {
        return alias;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setdValue(String dValue) {
        this.dValue = dValue;
    }

    public void setWritable(String writable) {
        this.writable = writable;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
