package ddr.example.com.nddrandroidclient.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ddr.example.com.nddrandroidclient.R;
import ddr.example.com.nddrandroidclient.entity.XyEntity;


/**
 * 机器人ID列表适配器
 * 可用于 设置路径的列表适配器 换个子项布局
 */
public  class RobotIDRecyclerAdapter extends RecyclerView.Adapter<RobotIDRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Object> mDataSet;
    private List<String> list;
    private List<XyEntity> pointList;
    private OnItemClickListener mOnItemClickListener;
    private List<String>maps=new ArrayList<>();
    private List<String>tasks=new ArrayList<>();

    private int viewType;   //设置的子项Id

    public RobotIDRecyclerAdapter(Context context){
        this.context=context;
    }

    /**
     * 设置列表类型
     * @param viewType
     */
    public void setViewType(int viewType){
        this.viewType=viewType;
    }

    /**
     * 设置新的数据
     * @param data
     */
    public void setData(List<Object> data){
        mDataSet=data;
        notifyDataSetChanged();
    }


    /**
     * 清空当前数据
     */
    public void clearData() {
        if (mDataSet == null || mDataSet.size() == 0) {
            return;
        }
        mDataSet.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = null;
        switch (viewType){
            case 1:
                view=LayoutInflater.from(context).inflate(R.layout.item_recycle_robot_id,viewGroup,false);
                break;

        }
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        switch (viewType){
            case 1:
                viewHolder.tv_robotId.setText((String)mDataSet.get(i));
                viewHolder.tv_robotId.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnItemClickListener.onItemClick(viewHolder,i);
                            }
                        });
                break;

        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_robotId;
        TextView group_name;
        TextView tv_name;
        LinearLayout itemLyout;
        RecyclerView child_recycle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            switch (viewType){
                case 1:
                    tv_robotId=itemView.findViewById(R.id.tv_robot_id);
                    break;
            }

        }
    }

    public void setOtemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener=onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(RecyclerView.ViewHolder viewHolder, int position);
    }
}
