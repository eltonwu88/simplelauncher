package com.example.simplelauncher;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ContactsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Cursor mCursor;
    private float  mDensity;

    public ContactsAdapter(Context context,Cursor cursor){
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCursor = cursor;
        mDensity = context.getResources().getDisplayMetrics().density;
    }

    public void setCursor(Cursor cursor){
        if(cursor == mCursor) return;

        if(mCursor != null){
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(mCursor != null){
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(mCursor != null){
            if(mCursor.moveToPosition(position)){
                int index = mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                if(index != -1){
                    return mCursor.getString(index);
                }
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.contacts_grid_item,parent,false);
        }
        if(convertView instanceof TextView){
            TextView tv = (TextView) convertView;
            if(mCursor != null){
                if(mCursor.moveToPosition(position)){
                    int index = mCursor.getColumnIndex(ContactsContract.Contacts.Entity.DISPLAY_NAME);
                    if(index != -1){
                        tv.setText(mCursor.getString(index));
                    }
                    index = mCursor.getColumnIndex(ContactsContract.Contacts.Entity.PHOTO_URI);
                    if(index != -1){
                        String uri = mCursor.getString(index);
                        Drawable drawable;
                        if(uri == null){
                            drawable = convertView.getResources().getDrawable(R.drawable.ic_launcher);
                        }else{
                            try {
                                InputStream inputStream = convertView.getContext().getContentResolver().openInputStream(Uri.parse(uri));
                                if(inputStream != null){
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    drawable = new BitmapDrawable(convertView.getResources(),bitmap);
                                    inputStream.close();
                                }else{
                                    drawable = convertView.getResources().getDrawable(R.drawable.ic_launcher);
                                }
                            } catch (IOException ignored) {
                                drawable = convertView.getResources().getDrawable(R.drawable.ic_launcher);
                            }
                        }
                        drawable.setBounds(0,0,(int)(160*mDensity),(int)(160*mDensity));
                        tv.setCompoundDrawables(null,drawable,null,null);
                    }
                }
            }
        }

        return convertView;
    }
}
