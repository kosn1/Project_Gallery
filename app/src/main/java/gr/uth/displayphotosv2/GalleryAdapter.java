package gr.uth.displayphotosv2;

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

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private Context context;
    private List<String> images;
    //protected PhotoListener photoListener;
    MediaListener clickListener;

    public GalleryAdapter(Context context, List<String> images, MediaListener clickListener) {
        this.context = context;
        this.images = images;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.gallery_item,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String image = images.get(position);
        final ProgressBar progressBar = holder.progressBar;
        //display images using Glide library
        Glide.with(context).load(image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        //make progress bar invisible when image loads
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.image);

        //set click listener for the photo
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                photoListener.onPhotoClick(image);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //ImageView and ProgressBar for each item within the recyclerview
            image = itemView.findViewById(R.id.image);
            progressBar = itemView.findViewById(R.id.progBar);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            //pass the view and photo's position in the recyclerview
            clickListener.onClick(v,getAdapterPosition());
        }
    }

//    public interface PhotoListener{
//        void onPhotoClick(String path);
//    }
}
