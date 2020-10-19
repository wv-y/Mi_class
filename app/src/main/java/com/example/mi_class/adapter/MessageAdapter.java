package com.example.mi_class.adapter;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.mi_class.R;
import com.example.mi_class.domain.Message;
import com.example.mi_class.fragment.MessageFragment;
import com.example.mi_class.stickydotslib.utils.DisplayUtils;
import com.example.mi_class.stickydotslib.view.StickyViewHelper;

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

        final ViewHolder viewHolder ;
        if(convertView == null){
            viewHolder = new ViewHolder();

            //引入布局
    //        convertView = View.inflate(context, R.layout.fragment_message_item,null);
            convertView = LayoutInflater.from(this.context).inflate(R.layout.fragment_message_item,parent,false);
            //实例化对象
            viewHolder.mDragView = (TextView) convertView.findViewById(R.id.mDragView);
            viewHolder.head_portrait = (ImageView)  convertView.findViewById(R.id.message_item_image);
            viewHolder.user = (TextView)    convertView.findViewById(R.id.message_item_user);
            viewHolder.last_message = (TextView)    convertView.findViewById(R.id.message_item_last_message);
            viewHolder.time = (TextView)    convertView.findViewById(R.id.message_item_time);

            convertView.setTag(viewHolder);
        }else{
            viewHolder =(ViewHolder) convertView.getTag();
        }
        //小红点存在
        if(messageList.get(position).getUnReadCnt()==0){
            viewHolder.mDragView.setVisibility(View.GONE);
        }
        else{
            viewHolder.mDragView.setVisibility(View.VISIBLE);
        }


        viewHolder.mDragView.setText(String.valueOf(messageList.get(position).getUnReadCnt()));
        StickyViewHelper stickyViewHelper = new StickyViewHelper(context, viewHolder.mDragView,R.layout.red_point_includeview);

        //给控件赋值
        viewHolder.user.setText(messageList.get(position).getName().trim());
        viewHolder.head_portrait.setImageResource(R.drawable.vector_drawable_teacher);
      //  viewHolder.head_portrait.setImageBitmap(messageList.get(position).getHead_portrait());
        viewHolder.last_message.setText(messageList.get(position).getLast_message().trim());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(messageList.get(position).getTime());
        //时间处理
        viewHolder.time.setText(simpleDateFormat.format(date));

        setViewOut2InRangeUp(stickyViewHelper);
        setViewOutRangeUp(position, stickyViewHelper);
        setViewInRangeUp(stickyViewHelper);
        setViewInRangeMove(stickyViewHelper);
        setViewOutRangeMove(stickyViewHelper);
        return convertView;

    }


    /**
     * view在范围外移动执行此Runnable
     * @param stickyViewHelper
     */
    private void setViewOutRangeMove(StickyViewHelper stickyViewHelper) {
        stickyViewHelper.setViewOutRangeMoveRun(new Runnable() {
            @Override
            public void run() {
                DisplayUtils.showToast(context, "ViewOutRangeMove");
            }
        });
    }

    /**
     * view在范围内移动指此此Runnable
     * @param stickyViewHelper
     */
    private void setViewInRangeMove(StickyViewHelper stickyViewHelper) {
        stickyViewHelper.setViewInRangeMoveRun(new Runnable() {
            @Override
            public void run() {
                DisplayUtils.showToast(context, "ViewInRangeMove");
            }
        });
    }

    /**
     * view没有移出过范围，在范围内松手
     * @param stickyViewHelper
     */
    private void setViewInRangeUp(StickyViewHelper stickyViewHelper) {
        stickyViewHelper.setViewInRangeUpRun(new Runnable() {
            @Override
            public void run() {
                DisplayUtils.showToast(context, "ViewInRangeUp");
       //         myAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * view移出范围，最后在范围外松手
     * @param position
     * @param stickyViewHelper
     */
    private void setViewOutRangeUp(final int position, StickyViewHelper stickyViewHelper) {
        stickyViewHelper.setViewOutRangeUpRun(new Runnable() {
            @Override
            public void run() {
                DisplayUtils.showToast(context, "ViewOutRangeUp");
        //        removeList.add(position);
//                myAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * view移出过范围，最后在范围内松手执行次Runnable
     * @param stickyViewHelper
     */
    private void setViewOut2InRangeUp(StickyViewHelper stickyViewHelper) {
        stickyViewHelper.setViewOut2InRangeUpRun(new Runnable() {
            @Override
            public void run() {
                DisplayUtils.showToast(context, "ViewOut2InRangeUp");
    //            myAdapter.notifyDataSetChanged();
            }
        });
    }


   static class ViewHolder{
        ImageView head_portrait;    //头像
        TextView user;  //用户名
        TextView last_message;  //最新消息
        TextView time;  //发送时间
        TextView mDragView;
    }
}
