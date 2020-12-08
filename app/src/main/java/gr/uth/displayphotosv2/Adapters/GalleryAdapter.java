package gr.uth.displayphotosv2.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import gr.uth.displayphotosv2.Type;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private Context context;
    private ArrayList<File> files;
    MediaListener clickListener;

    public GalleryAdapter(Context context, ArrayList<File> files, MediaListener clickListener) {
        this.context = context;
        this.files = files;
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
    public void onBindViewHolder(@NonNull GalleryAdapter.ViewHolder holder, int position) {
        final String filePath = files.get(position).getPath();
        final Type fileType = files.get(position).getType();
        final ProgressBar progressBar = holder.progressBar;
        final ImageView playArrow = holder.playArrow;
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

                        return false;
                    }
                })
                .into(holder.file);

    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView file;
        ImageView playArrow;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //ImageView, ProgressBar and play button for each item within the recyclerview
            file = itemView.findViewById(R.id.file_item);
            playArrow = itemView.findViewById(R.id.video_play);
            progressBar = itemView.findViewById(R.id.progBar);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            //pass the view and item's position in the recyclerview
            clickListener.onClick(v,getAdapterPosition());
        }
    }
}
