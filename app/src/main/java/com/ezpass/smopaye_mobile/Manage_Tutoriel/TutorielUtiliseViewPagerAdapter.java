package com.ezpass.smopaye_mobile.Manage_Tutoriel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class TutorielUtiliseViewPagerAdapter extends PagerAdapter {
    private Context context;
    private int[] imageUrls;

    TutorielUtiliseViewPagerAdapter(Context context, int[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);

        //image en GIF
        Glide.
                with(context)
                .load(imageUrls[position])
                //.placeholder(R.drawable.placeholder)
                //.error(R.drawable.imagenotfound)
                //.centerCrop()
                //.override(100, Target.SIZE_ORIGINAL) // resizes width to 100, preserves original height, does not respect aspect ratio
                .fitCenter() // scale to fit entire image within ImageView
                .transform(new RoundedCornersTransformation(30, 10))
                //.transform(new BlurTransformation())
                .into(imageView);

        //images simple
        /*Picasso.get()
                .load(imageUrls[position])
                .fit()
                .centerCrop()
                .into(imageView);*/

        container.addView(imageView);

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
