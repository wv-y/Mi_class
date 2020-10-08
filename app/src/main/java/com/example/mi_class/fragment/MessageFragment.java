package com.example.mi_class.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.mi_class.R;
import com.example.mi_class.activity.ChatActivity;
import com.example.mi_class.adapter.MessageAdapter;
import com.example.mi_class.domain.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {
    private ListView listView;
    private List<Message> list;
    private MessageAdapter messageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        listView = (ListView) view.findViewById(R.id.message_list);
        list = new ArrayList<Message>();
        //默认信息（展示用）
        Message message = new Message();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.vector_drawable_teacher);
        message.setHead_portrait(bitmap);
        message.setName("张三");
        message.setLast_message("你好啊！");
        message.setTime(200);
        list.add(message);
        messageAdapter = new MessageAdapter(MessageFragment.this,list);
        listView.setAdapter(messageAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = list.get(position);
                String name = message.getName();
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chat_name",name);
                startActivity(intent);
            }
        });
    }
}
