package ddr.example.com.nddrandroidclient.entity;

import java.util.ArrayList;
import java.util.List;

public class ComputerEditions {
    public static ComputerEditions computerEditions;
    private List<ComputerEdition> computerEditionList =new ArrayList<>();
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
}
