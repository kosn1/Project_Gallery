package gr.uth.displayphotosv2.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import gr.uth.displayphotosv2.File;
import gr.uth.displayphotosv2.Interfaces.MediaListener;
import gr.uth.displayphotosv2.R;
import gr.uth.displayphotosv2.SelectionManager;
import gr.uth.displayphotosv2.Type;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private Context context;
    private ArrayList<File> files;
    MediaListener clickListener;
    private SelectionManager selectionManager;

    public GalleryAdapter(Context context, ArrayList<File> files, MediaListener clickListener, SelectionManager selectionManager) {
        this.context = context;
        this.files = files;
        this.clickListener = clickListener;
        this.selectionManager = selectionManager;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public MediaListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(MediaListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, final int position) {
        final String filePath = files.get(position).getPath();
        final Type fileType = files.get(position).getType();
        final ProgressBar progressBar = holder.progressBar;
        final ImageView playArrow = holder.playArrow;
        final File file = files.get(position);
        //display photos/GIFS/videos using Glide library
        Glide.with(context).load(filePath)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //make progress bar invisible when file loads
                        progressBar.setVisibility(View.GONE);
                        //make play button visible if this is a video file
                        if(fileType == Type.VIDEO){
                            playArrow.setVisibility(View.VISIBLE);
                        }
                        //remove play button if file is an image
                        if(fileType == Type.IMAGE && playArrow.getVisibility()==View.VISIBLE){
                            playArrow.setVisibility(View.GONE);
                        }

                        return false;
                    }
                })
                .into(holder.file);

        //in some cases, it will prevent unwanted situations
        holder.checkBox.setOnCheckedChangeListener(null);

        //if true, checkbox will be selected, else unselected
        holder.checkBox.setChecked(file.isSelected());

        /*keep the position of the first checked item to prevent unexpected behavior
         by notifyDataSetChanged() call.*/
        if(selectionManager.position == position){
            holder.checkBox.setChecked(true);
            selectionManager.position = -1;
        }

        //check if action mode is on to show/hide the item's checkboxes
        if (selectionManager.isActionMode){
            holder.linearLayout.setVisibility(View.VISIBLE);
        }else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }


        // Set a listener to be invoked when the checked state of this button changes.
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                file.setSelected(isChecked);
                selectionManager.check(buttonView,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        ImageView file;
        ImageView playArrow;
        ProgressBar progressBar;
        LinearLayout linearLayout;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //ImageView, ProgressBar and play button for each item within the recyclerview
            file = itemView.findViewById(R.id.file_item);
            playArrow = itemView.findViewById(R.id.video_play);
            progressBar = itemView.findViewById(R.id.progBar);
            linearLayout =  itemView.findViewById(R.id.linearCheckBox);
            checkBox = itemView.findViewById(R.id.checkboxItem);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            //pass the view and item's position in the recyclerview
            clickListener.onClick(v,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(v,getAdapterPosition());
            return true;
        }
    }

}