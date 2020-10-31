package com.example.mi_class.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_class.R;
import com.example.mi_class.domain.Message;
import com.example.mi_class.tool.Base64Utils;

import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Message> mDatas;
    private final int[] portraits = {
            R.drawable.portrait_1,
            R.drawable.portrait_2,
            R.drawable.portrait_3,
            R.drawable.portrait_4,
            R.drawable.portrait_5,
            R.drawable.portrait_6,
            R.drawable.portrait_7,
            R.drawable.portrait_system
    };

    public ChatAdapter(Context context, List<Message> datas){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDatas = datas;
    }

    public void addItem(Message message){
        mDatas.add(message);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == Message.TYPE_RECEIVE){
            View view = mLayoutInflater.inflate(R.layout.chat_view_left,parent,false);
            return new ChatLeftViewHolder(view);
        }else {
            View view = mLayoutInflater.inflate(R.layout.chat_view_right,parent,false);
            return new ChatRightViewHolder(view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mDatas.get(position);
        long  time = message.getTime();
        String msg = message.getLast_message();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        String t = simpleDateFormat.format(date);
        if(holder instanceof ChatLeftViewHolder){
            Log.d("GGGGGG","发生1");
            ((ChatLeftViewHolder) holder).left_msg.setText(new String(Base64Utils.decodeFromString(msg)));
            ((ChatLeftViewHolder) holder).left_time.setText(t);
            ((ChatLeftViewHolder) holder).left_img.setBackgroundResource(portraits[message.getPic_id()]);
        }else if(holder instanceof ChatRightViewHolder){
            Log.d("GGGGGG","发生2");
            ((ChatRightViewHolder) holder).right_msg.setText(new String(Base64Utils.decodeFromString(msg)));
            ((ChatRightViewHolder) holder).right_time.setText(t);
            ((ChatRightViewHolder) holder).right_img.setBackgroundResource(portraits[message.getPic_id()]);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class ChatLeftViewHolder extends RecyclerView.ViewHolder{

        TextView left_time; //消息时间
        TextView left_msg;  //消息内容
        ImageView left_img; //头像
        ChatLeftViewHolder(View view){
            super(view);
            this.left_time = (TextView) view.findViewById(R.id.chat_left_time);
            this.left_msg = (TextView) view.findViewById(R.id.chat_left_msg);
            this.left_img = (ImageView) view.findViewById(R.id.chat_left_img);
        }
    }

    static class ChatRightViewHolder extends RecyclerView.ViewHolder{

        TextView right_time; //消息时间
        TextView right_msg;  //消息内容
        ImageView right_img; //头像
        ChatRightViewHolder(View view){
            super(view);
            this.right_time = (TextView) view.findViewById(R.id.chat_right_time);
            this.right_msg = (TextView) view.findViewById(R.id.chat_right_msg);
            this.right_img = (ImageView) view.findViewById(R.id.chat_right_img);
        }
    }
}
