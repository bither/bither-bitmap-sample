package net.bither.image;

import android.app.Application;
import android.content.Context;

/**
 * Created by nn on 14-10-8.
 */
public class BitherApplication extends Application {
    public static Context mContext;

    public static final String PIC_PASS_VALUE_TAG = "pic_pass_value";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
