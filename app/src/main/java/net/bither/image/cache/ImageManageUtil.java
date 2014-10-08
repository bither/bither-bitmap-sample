/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.image.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.Window;

import net.bither.image.BitherApplication;

public class ImageManageUtil {
    private static final int IMAGE_AVATAR_WIDTH_DP = 36;

    public static final int IMAGE_WIDTH = 612;
    public static final int MIDDLE_IMAGE_WIDTH = 305;
    public static final int SMALL_IMAGE_WIDTH = 150;
    public static final int IMAGE_SAVE_WIDTH = IMAGE_WIDTH * 2;
    public static final int IMAGE_ME_SAVE_QUALITY = 80;
    public static final int IMAGE_PICTURES_SAVE_QUALITY = 95;
    public static int PIIMAGE_MIDLLE_SIZE = getScreenWidth() - dip2pix(10);


    private static Bitmap getRoundCornerBitmapFromRes(int id) {
        Bitmap bit = BitmapFactory.decodeResource(
                BitherApplication.mContext.getResources(), id);
        bit = getRoundCornerBitmap(bit);
        return bit;
    }

    public static Bitmap getBitmapFromAsset(String path) {
        try {
            return BitmapFactory.decodeStream(BitherApplication.mContext
                    .getAssets().open(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Bitmap getNoImageAvailableBitmap(int width, int height) {
        Bitmap bitmap = getBitmapFromAsset("Image/no_image_available.jpg");
        return getThumbnailBitmap(bitmap, width, height, true);
    }

    public static Bitmap getImageRemovedBitmap(int width, int height) {
        Bitmap bitmap = getBitmapFromAsset("Image/image_removed.jpg");
        return getThumbnailBitmap(bitmap, width, height, true);
    }


    public static Bitmap getImageRemovedSmallBitmap(int width, int height) {
        Bitmap bitmap = getBitmapFromAsset("Image/image_removed_small.jpg");
        return getThumbnailBitmap(bitmap, width, height, true);
    }

    public static final int dip2pix(float dip) {
        final float scale = BitherApplication.mContext.getResources()
                .getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public static final int getSmallImageCacheWidth() {
        return Math.min((getScreenWidth() - dip2pix(9) * 2) / 3,
                SMALL_IMAGE_WIDTH);
    }


    public static int getScreenWidth() {
        return BitherApplication.mContext.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return BitherApplication.mContext.getResources().getDisplayMetrics().heightPixels;
    }

    // operate bitmap

    public static Bitmap getThumbnailBitmap(Bitmap source, int width,
                                            int height, boolean needRecycleSource) {
        if (source == null)
            return source;
        if (Math.min(source.getWidth(), source.getHeight()) > Math.min(width,
                height)) {
            Bitmap result = null;
            if (needRecycleSource) {
                result = extractThumbnail(source, width, height,
                        OPTIONS_RECYCLE_INPUT);
            } else {
                result = extractThumbnail(source, width, height);
            }
            return result;
        } else {
            return source;
        }
    }

    public static Bitmap getMatrixBitmap(Bitmap bm, int w, int h,
                                         boolean needRecycleSource) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        boolean isCompress = (width > w && height > h) && (w != 0 && h != 0)
                && (w != width || h != height);
        if (isCompress) {
            float scaleWidth = ((float) w) / width;
            float scaleHeight = ((float) h) / height;
            float scale = Math.max(scaleWidth, scaleHeight);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                    matrix, true);

            if (needRecycleSource && bm != null && bm != bitmap) {
                bm.recycle();
            }
            return bitmap;
        } else {
            return bm;
        }
    }

    public static Bitmap getMatrixBitmap(File file, int w, int h) {
        try {

            if (file == null || !file.exists()) {
                return null;
            }
            Bitmap bm = getBitmap(file, w, h);
            Bitmap result = getMatrixBitmap(bm, w, h, true);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {

        }
    }

    public static Bitmap getBitmap(File file, int width, int height) {
        try {
            final Bitmap bm;

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            if (file == null || !file.exists()) {
                return null;
            } else if (file.length() == 0) {
                file.delete();
                return null;
            }
            FileInputStream fileInputStream;

            fileInputStream = new FileInputStream(file);

            BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

            int be = 1;
            if (height != 0) {
                be = (int) (opts.outHeight / (float) width);
                if (be <= 0)
                    be = 1;
            }

            opts.inSampleSize = be;
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Config.ARGB_8888;

            bm = BitmapFactory.decodeStream(fileInputStream, null, opts);
            fileInputStream.close();
            return bm;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    public static Bitmap getRotationOfHorizontal(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        if (result != bitmap) {
            bitmap.recycle();
        }
        return result;

    }


    public static final Bitmap getRoundCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null)
            return bitmap;
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        final int color = 0xff010101;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }


    public static Bitmap getSquareBitmapFromOriBitmap(byte[] bytes,
                                                      int Orientation) {
        Bitmap bit = getBitmapNearestSize(bytes,
                IMAGE_SAVE_WIDTH);
        int oriSize = Math.min(bit.getWidth(), bit.getHeight());
        int targetSize = Math.min(oriSize, IMAGE_SAVE_WIDTH);
        Bitmap bmp = Bitmap.createBitmap(targetSize, targetSize,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.save();
        canvas.rotate(Orientation);
        Rect srcRect;
        if ((Orientation % 360) < 180) {
            srcRect = new Rect(0, 0, oriSize, oriSize);
        } else {
            srcRect = new Rect(bit.getWidth() - oriSize, bit.getHeight()
                    - oriSize, bit.getWidth(), bit.getHeight());
        }
        Rect destRect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        Matrix matrix = new Matrix();
        matrix.postRotate(-Orientation);
        RectF destRectF = new RectF(destRect);
        matrix.mapRect(destRectF);
        canvas.drawBitmap(bit, srcRect, destRectF, null);
        bit.recycle();
        canvas.restore();
        return bmp;
    }

    private static Bitmap getBitmapNearestSize(byte[] bytes, int size) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            int bmpSize = Math.min(opts.outHeight, opts.outWidth);
            opts.inSampleSize = getSampleSize(bmpSize, size);
            opts.inJustDecodeBounds = false;
            opts.inPurgeable = true;
            opts.inInputShareable = false;
            opts.inPreferredConfig = Config.ARGB_8888;
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getSampleSize(int fileSize, int targetSize) {
        int sampleSize = 1;
        if (fileSize > targetSize * 2) {
            int sampleLessThanSize = 0;
            do {
                sampleLessThanSize++;
            } while (fileSize / sampleLessThanSize > targetSize);

            for (int i = 1; i <= sampleLessThanSize; i++) {
                if (Math.abs(fileSize / i - targetSize) <= Math.abs(fileSize
                        / sampleSize - targetSize)) {
                    sampleSize = i;
                }
            }
        } else {
            if (fileSize <= targetSize) {
                sampleSize = 1;
            } else {
                sampleSize = 2;
            }
        }
        return sampleSize;
    }

    public static Bitmap getBitmapNearestSize(String fileName, int size) {
        try {
            File file = new File(fileName);
            if (file == null || !file.exists()) {
                return null;
            } else if (file.length() == 0) {
                file.delete();
                return null;
            }
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fileName, opts);
            int sampleSize = getSampleSize(
                    Math.min(opts.outHeight, opts.outWidth), size);
            opts.inSampleSize = sampleSize;
            opts.inJustDecodeBounds = false;
            opts.inPurgeable = true;
            opts.inInputShareable = false;
            opts.inPreferredConfig = Config.ARGB_8888;
            Bitmap bit = BitmapFactory.decodeFile(fileName, opts);
            return bit;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Bitmap byteArrayToBitmap(byte[] bytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inInputShareable = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    public static final Bitmap getRoundCornerBitmap(Bitmap bitmap) {
        if (bitmap == null)
            return bitmap;
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        float roundPx;
        final int color = 0xff010101;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        roundPx = 7;
        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        return output;
    }


    public static Bitmap getBitmapFromView(View v, int width, int height) {
        v.measure(width, height);
        v.layout(0, 0, width, height);
        Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        v.draw(new Canvas(bmp));
        return bmp;
    }


    public static Bitmap getBitmapFromView(View v) {
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Config.ARGB_8888);
        v.draw(new Canvas(bmp));
        return bmp;
    }


    public static Bitmap CorverBitmap(Bitmap base, Bitmap cover) {
        if (base != null && cover != null) {
            int width = base.getWidth();
            int height = base.getHeight();
            Canvas c = new Canvas(base);
            Paint paint = new Paint();
            c.drawBitmap(cover, null, new Rect(0, 0, width, height), paint);
            cover.recycle();
            cover = null;
        }
        return base;
    }


    public static final int getStatusBarHeight(Window window) {
        Rect frame = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * Copied from android ThumbnailUtils, we don't want the 0.9 threshold
     * <p/>
     * Creates a centered bitmap of the desired size.
     *
     * @param source  original bitmap source
     * @param width   targeted width
     * @param height  targeted height
     * @param options options used during thumbnail extraction
     */
    public static Bitmap extractThumbnail(Bitmap source, int width, int height,
                                          int options) {
        if (source == null) {
            return null;
        }

        float scale;
        if (source.getWidth() < source.getHeight()) {
            scale = width / (float) source.getWidth();
        } else {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap thumbnail = transform(matrix, source, width, height,
                OPTIONS_SCALE_UP | options);
        return thumbnail;
    }

    /**
     * Creates a centered bitmap of the desired size.
     *
     * @param source original bitmap source
     * @param width  targeted width
     * @param height targeted height
     */
    public static Bitmap extractThumbnail(Bitmap source, int width, int height) {
        return extractThumbnail(source, width, height, OPTIONS_NONE);
    }

    /**
     * Copied from android ThumbnailUtils, we don't want the 0.9 threshold
     * Transform source Bitmap to targeted width and height.
     */
    private static Bitmap transform(Matrix scaler, Bitmap source,
                                    int targetWidth, int targetHeight, int options) {
        boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
        boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
             * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
                    Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
                    + Math.min(targetWidth, source.getWidth()), deltaYHalf
                    + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight
                    - dstY);
            c.drawBitmap(source, src, dst, null);
            if (recycle) {
                source.recycle();
            }
            c.setBitmap(null);
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            scaler.setScale(scale, scale);
        } else {
            float scale = targetWidth / bitmapWidthF;
            scaler.setScale(scale, scale);
        }

        Bitmap b1;
        b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), scaler, true);

        if (recycle && b1 != source) {
            source.recycle();
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
                targetHeight);

        if (b2 != b1) {
            if (recycle || b1 != source) {
                b1.recycle();
            }
        }

        return b2;
    }

    private static final int OPTIONS_NONE = 0x0;
    private static final int OPTIONS_SCALE_UP = 0x1;
    public static final int OPTIONS_RECYCLE_INPUT = 0x2;


}
