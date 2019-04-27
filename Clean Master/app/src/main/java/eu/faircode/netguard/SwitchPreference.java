package eu.faircode.netguard;

/*
    This file is part of NetGuard.


*/

import android.content.Context;
import android.util.AttributeSet;

import com.alim.cleanmaster.*;
// https://code.google.com/p/android/issues/detail?id=26194

public class SwitchPreference extends android.preference.SwitchPreference {
    public SwitchPreference(Context context) {
        this(context, null);
    }

    public SwitchPreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.switchPreferenceStyle);
    }

    public SwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
