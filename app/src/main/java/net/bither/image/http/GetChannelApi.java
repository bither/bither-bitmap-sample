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
