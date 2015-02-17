package debas.com.beaconnotifier;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by debas on 17/02/15.
 */
public class TypefacedTextView extends TextView
{

    public TypefacedTextView(Context context, AttributeSet attrs)
    {

        super(context, attrs);

        if (attrs != null)
        {
            // Get Custom Attribute Name and value
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
            int typefaceCode = styledAttrs.getInt(R.styleable.TypefacedTextView_fontStyle, -1);
            styledAttrs.recycle();

            // Typeface.createFromAsset doesn't work in the layout editor.
            // Skipping...
            if (isInEditMode())
            {
                return;
            }

            Typeface typeface = TypefaceCache.get(context.getAssets(), 0);
            setTypeface(typeface);
        }
    }

    public TypefacedTextView(Context context)
    {
        super(context);
    }

}