package com.example.simplelauncher;

import android.app.Application;

import com.example.simplelauncher.loader.GalleryDownloader;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.getInstance().init(initImageLoader());
    }

    private ImageLoaderConfiguration initImageLoader(){
        return  new ImageLoaderConfiguration.Builder(this)
                .imageDownloader(new GalleryDownloader(this))
                .memoryCacheSizePercentage(25)
                .build();
    }
}
