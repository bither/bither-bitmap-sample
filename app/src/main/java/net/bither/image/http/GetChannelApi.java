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
package net.bither.image.http;

import net.bither.image.Top;

import java.util.List;

import org.json.JSONArray;


public class GetChannelApi extends HttpGetResponse<List<Top>> {

    public GetChannelApi(long topId) {
        String url = "http://ws.getcai.com/api/2/ch/popular/";
        if (topId > 0) {
            url = url + Long.toString(topId) + "/";
        }
        setUrl(url);

    }
    @Override
    public void setResult(String response) throws Exception {

        this.result = Top.formatArray(new JSONArray(response));

    }

}
