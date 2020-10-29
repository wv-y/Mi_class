package com.example.mi_class.tool;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.mi_class.R;


public class process_dialog extends AlertDialog {
    private Context context;
    private String text;

    public process_dialog(Context context, String text) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);


        View view = getLayoutInflater().inflate(R.layout.process_dialog,null);
        //view.setBackground(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(view);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent); // 去掉边角
        if(text != null) {
            TextView tv = (TextView) findViewById(R.id.tv_dialogmsg);
            tv.setText(text);
        }
    }
    @Override
    public void show() {
        // TODO Auto-generated method stub
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题<pre name="code" class="java">
        super.show();
    }
}
