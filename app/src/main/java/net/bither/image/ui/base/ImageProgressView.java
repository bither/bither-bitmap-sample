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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.bither.image.R;
import net.bither.image.cache.FetcherHolder;
import net.bither.image.cache.FileDowloadProgressListener;
import net.bither.image.cache.ImageManageUtil;
import net.bither.image.cache.ImageWorker;

import java.util.Locale;

public class ImageProgressView extends FrameLayout {
	private TextView tvReload;
	private ImageView mIv;
	private ProgressBar mPb;
	private LinearLayout mPbContainer;
	private Context mContext;
	private LinearLayout.LayoutParams pbLp;
	private LayoutInflater inflater;

	private String mImageUrl;
	private int mWidth;
	private int mHeight;
	private boolean mIsAddMemoryCache;

	private int pbHeight = -1;

	private OnClickListener oriOnClickListener;

	public ImageProgressView(Context context) {
		super(context);
		initViews();
	}

	public ImageProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews();
	}

	public ImageProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();
	}

	private void initViews() {
		this.mContext = this.getContext();
		inflater = LayoutInflater.from(mContext);
		mIv = new ImageView(mContext);
		mIv.setScaleType(ScaleType.FIT_XY);
        mPb = (ProgressBar) inflater.inflate(
                R.layout.image_progress_view_progress_bar, null);
		mPbContainer = new LinearLayout(mContext);
		this.addView(mIv, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		pbLp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		mPbContainer.addView(mPb, pbLp);
		mPbContainer.setPadding(40, 0, 40, 0);
		this.addView(mPbContainer, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
		pbLp = (LinearLayout.LayoutParams) mPb.getLayoutParams();
		mPbContainer.setVisibility(View.GONE);
		tvReload = new TextView(mContext);
		tvReload.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tvReload.setText("image_progress_view_tap_to_reload");
		tvReload.setTextColor(getResources().getColor(
                R.color.image_progress_bar));
		LayoutParams lpReload = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		addView(tvReload, lpReload);
		tvReload.setVisibility(View.GONE);
	}

	public void dowloadPicameraImage(String picName, boolean isAddMemoryCache) {
        String url = "http://pic.getcai.com/pic/%d/%s.jpg/";
        url = String.format(Locale.US, url, ImageManageUtil.IMAGE_WIDTH, picName);
		int imageSize = ImageManageUtil.PIIMAGE_MIDLLE_SIZE;
		dowloadImage(url, imageSize, imageSize, isAddMemoryCache);

	}

	FileDowloadProgressListener imageProgressListener = new FileDowloadProgressListener() {
		@Override
		public void onCancel() {
			hideAndResetProgressBar();

		}

		@Override
		public void onProgress(ProgressValue progressValue) {
			if (progressValue != null) {
				switch (progressValue.getDownloadType()) {
				case PREPARE:
					showProgressBar();
					break;
				case BEGIN:
					beginDownload(progressValue.getSum());
					// LogUtil.debug("progress",
					// "sum:"+progressValue.getValue());
					break;
				case DOWNLOING:
					setProgress(progressValue.getValue());
					break;
				case END:
					hideAndResetProgressBar();
					// LogUtil.debug("progress",
					// "count:"+progressValue.getValue());
					break;
				case ERROR:
					showError();
					break;
				default:
					break;
				}
			}

		}
	};

	public void onDestroy() {
		mIv.setImageBitmap(null);
	}

	public void dowloadImage(String url, int w, int h, boolean isAddMemoryCache) {
		mImageUrl = url;
		mWidth = w;
		mHeight = h;
		mIsAddMemoryCache = isAddMemoryCache;
		hideAndResetProgressBar();
		mIv.setTag(imageProgressListener);
		FetcherHolder.getLargeImageFetcher().loadImage(url, mIv,
				isAddMemoryCache);
	}

	/**
	 * ��ʼ���أ���ʾ����������ͼƬ
	 * 
	 * @param max
	 *            ��ʼ����ʱ����ProgressBar���ֵ
	 */
	public void beginDownload(int max) {
		showProgressBar();
		setMax(max);
	}

	public void showProgressBar() {
		tvReload.setVisibility(View.GONE);
		mPbContainer.setVisibility(View.VISIBLE);
	}

	public void hideAndResetProgressBar() {
		setOnClickListener(oriOnClickListener);
		mPbContainer.setVisibility(View.GONE);
		tvReload.setVisibility(View.GONE);
		setMax(100);// �����õĻ��첽��������ִ�е�ʱ���ȡ���ϴε�ֵ
		setProgress(0);
	}

	public void showError() {
		hideAndResetProgressBar();
		tvReload.setVisibility(View.VISIBLE);
		super.setOnClickListener(reloadClick);
		setClickable(true);
	}

	public void setMax(int max) {
		mPb.setMax(max);
	}

	public void setProgress(int progress) {
		mPb.setProgress(progress);
	}

	public void incrementProgressBy(int diff) {
		mPb.incrementProgressBy(diff);
	}

	public void setImageBitmap(Bitmap bm) {
		mIv.setImageBitmap(bm);

	}

	public Drawable getDrawable() {
		if (mIv.getDrawable() instanceof ImageWorker.AsyncDrawable) {
			return null;
		} else {
			return mIv.getDrawable();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (pbHeight == -1) {
			if (h > 150) {
				if (pbLp.height != 25) {
					pbLp.height = 25;
				}
			} else {
				if (pbLp.height != 7) {
					pbLp.height = 7;
				}
			}
		} else {
			if (pbLp.height != pbHeight) {
				pbLp.height = pbHeight;
			}
		}
	}

	public ProgressBar getProgressBar() {
		return mPb;
	}

	public ImageView getImageView() {
		return mIv;
	}

	public void setProgressBarHeight(int height) {
		pbHeight = height;
		pbLp.height = pbHeight;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		oriOnClickListener = l;
		super.setOnClickListener(l);
		if (oriOnClickListener != null) {
			setClickable(true);
		} else {
			setClickable(false);
		}
	}

	private OnClickListener reloadClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			dowloadImage(mImageUrl, mWidth, mHeight, mIsAddMemoryCache);
		}
	};

}
