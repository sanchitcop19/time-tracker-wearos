package com.example.android.wearable.timetracker;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.example.android.wearable.timetracker.ui.StatsListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatsActivity extends Activity {

    private SharedPreferences sharedPreferences;
    private WearableRecyclerView recyclerView;
    private StatsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

        List<Statistic> statistics = new ArrayList<>();
        adapter = new StatsListAdapter(this, statistics);
        recyclerView = findViewById(R.id.stats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setEdgeItemsCenteringEnabled(true);

        recyclerView.setAdapter(adapter);
        System.out.println(recyclerView.getAdapter());

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

        adapter.notifyDataSetChanged();


    }



}
