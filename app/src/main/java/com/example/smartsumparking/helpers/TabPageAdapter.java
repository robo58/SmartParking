package com.example.smartsumparking.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.smartsumparking.R;

import java.util.List;

public class TabPageAdapter extends PagerAdapter {
    private Context mContext;
    private List<Items> contentItems;

    public TabPageAdapter(Context mContext, List<Items> mListItems){
        this.mContext = mContext;
        this.contentItems = mListItems;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("InflateParams") View layoutItems = layoutInflater.inflate(R.layout.slide_tab_layout,null);

        TextView first = layoutItems.findViewById(R.id.TopText);
        TextView title = layoutItems.findViewById(R.id.Title);
        TextView description = layoutItems.findViewById(R.id.Description);
        ImageView images = layoutItems.findViewById(R.id.Image);

        first.setText(contentItems.get(position).getFirst());
        title.setText(contentItems.get(position).getTitles());
        description.setText(contentItems.get(position).getDescription());
        images.setImageResource(contentItems.get(position).getImages());
        container.addView(layoutItems);
        return layoutItems;

    }

    @Override
    public int getCount(){
        return contentItems.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View v, @NonNull Object a){
        return v == a;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object){
        container.removeView((View)object);
    }

}
