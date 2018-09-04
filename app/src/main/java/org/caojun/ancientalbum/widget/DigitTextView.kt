package org.caojun.ancientalbum.widget

import android.content.Context
import android.widget.TextView
import android.graphics.Typeface
import android.util.AttributeSet

class DigitTextView: TextView {

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        try {
            typeface = Typeface.createFromAsset(context.assets, "digit.TTF")
        } catch (e: Exception) {
        }
    }
}