package com.alim.cleanmaster;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.alim.cleanmaster.VideoDataModel.videos;


public class VideosFragment extends Fragment {
    static Boolean status = false;
    RecyclerView recyclerView;
    static ProgressBar progressBar;
    static VideosAdapter adapter;
    FloatingActionButton filters;

    boolean isSize = true;
    boolean isAscending = true;

    RadioButton bySize;
    RadioButton byDate;

    RadioButton byAscending;
    RadioButton byDescending;
    CheckBox markAll;

    RadioGroup rg1;
    RadioGroup rg2;

    Button btnOK;
    Button btnCancel;



    Button btnDelete;

    Dialog dialog;

    public void VideosFragment(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


       try {
           View view = inflater.inflate(R.layout.video_fragment, container, false);
            status = true;
           recyclerView = view.findViewById(R.id.videosRecyclerView);
           if (recyclerView == null) {
               Log.e("Recycler view ", "null");
               return null;
           }
           progressBar = view.findViewById(R.id.videosProgressBar);
           filters = view.findViewById(R.id.videoFilters);

           progressBar.setVisibility(View.VISIBLE);
           final CustomItemClickListner customItemClickListner = new CustomItemClickListner() {
               @Override
               public void onItemClick(View v, int position) {

               }

               @Override
               public void onItemLongClick(int position) {

               }
           };
           btnDelete = view.findViewById(R.id.btnVideoDelete_id);
           adapter = new VideosAdapter(getContext(), videos, customItemClickListner);
           LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
           recyclerView.setLayoutManager(linearLayoutManager);
           recyclerView.setAdapter(adapter);
           PushDownAnim.setPushDownAnimTo(btnDelete);

           Sprite doubleBounce = new Wave();
           doubleBounce.setColor(getResources().getColor(R.color.defaultGreen2));
           progressBar.setIndeterminateDrawable(doubleBounce);

           if (videos.size() != 0)
               progressBar.setVisibility(View.INVISIBLE);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Are you sure ?");

                    builder.setMessage("You want to delete Videos ..");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for(int i=0;i<VideoDataModel.videos.size();i++){
                                VideoDataModel video = VideoDataModel.videos.get(i);
                                if(video.isSelected())
                                {
                                    new File(video.getVideoPath()).getAbsoluteFile().delete();
                                    VideoDataModel.videos.remove(video);
                                }
                            }
                            adapter.updataList(getContext(),VideoDataModel.videos);
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

           PushDownAnim.setPushDownAnimTo(filters);

            filters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyFilters();
                }
            });

           return view;
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }
    }

    public void updateViews(ArrayList<VideoDataModel> videos, Context context){
        final CustomItemClickListner customItemClickListner = new CustomItemClickListner() {
            @Override
            public void onItemClick(View v, int position) {
            }

            @Override
            public void onItemLongClick(int position) {

            }
        };

        VideosAdapter adapter = new VideosAdapter(getContext(), videos, customItemClickListner);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);


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

        PushDownAnim.setPushDownAnimTo(btnOK);
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
        Collections.sort(VideoDataModel.videos, new Comparator<VideoDataModel>() {
            @Override
            public int compare(VideoDataModel o1, VideoDataModel o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        if(!flag)
            Collections.reverse(VideoDataModel.videos);
        adapter.updataList(getContext(),VideoDataModel.videos);
    }
    public void sortArrayListBySize(boolean flag){

        Collections.sort(VideoDataModel.videos, new Comparator<VideoDataModel>() {
            public int compare(VideoDataModel left, VideoDataModel right)  {
                return (int)left.getSize() -(int) right.getSize(); // The order depends on the direction of sorting.
            }
        });
        if(!flag)
            Collections.reverse(VideoDataModel.videos);
        adapter.updataList(getContext(),VideoDataModel.videos);

    }

    public void markAll(boolean flag ){
            for(int i=0;i<VideoDataModel.videos.size();i++){
                VideoDataModel.videos.get(i).setSelected(flag);
            }

            adapter.updataList(getContext(),VideoDataModel.videos);

    }


}
