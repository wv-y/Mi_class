package com.example.mi_class.activity;

import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.example.mi_class.R;
import com.example.mi_class.domain.Image;
import com.example.mi_class.adapter.ImageAdapter;

import java.util.Arrays;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    private ViewPager viewPager;

    List<Image> images = Arrays.asList(
            new Image( R.drawable.portrait_1),
            new Image( R.drawable.portrait_2),
            new Image( R.drawable.portrait_3),
            new Image( R.drawable.portrait_4),
            new Image( R.drawable.portrait_5),
            new Image( R.drawable.portrait_6),
            new Image( R.drawable.portrait_7));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_user_info);

        // 设置标题
        setTitle("修改信息");
        // 使用系统返回键
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        viewPager = findViewById(R.id.select_image);
        chooseImage();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chooseImage(){
        ImageAdapter imageAdapter = new ImageAdapter(this);

        imageAdapter.addCardItem(new Image( R.drawable.portrait_1));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_2));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_3));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_4));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_5));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_6));
        imageAdapter.addCardItem(new Image( R.drawable.portrait_7));


        viewPager.setAdapter(imageAdapter);
//        viewPager.setPageTransformer(false, mCardShadowTransformer);
        viewPager.setOffscreenPageLimit(3);
    }
}