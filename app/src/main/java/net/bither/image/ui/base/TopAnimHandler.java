package net.bither.image.ui.base;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.bither.image.R;
import net.bither.image.Top;
import net.bither.image.DisplayImagaUtil;

import java.util.ArrayList;
import java.util.List;

public class TopAnimHandler extends Handler {
    public static final class MSG {
        public static final int CHECK = 1745;
        public static final int ADD_CELL = 1758;
        public static final int ONSCROLL = 1826;
        public static final int SINGLE_ANIMATION_END = 1840;
        public static final int RESUME = 1355;
        public static final int PAUSE = 1356;
        public static final int CLEAR = 1819;
    }

    private static final int AnimDelay = 1700;
    private List<CellHolder> cells = new ArrayList<CellHolder>();
    private List<CellHolder> animCells = new ArrayList<CellHolder>();
    private NextAnim nextAnim = new NextAnim();

    private int scrollState = OnScrollListener.SCROLL_STATE_IDLE;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG.ADD_CELL:
                if (msg.obj != null && msg.obj instanceof CellHolder) {
                    addCell((CellHolder) msg.obj);
                }
                break;
            case MSG.CHECK:
            case MSG.RESUME:
                check();
                break;
            case MSG.ONSCROLL:
                scrollState = (Integer) msg.obj;
                onScrollStateChange(scrollState);
                break;
            case MSG.SINGLE_ANIMATION_END:
                nextAnim();
                break;
            case MSG.PAUSE:
                stopAnimQueue();
                break;
            case MSG.CLEAR:
                stopAnimQueue();
                clear();
                break;
            default:
                break;
        }
        super.handleMessage(msg);
    }

    private void clear() {
        cells.clear();
        animCells.clear();
    }

    private void startAnimQueue() {
        nextAnim.setCurrentCell(null);
        removeCallbacks(nextAnim);
        post(nextAnim);
    }

    private void nextAnim() {
        removeCallbacks(nextAnim);
        postDelayed(nextAnim, AnimDelay);
    }

    private void stopAnimQueue() {
        removeCallbacks(check);
        removeCallbacks(nextAnim);
        removeCallbacks(reset);
        nextAnim.setCurrentCell(null);
        postDelayed(reset, AnimDelay);
    }

    private void check() {
        if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
            return;
        }
        removeCallbacks(reset);
        removeCallbacks(check);
        postDelayed(check, AnimDelay);
    }

    private void onScrollStateChange(int state) {
        if (state == OnScrollListener.SCROLL_STATE_IDLE) {
            check();
        } else {
            stopAnimQueue();
        }
    }

    private Runnable reset = new Runnable() {

        @Override
        public void run() {
            for (CellHolder cell : cells) {
                cell.reset();
            }
        }
    };

    private Runnable check = new Runnable() {

        @Override
        public void run() {
            if (cells.size() == 0) {
                return;
            }
//            for (CellHolder cell : cells) {
//                if (cell.isToAnimate() && !animCells.contains(cell)) {
//                    animCells.add(cell);
//                }
//                if (!cell.isToAnimate() && animCells.contains(cell)) {
//                    animCells.remove(cell);
//                }
//                if (!cell.isReadyForAnimation()) {
//                    return;
//                }
//            }
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                startAnimQueue();
            }
        }
    };

    private void addCell(CellHolder cell) {
        if (!cells.contains(cell)) {
            cells.add(cell);
        }
    }

    private class NextAnim implements Runnable {
        private CellHolder currentCell;

        public void setCurrentCell(CellHolder cell) {
            currentCell = cell;
        }

        @Override
        public void run() {
            if (animCells.size() > 0) {
                CellHolder cell = animCells.get(0);
                if (currentCell != null && animCells.contains(currentCell)) {
                    int curPos = animCells.indexOf(currentCell);
                    curPos++;
                    if (curPos >= animCells.size()) {
                        curPos = 0;
                    }
                    cell = animCells.get(curPos);
                }
                cell.anim();
                setCurrentCell(cell);
            }
        }
    }

    public static class CellHolder {
        private static final int AnimDuration = 700;
        Rect paddingRect = new Rect();
        public FrameLayout fl;
        private FrameLayout flContainer;
        public ImageView iv;
        public ImageView ivAnimated;
        String imageName = "";
        int currentPosition;
        boolean isReadyForAnimation = false;

        AnimCheckRunnable animCheckRunnable = new AnimCheckRunnable();

        public CellHolder(LayoutInflater inflater) {
            fl = (FrameLayout) inflater.inflate(
                    R.layout.list_item_top_grid_item, null);

            iv = (ImageView) fl.findViewById(R.id.iv);
            ivAnimated = (ImageView) fl
                    .findViewById(R.id.iv_animated);
            flContainer = (FrameLayout) fl.findViewById(R.id.fl);
            Drawable foreground = inflater.getContext().getResources()
                    .getDrawable(R.drawable.grid_photo_overlay);
            foreground.getPadding(paddingRect);
            fl.setForeground(foreground);
            fl.setPadding(paddingRect.left, paddingRect.top, paddingRect.right,
                    paddingRect.bottom);
            fl.setForegroundGravity(Gravity.FILL);


        }


        private class AnimListener implements AnimationListener {
            private Bitmap bmp;

            public AnimListener(Bitmap bmp) {
                this.bmp = bmp;
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ivAnimated.removeCallbacks(animCheckRunnable);
                iv.setImageBitmap(bmp);
                bmp = null;
                ivAnimated.postDelayed(animEndRunnable, 50);
            }
        }

        ;

        private Runnable animEndRunnable = new Runnable() {

            @Override
            public void run() {
                ivAnimated.setImageBitmap(null);
                ivAnimated.setVisibility(View.INVISIBLE);

            }
        };

        public void anim() {
            currentPosition++;
            if (imageName == null) {
                return;
            }

            DisplayImagaUtil.showSmallImage(
                    imageName, ivAnimated, true);
        }

        public void addToView(LinearLayout v, LinearLayout.LayoutParams lp) {
            v.addView(fl, lp);
            iv.getLayoutParams().height = lp.height - paddingRect.top
                    - paddingRect.bottom;
            iv.getLayoutParams().width = lp.width - paddingRect.left
                    - paddingRect.right;
            ivAnimated.getLayoutParams().height = iv.getLayoutParams().height;
            ivAnimated.getLayoutParams().width = iv.getLayoutParams().width;
            flContainer.getLayoutParams().height = iv.getLayoutParams().height;
            flContainer.getLayoutParams().width = iv.getLayoutParams().width;

        }

        public void setPiCommon(Top pi) {
            imageName = pi.getPicName();
        }


        public boolean isReadyForAnimation() {
            return isReadyForAnimation;
        }

        public void reset() {
            ivAnimated.clearAnimation();
            ivAnimated.setImageBitmap(null);
            ivAnimated.setVisibility(View.INVISIBLE);
            ivAnimated.removeCallbacks(animEndRunnable);
            ivAnimated.removeCallbacks(animCheckRunnable);
            iv.clearAnimation();
            currentPosition = 0;

            DisplayImagaUtil.showSmallImage(
                    imageName, iv, true);

        }

        private TranslateAnimation getTransAnim() {
            int direction = (int) Math.floor(Math.random() * 4);
            TranslateAnimation a;
            switch (direction) {
                case 0:
                    a = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1,
                            Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                            Animation.ABSOLUTE, 0);
                    break;
                case 1:
                    a = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1,
                            Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0,
                            Animation.ABSOLUTE, 0);
                    break;
                case 2:
                    a = new TranslateAnimation(Animation.ABSOLUTE, 0,
                            Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, 1,
                            Animation.ABSOLUTE, 0);
                    break;
                case 3:
                default:
                    a = new TranslateAnimation(Animation.ABSOLUTE, 0,
                            Animation.ABSOLUTE, 0, Animation.RELATIVE_TO_SELF, -1,
                            Animation.ABSOLUTE, 0);
                    break;
            }
            a.setInterpolator(new DecelerateInterpolator(1.2f));
            a.setDuration(AnimDuration);
            return a;
        }

        private class AnimCheckRunnable implements Runnable {
            private Animation anim;

            public void setAnimation(Animation anim) {
                this.anim = anim;
            }

            @Override
            public void run() {
                if (anim != null && !anim.hasStarted()) {
                    ivAnimated.setImageBitmap(null);
                    ivAnimated.setVisibility(View.INVISIBLE);

                }
            }
        }
    }

}
