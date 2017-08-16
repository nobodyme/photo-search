package com.example.imnobody.photosearch;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;


public class ImageGridAdapter extends ArrayAdapter {

    private Context mContext;

    public ImageGridAdapter(Context context, List<String> imageUrlList) {
        super(context, R.layout.grid_image_item, imageUrlList);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (null == convertView) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_image_item, parent, false);
        }

        final View progressbar = (ProgressBar)convertView.findViewById(R.id.grid_item_loading_indicator);
        final ImageView imageview = (ImageView)convertView.findViewById(R.id.grid_item_image);

        Glide
                .with(getContext())
                .load(getItem(position))
                .listener(new RequestListener<Object, GlideDrawable>() {
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Object model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressbar.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e, Object model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressbar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .error(R.drawable.ic_image_error)
                .centerCrop()
                .crossFade()
                .into(imageview);

        return convertView;
    }
}
