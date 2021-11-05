package com.inudev.ygbatikar.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by Rem on 11/22/2017 on 8:05 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

public class TypefaceUtil {
    private static final String TAG = "TypefaceUtil";

    public static void overrideFont(Context context, String defaultFontNameToOverride) {
        String customFont = "fonts/Lato-Regular.ttf";
        Log.d(TAG, "d : " + defaultFontNameToOverride);
        Log.d(TAG, "d :" + customFont);
        try {
            final Typeface customTypeface = Typeface.createFromAsset(context.getAssets(), customFont);
            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customTypeface);
            Log.d(TAG, "Succes : font have been set to Open sans!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "overrodeFont :" + "Cant set custom Font" + customFont);
            Log.d(TAG, "instead of " + defaultFontNameToOverride);
        }
    }

    public static void setTypefaceLatoRegular(Context context, TextView textView){
        final Typeface typeFaceLatoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Regular.ttf");
        textView.setTypeface(typeFaceLatoRegular);
    }

    public static Typeface getLatoRegular(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Regular.ttf");
    }

    public static Typeface getNeoSans(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-Bold.ttf");
    }

    public static Typeface getNeoSansLight(Context context){
        return Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-Light.ttf");
    }
}
