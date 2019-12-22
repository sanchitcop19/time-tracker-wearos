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

package com.example.android.wearable.timetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.TextView;

import com.example.android.wearable.timetracker.ui.ProjectPickerListAdapter;
import com.example.android.wearable.timetracker.ui.StatsListAdapter2;

import java.util.ArrayList;
import java.util.Set;

/**
 * An activity that presents a list of speeds to user and allows user to pick one, to be used as
 * the current speed limit.
 */
public class StatsActivity2 extends Activity {

    private SharedPreferences sharedPreferences;

    /* Speeds, in mph, that will be shown on the list */
    private ArrayList<Statistic> statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_list_2);

        statistics = new ArrayList<>();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Set<String> keys = sharedPreferences.getAll().keySet();
        for (String key: keys){
            try{
                statistics.add(new Statistic(key, sharedPreferences.getInt(key, 0)));
            }
            catch (ClassCastException e){
                continue;
            }
        }



        if (statistics.isEmpty()){

        }


        // Get the list component from the layout of the activity
        WearableListView listView = (WearableListView) findViewById(R.id.wearable_list);

        // Assign an adapter to the list
        listView.setAdapter(new StatsListAdapter2(this, statistics));

        listView.addOnScrollListener(new WearableListView.OnScrollListener() {
            @Override
            public void onScroll(int i) {
            }

            @Override
            public void onAbsoluteScrollChange(int i) {
                // only scroll the header up from the base position, not down...
            }

            @Override
            public void onScrollStateChanged(int i) {
            }

            @Override
            public void onCentralPositionChanged(int i) {
            }
        });
    }
}
