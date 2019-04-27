package com.alim.cleanmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class CpuCoolerAdapter extends BaseAdapter {
    private ArrayList<Task> paths;
    private Context context;

    public CpuCoolerAdapter(Context context, ArrayList<Task> paths) {
        this.paths = paths;
        this.context=context;
    }

    @Override
    public int getCount() {
        if(paths!=null)
            return paths.size();
        else
            return 0;
    }


    @Override
    public Object getItem(int position) {
        return paths.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CpuCoolerAdapter.ViewHolder holder;
        try {
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.cpu_cooler_row, null);
                holder = new CpuCoolerAdapter.ViewHolder();
                holder.appTitle = convertView.findViewById(R.id.appTitle);
                holder.imageView = convertView.findViewById(R.id.appCool);
                convertView.setTag(holder);
            } else {
                holder = (CpuCoolerAdapter.ViewHolder) convertView.getTag();
            }
            holder.appTitle.setText(paths.get(position).getAppName());
            holder.imageView.setImageDrawable(paths.get(position).getIcon());

        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;

    }
    public  void updateData(ArrayList<Task> selected){
        this.paths=selected;
        notifyDataSetChanged();
    }


    class ViewHolder {
        ImageView imageView;
        TextView appTitle;
    }
}
