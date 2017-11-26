package junction.fortumquest.utils;

import android.content.Context;
import android.graphics.Typeface;

public class FontProvider {

    private static Typeface typeface = null;

    public static Typeface getTypeface(Context context) {
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.getAssets(),
                "fonts/Supercell-magic-webfont.ttf");
        }

        return typeface;
    }

}
