package net.bither.image.cache;

import android.graphics.Bitmap;

public abstract interface BitmapCallback {
	public abstract void bindToTask(
            AsyncTask<Object, Integer, Bitmap> paramLoadBitmapTask);

	public abstract void reportError();

	public abstract void reportProgress(String paramString, int paramInt);

	public abstract void setBitmap(String paramString, Bitmap paramBitmap);
}
