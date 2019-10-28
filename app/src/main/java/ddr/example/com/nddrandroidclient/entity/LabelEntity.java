package ddr.example.com.nddrandroidclient.entity;

import com.google.protobuf.ByteString;

/**
 *标签的数据结构
 */
public class LabelEntity {
    private ByteString name;
    private int label;
    private int selected; // 0默认 1被选中显示该层内容（参考层使用）


    public LabelEntity(ByteString name, int label){
        this.name=name;
        this.label=label;
    }


    public ByteString getName() {
        return name;
    }
    public void setName(ByteString name) {
        this.name = name;
    }

    public int getLabel() {
        return label;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getSelected() {
        return selected;
    }
}
