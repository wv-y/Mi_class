package com.example.mi_class.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_class.MainActivity;
import com.example.mi_class.R;
import com.example.mi_class.domain.SignInDetailList;

import java.util.List;

public class SignInDetailListAdapter extends  RecyclerView.Adapter<SignInDetailListAdapter.SignListViewHolder> implements View.OnClickListener {

    private Context context;
    private List<SignInDetailList> signList;

    public SignInDetailListAdapter(Context context, List<SignInDetailList> list){
        this.signList = list;
        this.context = context;
    }

    //    自定义回调接口实现点击和长按事件
    public interface OnItemClickListener{
        void onItemClick(View v,int position);
        //void onItemLongClick(View v, int position);
    }

    //    自定义接口声明
    private OnItemClickListener onItemClickListener;
    //    定义方法传递给外部使用者
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        System.out.println("homework_position"+position);
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(view, position);
        }
    }

    @NonNull
    @Override
    public SignListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_detail_list_item,parent,false);
        return new SignListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SignListViewHolder holder, int position) {
        SignInDetailList signIn = signList.get(position);
        holder.set.setTag(position);
        //holder.image.setBackgroundResource(MainActivity.portraits[Integer.parseInt(signIn.getImage())]);
        holder.image.setBackgroundResource(MainActivity.portraits[signIn.getImage()]);
        holder.name.setText(signIn.getName());
        holder.id.setText(signIn.getId());
        holder.style.setText(signIn.getStyle());
        switch (signIn.getStyle()) {
            case "已签到":
                holder.style.setTextColor(Color.parseColor("#39BC3E"));
                break;
            case "未签到":
                holder.style.setTextColor(Color.parseColor("#9D9D9D"));
                break;
            case "迟到":
                holder.style.setTextColor(Color.parseColor("#FFDF0D0D"));
                break;
            case "请假":
                holder.style.setTextColor(Color.parseColor("#ED930D"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return signList.size();
    }

    class SignListViewHolder extends RecyclerView.ViewHolder{
        protected ImageView image;
        protected TextView name;
        protected TextView id;
        protected TextView style;
        protected Button set;
        public SignListViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.member_image);
            name = itemView.findViewById(R.id.member_name);
            id = itemView.findViewById(R.id.member_id);
            style = itemView.findViewById(R.id.member_style);
            set = itemView.findViewById(R.id.set_sign);
//            添加点击事件
            set.setOnClickListener(SignInDetailListAdapter.this);
        }
    }

}
