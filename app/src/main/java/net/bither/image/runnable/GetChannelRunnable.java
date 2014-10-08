package net.bither.image.runnable;

import net.bither.image.Top;
import net.bither.image.http.GetChannelApi;

import java.util.List;

public class GetChannelRunnable extends BaseRunnable {
    private long mTopId;


    public GetChannelRunnable(long topId) {
        this.mTopId = topId;

    }

    @Override
    public void run() {
        obtainMessage(HandlerMessage.MSG_PREPARE);
        try {
            GetChannelApi getTopAPI = new GetChannelApi(mTopId);
            getTopAPI.handleHttpGet();
            List<Top> tops = getTopAPI.getResult();
            obtainMessage(HandlerMessage.MSG_SUCCESS, tops);
        } catch (Exception e) {
            obtainMessage(HandlerMessage.MSG_FAILURE);
            e.printStackTrace();
        }

    }
}
