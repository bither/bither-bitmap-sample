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

package net.bither.image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Top implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String TOP_ID = "pic_id";
    public static final String PIC_NAME = "pic_name";


    private long mTopId;
    private String picName;

    public long getTopId() {
        return mTopId;
    }

    public void setTopId(long mTopId) {
        this.mTopId = mTopId;
    }

    public String getPicName() {
        return this.picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public static List<Top> formatArray(JSONArray jsonArray)
            throws JSONException, ParseException {
        List<Top> tops = new ArrayList<Top>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Top top = new Top();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (!jsonObject.isNull(TOP_ID)) {
                top.setTopId(jsonObject.getLong(TOP_ID));
            }
            if (!jsonObject.isNull(PIC_NAME)) {
                top.setPicName(jsonObject.getString(PIC_NAME));
            }

            tops.add(top);
        }
        return tops;

    }

}
