package com.example.simplelauncher;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CustomFontTextView extends AppCompatTextView {
    private float letterSpacing = 0;
    private CharSequence mText = "";

    public CustomFontTextView(Context context) {
        super(context);
    }

    public CustomFontTextView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CustomFontTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewExTheme,defStyleAttr,0);
        String  font       = typedArray.getString(R.styleable.TextViewExTheme_ex_font_type);
        letterSpacing     = typedArray.getFloat(R.styleable.TextViewExTheme_ex_letter_spacing,0);
        typedArray.recycle();

        if(font != null){
            AssetManager assetManager = context.getAssets();
            Typeface typeface = Typeface.createFromAsset(assetManager, font);
            setTypeface(typeface);
        }

        if(letterSpacing != 0){
            applyLetterSpacing();
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        mText = text;
        applyLetterSpacing();
    }

    private Field findField(Class<?> clazz,String field_name){
        Field field = null;
        try {
            field = clazz.getDeclaredField(field_name);
        } catch (NoSuchFieldException e) {
            Log.i("Reflect",field_name+" not found in "+clazz.getCanonicalName());
            Class<?> klazz = clazz.getSuperclass();
            if(klazz !=null){
                field = findField(klazz,field_name);
            }else{
                Log.w("Reflect",field_name +" not found");
            }
        }
        return field;
    }

    public void setMarqueeSpeed(float speed){
        try {
            Field f = findField(getClass(),"mMarquee");
            if(f != null){
                f.setAccessible(true);
                Object marquee = f.get(this);
                if(marquee == null){
                    Class<?> marquee_type = f.getType();
                    Constructor<?> constructor = marquee_type.getDeclaredConstructor(TextView.class);
                    constructor.setAccessible(true);
                    Object new_marquee = constructor.newInstance(this);
                    f.set(this,new_marquee);
                    marquee = f.get(this);
                }
                if(marquee != null){
                    String speed_field = "mScrollUnit";
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        speed_field = "mPixelsPerSecond";
                    }
                    Field mf = marquee.getClass().getDeclaredField(speed_field);
                    mf.setAccessible(true);
                    mf.setFloat(marquee,speed);
                }
            }
        }catch (Exception e){
            Log.e("Reflect",e.getMessage());
        }
    }

    public void setExLetterSpacing(float letter_spacing) {
        if (letter_spacing != 0 && Float.compare(letterSpacing, letter_spacing) != 0) {
            letterSpacing = letter_spacing;
            applyLetterSpacing();
        }
    }

    @SuppressWarnings("deprecation")
    private void applyLetterSpacing() {
        if(letterSpacing == 0) return;

        String language = getResources().getConfiguration().locale.getLanguage();
        if(!(language.equals("zh"))){
            //only apply spacing for chinese
            return;
        }

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < mText.length(); i++) {
            builder.append(mText.charAt(i));
            if(i+1 < mText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if(builder.toString().length() > 1) {
            for(int i = 1; i < builder.toString().length(); i+=2) {
                finalText.setSpan(new ScaleXSpan((letterSpacing+1)/10), i, i+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        float width = getPaint().measureText(finalText.toString());
        if(width >= getWidth()){
            super.setText(mText, BufferType.NORMAL);
        }else{
            super.setText(finalText, BufferType.SPANNABLE);
        }


    }
}
