package com.alim.cleanmaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppsAdapter extends BaseAdapter implements Filterable {
    private ArrayList<PackageInfo> paths;
    private List<PackageInfo> filteredData = null;
    private AppsAdapter.ItemFilter mFilter = new AppsAdapter.ItemFilter();
    java.text.DateFormat dateFormat;
    private Context context;
    PackageManager pm ;
    ApplicationInfo ai;
    int i=0;

    public AppsAdapter(Context context, ArrayList<PackageInfo> paths) {
        filteredData = paths;
        this.paths = paths;
        this.context=context;
        this.pm = context.getPackageManager();
        this.dateFormat = android.text.format.DateFormat.getDateFormat(context);
        this.ai = new ApplicationInfo();
    }

    @Override
    public int getCount() {
        if(filteredData!=null)
            return filteredData.size();
        else
            return 0;
    }


    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final AppsAdapter.ViewHolder holder;
        try {
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.apps_row, null);
                holder = new AppsAdapter.ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.appName);
                holder.imageView = (ImageView) convertView.findViewById(R.id.appIcon);
                holder.date = (TextView) convertView.findViewById(R.id.appDate);
                holder.lock=(Button) convertView.findViewById(R.id.appLock);
                convertView.setTag(holder);
            } else {
                holder = (AppsAdapter.ViewHolder) convertView.getTag();
            }
            i = position;
            this.ai =pm.getApplicationInfo(filteredData.get(i).packageName,0);
            String title = (String) this.ai.loadLabel(this.pm);
            if(title!=""&&title.length()>20){
                title = title.substring(0,20);
            }
            holder.txtTitle.setText(title);
            File file= new File(this.ai.dataDir);
            Date date = new Date(file.lastModified());
            holder.date.setText(dateFormat.format(date));
            holder.imageView.setImageDrawable(this.ai.loadIcon(pm));
            if(AppLockActivity.isLocked.get(position))
                holder.lock.setBackgroundResource(R.drawable.locked);
            else
                holder.lock.setBackgroundResource(R.drawable.unlocked);
            holder.lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppLockActivity.topPackage=paths.get(position).packageName;
                    SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.prefrencesName),Context.MODE_PRIVATE);
                    if(sharedPreferences.contains(paths.get(position).packageName)){
                        sharedPreferences.edit().remove(paths.get(position).packageName).apply();
                        Messages.sendMessage(context, "app Unlocked");
                        AppLockActivity.isLocked.set(position,false);
                        notifyDataSetChanged();
                    }else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(paths.get(position).packageName, paths.get(position).packageName);
                        editor.apply();
                        Messages.sendMessage(context, "app locked");
                        AppLockActivity.isLocked.set(position,true);
                        notifyDataSetChanged();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;

    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView date;
        Button lock;
    }
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
//
           FilterResults results = new FilterResults();

              return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<PackageInfo>) results.values;
            if(results.count==0){
                Toast.makeText(context,"Nothing Found Modify Filters",Toast.LENGTH_LONG).show();
            }
            notifyDataSetChanged();
        }

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
