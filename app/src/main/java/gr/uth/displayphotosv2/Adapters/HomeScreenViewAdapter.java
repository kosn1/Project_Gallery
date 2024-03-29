package gr.uth.displayphotosv2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import gr.uth.displayphotosv2.Activities.AlbumActivity;
import gr.uth.displayphotosv2.Activities.SearchActivity;
import gr.uth.displayphotosv2.HomeButton;
import gr.uth.displayphotosv2.Activities.PhotosActivity;
import gr.uth.displayphotosv2.R;
import gr.uth.displayphotosv2.Activities.VideoActivity;

public class HomeScreenViewAdapter extends RecyclerView.Adapter<HomeScreenViewAdapter.MyViewHolder> {

    private Context context;
    private List<HomeButton> mData;

    public HomeScreenViewAdapter(Context context, List<HomeButton> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater myInflater = LayoutInflater.from(context);
        view = myInflater.inflate(R.layout.cardview_item_homebutton,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.textViewButtonName.setText(mData.get(position).getName());

        //FAST OPTIMAL WAY, recommended by android
        Glide.with(context).load(mData.get(position).getImage()).into(holder.imageViewButton);

        //homescreen buttons listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (holder.textViewButtonName.getText().toString()) {
                    case "Photos": {
                        Intent intent = new Intent(context, PhotosActivity.class);
                        context.startActivity(intent);
                        break;
                    }
                    case "Videos": {
                        Intent intent = new Intent(context, VideoActivity.class);
                        context.startActivity(intent);
                        break;
                    }
                    case "Albums": {
                        Intent intent = new Intent(context, AlbumActivity.class);
                        context.startActivity(intent);
                        break;
                    }
                    case "Search": {
                        Intent intent = new Intent(context, SearchActivity.class);
                        context.startActivity(intent);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textViewButtonName;
        ImageView imageViewButton;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewButtonName = itemView.findViewById(R.id.button_id);
            imageViewButton = itemView.findViewById(R.id.button_image_id);
            cardView = itemView.findViewById(R.id.cardview_id);
        }
    }

}
