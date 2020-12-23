package gr.uth.displayphotosv2.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import gr.uth.displayphotosv2.Adapters.GalleryAdapter;
import gr.uth.displayphotosv2.File;
import gr.uth.displayphotosv2.Interfaces.MediaListener;
import gr.uth.displayphotosv2.R;
import gr.uth.displayphotosv2.Type;

public class OpenAlbumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private ArrayList<File> files;
    private TextView numberOfFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_album);

        numberOfFiles = findViewById(R.id.files_number);
        recyclerView = findViewById(R.id.recyclerview_gallery_album_files);

        //get from the intent, the list with the photo/videos of the album
        Intent intent = getIntent();
        files = (ArrayList<File>) intent.getSerializableExtra("files");

        loadFiles(files);
    }


    //displays the photos/videos of the selected album
    private void loadFiles(final ArrayList<File> files){
        //set the size and layout of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        //onClick file listener
        final MediaListener listener = new MediaListener() {
            @Override
            public void onClick(View view, int position) {

                //prepare the Intent for OpenFile activity
                Intent intent = new Intent(getApplicationContext(), OpenFile.class);
                intent.putExtra("files", files);
                intent.putExtra("position",position);
                //start new Activity
                startActivity(intent);
            }
        };

        galleryAdapter = new GalleryAdapter(this, files, listener);

        //set the GalleryAdapter to RecyclerView
        recyclerView.setAdapter(galleryAdapter);

        //Number of photos/videos textview
        int videoCounter = 0;
        int imageCounter= 0;
        for(File f: files){
            if(f.getType() == Type.IMAGE){
                imageCounter++;
            }else {
                videoCounter++;
            }
        }
        if(imageCounter == 0){
            if(videoCounter==1){
                numberOfFiles.setText(videoCounter+" Video");
            }else {
                numberOfFiles.setText(videoCounter+" Videos");
            }
        }else if(videoCounter == 0) {
            if(imageCounter==1){
                numberOfFiles.setText(imageCounter + " Photo");
            }else {
                numberOfFiles.setText(imageCounter + " Photos");
            }
        }
        else {
            if(videoCounter==1 && imageCounter==1){
                numberOfFiles.setText(imageCounter+" Photo, "+ videoCounter+" Video");
            }else if(imageCounter==1  && videoCounter!=1){
                numberOfFiles.setText(imageCounter+" Photo, "+ videoCounter+" Videos");
            }else if(imageCounter!=1  && videoCounter==1){
                numberOfFiles.setText(imageCounter+" Photos, "+ videoCounter+" Video");
            }else {
                numberOfFiles.setText(imageCounter+" Photos, "+ videoCounter+" Videos");
            }
        }
    }
}