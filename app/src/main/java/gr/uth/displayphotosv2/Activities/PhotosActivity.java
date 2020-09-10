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

public class PhotosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private ArrayList<String> images;
    private TextView numberOfImages;

    private static final int READ_PERMISSION_CODE =101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        numberOfImages = findViewById(R.id.photos_number);
        recyclerView = findViewById(R.id.recyclerview_gallery_images);

        //check permission
        //if permission not granted ask for permission about internal storage, else load the photos
        if(ContextCompat.checkSelfPermission(PhotosActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(PhotosActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
        }
        else {
            loadImages();
        }
    }

    private void loadImages(){
        //set the size and layout of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        //get the path of all images
        images = MediaGallery.listOfImages(this);

        //onClick photo listener
        final MediaListener listener = new MediaListener() {
            @Override
            public void onClick(View view, int position) {

                //prepare the Intent for OpenImage activity
                Intent intent = new Intent(getApplicationContext(), OpenImage.class);
                intent.putStringArrayListExtra("images",images);
                intent.putExtra("position",position);
                //start new Activity
                startActivity(intent);
            }
        };

        galleryAdapter = new GalleryAdapter(this, images, listener);

        //set the GalleryAdapter to RecyclerView
        recyclerView.setAdapter(galleryAdapter);
        //Number of Photos textview
        numberOfImages.setText("Photos ("+images.size()+")");
    }


    //handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == READ_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Read external storage permission granted", Toast.LENGTH_SHORT).show();
                loadImages();
            }
            else{
                Toast.makeText(this,"Read external storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}