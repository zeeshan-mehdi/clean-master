package com.alim.cleanmaster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;



import java.text.DecimalFormat;
import java.util.ArrayList;

public class ProcessAdapter extends BaseAdapter {

    private ArrayList<Task> paths;
    private Context context;

    int i = 0;

    public ProcessAdapter(Context context, ArrayList<Task> paths) {
        this.paths = paths;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (paths != null)
            return paths.size();
        else
            return 0;
    }
    public void updateList(Context context,ArrayList<Task> paths){
        this.paths = paths;
        this.context = context;
        notifyDataSetChanged();
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ProcessAdapter.ViewHolder holder;
        try {
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.process_row, null);
                holder = new ProcessAdapter.ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.processName);
                holder.imageView = (ImageView) convertView.findViewById(R.id.processIcon);
                holder.chkbx= (CheckBox) convertView.findViewById(R.id.processChckbx);
                convertView.setTag(holder);
            } else {
                holder = (ProcessAdapter.ViewHolder) convertView.getTag();
            }
            holder.txtTitle.setText(paths.get(position).getAppName());
            holder.imageView.setImageDrawable(paths.get(position).getIcon());
            holder.chkbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    BoostDevice.isCleaned.set(position,isChecked);
                    Messages.sendMessage(context,"App selection : "+Boolean.toString(BoostDevice.isCleaned.get(position)));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        CheckBox chkbx;
    }

    public static String convert(long cacheSize) {
        try {
            DecimalFormat dec = new DecimalFormat("0.00");
            long k = cacheSize / 1024;
            long m = k / 1024;
            long g = m / 1024;
            long t = g / 1024;
            if (t > 1) {
                return dec.format(t) + " TB";
            } else if (g > 1) {
                return dec.format(g) + " GB";
            } else if (m > 1) {
                return dec.format(m) + " MB";
            } else if (k > 1) {
                return dec.format(k) + " KB";
            } else {
                return dec.format(cacheSize) + " B";
            }
        } catch (Exception e) {
            //ignore;
        }
        return "0 B";

    }
}
