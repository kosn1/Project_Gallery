package gr.uth.displayphotosv2.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import gr.uth.displayphotosv2.Adapters.GalleryAdapter;
import gr.uth.displayphotosv2.MediaGallery;
import gr.uth.displayphotosv2.Interfaces.MediaListener;
import gr.uth.displayphotosv2.R;

public class VideoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private ArrayList<String> videos;
    private TextView numberOfVideos;

    private static final int READ_PERMISSION_CODE =101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        numberOfVideos = findViewById(R.id.videos_number);
        recyclerView = findViewById(R.id.recyclerview_gallery_videos);

        //check permission
        //if permission not granted ask for permission about internal storage, else load the videos
        if(ContextCompat.checkSelfPermission(VideoActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(VideoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
        }
        else {
            loadVideos();
        }
    }

    private void loadVideos(){
        //set the size and layout of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        //get the path of all videos
        videos = MediaGallery.listOfVideos(this);

        //onClick video listener
        final MediaListener listener = new MediaListener() {
            @Override
            public void onClick(View view, int position) {

                //prepare the Intent for OpenImage activity
                Intent intent = new Intent(getApplicationContext(), OpenImage.class);
                intent.putStringArrayListExtra("images",videos);
                intent.putExtra("position",position);
                //start new Activity
                startActivity(intent);
            }
        };

        galleryAdapter = new GalleryAdapter(this, videos, listener);

        //set the GalleryAdapter to RecyclerView
        recyclerView.setAdapter(galleryAdapter);
        //Number of Videos textview
        numberOfVideos.setText("Videos ("+videos.size()+")");
    }

    //handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Read external storage permission granted", Toast.LENGTH_SHORT).show();
                loadVideos();
            }
            else{
                Toast.makeText(this,"Read external storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}