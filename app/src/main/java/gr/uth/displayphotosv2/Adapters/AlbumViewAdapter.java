package gr.uth.displayphotosv2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import gr.uth.displayphotosv2.R;

public class AlbumViewAdapter extends RecyclerView.Adapter<AlbumViewAdapter.AlbumViewHolder> {

    private Context context;
    private ArrayList<String> albumNameList;
    private ArrayList<String> albumThumbnailList;
    private ArrayList<Integer> albumSizeList;

    public AlbumViewAdapter(Context context, ArrayList<String> albumNameList, ArrayList<String> albumThumbnailList, ArrayList<Integer> albumSizeList) {
        this.context = context;
        this.albumNameList = albumNameList;
        this.albumThumbnailList = albumThumbnailList;
        this.albumSizeList = albumSizeList;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater myInflater = LayoutInflater.from(context);
        view = myInflater.inflate(R.layout.cardview_item_album,parent,false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewAdapter.AlbumViewHolder holder, int position) {
        //set album name and size TextViews
        holder.albumName.setText(albumNameList.get(position));
        holder.albumSize.setText(albumSizeList.get(position)+" Items");

        //display album thumbnails using Glide library
        Glide.with(context).load(albumThumbnailList.get(position)).into(holder.albumThumbnail);
    }

    @Override
    public int getItemCount() {
        return albumNameList.size();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder{

        TextView albumName;
        TextView albumSize;
        ImageView albumThumbnail;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            albumName = itemView.findViewById(R.id.album_name);
            albumSize = itemView.findViewById(R.id.album_size);
            albumThumbnail = itemView.findViewById(R.id.album_thumbnail_id);

        }
    }
}
