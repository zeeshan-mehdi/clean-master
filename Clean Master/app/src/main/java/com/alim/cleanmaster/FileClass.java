package com.alim.cleanmaster;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileClass {
    private Context context;
    private String path;
    private String title;
    private long size;
    private boolean isSelected;
    private String date;
    private String ext;
    private ArrayList<File> fileList = new ArrayList<File>();
    private static java.text.DateFormat dateFormat;
    static ArrayList<File> files = new ArrayList<>();
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public ArrayList<File> getFileList() {
        return fileList;
    }

    public void setFileList(ArrayList<File> fileList) {
        this.fileList = fileList;
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public static void setDateFormat(DateFormat dateFormat) {
        FileClass.dateFormat = dateFormat;
    }

    public FileClass(Context context, String path, String title, long size, String date, String ext, boolean isSelected) {
        this.setPath(path);
        this.setTitle(title);
        this.setSize(size);
        this.setSelected(isSelected);
        this.setDate(date);
        this.setContext(context);
        setDateFormat(android.text.format.DateFormat.getDateFormat(context));
        this.setExt(ext);
    }

    public FileClass(Context context) {
        this.context = context;
        dateFormat = android.text.format.DateFormat.getDateFormat(context);
    }

    public  void findAllFiles(File dir){
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    getFileList().add(listFile[i]);
                    findAllFiles(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".pdf")
                            || listFile[i].getName().endsWith(".xls")
                            || listFile[i].getName().endsWith(".doc")
                            || listFile[i].getName().endsWith(".ppt")
                            || listFile[i].getName().endsWith(".txt")
                            || listFile[i].getName().endsWith(".xml"))
                    {
                        getFileList().add(listFile[i]);
                        files.add(new File(listFile[i].getPath()));
                        // paths.add(new fileClass(listFile[i].getName()));
                    }
                }
            }
        }
    }
    public ArrayList<FileClass> loadFilesDate(Context context,ArrayList<FileClass> arr){
        for(int i = 0; i<files.size(); i++){
            Date date = new Date(files.get(i).lastModified());
            arr.add(new FileClass(context, files.get(i).getAbsolutePath(), files.get(i).getName(), files.get(i).length(), dateFormat.format(date),checkExtension(files.get(i)),false));
        }
        FilesFragment.filesScanned  = true;
        return  arr;

    }
    public String checkExtension(File file){
        if(file.getName().endsWith(".pdf")){
            return "pdf";
        }else if(file.getName().endsWith(".xls")){
            return "xls";
        }else if(file.getName().endsWith(".doc")){
            return "doc";
        }else if(file.getName().endsWith(".txt")){
            return "txt";
        }else if(file.getName().endsWith(".ppt")){
            return "ppt";
        }else if(file.getName().endsWith(".xml")){
            return "xml";
        }else{
            return null;
        }
    }
}
