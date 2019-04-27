package com.alim.cleanmaster;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class ImagesDataModel implements Comparable<ImagesDataModel> {
    private Bitmap image;
    private String name ;
    private String date;
    private long size;
    private String path;
    private boolean isSelected;
    static ArrayList<ImagesDataModel> images = new ArrayList<>();
    static ArrayList<String> paths = new ArrayList<>();

    public ImagesDataModel(Bitmap image, String name, String date, long size,String path,boolean isSelected) {
        this.image = image;
        this.name = name;
        this.date = date;
        this.size = size;
        this.path= path;
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    public void setSelected(boolean isSelected){
        this.isSelected = isSelected;
    }

    @Override
    public int compareTo(@NonNull ImagesDataModel o) {
        return getDate().compareTo(o.getDate());
    }

    static class asynchronousLoading extends AsyncTask<Void, Void, Bitmap> {

        static Activity context;
        java.text.DateFormat dateFormat;

        public asynchronousLoading(Activity context2) {
            context = context2;
            dateFormat = android.text.format.DateFormat.getDateFormat(context);
        }

        @Override
        protected Bitmap doInBackground(Void... viewHolders) {

            Bitmap bitmap = null;
            try {
                for (int i = 0; i < paths.size(); i++) {
                    int THUMBSIZE = 100;
                    bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(paths.get(i)), THUMBSIZE, THUMBSIZE);
                    File file = new File(paths.get(i));
                    Date date = new Date(file.lastModified());

                    images.add(new ImagesDataModel(bitmap, file.getName(), dateFormat.format(date), file.length(), paths.get(i),false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPreExecute() {

            paths = new ArrayList<>();
            images = new ArrayList<>();
            paths = getImagesPath(context);
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
        }
    }
        public static ArrayList<String> getImagesPath(Activity activity) {
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            try {
                Uri uri;
                // selected = new ArrayList<>();
                Cursor cursor;
                int column_index = 0;
                StringTokenizer st1;
                String absolutePathOfImage = null;
                uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                String[] projection = {MediaStore.MediaColumns.DATA};

                cursor = activity.getContentResolver().query(uri, projection, null,
                        null, null);

                if (cursor != null)
                    column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                while (cursor.moveToNext()) {
                    absolutePathOfImage = cursor.getString(column_index);
                    //  selected.add(true);
                    listOfAllImages.add(absolutePathOfImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                return listOfAllImages;
            }
        }
}
