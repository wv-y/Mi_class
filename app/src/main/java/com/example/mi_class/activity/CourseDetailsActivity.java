package com.example.mi_class.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mi_class.R;

public class CourseDetailsActivity extends AppCompatActivity {

    private String course_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_course_details);

        Intent intent = getIntent();
        course_code = intent.getStringExtra("course_code");
        Log.d("123",course_code);
    }
}