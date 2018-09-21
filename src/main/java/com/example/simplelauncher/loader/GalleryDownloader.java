package com.example.simplelauncher.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.io.InputStream;

public class GalleryDownloader extends BaseImageDownloader {
    private static final String THUMBNAIL_PREFIX = "thumbnail://";
    private static final Object GEN_LOCK = new Object();

    public GalleryDownloader(Context context) {
        super(context);
    }

    @Override
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        if(imageUri.startsWith(THUMBNAIL_PREFIX)){
            Cursor cursor = null;
            InputStream inputStream = null;
            String substring = imageUri.substring(THUMBNAIL_PREFIX.length());
            try{
                long _id = Long.parseLong(substring);
                ContentResolver resolver = context.getContentResolver();

                cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(resolver,
                        _id,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        new String[]{MediaStore.Images.ImageColumns.DATA});
                if(cursor != null){
                    if(cursor.getCount() == 0){
                        synchronized (GEN_LOCK){
                            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(resolver, _id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                            if(bitmap != null){
                                bitmap.recycle();
                                inputStream = getStreamFromOtherSource(imageUri,extra);
                            }
                        }
                    }else{
                        cursor.moveToFirst();
                        int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                        String path = cursor.getString(index);
//                        inputStream = resolver.openInputStream(Uri.parse(path));
                        inputStream = getStreamFromFile("file://"+path,extra);
                    }
                }

            }catch (NumberFormatException e){
                Log.e("ERR","not a id :"+substring);
            }catch (IOException e){
                Log.e("ERR","file exception:"+e.getMessage());
            } finally {
                if(cursor !=  null){
                    cursor.close();
                }
            }
            return inputStream;
        }else{
            return super.getStreamFromOtherSource(imageUri,extra);
        }
    }

//        if(extra instanceof String){
//            String str = (String) extra;
//            if(str.equals("corrupt")){
//                cursor = MediaStore.Images.Media.query(resolver,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        new String[]{MediaStore.Images.ImageColumns.DATA},
//                        "_id = "+_id,null
//                );
//                if(cursor != null && cursor.getCount() != 0){
//                    cursor.moveToFirst();
//                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//                    String path = cursor.getString(index);
//                    InputStream is = getStreamFromFile("file://"+path,extra);
//                    return is;
//                }
//            }
//        }}
}
