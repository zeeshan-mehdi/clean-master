package com.alim.cleanmaster;

import android.widget.ImageView;
import android.widget.TextView;

public class CpuApp {
    ImageView appIcon;
    String appTitle;

    public CpuApp(ImageView appIcon, String appTitle) {
        this.appIcon = appIcon;
        this.appTitle = appTitle;
    }

    public ImageView getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(ImageView appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }
}
