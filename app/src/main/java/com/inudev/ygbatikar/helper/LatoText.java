package com.inudev.ygbatikar.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Rem on 11/22/2017 on 8:15 PM.
 * YGbatikAR.
 * inutwp@gmail.com
 */

@SuppressLint("AppCompatCustomView")
public class LatoText extends TextView {
    public LatoText(Context context) {
        this(context, null);
    }

    public LatoText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.setTypeface(TypefaceUtil.getLatoRegular(context));
    }
}
