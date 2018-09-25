package com.example.simplelauncher;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

public class GalleryActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final int LOADER_GALLERY = 0;
    private static final int REQ_START_BIG  = 0;

    private GalleryAdapter mGalleryAdapter;

    private String[] mImagePaths;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_gallery);
        GridView gridView = findViewById(R.id.list);
        mGalleryAdapter = new GalleryAdapter(this,new String[]{});
        gridView.setAdapter(mGalleryAdapter);
        gridView.setOnItemClickListener(this);
        getLoaderManager().initLoader(LOADER_GALLERY,null,this);

        Transition transition = TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
        getWindow().setExitTransition(transition);
    }

    private void test(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, null, null, null, null);
        if(cursor != null){
            int count = cursor.getCount();
            Log.i("TEST","count : "+count);
            String[] names = cursor.getColumnNames();
            for(String name : names){
                Log.i("TEST","name:"+name);
            }
            while(cursor.moveToNext()){
                int index = cursor.getColumnIndex("_data");
                String _data = cursor.getString(index);
                index = cursor.getColumnIndex("image_id");
                String image_id = cursor.getString(index);
                index = cursor.getColumnIndex("kind");
                String kind = cursor.getString(index);
                Log.i("TEST","id :"+image_id+", data:"+_data+", kind:"+kind);
            }
            cursor.close();
        }
        cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if(cursor != null){
            int count = cursor.getCount();
            Log.i("TEST","count : "+count);
            String[] names = cursor.getColumnNames();
            for(String name : names){
                Log.i("TEST","name:"+name);
            }
            while(cursor.moveToNext()){
                int index = cursor.getColumnIndex("_data");
                String _data = cursor.getString(index);
                index = cursor.getColumnIndex("_id");
                String image_id = cursor.getString(index);
                index = cursor.getColumnIndex("mime_type");
                String kind = cursor.getString(index);
                Log.i("TEST","id :"+image_id+", data:"+_data+", kind:"+kind);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{MediaStore.Images.ImageColumns._ID,MediaStore.Images.ImageColumns.DATA};
        return new CursorLoader(this,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null){
            String[] paths = new String[data.getCount()];
            mImagePaths   = new String[data.getCount()];
            int i=0;
            while(data.moveToNext()){
                int index = data.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                int index2= data.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                paths[i] = data.getString(index);
                mImagePaths[i] = data.getString(index2);
                i++;
            }
            data.close();
            mGalleryAdapter.reload(paths);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_START_BIG){
            if(resultCode == RESULT_OK){
                if(data != null){
                    int position = data.getIntExtra("position", 0);
                    if(position >= 0 && position < mImagePaths.length){
                        GridView gridView = findViewById(R.id.list);
                        gridView.smoothScrollToPosition(position,1);
                    }
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this,ImageActivity.class);
        intent.putExtra("image_address",mImagePaths);
        intent.putExtra("position",position);
        startActivityForResult(intent,REQ_START_BIG,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TEST","Gallery Destory");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.scale_up,R.anim.scale_down);
    }
}
