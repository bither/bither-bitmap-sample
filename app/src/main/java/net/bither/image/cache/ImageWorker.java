/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.image.cache;

import java.lang.ref.WeakReference;

import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView;

import net.bither.image.BuildConfig;
import net.bither.image.exception.Http404Exception;



/**
 * This class wraps up completing some arbitrary long running work when loading
 * a bitmap to an ImageView. It handles things like using a memory and disk
 * cache, running the work in a background thread and setting a placeholder
 * image.
 */
public abstract class ImageWorker {
    private static final String TAG = "ImageWorker";
    private static final int FADE_IN_TIME = 200;

    private ImageCache mImageCache;
    private ImageCache.ImageCacheParams mImageCacheParams;
    private Bitmap mLoadingBitmap;
    private boolean mFadeInBitmap = true;
    private boolean mExitTasksEarly = false;
    protected boolean mPauseWork = false;
    private final Object mPauseWorkLock = new Object();

    protected Resources mResources;

    private static final int MESSAGE_CLEAR = 0;
    private static final int MESSAGE_INIT_DISK_CACHE = 1;
    private static final int MESSAGE_FLUSH = 2;
    private static final int MESSAGE_CLOSE = 3;

    protected ImageWorker(Context context) {
        mResources = context.getResources();
    }

    /**
     * Load an image specified by the data parameter into an ImageView (override
     * {@link (Object)} to define the processing
     * logic). A memory and disk cache will be used if an {@link ImageCache} has
     * been set using {@link net.bither.image.cache.ImageWorker#setImageCache(ImageCache)}. If the
     * image is found in the memory cache, it is set immediately, otherwise an
     * {@link AsyncTask} will be created to asynchronously load the bitmap.
     *
     * @param data      The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    public void loadImage(Object data, ImageView imageView,
                          boolean isAddMemoryCache) {
        // ��Ӳ��� --addMemoryCache
        if (data == null) {
            return;
        }

        Bitmap bitmap = null;
        // ���ʹ��MemoryCache��Ҳ����MemoryCahce�ж����--addMemoryCache
        if (isAddMemoryCache) {
            if (mImageCache != null) {
                bitmap = mImageCache
                        .getBitmapFromMemCache(String.valueOf(data));
            }
        }

        if (bitmap != null) {
            // Bitmap found in memory cache
            imageView.setImageBitmap(bitmap);
            imageView.setTag(null);// �ӻ����ж�ȡ��ʱ�����ý�����Ĳ���Ϊnull,--�����
        } else if (cancelPotentialWork(data, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources,
                    mLoadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);

            // NOTE: This uses a custom version of AsyncTask that has been
            // pulled from the
            // framework and slightly modified. Refer to the docs at the top of
            // the class
            // for more info on what was changed.
            task.executeOnExecutor(AsyncTask.DUAL_THREAD_EXECUTOR, data,
                    isAddMemoryCache);
        }
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is
     * running.
     *
     * @param bitmap
     */
    public void setLoadingImage(Bitmap bitmap) {
        mLoadingBitmap = bitmap;
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is
     * running.
     *
     * @param resId
     */
    public void setLoadingImage(int resId) {
        mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
    }

    /**
     * Adds an {@link ImageCache} to this worker in the background (to prevent
     * disk access on UI thread).
     *
     * @param fragmentManager
     * @param cacheParams
     */
    public void addImageCache(FragmentManager fragmentManager,
                              ImageCache.ImageCacheParams cacheParams) {
        mImageCacheParams = cacheParams;
        setImageCache(ImageCache.findOrCreateCache(fragmentManager,
                mImageCacheParams));
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE);
    }

    /**
     * Adds an {@link ImageCache} to this worker in the background (to prevent
     * disk access on UI thread).
     *
     * @param cacheParams
     */
    public void addImageCache(ImageCache.ImageCacheParams cacheParams,
                              boolean checkJournal) {
        // -checkJournal
        mImageCacheParams = cacheParams;
        if (mImageCache == null) {
            mImageCache = new ImageCache(cacheParams, checkJournal);
        }
        new CacheAsyncTask().execute(MESSAGE_INIT_DISK_CACHE, checkJournal);
    }

    /**
     * Sets the {@link ImageCache} object to use with this ImageWorker. Usually
     * you will not need to call this directly, instead use
     * {@link net.bither.image.cache.ImageWorker#addImageCache} which will create and add the
     * {@link ImageCache} object in a background thread (to ensure no disk
     * access on the main/UI thread).
     *
     * @param imageCache
     */
    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    public ImageCache getImageCache() {
        return mImageCache;
    }

    /**
     * If set to true, the image will fade-in once it has been loaded by the
     * background thread.
     */
    public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }

    public void setExitTasksEarly(boolean exitTasksEarly) {
        mExitTasksEarly = exitTasksEarly;
    }

    /**
     * Subclasses should override this to define any processing or work that
     * must happen to produce the final bitmap. This will be executed in a
     * background thread and be long running. For example, you could resize a
     * large bitmap here, or pull down an image from the network.
     *
     * @param data The data to identify which image to process, as provided by
     *             {@link net.bither.image.cache.ImageWorker#(Object, android.widget.ImageView)}
     * @return The processed bitmap
     */
    protected abstract Bitmap processBitmap(Object data,
                                            AsyncTask<Object, Object, Bitmap> bitmapWorkerTask)
            throws Http404Exception;


    /**
     * Cancels any pending work attached to the provided ImageView.
     *
     * @param imageView
     */
    public static void cancelWork(ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if (bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            if (BuildConfig.DEBUG) {
                final Object bitmapData = bitmapWorkerTask.data;
                Log.d(TAG, "cancelWork - cancelled work for " + bitmapData);
            }
        }
    }

    /**
     * Returns true if the current work has been canceled or if there was no
     * work in progress on this image view. Returns false if the work in
     * progress deals with the same data. The work is not stopped in that case.
     */
    public static boolean cancelPotentialWork(Object data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Object bitmapData = bitmapWorkerTask.data;
            if (bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkerTask.cancel(true);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "cancelPotentialWork - cancelled work for "
                            + data);
                }
            } else {
                // The same work is already in progress.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active work task (if any) associated with
     * this imageView. null if there is no such task.
     */
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * The actual AsyncTask that will asynchronously process the image.
     */
    private class BitmapWorkerTask extends AsyncTask<Object, Object, Bitmap> {
        // �޸Ľ�Ȳ�������ΪObject --�����
        private Object data;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        /**
         * Background processing.
         */
        @Override
        protected Bitmap doInBackground(Object... params) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground - starting work");
            }

            data = params[0];
            // �ഫ������ --AddMemoryCache
            boolean isAddMemoryCache = Boolean.valueOf(params[1].toString());
            final String dataString = String.valueOf(data);
            Bitmap bitmap = null;

            // Wait here if work is paused and the task is not cancelled
            synchronized (mPauseWorkLock) {
                while (mPauseWork && !isCancelled()) {
                    try {
                        mPauseWorkLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            // If the image cache is available and this task has not been
            // cancelled by another
            // thread and the ImageView that was originally bound to this task
            // is still bound back
            // to this task and our "exit early" flag is not set then try and
            // fetch the bitmap from
            // the cache
            if (mImageCache != null && !isCancelled()
                    && getAttachedImageView() != null && !mExitTasksEarly) {
                bitmap = mImageCache.getBitmapFromDiskCache(dataString);
            }

            // If the bitmap was not found in the cache and this task has not
            // been cancelled by
            // another thread and the ImageView that was originally bound to
            // this task is still
            // bound back to this task and our "exit early" flag is not set,
            // then call the main
            // process method (as implemented by a subclass)
            boolean isHttp404 = false;
            if (bitmap == null && !isCancelled()
                    && getAttachedImageView() != null && !mExitTasksEarly) {
                try {
                    bitmap = processBitmap(params[0], BitmapWorkerTask.this);
                } catch (Http404Exception e) {
                    e.printStackTrace();
                    bitmap = ImageManageUtil
                            .getNoImageAvailableBitmap(400, 400);
                    isHttp404 = true;
                }
            }

            // If the bitmap was processed and the image cache is available,
            // then add the processed
            // bitmap to the cache for future use. Note we don't check if the
            // task was cancelled
            // here, if it was, and the thread is still running, we may as well
            // add the processed
            // bitmap to our cache as it might be used again in the future
            if (bitmap != null && mImageCache != null && !isHttp404) {
                // ������,--AddMemoryCache
                mImageCache.addBitmapToCache(dataString, bitmap,
                        isAddMemoryCache);
            }

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground - finished work");
            }

            return bitmap;
        }

        /**
         * Once the image is processed, associates it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // if cancel was called on this task or the "exit early" flag is set
            // then we're done
            if (isCancelled() || mExitTasksEarly) {
                bitmap = null;
            }

            final ImageView imageView = getAttachedImageView();
            if (bitmap != null && imageView != null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "onPostExecute - setting bitmap");
                }
                setImageBitmap(imageView, bitmap);
            }
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            if (imageViewReference != null) {//
                ImageView imageView = imageViewReference.get();
                if (imageView != null
                        && imageView.getTag() != null
                        && (imageView.getTag() instanceof FileDowloadProgressListener)) {
                    FileDowloadProgressListener imageProgressListener = (FileDowloadProgressListener) imageView
                            .getTag();
                    imageProgressListener.onCancel();
                }
            }
            super.onCancelled(bitmap);
            synchronized (mPauseWorkLock) {
                mPauseWorkLock.notifyAll();
            }
        }

        //
        @Override
        protected void onProgressUpdate(Object... values) {
            if (!isCancelled()) {
                if (imageViewReference != null && values != null
                        && values.length > 0) {
                    ImageView imageView = imageViewReference.get();
                    if (imageView != null
                            && imageView.getTag() != null
                            && (imageView.getTag() instanceof FileDowloadProgressListener)) {
                        FileDowloadProgressListener imageProgressListener = (FileDowloadProgressListener) imageView
                                .getTag();
                        if (values[0] instanceof FileDowloadProgressListener.ProgressValue) {
                            FileDowloadProgressListener.ProgressValue v = (FileDowloadProgressListener.ProgressValue) values[0];
                            if (v.getDownloadType() == FileDowloadProgressListener.DownloadType.ERROR) {
                                imageView.setImageDrawable(null);
                            }
                            imageProgressListener.onProgress(v);
                        }
                    }
                }
            }
            super.onProgressUpdate(values);
        }

        /**
         * Returns the ImageView associated with this task as long as the
         * ImageView's task still points to this task as well. Returns null
         * otherwise.
         */
        private ImageView getAttachedImageView() {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask) {
                return imageView;
            }

            return null;
        }
    }

    /**
     * A custom Drawable that will be attached to the imageView while the work
     * is in progress. Contains a reference to the actual worker task, so that
     * it can be stopped if a new binding is required, and makes sure that only
     * the last started worker process can bind its result, independently of the
     * finish order.
     */
    public static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(
                    bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    /**
     * Called when the processing is complete and the final bitmap should be set
     * on the ImageView.
     *
     * @param imageView
     * @param bitmap
     */
    private void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        if (mFadeInBitmap) {
            // Transition drawable with a transparent drwabale and the final
            // bitmap
            final TransitionDrawable td = new TransitionDrawable(
                    new Drawable[]{
                            new ColorDrawable(android.R.color.transparent),
                            new BitmapDrawable(mResources, bitmap)});
            // Set background to loading bitmap
            imageView.setBackgroundDrawable(new BitmapDrawable(mResources,
                    mLoadingBitmap));

            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void setPauseWork(boolean pauseWork) {
        synchronized (mPauseWorkLock) {
            mPauseWork = pauseWork;
            if (!mPauseWork) {
                mPauseWorkLock.notifyAll();
            }
        }
    }

    protected class CacheAsyncTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            switch ((Integer) params[0]) {
                case MESSAGE_CLEAR:
                    clearCacheInternal();
                    break;
                case MESSAGE_INIT_DISK_CACHE:
                    // -checkJournal Ĭ��Ϊtrue
                    boolean checkJournal = true;
                    if (params.length > 1) {
                        checkJournal = Boolean.valueOf(params[1].toString());
                    }
                    initDiskCacheInternal(checkJournal);
                    break;
                case MESSAGE_FLUSH:
                    flushCacheInternal();
                    break;
                case MESSAGE_CLOSE:
                    closeCacheInternal();
                    break;
            }
            return null;
        }
    }

    protected void initDiskCacheInternal(boolean checkJournal) {
        // -checkJournal
        if (mImageCache != null) {
            mImageCache.initDiskCache(checkJournal);
        }
    }

    protected void clearCacheInternal() {
        if (mImageCache != null) {
            mImageCache.clearCache();
        }
    }

    protected void flushCacheInternal() {
        if (mImageCache != null) {
            mImageCache.flush();
        }
    }

    protected void closeCacheInternal() {
        if (mImageCache != null) {
            mImageCache.close();
            mImageCache = null;
        }
    }

    public void clearCache() {
        new CacheAsyncTask().execute(MESSAGE_CLEAR);
    }

    public void flushCache() {
        new CacheAsyncTask().execute(MESSAGE_FLUSH);
    }

    public void closeCache() {
        new CacheAsyncTask().execute(MESSAGE_CLOSE);
    }
}
