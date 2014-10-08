package net.bither.image.runnable;

import android.os.Handler;

public abstract class BaseRunnable implements Runnable {
	private Handler mHandler;

	public Handler getHandler() {
		return mHandler;
	}

	public void setHandler(Handler handler) {
		this.mHandler = handler;
	}
	
	public void obtainMessage(int what) {
		if (mHandler != null) {
			this.mHandler.obtainMessage(what).sendToTarget();
		}
	}
	
	public void obtainMessage(int what, Object obj) {
		if (mHandler != null) {
			this.mHandler.obtainMessage(what, obj).sendToTarget();
		}
	}

}
