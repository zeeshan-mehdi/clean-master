package com.alim.cleanmaster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FilesAdapter extends BaseAdapter implements Filterable {
    private ArrayList<FileClass> paths;
    private List<FileClass> filteredData = null;
    private FilesAdapter.ItemFilter mFilter = new FilesAdapter.ItemFilter();
    private Context context;
    int i=0;

    public FilesAdapter(Context context, ArrayList<FileClass> paths) {
        filteredData = paths;
        this.paths = paths;
        this.context=context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FilesAdapter.ViewHolder holder;
        try {
            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.files_row, null);
                holder = new FilesAdapter.ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.appName);
                holder.imageView = (ImageView) convertView.findViewById(R.id.appIcon);
                holder.date = (TextView) convertView.findViewById(R.id.txtDate);
                holder.size= (TextView) convertView.findViewById(R.id.txtSize);
                holder.checkBox= convertView.findViewById(R.id.chkBox);
                convertView.setTag(holder);
            } else {
                holder = (FilesAdapter.ViewHolder) convertView.getTag();
            }
            i = position;

            String temp;

            String title =filteredData.get(position).getTitle();

            if(!title.equals("")&&title.length()>20){
                temp = title.substring(0,20);
                holder.txtTitle.setText(temp);
            }else{
                holder.txtTitle.setText(title);
            }


            holder.date.setText(filteredData.get(position).getDate());
            holder.size.setText(convert(filteredData.get(position).getSize()));
            if(filteredData.get(position).getExt().equals("pdf")){
                holder.imageView.setImageResource(R.drawable.pdf);
            }else if(filteredData.get(position).getExt().equals("doc")){
                holder.imageView.setImageResource(R.drawable.doc);
            }
            else if(filteredData.get(position).getExt().equals("xml")){
                holder.imageView.setImageResource(R.drawable.xml);
            }else if(filteredData.get(position).getExt().equals("xls")){
                holder.imageView.setImageResource(R.drawable.xls);
            }else if(filteredData.get(position).getExt().equals("ppt")){
                holder.imageView.setImageResource(R.drawable.ppt);
            }else if(filteredData.get(position).getExt().equals("txt")){
                holder.imageView.setImageResource(R.drawable.txt);
            }else{
                holder.imageView.setImageResource(R.drawable.file);
            }

            holder.checkBox.setChecked(filteredData.get(position).isSelected());

//            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    //boolean current = filteredData.get(i).isSelected();
//                    filteredData.get(i).setSelected(isChecked);
//                    FilesFragment.paths.get(i).setSelected(isChecked);
//
//                    Log.e("index",Integer.toString(i));
//
//                    notifyDataSetChanged();
//                }
//            });

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean current = filteredData.get(position).isSelected();
                    filteredData.get(position).setSelected(!current);
                    FilesFragment.paths.get(position).setSelected(!current);

                    Log.e("index",Integer.toString(position));

                    //notifyDataSetChanged();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
        return convertView;

    }

    private void updateCheckStatus(boolean checked,int pos) {
        paths.get(pos).setSelected(checked);
        notifyDataSetChanged();
    }
    public  void updateData(ArrayList<FileClass> selected){
        this.filteredData=selected;
        this.paths = selected;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView date;
        TextView size;
        CheckBox checkBox;
    }
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();

            final List<FileClass> list = paths;

            int count = list.size();
            final ArrayList<FileClass> nlist = new ArrayList<>(count);
            String filterableString;
            try {
                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).getTitle();
                     if(FilesActivity.flag==1){
                        String size = convert(FilesActivity.getSize());

                        String size2= convert(list.get(i).getSize());
                        if(size.equals(size2)){
                            nlist.add(list.get(i));

                        }

                    }else if(FilesActivity.flag==2){
                        if(FilesActivity.date.equals(list.get(i).getDate())){
                            nlist.add(list.get(i));
                        }
                    }else if(FilesActivity.flag==0){
                        if(FilesActivity.ext.equals(list.get(i).getExt())){
                            nlist.add(list.get(i));
                        }
                    }

                }
              //  FilesActivity.flag=0;
                FilesActivity.setSize(FilesActivity.getOriginal());
            }catch (Exception e){
                //ignore
            }

            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<FileClass>) results.values;
            if(results.count==0){
                Toast.makeText(context,"Nothing Found Modify Filters",Toast.LENGTH_LONG).show();
            }
            notifyDataSetChanged();
        }

    }
    public static String convert(long cacheSize) {
        try {
            DecimalFormat dec = new DecimalFormat("0.0");
            long k = cacheSize / 1024;
            long m = k / 1024;
            long g = m / 1024;
            long t = g / 1024;
            if (t >= 1) {
                return dec.format(t) + " TB";
            } else if (g >= 1) {
                return dec.format(g) + " GB";
            } else if (m >= 1) {
                return dec.format(m) + " MB";
            } else if (k >= 1) {
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
