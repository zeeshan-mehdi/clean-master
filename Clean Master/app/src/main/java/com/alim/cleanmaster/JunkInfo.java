package com.alim.cleanmaster;

public class JunkInfo {
    long cacheSize;
    long dataSize;
    long totalTrash;

    public JunkInfo(long cacheSize, long dataSize, long totalTrash) {
        this.cacheSize = cacheSize;
        this.dataSize = dataSize;
        this.totalTrash = totalTrash;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public long getTotalTrash() {
        return totalTrash;
    }

    public void setTotalTrash(long totalTrash) {
        this.totalTrash = totalTrash;
    }
}
