package com.example.simplelauncher;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import uk.co.senab.photoview.PhotoViewAttacher;

public class BigImageFragment extends Fragment {
    private static final DisplayImageOptions options;
    static {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(new ColorDrawable(Color.BLACK))
                .showImageForEmptyUri(R.drawable.img_photo_loading)
                .showImageOnFail(android.R.drawable.ic_delete)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
    }

    private ImageView         mImageView;
    private PhotoViewAttacher mPhotoViewAttacher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_big_image, container,false);
        mImageView = view.findViewById(R.id.image_view);

        Bundle arguments = getArguments();
        if(arguments == null)  return view;

        String photoname = arguments.getString("photoname");

        ImageLoader.getInstance().displayImage("file://"+photoname,mImageView,options,mImageLoadingListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPhotoViewAttacher = new PhotoViewAttacher(mImageView);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPhotoViewAttacher.cleanup();
        mPhotoViewAttacher = null;
    }

    private SimpleImageLoadingListener mImageLoadingListener = new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            if(mPhotoViewAttacher != null){
                mPhotoViewAttacher.update();
            }
        }
    };
}
