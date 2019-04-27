package com.alim.cleanmaster;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.jar.Attributes;

public class VideoDataModel {
    private String videoName ;
    private String videoPath;
    private Bitmap videoThumbnail;
    private boolean isSelected;
    private String Date;
    private  long size;
    static ArrayList<VideoDataModel> videos = new ArrayList<>();

    public VideoDataModel(){
    }

    public VideoDataModel(String videoName, String videoPath, Bitmap videoThumbnail,boolean isSelected,String Date,long size) {
        this.videoName = videoName;
        this.videoPath = videoPath;
        this.videoThumbnail = videoThumbnail;
        this.isSelected = isSelected;
        this.Date = Date;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Bitmap getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(Bitmap videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public static class CollectVideosData extends AsyncTask<Context,Void,Void>{
        Context context2;

        @Override
        protected Void doInBackground(Context... contexts) {
            try {
                context2 = contexts[0];
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {MediaStore.Video.VideoColumns.DATA};
                Cursor cursor = context2.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        // pathArrList.add(cursor.getString(0));
                        String path = cursor.getString(0);

                        File file = new File(path);

                        String date = String.valueOf(file.lastModified());

                        videos.add(new VideoDataModel(getVideoTitle(path), path, getThumbnail(path),false,date,file.length()));
                        Log.d("video path ",path);
                    }
                    cursor.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(VideosFragment.status) {
                VideosFragment.adapter.updataList(context2, videos);
                VideosFragment.progressBar.setVisibility(View.INVISIBLE);
            }

                //new VideosFragment().updateViews(videos,context2);
            super.onPostExecute(aVoid);
        }

    }
    private static Bitmap getThumbnail(String path){
        try {
            final int THUMBSIZE = 150;

            Bitmap thumbImage = ThumbnailUtils.createVideoThumbnail(path,0);
            //Bitmap thumbImage2=scaleBitmap(thumbImage);
            return Bitmap.createScaledBitmap(thumbImage,THUMBSIZE,THUMBSIZE,false);
        }catch (Exception e ){
            e.printStackTrace();
            return null;
        }

    }
    private static String getVideoTitle(String path){
        File  file = new File(path);
        return file.getName();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


}

