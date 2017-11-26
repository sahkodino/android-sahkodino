package junction.fortumquest.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class DisplaySize {
    private static float height = Float.MIN_VALUE;
    private static float width = Float.MIN_VALUE;

    public static float height(Context context) {
        if (height == Float.MIN_VALUE) {
            Display disp = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            disp.getSize(size);
            height = size.y;
        }

        return height;
    }

    public static float width(Context context) {
        if (width == Float.MIN_VALUE) {
            Display disp = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            disp.getSize(size);
            width = size.x;
        }

        return width;
    }

}
