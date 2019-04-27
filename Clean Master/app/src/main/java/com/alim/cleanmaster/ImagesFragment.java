package com.alim.cleanmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ImagesFragment extends Fragment {

    RecyclerView recyclerView;
    static ListAdapter listAdapter;
    static  boolean status = false;
    FloatingActionButton filters;
    Button btnDelete;
    AlertDialog levelDialog;

    RadioButton bySize;
    RadioButton byDate;
    RadioButton byAscending;
    RadioButton byDescending;
    CheckBox markAll;

    RadioGroup rg1;
    RadioGroup rg2;

    Button btnOK;
    Button btnCancel;

    boolean isSize=true;
    boolean isAscending=true;

    Dialog dialog;


    public ImagesFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.images_fragment, container, false);
            recyclerView = view.findViewById(R.id.imagesRecyclerView_id);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            btnDelete = view.findViewById(R.id.btnDeleteImages_id);
            filters = view.findViewById(R.id.fab);

            listAdapter = new ListAdapter(getContext(), ImagesDataModel.images, new CustomItemClickListner() {
                @Override
                public void onItemClick(View v, int position) {

                }

                @Override
                public void onItemLongClick(int position) {

                }
            });

            recyclerView.setAdapter(listAdapter);

            PushDownAnim.setPushDownAnimTo(btnDelete);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Are you sure ?");

                    builder.setMessage("You want to delete Images ..");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(int i=0;i<ImagesDataModel.images.size();i++){
                                ImagesDataModel  ImagesNode = ImagesDataModel.images.get(i);
                                if(ImagesNode.isSelected()){
                                    deleteImage(ImagesNode.getPath());
                                    ImagesDataModel.images.remove(ImagesNode);
                                }
                            }
                            listAdapter.updataList(getContext(),ImagesDataModel.images);
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

            status = true;
            PushDownAnim.setPushDownAnimTo(filters);
            filters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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
            });

            // progressBar.setVisibility(View.GONE);
            return view;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
    public void deleteImage(String photoUri){
        try {
            File file = new File(photoUri);
            file.delete();

            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(photoUri))));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public int getScreenSize(){
        try {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
//            //1080 * 1776
//
//            //480 800
            return width;
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public void sortArrayListByDate(boolean flag){
        Collections.sort(ImagesDataModel.images);
        if(!flag)
            Collections.reverse(ImagesDataModel.images);
        listAdapter.updataList(getContext(),ImagesDataModel.images);
    }
    public void sortArrayListBySize(boolean flag){

        Collections.sort(ImagesDataModel.images, new Comparator<ImagesDataModel>() {
            public int compare(ImagesDataModel left, ImagesDataModel right)  {
                return (int)left.getSize() -(int) right.getSize(); // The order depends on the direction of sorting.
            }
        });
        if(!flag)
            Collections.reverse(ImagesDataModel.images);
        listAdapter.updataList(getContext(),ImagesDataModel.images);

    }
    public void markAll(boolean flag ){
        for(int i=0;i<ImagesDataModel.images.size();i++){
            ImagesDataModel.images.get(i).setSelected(flag);
        }

        listAdapter.updataList(getContext(),ImagesDataModel.images);

    }



}
