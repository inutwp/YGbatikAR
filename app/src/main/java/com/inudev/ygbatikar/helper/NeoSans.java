package com.inudev.ygbatikar.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Rem on 2/2/2018 on 4:44 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

@SuppressLint("AppCompatCustomView")
public class NeoSans extends TextView {
    public NeoSans(Context context) {
        this(context, null);
    }

    public NeoSans(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setTypeface(TypefaceUtil.getNeoSans(context));
    }
}
