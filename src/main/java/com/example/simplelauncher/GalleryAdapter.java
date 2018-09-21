package com.example.simplelauncher;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;

public class GalleryAdapter extends BaseAdapter {
    private ArrayList<String> mArrays = new ArrayList<>();
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    public GalleryAdapter(@NonNull Context context,String[] paths) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Collections.addAll(mArrays, paths);
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_photo_loading)
                .showImageForEmptyUri(R.drawable.img_photo_loading)
                .showImageOnFail(R.drawable.img_photo_loading)
                .cacheInMemory(true)
                .cacheOnDisk(true)

                .build();
    }

    public void reload(String[] newPaths){
        mArrays.clear();
        Collections.addAll(mArrays, newPaths);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mArrays.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.gallery_item,parent,false);
        }
        ImageView imageView = (ImageView) convertView;
        String uri = "thumbnail://"+mArrays.get(position);
        mImageLoader.displayImage(uri,imageView,options);

        return convertView;
    }
}
