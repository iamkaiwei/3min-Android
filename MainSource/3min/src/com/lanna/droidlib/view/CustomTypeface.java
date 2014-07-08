package com.lanna.droidlib.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.threemins.R;
import com.threemins.R.styleable;



public class CustomTypeface {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created
     * every time when they are referenced.
     */
    private static Map<String, Typeface> mTypefaces;

	public static void setCustomTypeface(Context context, AttributeSet attrs, TextView textView) {
		if (textView == null) {
			return;
		}
		
        if (mTypefaces == null) {
            mTypefaces = new HashMap<String, Typeface>();
        }
        
		final TypedArray array = context.obtainStyledAttributes(attrs, styleable.CustomFontTextView);
        if (array == null) {
        	setTypeface(context, textView);
        }
        else {
            String typefaceAssetPath = array.getString(R.styleable.CustomFontTextView_customTypeface);
            setTypeface(context, textView, typefaceAssetPath);
            array.recycle();
        }
	}

	public static void setTypeface(Context context, TextView textView, String... customTypefaceAssetPath) {
		String typefaceAssetPath = (customTypefaceAssetPath != null && customTypefaceAssetPath.length >= 1) 
				? customTypefaceAssetPath[0] : "";
        if (TextUtils.isEmpty(typefaceAssetPath)) {
        	return;
        }
        
//		String suffix = typefaceAssetPath.substring(typefaceAssetPath.length() - 4, typefaceAssetPath.length());
//		if (!suffix.equalsIgnoreCase(".ttf")
//				&& !suffix.equalsIgnoreCase(".otf"))
//			typefaceAssetPath += ".ttf";
			
		Typeface typeface = null;
		if (mTypefaces.containsKey(typefaceAssetPath)) {
			typeface = mTypefaces.get(typefaceAssetPath);
		} else {
			boolean isBoldFontSet = false;
			if (textView.getTypeface() == Typeface.DEFAULT_BOLD) {
				try {
		    		typeface = Typeface.createFromAsset(context.getAssets(), typefaceAssetPath + "Bold");
		    		isBoldFontSet = true;
				} catch (Exception e) {
					Log.e("CustomTypeface", "font not found in assets: " + typefaceAssetPath);
				}
			}
			if (!isBoldFontSet) {
				try {
		            typeface = Typeface.createFromAsset(context.getAssets(), typefaceAssetPath);
				} catch (Exception e) {
					Log.e("CustomTypeface", "font not found in assets: " + typefaceAssetPath);
				}
			}
		    
		    if (typeface != null) {
		    	mTypefaces.put(typefaceAssetPath, typeface);
		    }
		}
		
		if (typeface != null) {
        	textView.setTypeface(typeface);
        }
	}

}
