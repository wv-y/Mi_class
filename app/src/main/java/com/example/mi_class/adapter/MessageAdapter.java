package com.example.mi_class.adapter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mi_class.R;
import com.example.mi_class.domain.Message;
import com.example.mi_class.fragment.MessageFragment;

import java.util.Date;
import java.util.List;




public class MessageAdapter extends BaseAdapter {
    private Context context;
    private List<Message> messageList;

    public MessageAdapter(MessageFragment messageFragment, List<Message> list){
        this.context = messageFragment.getActivity();
        this.messageList = list;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();

            //引入布局
            convertView = View.inflate(context, R.layout.fragment_message_item,null);

            //实例化对象
            viewHolder.head_portrait = (ImageView)  convertView.findViewById(R.id.message_item_image);
            viewHolder.user = (TextView)    convertView.findViewById(R.id.message_item_user);
            viewHolder.last_message = (TextView)    convertView.findViewById(R.id.message_item_last_message);
            viewHolder.time = (TextView)    convertView.findViewById(R.id.message_item_time);

            convertView.setTag(viewHolder);
        }else{
            viewHolder =(ViewHolder) convertView.getTag();
        }

        //给控件赋值
        viewHolder.user.setText(messageList.get(position).getName().trim());
        viewHolder.head_portrait.setImageResource(R.drawable.vector_drawable_teacher);
      //  viewHolder.head_portrait.setImageBitmap(messageList.get(position).getHead_portrait());
        viewHolder.last_message.setText(messageList.get(position).getLast_message().trim());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(messageList.get(position).getTime());
        //时间处理（未做）
        viewHolder.time.setText(simpleDateFormat.format(date));
//        viewHolder.time.setText(String.valueOf(messageList.get(position).getTime()));

        return convertView;

    }
    class ViewHolder{
        ImageView head_portrait;    //头像
        TextView user;  //用户名
        TextView last_message;  //最新消息
        TextView time;  //发送时间
    }
}
