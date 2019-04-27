package com.alim.cleanmaster;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MediaActivity extends AppCompatActivity {

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    android.support.v7.widget.Toolbar toolbar;
    ViewPager viewPager ;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        toolbar = findViewById(R.id.toolbar_id);
        viewPager = findViewById(R.id.viewPager_id);
        tabLayout = findViewById(R.id.tableLayout_id);

        toolbar.setTitle("Clean Master");
        setSupportActionBar(toolbar);



        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        adapter.addFragment(new ImagesFragment(),"Images");
        adapter.addFragment(new VideosFragment(),"Videos");
        adapter.addFragment(new FilesFragment(),"Files");

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

//        new VideoDataModel.CollectVideosData().execute(getApplicationContext());

    }
    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }
    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
