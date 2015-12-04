package net.stegochat;

import android.graphics.Bitmap;
import android.widget.EditText;

/**
 * Created by bengranberry on 12/3/15.
 */
public class Util {
    public static Bitmap resizeBitmap(Bitmap mBitmap){
        int [] sizes = new int[2];

        sizes[0] = mBitmap.getWidth();
        sizes[1] = mBitmap.getHeight();
        int max = Math.max(sizes[0], sizes[1]);
        while(sizes[0] > 2000 || sizes[1] > 2000) {
            if (sizes[0] == max) {
                double ratio = (double)2000 / (double)sizes[0];
                sizes[0] = 2000;
                sizes[1] = (int) (sizes[1] * ratio);
            }
            else {
                double ratio = 2000 / sizes[1];
                sizes[1] = 2000;
                sizes[0] = (int) (sizes[0] * ratio);
            }
        }

        return Bitmap.createScaledBitmap(mBitmap, sizes[0], sizes[1], false);
    }

    /*
        Checks if the EditText object has text or not for
        use in conditionals.

        @author Nicky Duvieilh

        @param EditText etText the text object to check

        @return Boolean return true if EditText is empty, false otherwise
     */
    public static Boolean isEmptyText(EditText etText) {
        if(etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }
}

