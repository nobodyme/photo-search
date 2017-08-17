package com.example.imnobody.photosearch;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    ShareActionProvider myShareActionProvider;
    Intent shareIntent;
    private ImageView fullImageView;
    private String fullImageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        fullImageView = (ImageView) findViewById(R.id.fullImageid);
        fullImageString = getIntent().getExtras().getString("imageuri");
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.loading_indicator_main_grid);

        Glide
                .with(this)
                .load(fullImageString)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        prepareShareIntent(((GlideBitmapDrawable) resource).getBitmap());
                        attachShareIntentAction();
                        return false;
                    }
                })
                .error(R.drawable.ic_image_error)
                .into(fullImageView);

        fullImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.checkImageResource(ImageActivity.this, (ImageView) view, R.drawable.ic_image_error)) {
                    Toast.makeText(ImageActivity.this, getResources().getString(R.string.error_loading), Toast.LENGTH_SHORT).show();
                } else if (Utils.checkImageResource(ImageActivity.this, (ImageView) view, R.drawable.ic_loading)) {
                    Toast.makeText(ImageActivity.this, getResources().getString(R.string.image_loading), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.full_img_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        myShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        attachShareIntentAction();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // toggle menu item
        switch (id) {
            case R.id.menu_zoom:
                if (item.getTitle() == getResources().getString(R.string.menu_zoom)) {
                    fullImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    item.setTitle(getResources().getString(R.string.menu_original_size));
                } else {
                    fullImageView.setScaleType(ImageView.ScaleType.CENTER);
                    item.setTitle(getResources().getString(R.string.menu_zoom));
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void prepareShareIntent(Bitmap drawableImage) {
        Uri bmpUri = getBitmapFromDrawable(drawableImage);
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");

    }


    public void attachShareIntentAction() {
        if (myShareActionProvider != null && shareIntent != null)
            myShareActionProvider.setShareIntent(shareIntent);
    }

    public Uri getBitmapFromDrawable(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getCacheDir(), "images" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(ImageActivity.this, "com.example.imnobody.photosearch.fileprovider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
