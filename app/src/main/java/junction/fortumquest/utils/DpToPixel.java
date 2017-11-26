package junction.fortumquest.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DpToPixel {
    public static float convert(float dp, Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
