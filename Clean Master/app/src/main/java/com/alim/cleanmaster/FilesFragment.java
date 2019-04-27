package com.alim.cleanmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class FilesFragment extends Fragment {
    ListView listView;
    Button btnDelete;
    FilesAdapter filesAdapter;
    static  ArrayList<FileClass> paths = new ArrayList<>();

    FloatingActionButton filters;

    boolean isSize=true;
    boolean isAscending=true;

    RadioButton bySize;
    RadioButton byDate;

    RadioButton byAscending;
    RadioButton byDescending;
    CheckBox markAll;

    RadioGroup rg1;
    RadioGroup rg2;

    Button btnOK;
    Button btnCancel;

    Dialog dialog;

    static boolean filesScanned = false;

    public FilesFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.files_fragment,container,false);
        listView = view.findViewById(R.id.listViewFiles_id);
        btnDelete = view.findViewById(R.id.btnDeleteFiles_id);



        PushDownAnim.setPushDownAnimTo(btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // dialog = createDialog();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//                View v =  inflater.inflate(R.layout.progress_dialog,null);
//                builder.setView(v);
                builder.setTitle("Are you sure ?");

                builder.setMessage("You want to delete Files ..");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFiles(null);
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog = builder.create();

                dialog.show();



            }
        });
//        FileClass fileClass = new FileClass(getContext());
//////        fileClass.findAllFiles(Environment.getExternalStorageDirectory());
////
////        paths = fileClass.loadFilesDate(getContext(),paths);
//        if(paths.size()==0){
//            Messages.sendMessage(getContext(),"No files found ");
//            return null;
//        }




        filters = view.findViewById(R.id.filesFilter);

        PushDownAnim.setPushDownAnimTo(filters);

        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilters();
            }
        });
        final Timer timer = new Timer();
        final Handler handler2 = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler2.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("timer", "timer task is running");
                        if (filesScanned) {
                            if(paths.size()==0){
                                Messages.sendMessage(getContext(),"No files found");
                            }
                            filesAdapter = new FilesAdapter(getContext(),paths);
                            listView.setAdapter(filesAdapter);
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 1000);

        return view;
    }

    public void deleteFiles(View view){
        ArrayList<FileClass> temp= new ArrayList<>();
        for (int i=0;i<paths.size();i++){
            if(paths.get(i).isSelected()){
                deleteFile(paths.get(i).getPath());
                FileClass.files.remove(i);
            }else{
                temp.add(paths.get(i));
            }
        }

        paths = temp;
        filesAdapter.updateData(paths);
    }
    public void deleteFile(String path){
        File fdelete = new File(path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" +path);
            } else {
                System.out.println("file not Deleted :" + path);
            }
        }
    }

    public void applyFilters(){
        final Dialog dialog = new Dialog(getActivity());

        //  dialog.setTitle("title");
        dialog.setContentView(R.layout.images_filter_dialogs);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        bySize = dialog.findViewById(R.id.btnSizes);
        byDate = dialog.findViewById(R.id.btnDates);

        byAscending = dialog.findViewById(R.id.btnAscending);
        byDescending = dialog.findViewById(R.id.btnDescending);
        markAll = dialog.findViewById(R.id.btnMarkAll);

        btnOK = dialog.findViewById(R.id.btnOK);
        btnCancel = dialog.findViewById(R.id.btnCancelIt);

        rg1 = dialog.findViewById(R.id.radioGroup2);
        rg2 = dialog.findViewById(R.id.radioGroup3);

        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.btnSizes){
                    isSize=true;
                }else if (checkedId==R.id.btnDates){
                    isSize = false;
                }
            }
        });

        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.btnAscending){
                    isAscending =true;
                }else if (checkedId==R.id.btnDescending){
                    isAscending = false;
                }
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSize){
                    sortArrayListBySize(isAscending);
                }else{
                    sortArrayListByDate(isAscending);
                }
                markAll(markAll.isChecked());
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void sortArrayListByDate(boolean flag){
        Collections.sort(paths, new Comparator<FileClass>() {
            @Override
            public int compare(FileClass o1, FileClass o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        if(!flag)
            Collections.reverse(paths);
        filesAdapter.updateData(paths);
    }
    public void sortArrayListBySize(boolean flag){

        Collections.sort(paths, new Comparator<FileClass>() {
            public int compare(FileClass left, FileClass right)  {
                return (int)left.getSize() -(int) right.getSize(); // The order depends on the direction of sorting.
            }
        });
        if(!flag)
            Collections.reverse(paths);
        filesAdapter.updateData(paths);

    }

    public void markAll(boolean flag ){
        for(int i=0;i<paths.size();i++){
            paths.get(i).setSelected(flag);
        }

        filesAdapter.updateData(paths);

    }

}
