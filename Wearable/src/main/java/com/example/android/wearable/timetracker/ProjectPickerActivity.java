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
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import com.example.android.wearable.timetracker.ui.ProjectPickerListAdapter;

/**
 * An activity that presents a list of speeds to user and allows user to pick one, to be used as
 * the current speed limit.
 */
public class ProjectPickerActivity extends Activity implements WearableListView.ClickListener {

    public static final String CURRENT_PROJECT =
            "com.example.android.wearable.timetracker.extra.CURRENT_PROJECT";

    private SharedPreferences sharedPreferences;

    /* Speeds, in mph, that will be shown on the list */
    private ArrayList<String> projects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_picker_activity);

        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        projects = new ArrayList<>();
        addProject("CSE410");
        addProject("Leetcode");
        addProject("Productiv");

        if (projects.isEmpty()){
            View add = findViewById(R.id.add);
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        final TextView header = (TextView) findViewById(R.id.header);
        header.setText(sharedPreferences.getString(CURRENT_PROJECT, "Pick Project"));

        // Get the list component from the layout of the activity
        WearableListView listView = (WearableListView) findViewById(R.id.wearable_list);

        // Assign an adapter to the list
        listView.setAdapter(new ProjectPickerListAdapter(this, projects));

        // Set a click listener
        listView.setClickListener(this);

        listView.addOnScrollListener(new WearableListView.OnScrollListener() {
            @Override
            public void onScroll(int i) {
            }

            @Override
            public void onAbsoluteScrollChange(int i) {
                // only scroll the header up from the base position, not down...
                if (i > 0) {
                    header.setY(-i);
                }
            }

            @Override
            public void onScrollStateChanged(int i) {
            }

            @Override
            public void onCentralPositionChanged(int i) {
            }
        });
    }



    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {

        if (WearableMainActivity.is_tracking){
            System.err.println("Do not change the activity while tracking one");
            finish();
            return;
        }

        String project = projects.get(viewHolder.getPosition());

        TextView project_view = (TextView) findViewById(R.id.header);
        project_view.setText(project);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENT_PROJECT, project);
        editor.apply();


        Intent resultIntent = new Intent(Intent.ACTION_PICK);
        resultIntent.putExtra(CURRENT_PROJECT, project);
        setResult(RESULT_OK, resultIntent);

        finish();

    }

    public void addProject(String project){
        projects.add(project);
    }

    @Override
    public void onTopEmptyRegionClick() {
    }
}
