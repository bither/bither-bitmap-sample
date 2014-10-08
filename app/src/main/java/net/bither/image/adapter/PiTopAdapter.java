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

package net.bither.image.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import net.bither.image.ui.base.PicDetailClickListener;
import net.bither.image.R;
import net.bither.image.Top;
import net.bither.image.cache.ImageManageUtil;
import net.bither.image.ui.base.TopAnimHandler;

import java.util.ArrayList;
import java.util.List;

public class PiTopAdapter extends BaseAdapter {
    private List<Top> mTops;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public static final int ColumnCount = ImageManageUtil.getScreenWidth() >= 1080 ? 4
            : 3;
    static int margin = ImageManageUtil.dip2pix(1);
    static int topMargin = ImageManageUtil.dip2pix(0);
    static int topPadding = ImageManageUtil.dip2pix(7);
    static int horizontalPadding = ImageManageUtil.dip2pix(5);
    public static final int imageSize = (ImageManageUtil.getScreenWidth()
            - margin * (ColumnCount + 1) - horizontalPadding * 2)
            / ColumnCount;

    public PiTopAdapter(Context context, List<Top> tops) {
        mTops = tops;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        if (mTops == null) {
            return 0;
        }
        int count = (int) Math.ceil((double) mTops.size()
                / (double) ColumnCount);
        return count;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        List<TopAnimHandler.CellHolder> cells;
        if (convertView == null) {
            LinearLayout ll = (LinearLayout) mLayoutInflater.inflate(
                    R.layout.list_item_top, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    imageSize, imageSize);
            lp.rightMargin = margin;
            cells = new ArrayList<TopAnimHandler.CellHolder>();
            for (int i = 0; i < ColumnCount; i++) {
                TopAnimHandler.CellHolder cell = new TopAnimHandler.CellHolder(mLayoutInflater);
                cells.add(cell);
                cell.addToView(ll, lp);
            }
            ll.setTag(cells);
            convertView = ll;
        } else {
            cells = (List<TopAnimHandler.CellHolder>) convertView.getTag();
//            for (TopAnimHandler.CellHolder cellHolder : cells) {
//                animHandler.obtainMessage(TopAnimHandler.MSG.ADD_CELL, cellHolder)
//                        .sendToTarget();
//            }
        }

        if (position == 0) {
            convertView.setPadding(margin + horizontalPadding, margin
                    + topMargin + topPadding, horizontalPadding, 0);
        } else {
            convertView.setPadding(margin + horizontalPadding, margin
                    + topMargin, horizontalPadding, 0);
        }
        for (int i = 0; i < ColumnCount; i++) {
            TopAnimHandler.CellHolder cell = cells.get(i);
            int p = position * ColumnCount + i;
            if (p < mTops.size()) {
                cell.fl.setVisibility(View.VISIBLE);
                Top top = mTops.get(p);
                cell.setPiCommon(top);

                cell.fl.setOnClickListener(new PicDetailClickListener(top
                ));
                if (top != null) {
                    cell.reset();
                }
            } else {
                cell.setPiCommon(null);
                cell.fl.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
