package eu.faircode.netguard;

import android.graphics.drawable.Drawable;

public class appDataItem {
    private String title;
    private String descripton;
    private String size;
    private String packageName;
    private Drawable icon;
    private String path;

    public appDataItem(String title, String descripton, String size,  Drawable icon) {
        this.title = title;
        this.descripton = descripton;
        this.size = size;
        this.icon = icon;
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

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
