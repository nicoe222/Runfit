package com.example.nicosetiawan.runfit.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.nicosetiawan.runfit.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class UniversalImageLoader {

    private static final int defaultImage = R.drawable.ic_profile_icon_blank;
    private Context mContext;

    public UniversalImageLoader(Context context){
        mContext = context;
    }

    public ImageLoaderConfiguration getConfig(){
        DisplayImageOptions defaultOption = new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOption)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024)
                .build();

        return configuration;
    }


    /**
     *
     * @param imgURL
     * @param image
     * @param mProgress
     * @param append
     */
    public static void setImage (String imgURL, ImageView image, final ProgressBar mProgress, String append){
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                    if (mProgress != null) {
                        mProgress.setVisibility(View.VISIBLE);
                    }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if (mProgress != null) {
                    mProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (mProgress != null) {
                    mProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if (mProgress != null) {
                    mProgress.setVisibility(View.GONE);
                }
            }
        });
    }

}
