package com.example.mi_class.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.mi_class.R;
import com.example.mi_class.domain.Image;


import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<Image> mData;
    private float mBaseElevation;
    private Context context;

    public ImageAdapter(Context context) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
        this.context = context;
    }

    public void addCardItem(Image item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.image_item, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(Image item, View view) {
        ImageView image = view.findViewById(R.id.image);
        Glide
                .with(context)
                .load(item.getImgId())
//                .placeholder(R.mipmap.ic_launcher) // can also be a drawable
                // will be displayed if the image cannot be loaded
                .into(image);
    }

}