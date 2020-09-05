package ddr.example.com.nddrandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

public class ComputerEditions {
    public static ComputerEditions computerEditions;
    private List<ComputerEdition> computerEditionList =new ArrayList<>();
    private int robotType;             //地盘类型
    public static ComputerEditions getInstance(){
        if (computerEditions ==null){
            synchronized (ComputerEditions.class){
                if (computerEditions ==null){
                    computerEditions =new ComputerEditions();
                }
            }
        }
        return computerEditions;
    }

    public void setComputerEditionList(List<ComputerEdition> computerEditionList) {
        this.computerEditionList = computerEditionList;
    }

    public List<ComputerEdition> getComputerEditionList() {
        return computerEditionList;
    }

    public int getRobotType() {
        return robotType;
    }

    public void setRobotType(int robotType) {
        this.robotType = robotType;
    }
}
