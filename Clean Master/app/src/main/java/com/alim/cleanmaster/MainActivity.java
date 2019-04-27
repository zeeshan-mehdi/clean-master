package com.alim.cleanmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    static ArrayList<JunkInfo> junckInfo;
    static ArrayList<AppDetails.PackageInfoStruct> packageInfo;
    ExpandableListView cachedItemsList;
    static HashMap<String, ArrayList<CacheItem>> listHashMap;
    ArrayList<CacheItem> cacheItems;
     ArrayList<CacheItem> appDataItems;
    static ArrayList<String> header;
    Context context = this;
    static long totalCacheSize;
    static long totalDataSize;
    Button button;
    TextView totalJunkSize;
    TextView packageName;
    TextView found;
    ProgressBar progressBar;
    TextView textUnit;
    static String sizeUnit;
    static String size;
    ConstraintLayout constraintLayout;

    int iterations;

    ExpandableListAdapter listAdapter2;

    Dialog dialog;

    int it=0;

    Button btnClean;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialization

      try {
          junckInfo = new ArrayList<>();
          packageInfo = new ArrayList<>();
          cacheItems = new ArrayList<>();
          appDataItems = new ArrayList<>();
          context = getApplicationContext();
          totalJunkSize = findViewById(R.id.totalJunk);
          packageName = findViewById(R.id.txtPackage);
          found = findViewById(R.id.txtFound);
          progressBar = findViewById(R.id.scanProgress);

          Sprite doubleBounce = new Wave();
          doubleBounce.setColor(getResources().getColor(R.color.defaultGreen2));
          progressBar.setIndeterminateDrawable(doubleBounce);


          btnClean = findViewById(R.id.clearCache);
          PushDownAnim.setPushDownAnimTo(btnClean);

          textUnit = findViewById(R.id.junkUnit);

          cachedItemsList = findViewById(R.id.listView2);


          constraintLayout = findViewById(R.id.constraintLayout);

          setTitle("Junk Files");
          try {
              getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          }catch (Exception e){
              e.printStackTrace();
          }finally {
              //call scan Method

              scan();
          }

      }catch (Exception e){
          e.printStackTrace();
      }
    }
   //perparing listview
    private void loadListView() {
        try {
            listHashMap = new HashMap<>();
            header = new ArrayList<>();

            header.add("Cache");
            header.add("App Data");
            listHashMap.put(header.get(0), SplashScreen.cacheItems);
            listHashMap.put(header.get(1), SplashScreen.appDataItems);

            Log.e("info : ",Integer.toString(cacheItems.size())+Integer.toString(appDataItems.size()));

             listAdapter2 = new ExpandableListAdapter(getApplicationContext(), header, listHashMap);

            cachedItemsList.setAdapter(listAdapter2);

        }catch (Exception e){

        }
    }


    public static String convert(long cacheSize) {
        try {
            DecimalFormat dec = new DecimalFormat("0.0");
            float k = cacheSize / 1024;
            float m = k / 1024;
            float g = m / 1024;
            float t = g / 1024;
            if (t >1) {
                size = dec.format(t);
                sizeUnit = "TB";
                return  size+ " TB";
            } else if (g >1) {
                size = dec.format(g);
                sizeUnit = "GB";
                return  size+ " GB";
            } else if (m > 1) {
                size = dec.format(m);
                sizeUnit = "MB";
                return  size+ " MB";
            } else if (k > 1) {
                size = dec.format(k);
                sizeUnit = "KB";
                return  size+ " KB";
            } else {
                size = dec.format(cacheSize);
                sizeUnit = "B";
                return  size+ " B";
            }
        } catch (Exception e) {
            //ignore;
        }
        return "0 B";

    }
    public String convertSize(long cacheSize) {
        try {
            DecimalFormat dec = new DecimalFormat("0.0");
            float k = cacheSize / 1024;
            float m = k / 1024;
            float g = m / 1024;
            float t = g / 1024;
            if (t >= 1) {
                sizeUnit ="TB";
                return dec.format(t);
            } else if (g >= 1) {
                sizeUnit ="GB";
                return dec.format(g);
            } else if (m >= 1) {
                sizeUnit ="MB";
                return dec.format(m);
            } else if (k >= 1) {
                sizeUnit ="KB";
                return dec.format(k);
            } else {
                sizeUnit ="B";
                return dec.format(cacheSize);
            }
        } catch (Exception e) {
            //ignore;
        }
        return "0";

    }





    public void onClick(View view){
        try {
            cachedItemsList.collapseGroup(0);
            cachedItemsList.collapseGroup(1);
            long totalCleaned = 0;
            ArrayList<CacheItem> removableItems = new ArrayList<>();
            for (int i = 0; i < cacheItems.size(); i++) {
                if (cacheItems.get(i).isSelected()) {
                    removableItems.add(cacheItems.get(i));
                    totalCleaned += cacheItems.get(i).getSize();
                }
            }
            cacheItems.removeAll(removableItems);
            removableItems.clear();
            for (int i = 0; i < appDataItems.size(); i++) {
                if (appDataItems.get(i).isSelected()) {
                    removableItems.add(appDataItems.get(i));
                    totalCleaned += appDataItems.get(i).getSize();
                }
            }
            appDataItems.removeAll(removableItems);

            listHashMap.clear();
            listHashMap.put(header.get(0), cacheItems);
            listHashMap.put(header.get(1), appDataItems);
            calculate();
            if(cacheItems.size() ==0&&appDataItems.size()==0){
                Home.cleanFlag = false;
            }
            ExpandableListAdapter listAdapter = new ExpandableListAdapter(getApplicationContext(), header, listHashMap);
            listAdapter.setNewItems(header, listHashMap);

            Intent intent = new Intent(getApplicationContext(), CleanedActivity.class);
            intent.putExtra("total", totalCacheSize+totalDataSize);
            startActivity(intent);
            if(!Home.cleanFlag)
                finish();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        if(CleanedActivity.comingBack){
            CleanedActivity.comingBack = false;
            finish();
        }
        super.onResume();
        calculate();
    }
    public void calculate(){
        try {
            long totalRemaining = 0;
            long remainingCacheSize = 0;
            long remainingDataSize = 0;
            for (int i = 0; i < cacheItems.size(); i++) {
                totalRemaining += cacheItems.get(i).getSize();
            }
            remainingCacheSize = totalRemaining;
            for (int i = 0; i < appDataItems.size(); i++) {
                totalRemaining += appDataItems.get(i).getSize();
            }
            remainingDataSize = totalRemaining - remainingCacheSize;
            MainActivity.totalCacheSize = remainingCacheSize;
            MainActivity.totalDataSize = remainingDataSize;
            size = convertSize(totalRemaining);
            totalJunkSize.setText(size);
        }catch (Exception e){

        }
    }
    public void updateViews(){
        packageName.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        totalJunkSize.setVisibility(View.VISIBLE);
        found.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.VISIBLE);
        //scanningText.setVisibility(View.GONE);
        btnClean.setVisibility(View.VISIBLE);
        textUnit.setVisibility(View.VISIBLE);
        cachedItemsList.setVisibility(View.VISIBLE);

    }
    public void scan() {
       // progressBar.setVisibility(View.VISIBLE);
         dialog = createDialog();
        final TextView text = dialog.findViewById(R.id.txtCacheApp);


      final Handler handler = new Handler();
        final Timer  timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               handler.post(new Runnable() {
                   @Override
                   public void run() {

                       if(iterations==10){
                           text.setText("Almost Done ");
                       }

                       if(SplashScreen.scanned){
                           Log.e("run","true");
                           appDataItems = SplashScreen.appDataItems;
                           cacheItems = SplashScreen.cacheItems;
                           Log.e("size",Integer.toString(appDataItems.size())+" "+Integer.toString(cacheItems.size()));
                           totalCacheSize = SplashScreen.totalCacheSize;
                           totalDataSize = SplashScreen.totalDataSize;
                           MainActivity.convert(totalCacheSize+totalDataSize);
                           totalJunkSize.setText(size);
                           textUnit.setText(sizeUnit);
                           updateViews();
                           loadListView();
                           dialog.hide();
                           timer.cancel();
                       }
                       iterations++;
                   }
               });
            }
        },0,1000);

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

    public Dialog createDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v =  inflater.inflate(R.layout.progress_dialog,null);
        builder.setView(v);
        builder.setCancelable(false);
        Dialog dialog = builder.create();

        dialog.show();
        return  dialog;
    }

}