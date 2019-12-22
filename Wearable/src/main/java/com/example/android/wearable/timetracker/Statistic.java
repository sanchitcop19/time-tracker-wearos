package com.example.android.wearable.timetracker;

public class Statistic {

    private String project;
    private int time;

    public Statistic(String project, int time){

        this.project = project;
        this.time = time;

    }

    public String getProject() {
        return project;
    }

    public int getTime() {
        return time;
    }
}
