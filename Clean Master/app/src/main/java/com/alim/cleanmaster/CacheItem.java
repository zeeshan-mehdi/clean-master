package com.alim.cleanmaster;

import android.graphics.drawable.Drawable;

public class CacheItem {
    private String title;
    private String descripton;
    private long size;
    private String packageName;
    private Drawable icon;
    private String path;
    private boolean isSelected ;

    public CacheItem(String title, String descripton, long size,  Drawable icon,boolean isSelected,String packageName) {
        this.title = title;
        this.descripton = descripton;
        this.size = size;
        this.icon = icon;
        this.isSelected=isSelected;
        this.packageName= packageName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescripton() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
