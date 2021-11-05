package com.inudev.ygbatikar.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Rem on 2/7/2018 on 9:20 AM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

@SuppressLint("AppCompatCustomView")
public class NeoSansLight extends TextView{
    public NeoSansLight(Context context) {
        this(context, null);
    }

    public NeoSansLight(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setTypeface(TypefaceUtil.getNeoSansLight(context));
    }
}
