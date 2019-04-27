package com.alim.cleanmaster;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FilesActivity extends AppCompatActivity {
    //header elements
    private ConstraintLayout filesHeader;
    private Button pickDate;
    private TextView txtDate;
    private Spinner fileSize;
    private Spinner fileUnit;
    private Spinner fileExt;
    private CheckBox checkAll;
    private  TextView txtSize;
    private SeekBar seekBar;
    //below header layout
    private ListView filesList;
    private FloatingActionButton fab;
    private ProgressBar progressBar;

    //Ads
    InterstitialAd mInterstitialAd;


    /*............................*/

    private ArrayList<FileClass> paths;

    private  FilesAdapter filesAdapter;

    private Boolean[] changed={false,false,false};

    static int flag=0;
    private static long original=0;

    private static long size=0;
    static String ext = "pdf";
    static String date= "";
    String unit;
    long value;
    HashMap<String,Int> list = new HashMap<String, Int>();

    static Boolean [] flags={false,false,false};
    final Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog datePickerDialog;
    int lastID=0;

    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            int width = getScreenSize(getApplicationContext());
            if (width == 480)
                setContentView(R.layout.activity_files_small);
            else
                setContentView(R.layout.activity_files);

            //header layout fidings

            filesHeader = findViewById(R.id.filesHeaderLayout);

            pickDate = findViewById(R.id.btnDateFiles);

            txtDate = findViewById(R.id.fielsDate);

            fileUnit = findViewById(R.id.fileUnit);

            checkAll = findViewById(R.id.markFiles);
            lastID = R.id.btnSeven2;
            seekBar = findViewById(R.id.seekBar2);
            //below header layout

            filesList = findViewById(R.id.filesList);

            fab = findViewById(R.id.filesFab);

            progressBar = findViewById(R.id.progressBarFiles);
            txtSize = findViewById(R.id.txtSize2);


            //Layout findings complete
            /*........................................................................................*/
            //Array Initialization

            paths = new ArrayList<>();


            //functions
            getPermissions();
            updateList();
            filesHeader.setVisibility(View.GONE);
            FileClass fileClass = new FileClass(getApplicationContext());
            fileClass.findAllFiles(Environment.getExternalStorageDirectory());
            paths = fileClass.loadFilesDate(getApplicationContext(), paths);
            //Got necessary Information of files
            filesAdapter = new FilesAdapter(getApplicationContext(), paths);
            filesList.setAdapter(filesAdapter);
            progressBar.setVisibility(View.GONE);

            if (paths.size() == 0) {
                Messages.sendMessage(getApplicationContext(), "No files Found");
            }
            //spinners

            //adapter for file extension


            // adapter for size


            //adapter for unit
            ArrayAdapter arrayAdapter3 = ArrayAdapter.createFromResource(getApplicationContext(), R.array.filesUnit, android.R.layout.simple_spinner_item);
            arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fileUnit.setAdapter(arrayAdapter3);
            //listnerers for file extension

            //listner for size
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    original = progress;
                    size = original;
                    txtSize.setText("File Size : " + Long.toString(size) + " " + unit.toString());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            //listner for unit

            fileUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // fileUnit.setBackgroundColor(green);
                    unit = (String) parent.getItemAtPosition(position);
                    flag = 1;
                    size = size * list.get(unit).value;
                    //Messages.sendMessage(getApplicationContext(),size+"after multiplication");
                    if (changed[1]) {
                        //Messages.sendMessage(getApplicationContext(),size+"after multiplication");
                        filesAdapter.getFilter().filter(unit);
                    }
                    flags[1] = true;
                    changed[1] = true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //unit1="KB";
                }
            });


            /*888888888888888888888888888888888888888888888888888888888888*/
            //date picker
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }

            };
            pickDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog = new DatePickerDialog(FilesActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });


            MobileAds.initialize(getApplicationContext(), "ca-app-pub-2958463332087114~5829629490");
            mInterstitialAd = new InterstitialAd(this);

            mInterstitialAd.setAdUnitId("ca-app-pub-2958463332087114/7532519946");

            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }catch (Exception e){

        }
    }
    private void updateLabel() {
        //String myFormat = "MM/dd/yy"; //In which you need put here
        //SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        flag=2;
        date=FileClass.getDateFormat().format(myCalendar.getTime());
        txtDate.setText(date);
        filesAdapter.getFilter().filter((CharSequence) date);
    }
    public void markAllFiles(View view) {
        for(int i=0;i<paths.size();i++){
            paths.get(i).setSelected(checkAll.isChecked());
        }
        filesAdapter.updateData(paths);
    }

//    public void deleteFiles(View view) {
//        try {
//            ArrayList<FileClass> tempArr = new ArrayList<>();
//            for (int i = 0; i < paths.size(); i++) {
//                if (paths.get(i).isSelected()) {
//                    File file = new File(paths.get(i).getPath());
//                    tempArr.add(paths.get(i));
//                    file.getAbsoluteFile().delete();
//                }
//            }
//            paths.removeAll(tempArr);
//            filesAdapter.updateData(paths);
//        }catch (Exception e){
//
//        }
//    }


    //permissions
    public void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){

            //resume tasks needing this permission
        }else{
            Toast.makeText(getApplicationContext(),"No Permisson Granted",Toast.LENGTH_LONG).show();
        }
    }


    public void toggleHead(View view) {
        try {
            if (filesHeader.getVisibility() == View.VISIBLE) {
                filesHeader.setVisibility(View.GONE);
            } else {
                filesHeader.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        try{
            int id = view.getId();
            Button button = findViewById(id);
            button.setBackgroundColor(getResources().getColor(R.color.selected));
            ext = button.getText().toString();
            if (lastID != 0) {
                button = findViewById(lastID);
                button.setBackgroundColor(getResources().getColor(R.color.headerColor));
            }
            lastID = id;
            if (ext.equalsIgnoreCase("all")) {
                filesAdapter.updateData(paths);
            } else
                filesAdapter.getFilter().filter(ext.toLowerCase());
            flag = 0;
        }catch (Exception e){

        }
    }

    class Int{
        long value;
        public Int(long value){
            this.value=value;
        }
    }
    void updateList(){
        list.put("B",new Int(1));
        list.put("KB",new Int(1024));
        list.put("MB",new Int(1024*1024));
        list.put("GB",new Int(1024*1024*1024));
    }
    public static long getOriginal() {
        return original;
    }

    public static void setOriginal(long original) {
        original = original;
    }

    public static long getSize() {
        return size;
    }

    public static void setSize(long size) {
        FilesActivity.size = size;
    }
    public int getScreenSize(Context context){
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
//            int height = metrics.heightPixels;
//            //1080 * 1776
//
//            //480 800
            return width;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void onBackPressed() {
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
        super.onBackPressed();
    }
}
