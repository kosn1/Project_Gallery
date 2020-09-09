package gr.uth.displayphotosv2;

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

        //slow, laggy in old devices, skips frames DONT DO IT
        //holder.imageViewButton.setImageResource(mData.get(position).getImage());

        //SUPER FAST OPTIMAL WAY, recommended by android
        Glide.with(context).load(mData.get(position).getImage()).into(holder.imageViewButton);

        //homescreen buttons listener
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(holder.textViewButtonName.getText().toString());
                if(holder.textViewButtonName.getText().toString().equals("Photos")){
                    Intent intent = new Intent(context, PhotosActivity.class);
                    context.startActivity(intent);
                }else if(holder.textViewButtonName.getText().toString().equals("Videos")){
                    Intent intent = new Intent(context, VideoActivity.class);
                    context.startActivity(intent);
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
