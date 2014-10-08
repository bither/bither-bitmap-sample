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

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import net.bither.image.adapter.PiTopAdapter;
import net.bither.image.runnable.GetChannelRunnable;
import net.bither.image.runnable.HandlerMessage;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private ListView lv;
    private List<Top> mTops = new ArrayList<Top>();
    private PiTopAdapter mAdapter;
    private boolean isLoading = false;
    private boolean noMore = false;
    private long lastTopId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        lv = (ListView) findViewById(R.id.lv_top);
        lv.setOnScrollListener(onScrollListener);

        mAdapter = new PiTopAdapter(MainActivity.this, mTops);
        lv.setAdapter(mAdapter);
        getTops();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        private int lastFirstVisibleItem;

        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount >= totalItemCount - 2
                    && !noMore && mTops.size() > 0 && !isLoading
                    && lastFirstVisibleItem < firstVisibleItem) {
                getTops();
            }
            lastFirstVisibleItem = firstVisibleItem;
        }
    };

    private void getTops() {
        isLoading = true;
        GetChannelRunnable getTopRunnable = new GetChannelRunnable(
                lastTopId);
        getTopRunnable.setHandler(getTopHandler);
        new Thread(getTopRunnable).start();
    }

    private Handler getTopHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case HandlerMessage.MSG_PREPARE:

                    break;
                case HandlerMessage.MSG_SUCCESS:

                    @SuppressWarnings("unchecked")
                    List<Top> tempList = (List<Top>) msg.obj;
                    if (lastTopId <= 0) {

                        mTops.clear();
                    }
                    if (tempList != null && tempList.size() > 0) {

                        lastTopId = tempList.get(tempList.size() - 1)
                                .getTopId();

                        noMore = false;
                        mTops.addAll(tempList);
                        mAdapter.notifyDataSetChanged();

                    } else {
                        noMore = true;
                    }
                    isLoading = false;
                    lv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            int lastVisiblePosition = lv
                                    .getFirstVisiblePosition()
                                    + lv.getChildCount();
                            int totalCount = mAdapter.getCount();
                            if (!noMore && lastVisiblePosition >= totalCount - 2
                                    && totalCount > 0) {
                                getTops();
                            }
                        }
                    }, 100);
                    break;
                case HandlerMessage.MSG_FAILURE:

                    isLoading = false;
                    break;
                default:
                    break;
            }
            super.dispatchMessage(msg);
        }
    };

}
