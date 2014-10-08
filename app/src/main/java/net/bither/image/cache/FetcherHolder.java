package net.bither.image.cache;

import android.graphics.Bitmap.CompressFormat;

import net.bither.image.BitherApplication;


public class FetcherHolder {
    private static ImageFetcher LARGE_IMAGE_FETCHER;
    private static ImageFetcher SMALL_IMAGE_FETCHER;
    // public static final String LARGE_IMAGE_CACHE_DIR = "lcache";
    // public static final String SMALL_IMAGE_CACHE_DIR = "scache";

    public static final String PI_IMAGE_CACHE_DIR = "picache";

    private static ImageCache IMAGE_CACHE;
    private static boolean CHECK_JOURNAL = true;

    // TODO : jjz considering to upgrade the image cache to use same fetcher and
    // same imagecache, bitmap should be with the size info, when upgrade getcai
    private static void addImageCacheToFetcher(ImageFetcher fetcher) {
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
                BitherApplication.mContext, PI_IMAGE_CACHE_DIR);
        cacheParams.compressFormat = CompressFormat.JPEG;
        cacheParams.compressQuality = 100;
        cacheParams.diskCacheSize = 100 * 1024 * 1024;
        // Set memory cache to 25% of mem class
        cacheParams.setMemCacheSizePercent(BitherApplication.mContext, 0.25f);
        if (IMAGE_CACHE == null) {
            fetcher.addImageCache(cacheParams, CHECK_JOURNAL);
            IMAGE_CACHE = fetcher.getImageCache();
        } else {
            fetcher.setImageCache(IMAGE_CACHE);
            // because we can only use addImageCache to init Disk Cache, so here
            // we call it again
            fetcher.addImageCache(cacheParams, CHECK_JOURNAL);
        }
        CHECK_JOURNAL = false;
    }

    public static ImageFetcher getLargeImageFetcher() {
        if (LARGE_IMAGE_FETCHER == null) {
            // In fact we don't use this size at all. Because inPurgeable will
            // make bitmap smaller than reducing size
            LARGE_IMAGE_FETCHER = new ImageFetcher(BitherApplication.mContext,
                    Math.min(ImageManageUtil.getScreenWidth(),
                            ImageManageUtil.IMAGE_WIDTH));
            addImageCacheToFetcher(LARGE_IMAGE_FETCHER);
        }
        return LARGE_IMAGE_FETCHER;
    }

    public static ImageFetcher getSmallImageFetcher() {
        // The ImageFetcher takes care of loading images into our ImageView
        // children asynchronously
        if (SMALL_IMAGE_FETCHER == null) {
            SMALL_IMAGE_FETCHER = new ImageFetcher(BitherApplication.mContext,
                    ImageManageUtil.getSmallImageCacheWidth());
            addImageCacheToFetcher(SMALL_IMAGE_FETCHER);
        }
        return SMALL_IMAGE_FETCHER;
    }

    public static void closeImageFetchers() {
        if (FetcherHolder.LARGE_IMAGE_FETCHER != null) {
            FetcherHolder.LARGE_IMAGE_FETCHER.closeCache();
            FetcherHolder.LARGE_IMAGE_FETCHER = null;
        }
        if (FetcherHolder.SMALL_IMAGE_FETCHER != null) {
            FetcherHolder.SMALL_IMAGE_FETCHER.closeCache();
            FetcherHolder.SMALL_IMAGE_FETCHER = null;
        }
        IMAGE_CACHE = null;
        CHECK_JOURNAL = true;
    }

}
