/*
 * Copyright (C) 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.wearable.timetracker.ui;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.wearable.timetracker.R;
import com.example.android.wearable.timetracker.Statistic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A {@link WearableListView.Adapter} that is used to populate the
 * list of speeds.
 */
public class StatsListAdapter2 extends WearableListView.Adapter {

    private ArrayList<Statistic> mDataSet;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public StatsListAdapter2(Context context, ArrayList<Statistic> dataset) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataSet = dataset;
    }

    /**
     * Displays all possible speed limit choices.
     */
    public static class ItemViewHolder extends WearableListView.ViewHolder {

        private TextView mTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            mTextView = (TextView) itemView.findViewById(R.id.name);
        }
    }

    /**
     * Create new views for list items (invoked by the WearableListView's layout manager)
     */
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
            int viewType) {
        // Inflate our custom layout for list items
        return new ItemViewHolder(mInflater.inflate(R.layout.stats_list, null));
    }

    /**
     * Replaces the contents of a list item. Instead of creating new views, the list tries to
     * recycle existing ones. This is invoked by the WearableListView's layout manager.
     */
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
            int position) {
        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        TextView view = itemHolder.mTextView;
        // replace text contents
        Statistic data = mDataSet.get(position);

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        int hours = data.getTime() / 60;
        int minutes = data.getTime() % 60;
        calendar.set(0, 0, 0, hours, minutes, 0);
        String time = format.format(calendar.getTime());
        view.setText(String.format("%s (%s)", data.getProject(), time));
        // replace list item's metadata
        holder.itemView.setTag(position);
    }

    /**
     * Return the size of the data set (invoked by the WearableListView's layout manager).
     */
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
