# Android, Bitmap and OOM (Out Of Memory)

A month ago, I wrote an article ["Why the image quality of iPhone is much better than Android?"] (https://github.com/bither/bither-android-lib/blob/master/REASON.md). After reading that article, you may understand the importance of the `optimize_coding` parameter. In fact, there are more confusions made by Goolge, not only in image quality.

Today, I want to tell you something about the Bitmap object on Android platform.

We all know that the Bitmap object consumes memory. For example, if you want to show a square image with 612 pixels (both height and width), the related Bitmap object may need `612*612*4=1498176 bytes` of memory, nearly `1.5MB`. For a desktop application, that is not a problem, we can get as many available memory as we need. But for a mobile App, it is too much, mobile OS (iOS or Android) strictly limit the memory usage for each App. If your App exceed the limit, then there will be an OOM (Out Of Memory) exception, and the OS will kill your App to protect the whole system and other normal running Apps.

The OOM exception is a headache problem for almost every developers. (What? You haven't faced OOM yet? Are you really a developer?)

After searching on the Internet, we may find many "funny" solutions:

1. Reduce the display size of the image, instead of using` ARGB_8888` (32 bits), use `ARGB_4444` (16 bits) or even `RGB_565` (8 bits). Yes, smaller and worse quality may use less amount of memory, but is that really a good idea?

2. Explicit(manually) recycle the memory of the Bitmap object. It seems to be a good solution, but when you use this method on a listview scrolling to display so many images, you may have to face another exception `trying to use a recycled bitmap`.

3. Set the Bitmap object as soft reference, frequently calling GC (Garbage Collection) manually. It may lower the probability of OOM a little, but the exception still happens.

We had tried all these "solutions", and also we used DDMS to monitor the memory usage. We even tried to analyse the possible memory leak reasons with the MAT (Memory Analyzer Tool).

For simplicity, we ran a clean project to trace the problem, BitmapFun (developed by Google for training purpose). But still helpless, when scrolling the listview of the BitmapFun, the memory usage are increased raplidly, and if scrolled faster, the BitmapFun will throw OOM exception too. WTF!

Finally, after deeply digging, we found the solution. It's also a simple option : `inPurgeable` (and related `inInputShareable`).

You can find [more detailed description of BitmapFactory.options.inPurgeable on Google's website] (http://developer.android.com/reference/android/graphics/BitmapFactory.Options.html#inPurgeable).


> If this is set to true, then the resulting bitmap will allocate its pixels such that they can be purged if the system needs to reclaim memory. In that instance, when the pixels need to be accessed again (e.g. the bitmap is drawn, getPixels() is called), they will be automatically re-decoded.

This is the ultimate solution for OOM exception. Because the image displayed in an App often comes from jpg or png format, in contrast to the Bitmap object's memory usage (e.g. : nearly 1.5MB), jpg or png are much smaller (e.g. : 50KB). With reserving the reference to the source (InputStream or ByteArray), we only need much smaller amount of memory, and when we need to redisplay the image, the re-decode consumptions are even not a problem for modern computing devices. Needing re-decode, but no OOM exception, what an important option it is!

Now you can set inPurgeable to true (also set InputShareable to true), and use DDMS to monitor the memory usage. You may find that no matter how fast you scrolling the listview or displaying the full size image, the memory usage will remain low and stable. You will never face the OOM (caused by Bitmap) again!

The inPurgeable options is very important, but the Google's advice is "avoid using". How funny is this?

After the discussion of the memory usage of the Bitmap object, here I want to tell you some principles on resolving the memory issue on Android platform:

1. OOM happens on any place (normally not on the problem line), the only thing we can do is to analyze the memory leak.

2. Android GC is trustable, and we don't need to recycle manually, we don't need to frequently calling the `gc()`. After the lifecycle of an object, just remove the reference is enough, the Android GC will finish its work.

3. Always use `ARGB_8888`.

4. All memory leak problems are related to reference issue, normally DDMS+reviewing code is enough to solve these problems.

5. We may find that OOM in this year are not as severe as before. Thanks for the hardware industry, now we have 3GB memory mobile phones, :)

In conclusion, no matter for `optimize_coding` or `inPurgeable`, Google are confused, but Apple really understand these. The iOS developers are so lucky!

[Source code](https://github.com/bither/bither-bitmap-sample)
