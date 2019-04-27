package com.alim.cleanmaster;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.HashMap;

import static com.alim.cleanmaster.SplashScreen.appDataItems;
import static com.alim.cleanmaster.SplashScreen.cacheItems;

/**
 * Created by Zeeshan on 03/07/2018
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> listDataHeader;
    private HashMap<String,ArrayList<CacheItem>> listHashMap;

    public ExpandableListAdapter(Context context, ArrayList<String> listDataHeader, HashMap<String, ArrayList<CacheItem>> listHashMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }
    public void setNewItems(ArrayList<String> listDataHeader,HashMap<String, ArrayList<CacheItem>> listChildData) {
        this.listDataHeader = listDataHeader;
        this.listHashMap = listChildData;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listDataHeader.get(i)).get(i1); // i = Group Item , i1 = ChildItem
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String)getGroup(i);
        final int headerPos = i;
        if(view == null)
        {
            try {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_group, null);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        try {
            TextView lblListHeader = (TextView) view.findViewById(R.id.listGroup);
            final CheckBox headerCheckBox = (CheckBox) view.findViewById(R.id.headerCheckBox2);
            final TextView size = view.findViewById(R.id.groupSize);
                lblListHeader.setTypeface(null, Typeface.BOLD);
                lblListHeader.setText(headerTitle);
            if (headerPos == 0) {
                String totalSize = MainActivity.convert(MainActivity.totalCacheSize);
                size.setText(totalSize);
            } else {
                String totalSize = MainActivity.convert(MainActivity.totalDataSize);
                size.setText(totalSize);
            }
            headerCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (headerCheckBox.isChecked())
                        markAll(headerPos, true);
                    else
                        markAll(headerPos, false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public View getChildView(int i, int il, boolean b, View view, ViewGroup viewGroup) {
        try {
            final CacheItem childObject = (CacheItem) getChild(i, il);
            final int itemPos = il;
            final int headerPos = i;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_item, null);
            }

            //declaration
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView description = (TextView) view.findViewById(R.id.desc);
            TextView size = (TextView) view.findViewById(R.id.size);

            ImageView imageView = (ImageView) view.findViewById(R.id.appIcon);
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox3);
            //setting resource
            String appName = childObject.getTitle();
            if (appName.length() > 25) {
                appName = appName.substring(0, 25);
            }
            title.setText(appName);

            description.setText(childObject.getDescripton());

            size.setText(MainActivity.convert(childObject.getSize()));
            if (childObject.getIcon() != null)
                imageView.setImageDrawable(childObject.getIcon());
            else {
                imageView.setImageResource(R.mipmap.ic_launcher);
            }


            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCheckStatus(headerPos, itemPos, checkBox.isChecked());
                }
            });
            if (childObject.isSelected()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
    public void updateCheckStatus(int pos ,int itemPos,boolean isSeleceted){
        if(pos==0){
                if(isSeleceted){
                    cacheItems.get(itemPos).setIsSelected(true);
                }else{
                    cacheItems.get(itemPos).setIsSelected(false);
                }

        }else if(pos==1){
                if(isSeleceted){
                    appDataItems.get(itemPos).setIsSelected(true);
                }else{
                    appDataItems.get(itemPos).setIsSelected(false);
                }
        }else{
//                if(isSeleceted){
//                    otherItems.get(itemPos).setIsSelected(true);
//                }else{
//                    otherItems.get(itemPos).setIsSelected(false);
//                }
        }
        MainActivity.listHashMap.clear();
        MainActivity.listHashMap.put(MainActivity.header.get(0), cacheItems);
        MainActivity.listHashMap.put(MainActivity.header.get(1),appDataItems);
//        MainActivity.listHashMap.put(MainActivity.header.get(2),otherItems);
        setNewItems(MainActivity.header,MainActivity.listHashMap);
    }
    public void markAll(int pos ,boolean isSeleceted){
        if(pos==0){
            for(int i = 0; i< cacheItems.size(); i++){
            if(isSeleceted){
             cacheItems.get(i).setIsSelected(true);
            }else{
                cacheItems.get(i).setIsSelected(false);
            }
         }
        }else if(pos==1){
            for(int i = 0; i< appDataItems.size(); i++){
            if(isSeleceted){
                appDataItems.get(i).setIsSelected(true);
            }else{
                appDataItems.get(i).setIsSelected(false);
            }
            }
        }else{
//            for(int i = 0; i< otherItems.size(); i++){
//                if(isSeleceted){
//                    otherItems.get(i).setIsSelected(true);
//                }else{
//                    otherItems.get(i).setIsSelected(false);
//                }
//           }
        }
        MainActivity.listHashMap.clear();
        MainActivity.listHashMap.put(MainActivity.header.get(0), cacheItems);
        MainActivity.listHashMap.put(MainActivity.header.get(1),appDataItems);
        //MainActivity.listHashMap.put(MainActivity.header.get(2),otherItems);
        setNewItems(MainActivity.header,MainActivity.listHashMap);
    }


}

