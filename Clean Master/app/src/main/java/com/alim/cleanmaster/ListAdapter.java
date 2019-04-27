package com.alim.cleanmaster;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
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


import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {
    private ArrayList<ImagesDataModel> mDataset;
    private CustomItemClickListner customListener;
    private Context context;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView mTextView;
        public TextView mTxtSize;
        public TextView mDate;
        public ImageView mThumb;
        public CheckBox checkBox;
        public MyViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.appName);
            mDate = v.findViewById(R.id.txtDate);
            mTxtSize = v.findViewById(R.id.txtSize);
            mThumb = v.findViewById(R.id.appIcon);
            checkBox = v.findViewById(R.id.chkBox);
        }
    }
    public void updataList(Context context,ArrayList<ImagesDataModel> videos){
        this.mDataset = videos;
        this.context = context;
        notifyDataSetChanged();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ListAdapter(Context context,ArrayList<ImagesDataModel> myDataset,CustomItemClickListner customListener) {
        mDataset = myDataset;
        this.customListener = customListener;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent,
                                                         int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_layout, parent, false);
        final ListAdapter.MyViewHolder viewHolder = new ListAdapter.MyViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customListener.onItemClick(v,viewHolder.getPosition());
            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                customListener.onItemLongClick(viewHolder.getPosition());
                return true;
            }
        });
        return viewHolder;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ListAdapter.MyViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).getName());
        holder.mTxtSize.setText(MainActivity.convert(mDataset.get(position).getSize()));

        //to convert Date to String, use format method of SimpleDateFormat class.
       // String strDate = dateFormat.format(date);
        holder.mDate.setText(mDataset.get(position).getDate());
        holder.checkBox.setChecked(mDataset.get(position).isSelected());
        final int  i=position;
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean current = mDataset.get(i).isSelected();
                mDataset.get(i).setSelected(!current);
                ImagesDataModel.images.get(i).setSelected(!current);
                notifyDataSetChanged();
            }
        });
       // Glide.with(context).load(mDataset.get(position).getImage()).into(holder.mThumb);
        holder.mThumb.setImageBitmap(mDataset.get(position).getImage());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        try {
            return mDataset.size();
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }}


//in your A
