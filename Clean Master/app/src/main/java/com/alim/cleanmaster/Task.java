package com.alim.cleanmaster;

import android.graphics.drawable.Drawable;

public class Task {
    private int pid;
    private String packageName;
    private long startTime;
    private int policy;
    private char state;
    private long totalSizeOfProcess;
    private long residentSetSize;
    private String appName;
    private Drawable icon;

    public Task(int pid, String packageName, long startTime, int policy, char state, long totalSizeOfProcess, long residentSetSize, String appName,Drawable icon) {
        this.pid = pid;
        this.packageName= packageName;
        this.startTime = startTime;
        this.policy = policy;
        this.state = state;
        this.totalSizeOfProcess = totalSizeOfProcess;
        this.residentSetSize = residentSetSize;
        this.appName = appName;
        this.icon=icon;
    }

    public Task() {
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getParentProcessId() {
        return packageName;
    }

    public void setParentProcessId(String parentProcessId) {
        this.packageName= parentProcessId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getPolicy() {
        return policy;
    }

    public void setPolicy(int policy) {
        this.policy = policy;
    }

    public char getState() {
        return state;
    }

    public void setState(char state) {
        this.state = state;
    }

    public long getTotalSizeOfProcess() {
        return totalSizeOfProcess;
    }

    public void setTotalSizeOfProcess(long totalSizeOfProcess) {
        this.totalSizeOfProcess = totalSizeOfProcess;
    }

    public long getResidentSetSize() {
        return residentSetSize;
    }

    public void setResidentSetSize(long residentSetSize) {
        this.residentSetSize = residentSetSize;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
