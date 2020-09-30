package com.example.mi_class;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.view_pager);
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
    }

    @Override
    public void onPageSelected(int position) {
        int index = 0;
        int len = fragments.size();
        while (index < len) {
            fragments.get(index).onHiddenChanged(index != position);
            index++;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}