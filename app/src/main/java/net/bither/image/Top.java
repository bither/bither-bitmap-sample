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
