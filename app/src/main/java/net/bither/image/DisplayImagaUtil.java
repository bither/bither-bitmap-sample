package net.bither.image;

import android.widget.ImageView;

import net.bither.image.cache.FetcherHolder;
import net.bither.image.cache.ImageManageUtil;

import java.util.Locale;

public class DisplayImagaUtil {

    public static void showSmallImage(String fileName,
                                      ImageView iv, boolean isAddMemoryCache) {
        String url = "http://pic.getcai.com/pic/%d/%s.jpg/";
        url = String.format(Locale.US, url, ImageManageUtil.SMALL_IMAGE_WIDTH, fileName);
        FetcherHolder.getSmallImageFetcher().loadImage(
                url, iv, isAddMemoryCache);
    }

    public static void showLargeImage(String fileName,
                                      ImageView iv, boolean isAddMemoryCache) {
        String url = "http://pic.getcai.com/pic/%d/%s.jpg/";
        url = String.format(Locale.US, url, ImageManageUtil.IMAGE_WIDTH, fileName);
        FetcherHolder.getLargeImageFetcher().loadImage(
                url, iv, isAddMemoryCache);
    }

}
