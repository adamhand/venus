package com.venus.utils;

public class CostTimeWatcher {
    private long startTime;
    private long stopTime;

    public void start(){
        startTime = System.currentTimeMillis();
    }

    public void stop(){
        stopTime = System.currentTimeMillis();
    }

    public float getCostTime(){
        return (float) (stopTime - startTime) / 1000;
    }
}
