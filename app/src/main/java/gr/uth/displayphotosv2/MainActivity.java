package gr.uth.displayphotosv2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    List<String> images;
    TextView gallery_number;

    private static final int READ_PERMISSION_CODE =101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gallery_number = findViewById(R.id.gallery_number);
        recyclerView = findViewById(R.id.recyclerview_gallery_images);

        //check permission
        //if permission not granted ask for permission about internal storage, else load the photos
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
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
        images = ImageGallery.listOfImages(this);


        galleryAdapter = new GalleryAdapter(this, images, new GalleryAdapter.PhotoListener() {
            @Override
            //photo click listener
            public void onPhotoClick(String path) {
                //Do something with photo
                Toast.makeText(MainActivity.this,""+path,Toast.LENGTH_SHORT).show();
            }
        });

        //set the GalleryAdapter to RecyclerView
        recyclerView.setAdapter(galleryAdapter);
        //Number of Photos textview
        gallery_number.setText("Photos ("+images.size()+")");
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