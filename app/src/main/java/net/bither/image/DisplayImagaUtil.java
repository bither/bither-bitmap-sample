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
