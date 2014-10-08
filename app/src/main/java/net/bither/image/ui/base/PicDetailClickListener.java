package net.bither.image.ui.base;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import net.bither.image.BitherApplication;
import net.bither.image.PicDetailActivity;
import net.bither.image.Top;

public class PicDetailClickListener implements OnClickListener {

    /**
     * can't click again in 1s
     */
    private boolean clicked = false;
    private Context context;
    private Top pic;

    public PicDetailClickListener(Top pic) {
        this.pic = pic;
    }

    protected Intent processIntent(Intent intent) {
        return intent;
    }

    @Override
    public void onClick(View v) {
        if (!clicked) {
            clicked = true;
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clicked = false;
                }
            }, 1000);
            context = v.getContext();

            Intent intent = new Intent(context, PicDetailActivity.class);
            intent.putExtra(BitherApplication.PIC_PASS_VALUE_TAG, pic);
            context.startActivity(processIntent(intent));
        }
    }
}
