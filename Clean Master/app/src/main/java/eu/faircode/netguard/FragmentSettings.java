package eu.faircode.netguard;

/*
    This file is part of NetGuard.


*/

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.alim.cleanmaster.*;


public class FragmentSettings extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            addPreferencesFromResource(R.xml.preferences);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
