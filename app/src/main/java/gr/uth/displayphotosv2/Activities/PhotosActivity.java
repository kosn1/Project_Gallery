package gr.uth.displayphotosv2.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;

import gr.uth.displayphotosv2.Adapters.GalleryAdapter;
import gr.uth.displayphotosv2.File;
import gr.uth.displayphotosv2.MediaGallery;
import gr.uth.displayphotosv2.Interfaces.MediaListener;
import gr.uth.displayphotosv2.R;
import gr.uth.displayphotosv2.SelectionManager;

public class PhotosActivity extends Activity {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private ArrayList<File> images;
    private TextView numberOfImages;
    private SelectionManager selectionManager;
    private ImageButton backBtn;
    LayoutInflater inflater;

    private static final int READ_PERMISSION_CODE =101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        numberOfImages = findViewById(R.id.photos_number);
        recyclerView = findViewById(R.id.recyclerview_gallery_images);
        backBtn =findViewById(R.id.btnBack);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTextView = findViewById(R.id.text_toolbar);
        setActionBar(toolbar);
        inflater =  (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectionManager = new SelectionManager(this, toolbarTextView, toolbar, backBtn, inflater);

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

                //prepare the Intent for OpenFile activity
                Intent intent = new Intent(getApplicationContext(), OpenFile.class);
                intent.putExtra("files",images);
                intent.putExtra("position",position);
                //start new Activity
                startActivity(intent);
            }

            //on long click set the SelectionManager properly
            @Override
            public void onItemLongClick(View view, int position) {
                selectionManager.setGalleryAdapter(galleryAdapter);
                selectionManager.setFiles(images);
                selectionManager.startSelection(position);
            }
        };

        galleryAdapter = new GalleryAdapter(this, images, listener,selectionManager);

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