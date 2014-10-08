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
