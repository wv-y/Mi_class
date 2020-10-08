package com.example.mi_class;

import android.app.ActionBar;
import android.content.ClipData;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.example.mi_class.R;
import com.example.mi_class.adapter.MainPagerAdapter;
import com.example.mi_class.fragment.CourseFragment;
import com.example.mi_class.fragment.MessageFragment;
import com.example.mi_class.fragment.UserFragment;
import com.example.mi_class.mainToolbar.TabContainerView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private int fragmentIndex = 0;

    private final int[][] icons = {
            {R.drawable.ic_courses,R.drawable.ic_courses_checked},
            {R.drawable.ic_message,R.drawable.ic_message_checked},
            {R.drawable.ic_user,R.drawable.ic_user_checked}
    };

    private final ArrayList<Fragment> fragments = new ArrayList<>(Arrays.asList(
            new CourseFragment(),
            new MessageFragment(),
            new UserFragment()
    ));

    private int[] TAB_COLORS = {
            R.color.main_bottom_tab_textcolor_normal,
            R.color.main_bottom_tab_textcolor_selected};


    private ViewPager viewPager ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏文字自适应
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);
        setTitle("全部课程");
        viewPager = findViewById(R.id.view_pager);
        //避免自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initToolbar();
    }

    private void initToolbar() {        //加载导航栏
        MainPagerAdapter mAdapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(mAdapter);

        TabContainerView tabContainerView = findViewById(R.id.main_toolbar);
        tabContainerView.setOnPageChangeListener(this);

        tabContainerView.initContainer(getResources().getStringArray(R.array.tab_main_title), icons, TAB_COLORS, true);

        int width = getResources().getDimensionPixelSize(R.dimen.tab_icon_width);
        int height = getResources().getDimensionPixelSize(R.dimen.tab_icon_height);
        tabContainerView.setContainerLayout(R.layout.tab_container_view, R.id.iv_tab_icon, R.id.tv_tab_text, width, height);
        tabContainerView.setViewPager(viewPager);

        viewPager.setCurrentItem(getIntent().getIntExtra("tab", 0));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        getActionBar().setTitle();
        fragmentIndex = position;
    }

    @Override
    public void onPageSelected(int position) {
        fragmentIndex = position;
        int index = 0;
        int len = fragments.size();
        while (index < len) {
            fragments.get(index).onHiddenChanged(index != position);
            index++;
        }
        switch (position){
            case 0 : setTitle("全部课程");break;
            case 1 : setTitle("信息");break;
            case 2 : setTitle("我的");break;
         }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_add_btn, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_cart) {//监听菜单按钮
            Toast.makeText(this, "add selected!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem item = menu.findItem(R.id.action_cart);
        item.setVisible(fragmentIndex == 0);

        return super.onPrepareOptionsMenu(menu);
    }
}