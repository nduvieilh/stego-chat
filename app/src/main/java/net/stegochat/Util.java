package net.stegochat;

import android.graphics.Bitmap;

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
}

