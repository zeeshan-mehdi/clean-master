package com.alim.cleanmaster;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {
    private ArrayList<VideoDataModel> mDataset;
    private CustomItemClickListner customListener;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mThumb;
        public CheckBox checkBox;
        public MyViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.txtTitle);
            mThumb = v.findViewById(R.id.imgThumbnail);
            checkBox = v.findViewById(R.id.videosChkbox_id);
        }
    }
    public void updataList(Context context,ArrayList<VideoDataModel> videos){
        this.mDataset = videos;
        this.context = context;
        notifyDataSetChanged();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public VideosAdapter(Context context,ArrayList<VideoDataModel> myDataset,CustomItemClickListner customListener) {
        mDataset = myDataset;
        this.customListener = customListener;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_row, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(v);
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
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position).getVideoName());
        Glide.with(context).load(mDataset.get(position).getVideoThumbnail()).into(holder.mThumb);

        //holder.mThumb.setImageBitmap(mDataset.get(position).getVideoThumbnail());
        holder.checkBox.setChecked(mDataset.get(position).isSelected());
        final int i = position;
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean current =VideoDataModel.videos.get(i).isSelected();

                VideoDataModel.videos.get(i).setSelected(!current);
                mDataset = VideoDataModel.videos;
                notifyDataSetChanged();
            }
        });
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
    }

}