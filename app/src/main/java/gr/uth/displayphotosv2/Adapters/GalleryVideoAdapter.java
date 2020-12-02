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

import java.util.List;

import gr.uth.displayphotosv2.Interfaces.MediaListener;
import gr.uth.displayphotosv2.R;

public class GalleryVideoAdapter extends RecyclerView.Adapter<GalleryVideoAdapter.ViewHolder> {

    private Context context;
    private List<String> videos;
    MediaListener clickListener;

    public GalleryVideoAdapter(Context context, List<String> videos, MediaListener clickListener) {
        this.context = context;
        this.videos = videos;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public GalleryVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GalleryVideoAdapter.ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_video,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryVideoAdapter.ViewHolder holder, int position) {
        final String video = videos.get(position);
        final ProgressBar progressBar = holder.progressBar;
        final ImageView playArrow = holder.playArrow;
        //display videos using Glide library
        Glide.with(context).load(video)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //make progress bar invisible and play button visible when video loads
                        progressBar.setVisibility(View.GONE);
                        playArrow.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.video);

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView video;
        ImageView playArrow;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //ImageView, ProgressBar and play button for each item within the recyclerview
            video = itemView.findViewById(R.id.video_item);
            playArrow = itemView.findViewById(R.id.video_play);
            progressBar = itemView.findViewById(R.id.progBar);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            //pass the view and video's position in the recyclerview
            clickListener.onClick(v,getAdapterPosition());
        }
    }
}
