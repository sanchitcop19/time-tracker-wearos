package com.example.android.wearable.timetracker.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.wearable.timetracker.R;
import com.example.android.wearable.timetracker.Statistic;

import java.util.List;

public class StatsListAdapter extends RecyclerView.Adapter<StatsListAdapter.ViewHolder> {

    private List<Statistic> statistics;
    private Context context;
    private LayoutInflater inflater;

    public StatsListAdapter(Context context, List<Statistic> statistics){

        this.statistics = statistics;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View statView = inflater.inflate(R.layout.stats, parent, false);

        return new ViewHolder(statView);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsListAdapter.ViewHolder holder, int position) {

        Statistic statistic = statistics.get(position);

        TextView textView = holder.stat;
        textView.setText(statistic.getProject());

        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView stat;

        public ViewHolder(View itemView){
            super(itemView);
            stat = itemView.findViewById(R.id.stat);
        }

    }
}
