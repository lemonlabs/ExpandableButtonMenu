package lt.lemonlabs.android.expandablebuttonmenu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;


/**
 * Screen size and other metrics helper
 */
public class ScreenHelper {

    private static int mWidth;
    private static int mHeight;

    public static int getScreenWidth(Context context) {
        if (mWidth == 0) {
            calculateScreenDimensions(context);
        }
        return mWidth;
    }

    public static int getScreenHeight(Context context) {
        if (mHeight == 0) {
            calculateScreenDimensions(context);
        }
        return mHeight;
    }

    private static void calculateScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final Point point = new Point();
            display.getSize(point);
            mWidth = point.x;
            mHeight = point.y;
        } else {
            mWidth = display.getWidth();
            mHeight = display.getHeight();
        }
    }

    public static float dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
