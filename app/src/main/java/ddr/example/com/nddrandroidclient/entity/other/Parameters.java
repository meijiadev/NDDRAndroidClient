package ddr.example.com.nddrandroidclient.entity.other;

import java.util.ArrayList;
import java.util.List;

public class Parameters {
    public static Parameters parameters;
    private List<Parameter> parameterList=new ArrayList<>();
    public static Parameters getInstance(){
        if(parameters==null){
            synchronized (Parameters.class){
                if (parameters==null){
                    parameters=new Parameters();
                }
            }
        }
        return parameters;
    }

    public List<Parameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Parameter> parameterList) {
        this.parameterList = parameterList;
    }
}
