package android.support.v4.view;

import android.view.*;
import android.graphics.*;

class ViewCompatJellybeanMr1
{
    public static int getLabelFor(final View view) {
        return view.getLabelFor();
    }
    
    public static int getLayoutDirection(final View view) {
        return view.getLayoutDirection();
    }
    
    public static void setLabelFor(final View view, final int labelFor) {
        view.setLabelFor(labelFor);
    }
    
    public static void setLayerPaint(final View view, final Paint layerPaint) {
        view.setLayerPaint(layerPaint);
    }
    
    public static void setLayoutDirection(final View view, final int layoutDirection) {
        view.setLayoutDirection(layoutDirection);
    }
}
