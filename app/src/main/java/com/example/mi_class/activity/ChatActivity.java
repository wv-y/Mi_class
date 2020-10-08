package com.example.mi_class.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mi_class.R;
import com.example.mi_class.adapter.ChatAdapter;
import com.example.mi_class.domain.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private List<Message> chatList;
    private ChatAdapter chatAdapter;

    RecyclerView recyclerView;
    EditText editText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);
        // 状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        recyclerView = findViewById(R.id.chat_recycler_list);
        editText = findViewById(R.id.chat_edit_msg);
        button = findViewById(R.id.chat_send_button);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        Message msg1 = new Message();
        msg1.setType(Message.TYPE_RECEIVE);
        Message msg2 = new Message();
        msg2.setType(Message.TYPE_SEND);

        chatList = new ArrayList<Message>();

        chatList.add(msg1);
        chatList.add(msg2);
        chatAdapter = new ChatAdapter(this,chatList);
        recyclerView.setAdapter(chatAdapter);

        Intent intent = getIntent();
        String name = intent.getStringExtra("chat_name");
        setTitle(name);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
