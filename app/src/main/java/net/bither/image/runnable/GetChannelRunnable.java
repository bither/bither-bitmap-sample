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
