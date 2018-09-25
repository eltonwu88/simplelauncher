package com.example.simplelauncher;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class ContactsActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private static final int LOADER_CONTACTS = 0;
    private ContactsAdapter mContactsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        GridView gridView = findViewById(R.id.contacts_view);
        mContactsAdapter = new ContactsAdapter(this,null);
        gridView.setAdapter(mContactsAdapter);

        gridView.setOnItemClickListener(this);

        getLoaderManager().initLoader(LOADER_CONTACTS,null,this);

//        Uri uri = CommonDataKinds.Phone.CONTENT_URI;
//        String[] projection = new String[]{
//                BaseColumns._ID,
//                Contacts.Entity.DISPLAY_NAME,
//                Contacts.Entity.PHOTO_THUMBNAIL_URI,
//                CommonDataKinds.Phone.NUMBER,
//                CommonDataKinds.Photo.PHOTO,
//        };
//
//        Cursor cursor = getContentResolver().query(uri, projection, null, null, null, null);
//        if (cursor != null) {
//            int count = cursor.getCount();
//            Log.i("TEST", "count :" + count);
//            while (cursor.moveToNext()) {
//                int index1 = cursor.getColumnIndex(Contacts.Entity.DISPLAY_NAME);
//                int index2 = cursor.getColumnIndex(Contacts.Entity.PHOTO_THUMBNAIL_URI);
//                int index3 = cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER);
//
//                String name = cursor.getString(index1);
//                String thumb = cursor.getString(index2);
//                String number = cursor.getString(index3);
//
//                Log.i("TEST", "name :" + name + ", number:" + number + ", uri:" + thumb);
//            }
//        }
        Log.e("TEST","Contacts create :"+getTaskId());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                BaseColumns._ID,
                Contacts.Entity.DISPLAY_NAME,
                Contacts.Entity.PHOTO_URI,
                CommonDataKinds.Phone.NUMBER,
                CommonDataKinds.Photo.PHOTO,
        };

        return new CursorLoader(this,uri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mContactsAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = (String) parent.getItemAtPosition(position);
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TEST","Contacts resume :"+getTaskId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContactsAdapter.setCursor(null);
        Log.e("TEST","Contacts Destory");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.scale_up,R.anim.scale_down);
    }
}
